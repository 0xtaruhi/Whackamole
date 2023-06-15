package whackamole.game.components

import spinal.core._
import spinal.lib._
import whackamole.graphics._
import whackamole._

case class GameAreaBackground(config: GameConfig = GameConfig())
    extends Drawable(config.graphicsConfig) {

  def hSize: Int = config.gameAreaWidth
  def vSize: Int = config.graphicsConfig.height

  def draw(): Vec[UInt] = {
    val borderThickness = config.gameAreaBorderThickness

    def inBorder(): Bool = {
      (hPos >= borderThickness) && (hPos < (hSize - borderThickness)) &&
      (vPos >= borderThickness) && (vPos < (vSize - borderThickness))
    }

    val result = Vec(UInt(8 bits), 3)
    val gradient = ((hPos + vPos) |>> 3).resize(8 bits)

    when(inBorder()) {
      result(0) := 253 - gradient
      result(1) := 222 - gradient
      result(2) := 165 - gradient
    } otherwise {
      // Border color
      result(0) := 188
      result(1) := 118
      result(2) := 60
    }
    result
  }

  io.info.rgb     := draw()
  io.info.visible := super.visible()
}
