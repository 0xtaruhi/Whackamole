package whackamole.game.components

import spinal.core._
import spinal.lib._

import whackamole.graphics._
import whackamole.GameConfig

case class ScoreDisp(config: GameConfig = GameConfig())
    extends Drawable(config.graphicsConfig) {
  def hSize: Int = 80
  def vSize: Int = 64

  val extraIo = new Bundle {
    val score   = in Vec (UInt(4 bits), 2)
    val reqAddr = out UInt (18 bits)
    val reqData = in UInt (16 bits)
    val inArea  = out Bool ()
  }

  val digits =
    Seq.fill(2)(Number64x32(color = (0, 0, 0), config = config.graphicsConfig))

  digits.zip(extraIo.score).foreach { case (digit, score) =>
    digit.extraIo.number  := score
    digit.extraIo.reqData := extraIo.reqData
    digit.io.hPos         := io.hPos
    digit.io.vPos         := io.vPos

    // The lower digit is 40 pixels to the right of the higher digit
    digit.io.startHPos := io.startHPos + { if (digit == digits.head) 40 else 0 }
    digit.io.startVPos := io.startVPos
  }

  val rgb = Vec(UInt(8 bits), 3)

  extraIo.inArea := digits.map(_.extraIo.inArea).orR

  when(digits(0).extraIo.inArea) {
    extraIo.reqAddr := digits(0).extraIo.reqAddr
    rgb             := digits(0).io.info.rgb
  } otherwise {
    extraIo.reqAddr := digits(1).extraIo.reqAddr
    rgb             := digits(1).io.info.rgb
  }
  
  override def visible(): Bool = {
    digits.map(_.io.info.visible).orR
  }

  def draw(): Vec[UInt] = rgb

  io.info.rgb     := draw()
  io.info.visible := visible()
}
