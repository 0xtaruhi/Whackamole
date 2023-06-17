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
    val gameConfig = GameConfig()

    // Configuration for generating Verilog
    val config = SpinalConfig(
      defaultConfigForClockDomains =
        ClockDomainConfig(resetKind = SYNC, resetActiveLevel = LOW),
      targetDirectory = "rtl/verilog",
      defaultClockDomainFrequency = FixedFrequency(gameConfig.frequency),
      nameWhenByFile = true
    )

    config.generateVerilog(new GameTop(gameConfig))
    config.generateVerilog(new BoardTop(gameConfig))
  }
}
