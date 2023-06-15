package whackamole.game.components

import spinal.core._
import spinal.lib._
import whackamole.graphics._
import whackamole._

case class ScoreAreaBackground(config: GameConfig = GameConfig())
    extends Drawable(config.graphicsConfig) {

  def hSize: Int = config.graphicsConfig.width - config.gameAreaWidth
  def vSize: Int = config.graphicsConfig.height

  def draw(): Vec[UInt] = {
    val borderThickness = config.scoreAreaBorderThickness

    def inBorder(): Bool = {
      (hPos >= borderThickness + startHPos) && (hPos < (startHPos + hSize - borderThickness)) &&
      (vPos >= borderThickness + startVPos) && (vPos < (startVPos + vSize - borderThickness))
    }

    val result = Vec(UInt(8 bits), 3)

    val gradient = (vPos |>> 3).resize(8 bits)

    when(inBorder()) {
      result(0) := 160 - gradient
      result(1) := 216 - gradient
      result(2) := 239 - gradient
    } otherwise {
      // Border color
      result(0) := 0
      result(1) := 123
      result(2) := 187
    }
    result
  }

  io.info.rgb     := draw()
  io.info.visible := super.visible()
}
