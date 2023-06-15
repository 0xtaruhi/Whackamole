package whackamole.graphics

import spinal.core._
import spinal.lib._

import whackamole.GameConstants._

case class Number64x64(
    color: (Int, Int, Int) = (0, 0, 0),
    config: GraphicsConfig
) extends Drawable(config) {
  def hSize: Int = 64
  def vSize: Int = 64

  val extraIo = new Bundle {
    val number  = in UInt (4 bits)
    val reqAddr = out UInt (18 bits)
    val reqData = in UInt (8 bits)
    val inArea  = out Bool()
  }

  def draw(): Vec[UInt] = {
    val result = Vec(UInt(8 bits), 3)
    result(0) := color._1
    result(1) := color._2
    result(2) := color._3
    result
  }

  val hOffset = (hPos - startHPos)(5 downto 3)
  val vOffset = (vPos - startVPos)(5 downto 3)

  extraIo.reqAddr := U(numberAddrPrefix, 4 bits) @@ extraIo.number.resize(11 bits) @@ hOffset

  override def visible(): Bool = {
    val inArea = super.visible()
    inArea && (extraIo.reqData >> vOffset)(0)
  }

  io.info.rgb     := draw()
  io.info.visible := visible()
  extraIo.inArea  := super.visible()
}
