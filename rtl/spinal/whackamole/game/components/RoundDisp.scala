package whackamole.game.components

import spinal.core._
import spinal.lib._

import whackamole.graphics._
import whackamole.GameConfig

case class RoundDisp(config: GameConfig = GameConfig())
    extends Drawable(config.graphicsConfig) {
  def hSize: Int = 64
  def vSize: Int = 64

  val extraIo = new Bundle {
    val round   = in UInt (4 bits)
    val reqAddr = out UInt (18 bits)
    val reqData = in UInt (8 bits)
    val inArea  = out Bool ()
  }

  val number = Number64x64(
    color = (0, 0, 0),
    config = config.graphicsConfig
  )
  number.extraIo.number  := extraIo.round
  number.extraIo.reqData := extraIo.reqData
  number.io.hPos         := io.hPos
  number.io.vPos         := io.vPos
  number.io.startHPos    := io.startHPos
  number.io.startVPos    := io.startVPos
  extraIo.reqAddr        := number.extraIo.reqAddr

  def draw(): Vec[UInt] = {
    number.io.info.rgb
  }

  override def visible(): Bool = {
    number.io.info.visible
  }

  io.info.rgb     := draw()
  io.info.visible := visible()
  extraIo.inArea  := number.extraIo.inArea
}
