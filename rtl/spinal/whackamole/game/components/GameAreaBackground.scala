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

    when(inBorder()) {
      result(0) := 188
      result(1) := 226
      result(2) := 232
    } otherwise {
      // Border color
      result(0) := 68
      result(1) := 97
      result(2) := 123
    }
    result
  }

  io.info.rgb     := draw()
  io.info.visible := super.visible()
}
