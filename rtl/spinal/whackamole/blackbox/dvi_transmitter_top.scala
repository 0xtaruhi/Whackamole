package whackamole.blackbox

import spinal.core._
import spinal.lib._

case class dvi_transmitter_top() extends BlackBox {
  val io = new Bundle {
    val pclk        = in Bool ()
    val pclk_x5     = in Bool ()
    val reset_n     = in Bool ()
    val video_din   = in Bits (24 bits)
    val video_hsync = in Bool ()
    val video_vsync = in Bool ()
    val video_de    = in Bool ()

    val tmds_clk_p  = out Bool ()
    val tmds_clk_n  = out Bool ()
    val tmds_data_p = out Bits (4 bits)
    val tmds_data_n = out Bits (4 bits)
  }
  noIoPrefix()
  mapClockDomain(clock = io.pclk, reset = io.reset_n)
}
