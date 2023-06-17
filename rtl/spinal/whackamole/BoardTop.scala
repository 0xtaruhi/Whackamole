/*
 * File: BoardTop.scala
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Thursday, 15th June 2023 6:56:52 pm
 * Last Modified: Saturday, 17th June 2023 10:47:25 am
 * Copyright: 2023 - 2023 Fudan University
 */
package whackamole

import spinal.core._
import spinal.lib._

import whackamole.blackbox._

case class BoardTop(config: GameConfig) extends Component {
  val io = new Bundle {
    val clkx5       = in Bool ()
    val r_pin       = in Bits (4 bits)
    val c_pin       = out Bits (4 bits)
    val start       = in Bool ()
    val tmds_clk_p  = out Bool ()
    val tmds_clk_n  = out Bool ()
    val tmds_data_p = out Bits (3 bits)
    val tmds_data_n = out Bits (3 bits)

    val memAddr = out UInt (18 bits)
    val memData = in UInt (16 bits)
  }

  noIoPrefix()

  val game = GameTop(config)

  val testKey           = test_key()
  val dviTransmitterTop = dvi_transmitter_top()

  game.io.start    := io.start
  io.memAddr       := game.io.memAddr
  game.io.memData  := io.memData
  game.io.keyIndex := testKey.io.key_out.asUInt
  game.io.keyPress := testKey.io.o_key_out_en
  // game.io.keyPress := True

  testKey.io.r_pin := io.r_pin
  io.c_pin         := testKey.io.c_pin

  dviTransmitterTop.io.pclk_x5   := io.clkx5
  dviTransmitterTop.io.video_de  := game.io.dispEn
  dviTransmitterTop.io.video_din := game.io.rgb(0) ## game.io.rgb(1) ## game.io
    .rgb(2)
  dviTransmitterTop.io.video_hsync := game.io.hSync
  dviTransmitterTop.io.video_vsync := game.io.vSync

  io.tmds_clk_n  := dviTransmitterTop.io.tmds_clk_n
  io.tmds_clk_p  := dviTransmitterTop.io.tmds_clk_p
  io.tmds_data_n := dviTransmitterTop.io.tmds_data_n
  io.tmds_data_p := dviTransmitterTop.io.tmds_data_p
}
