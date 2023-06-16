package whackamole.game

import spinal.core._
import spinal.lib._

import whackamole.GameConfig

case class ScoreBoard(config: GameConfig = GameConfig()) extends Component {
  val io = new Bundle {
    val round = in UInt (log2Up(config.rounds.size) bits)
    val hit   = in Bool ()
    val score = out(Vec(UInt(4 bits), 2))
  }

  // The following value is the maximum possible score increment in a hit.
  val maxPossibleScoreIncr = config.rounds.map(_.points).max
  val scoreIncr            = UInt(log2Up(maxPossibleScoreIncr) bits)

  scoreIncr := 0
  for ((round, i) <- config.rounds.zipWithIndex) {
    when(io.round === i + 1) {
      scoreIncr := round.points
    }
  }

  val score          = Vec(RegInit(U(0, 4 bits)), 2)
  val waitForIncrBuf = RegInit(U(0, log2Up(maxPossibleScoreIncr) bits))

  def doScoreIncremant(): Unit = {
    when(score(0) === 9) {
      score(0) := 0
      score(1) := score(1) + 1
    } otherwise {
      score(0) := score(0) + 1
    }
  }

  when(io.hit) {
    waitForIncrBuf := scoreIncr
  }

  when(waitForIncrBuf =/= 0) {
    doScoreIncremant()
    waitForIncrBuf := waitForIncrBuf - 1
  }

  io.score := score
}
