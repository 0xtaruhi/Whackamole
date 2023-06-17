/*
 * File: GameController.scala
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Friday, 16th June 2023 7:04:08 pm
 * Last Modified: Saturday, 17th June 2023 10:46:34 am
 * Copyright: 2023 - 2023 Fudan University
 */
package whackamole.game

import spinal.core._
import spinal.lib._
import spinal.lib.fsm._
import whackamole._
import whackamole.utils.LSFR

case class GameController(config: GameConfig) extends Component {
  val io = new Bundle {
    val start      = in Bool ()
    val updateEn   = in Bool ()
    val gameover   = out Bool ()
    val round      = out UInt (log2Up(config.rounds.size) bits)
    val moleAppear = out Bool ()
    val moleHit    = in Bool ()
    val moleIndex  = out UInt (4 bits)
  }

  val roundNum = RegInit(U(0, log2Up(config.rounds.size) bits))
  io.round := roundNum
  val random = LSFR(io.updateEn, 16, Seq(0, 2, 9), 0xabcd)

  val gameover = RegInit(False)
  io.gameover := gameover

  val moleIndex = RegInit(U(0, 4 bits))
  io.moleIndex := moleIndex
  val moleAppear = RegInit(False)
  io.moleAppear := moleAppear

  val gameFsm = new StateMachine {
    def roundInternalFsm = new StateMachine {
      val points           = Reg(UInt(log2Up(config.maxScoreIncr) bits)).init(0)
      val maxChildRoundNum = RegInit(U(0, log2Up(config.maxChildRound) bits))
      val curChildRoundNum = RegInit(U(0, log2Up(config.maxChildRound) bits))

      val minCyclesBeforeAppear = RegInit(
        U(0, log2Up(config.maxCyclesBeforeAppear) bits)
      )
      val appearCycles = RegInit(U(0, log2Up(config.maxAppearCycles) bits))

      val configureInfoState: State = new State with EntryPoint {
        whenIsActive {
          curChildRoundNum := 0
          switch(roundNum - 1) {
            for (i <- 0 until config.rounds.size) {
              is(U(i)) {
                maxChildRoundNum      := config.rounds(i).molesNum
                points                := config.rounds(i).points
                minCyclesBeforeAppear := config.toUpdateCyclesNum(
                  config.rounds(i).minTimeBeforeAppear
                )
                appearCycles          := config.toUpdateCyclesNum(
                  config.rounds(i).appearTime
                )
              }
            } // for
          } // switch
          goto(moleInitState)
        } // whenIsActive
      }

      val cyclesNumBeforeAppear =
        RegInit(U(0, log2Up(config.maxCyclesBeforeAppear) bits))
      val maxFloatingCycles     = config.maxFloatingCycles

      val moleInitState: State = new State {
        whenIsActive {
          val floatingLSFR =
            LSFR(
              io.updateEn,
              3 * log2Up(maxFloatingCycles),
              Seq(0, 2 * log2Up(maxFloatingCycles)),
              0xdead
            )
          cyclesNumBeforeAppear := minCyclesBeforeAppear + floatingLSFR.resized
          moleIndex             := random(3 downto 0)
          goto(waitForAppearState)
        }
      }

      val waitForAppearState: State = new State {
        val counter = RegInit(U(0, log2Up(config.maxCyclesBeforeAppear) bits))

        onEntry {
          counter := 0
        }
        whenIsActive {
          when(io.updateEn) {
            counter := counter + 1
          }

          when(io.updateEn && counter === cyclesNumBeforeAppear) {
            goto(moleAppearState)
          }
        }
      }

      val moleAppearState: State = new State {
        val counter = RegInit(U(0, log2Up(config.maxAppearCycles) bits))
        onEntry {
          moleAppear := True
          counter    := 0
        }

        whenIsActive {
          when(io.updateEn) {
            counter := counter + 1
          }

          when((io.updateEn && counter === appearCycles - 1) || io.moleHit) {
            when(curChildRoundNum === maxChildRoundNum - 1) {
              exit()
            } otherwise {
              curChildRoundNum := curChildRoundNum + 1
              goto(moleInitState)
            }
          }
        }

        onExit {
          moleAppear := False
        }
      }
    }

    val preStartState: State = new State with EntryPoint {
      whenIsActive {
        when(io.start) {
          goto(preRoundState)
        }
      }
    }

    val preRoundState: State = new StateDelay(
      (config.frequency * config.roundGapTime).toBigInt()
    ) {
      whenCompleted {
        goto(inRoundState)
      }
    }

    val inRoundState: State = new StateFsm(fsm = roundInternalFsm) {
      onEntry {
        roundNum := roundNum + 1
      }
      whenCompleted {
        when(roundNum === config.rounds.size) {
          goto(gameOverState)
        } otherwise {
          goto(preRoundState)
        }
      }
    }

    val gameOverState = new State {
      onEntry {
        gameover := True
      }
    }
  }
}
