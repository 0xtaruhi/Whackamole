package whackamole.graphics

import spinal.core._
import spinal.lib._

case class Number(config: GraphicsConfig) extends Drawable(config) {
  def hSize: Int = 40
  def vSize: Int = 40

  case class NumberInterface() extends DrawableInterface

  override val io = NumberInterface()

  def draw(): Vec[UInt] = {
    val result = Vec(UInt(8 bits), 3)
    result(0) := 0xf0
    result(1) := 0x10
    result(2) := 0xd0
    result
  }

  override def visible(): Bool = {
    super.visible()
  }

  io.info.rgb     := draw()
  io.info.visible := super.visible()
}
