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

case class BoardTop(config: GameConfig = GameConfig()) extends Component {
  val io = new Bundle {
    val rgb      = Vec(out UInt (8 bits), 3)
    val hSync    = out Bool ()
    val vSync    = out Bool ()
    val keyInput = in Bits (4 bits)
  }

  val game = GameTop()

  val keypadDriver = drivers.KeypadDriver()
  keypadDriver.io.keyInput := io.keyInput

  io.hSync         := game.io.hSync
  io.vSync         := game.io.vSync
  io.rgb           := game.io.rgb
  game.io.keyPress := keypadDriver.io.pressed
  game.io.keyIndex := keypadDriver.io.keyIndex
  game.io.memData  := 0
}
