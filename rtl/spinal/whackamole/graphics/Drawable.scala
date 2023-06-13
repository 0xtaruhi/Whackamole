package whackamole.graphics

import spinal.core._
import spinal.lib._

case class GraphicsInfo(config: GraphicsConfig)
    extends Bundle
    with IMasterSlave {
  val hPos = UInt(config.widthBits bits)
  val vPos = UInt(config.heightBits bits)

  val rgb     = Vec(UInt(8 bits), 3)
  val visible = Bool()

  override def asMaster(): Unit = {
    in(hPos, vPos)
    out(rgb, visible)
  }
}

abstract class Drawable(config: GraphicsConfig) extends Component {
  class DrawableInterface extends Bundle {
    val startHPos = in UInt (config.widthBits bits)
    val startVPos = in UInt (config.heightBits bits)
    val info      = master(GraphicsInfo(config))
  }

  val io = new DrawableInterface

  val hPos      = io.info.hPos
  val vPos      = io.info.vPos
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
