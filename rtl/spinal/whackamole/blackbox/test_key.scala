package whackamole.blackbox

import spinal.core._
import spinal.lib._

case class test_key() extends BlackBox {
  val io = new Bundle {
    val clk = in Bool ()
    val rst = in Bool ()

    val r_pin        = in Bits (4 bits)
    val c_pin        = out Bits (4 bits)
    val key_out      = out Bits (4 bits)
    val o_key_out_en = out Bool ()
  }

  noIoPrefix()
  mapClockDomain(clock = io.clk, reset = io.rst)
}
