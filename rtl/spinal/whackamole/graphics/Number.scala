package whackamole.graphics

import spinal.core._
import spinal.lib._

import whackamole.GameConstants._

case class Number64x32(
    color: (Int, Int, Int) = (0, 0, 0),
    config: GraphicsConfig
) extends Drawable(config) {
  def hSize: Int = 32
  def vSize: Int = 64

  val extraIo = new Bundle {
    val number  = in UInt (4 bits)
    val reqAddr = out UInt (18 bits)
    val reqData = in UInt (16 bits)
    val inArea  = out Bool ()
  }

  def draw(): Vec[UInt] = {
    val result = Vec(UInt(8 bits), 3)
    result(0) := color._1
    result(1) := color._2
    result(2) := color._3
    result
  }

  val hOffset = (hPos - startHPos)(4 downto 1)
  val vOffset = (vPos - startVPos)(5 downto 1)

  extraIo.reqAddr := U(numberAddrPrefix, 4 bits) @@
    extraIo.number.resize(9 bits) @@
    vOffset

  override def visible(): Bool = {
    val inArea = super.visible()
    inArea && extraIo.reqData(15 - hOffset)
  }

  io.info.rgb     := draw()
  io.info.visible := visible()
  extraIo.inArea  := super.visible()
}
