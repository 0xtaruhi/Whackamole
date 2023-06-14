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
