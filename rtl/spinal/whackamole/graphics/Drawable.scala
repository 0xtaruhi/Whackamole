/*
 * File: Drawable.scala
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Thursday, 15th June 2023 6:56:52 pm
 * Last Modified: Saturday, 17th June 2023 10:47:02 am
 * Copyright: 2023 - 2023 Fudan University
 */
package whackamole.graphics

import spinal.core._
import spinal.lib._

case class GraphicsInfo() extends Bundle {
  val rgb     = Vec(UInt(8 bits), 3)
  val visible = Bool()
}

abstract class Drawable(config: GraphicsConfig) extends Component {
  class DrawableInterface extends Bundle {
    val startHPos = in UInt (config.widthBits bits)
    val startVPos = in UInt (config.heightBits bits)
    val hPos      = in UInt (config.widthBits bits)
    val vPos      = in UInt (config.heightBits bits)
    val info      = out(GraphicsInfo())
  }

  val io = new DrawableInterface

  val hPos      = io.hPos
  val vPos      = io.vPos
  val startHPos = io.startHPos
  val startVPos = io.startVPos

  def hSize: Int
  def vSize: Int
  def draw(): Vec[UInt]

  def visible(): Bool = {
    (hPos >= io.startHPos) && (hPos < (io.startHPos + hSize)) &&
    (vPos >= io.startVPos) && (vPos < (io.startVPos + vSize))
  }
}

