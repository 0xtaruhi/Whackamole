package whackamole.game.components

import spinal.core._
import spinal.lib._
import whackamole.graphics._
import whackamole._

case class Mole(config: GameConfig = GameConfig())
    extends Drawable(config.graphicsConfig) {

  val extraIo    = new Bundle {
    val press  = in Bool ()
    val appear = in Bool ()
  }
  def hSize: Int =
    (config.gameAreaWidth - 2 * config.gameAreaBorderThickness) / 4 - 2 * config.moleHalfGap
  def vSize: Int = hSize

  def draw(): Vec[UInt] = {
    val result = Vec(UInt(8 bits), 3)
    when(extraIo.appear) {
      result(0) := 43
      result(1) := 142
      result(2) := 200
    } elsewhen (!extraIo.press) {
      result(0) := 25
      result(1) := 68
      result(2) := 142
    } otherwise {
      result(0) := 142
      result(1) := 68
      result(2) := 25
    }
    result
  }

  io.info.rgb     := draw()
  io.info.visible := super.visible()
}

case class Moles(config: GameConfig = GameConfig())
    extends Drawable(config.graphicsConfig) {

  val extraIo = new Bundle {
    val keyPress        = in Bool ()
    val keyIndex        = in UInt (4 bits)
    val moleAppear      = in Bool ()
    val moleAppearIndex = in UInt (4 bits)
  }

  val moles = Seq.fill(16)(Mole())

  val moleAppearVecs = Vec(Bool(), 16)
  for (i <- 0 until 16) {
    moleAppearVecs(i) := extraIo.moleAppear && extraIo.moleAppearIndex === i
  }

  val rawGameAreaWidth = config.gameAreaWidth - 2 * config.moleHalfGap

  for {
    i <- 0 until 4
    j <- 0 until 4
  } {
    val moleIdx = i * 4 + j
    moles(moleIdx).extraIo.appear := moleAppearVecs(moleIdx)
    moles(
      moleIdx
    ).io.startHPos := j * (rawGameAreaWidth / 4) + config.moleHalfGap + config.gameAreaBorderThickness
    moles(
      moleIdx
    ).io.startVPos := i * (rawGameAreaWidth / 4) + config.moleHalfGap + config.gameAreaBorderThickness
  }

  for (mole <- moles) {
    mole.io.hPos := hPos
    mole.io.vPos := vPos
  }

  when(extraIo.keyPress) {
    for (i <- 0 until moles.size) {
      when(U(i) === extraIo.keyIndex) {
        moles(i).extraIo.press := True
      } otherwise {
        moles(i).extraIo.press := False
      }
    }
  } otherwise {
    moles.foreach(_.extraIo.press := False)
  }

  def hSize: Int = config.gameAreaWidth - 2 * config.gameAreaBorderThickness
  def vSize: Int =
    config.graphicsConfig.height - 2 * config.gameAreaBorderThickness

  def draw(): Vec[UInt] = {
    val result = Vec(UInt(8 bits), 3)
    result := MuxOH(
      moles.map(_.io.info.visible).asBits(),
      moles.map(_.io.info.rgb)
    )
    result
  }

  override def visible(): Bool = {
    moles.map(_.io.info.visible).orR
  }

  io.info.rgb     := draw()
  io.info.visible := visible()
}
