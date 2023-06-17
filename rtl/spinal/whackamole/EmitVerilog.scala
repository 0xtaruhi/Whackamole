/*
 * File: EmitVerilog.scala
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Wednesday, 14th June 2023 9:53:59 pm
 * Last Modified: Saturday, 17th June 2023 10:47:35 am
 * Copyright: 2023 - 2023 Fudan University
 */
package whackamole

import spinal.core._
import spinal.lib._
import whackamole.game.GameController

object EmitVerilog {
  def main(args: Array[String]) {
    val gameSimConfig   = GameConfig()
    val gameBoardConfig = GameConfig(frequency = 25.175 MHz)

    // Configuration for generating Verilog
    val simConfig = SpinalConfig(
      defaultConfigForClockDomains =
        ClockDomainConfig(resetKind = SYNC, resetActiveLevel = LOW),
      targetDirectory = "rtl/verilog",
      defaultClockDomainFrequency = FixedFrequency(gameSimConfig.frequency),
      nameWhenByFile = true
    )

    val boardConfig = SpinalConfig(
      defaultConfigForClockDomains =
        ClockDomainConfig(resetKind = SYNC, resetActiveLevel = LOW),
      targetDirectory = "rtl/verilog",
      defaultClockDomainFrequency = FixedFrequency(gameBoardConfig.frequency),
      nameWhenByFile = true,
    )

    simConfig.generateVerilog(new GameTop(gameSimConfig))
    boardConfig.generateVerilog(new BoardTop(gameBoardConfig))
  }
}
