package whackamole

import spinal.core._
import spinal.lib._

object EmitVerilog {
  def main(args: Array[String]) {

    // Configuration for generating Verilog
    val config = SpinalConfig(defaultConfigForClockDomains =
      ClockDomainConfig(resetKind = SYNC),
      targetDirectory = "rtl/verilog",
      defaultClockDomainFrequency = FixedFrequency(50 MHz),
      nameWhenByFile = true
    )

    config.generateVerilog(new Top)
  }
}
