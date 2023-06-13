package whackamole.game.components

import spinal.core._
import spinal.lib._
import whackamole.graphics._
import whackamole._

case class Mole(config: GameConfig = GameConfig())
    extends Drawable(config.graphicsConfig) {
  def hSize: Int =
    (config.gameAreaWidth - 2 * config.gameAreaBorderThickness) / 4 - 2 * config.moleHalfGap
  def vSize: Int = hSize

  def draw(): Vec[UInt] = {
    val result = Vec(UInt(8 bits), 3)
    result(0) := 25
    result(1) := 68
    result(2) := 142
    result
  }

  io.info.rgb     := draw()
  io.info.visible := super.visible()
}

case class Moles(config: GameConfig = GameConfig())
    extends Drawable(config.graphicsConfig) {
  val moles = Seq.fill(16)(Mole())

  val rawGameAreaWidth = config.gameAreaWidth - 2 * config.moleHalfGap

  for {
    i <- 0 until 4
    j <- 0 until 4
  } {
    val moleIdx = i * 4 + j

    moles(
      moleIdx
    ).io.startHPos := j * (rawGameAreaWidth / 4) + config.moleHalfGap + config.gameAreaBorderThickness
    moles(
      moleIdx
    ).io.startVPos := i * (rawGameAreaWidth / 4) + config.moleHalfGap + config.gameAreaBorderThickness
  }

  for (mole <- moles) {
    mole.io.info.hPos := hPos
    mole.io.info.vPos := vPos
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
