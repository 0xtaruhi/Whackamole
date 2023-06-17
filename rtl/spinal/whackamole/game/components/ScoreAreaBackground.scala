/*
 * File: ScoreAreaBackground.scala
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Friday, 16th June 2023 7:04:08 pm
 * Last Modified: Saturday, 17th June 2023 10:45:06 am
 * Copyright: 2023 - 2023 Fudan University
 */
package whackamole.game.components

import spinal.core._
import spinal.lib._
import whackamole.graphics._
import whackamole._

case class ScoreAreaBackground(config: GameConfig)
    extends Drawable(config.graphicsConfig) {

  def hSize: Int = config.graphicsConfig.width - config.gameAreaWidth
  def vSize: Int = config.graphicsConfig.height

  val extraIo = new Bundle {
    val gameover = in Bool ()
  }

  def draw(): Vec[UInt] = {
    val borderThickness = config.scoreAreaBorderThickness

    def inBorder(): Bool = {
      (hPos >= borderThickness + startHPos) && (hPos < (startHPos + hSize - borderThickness)) &&
      (vPos >= borderThickness + startVPos) && (vPos < (startVPos + vSize - borderThickness))
    }

    val result = Vec(UInt(8 bits), 3)

    val gradient = (vPos |>> 3).resize(8 bits)

    val baseNormalContentColor = Vec(Seq(160, 216, 239).map(x => U(x, 8 bits)))
    val baseGameOverContentColor = Vec(
      Seq(242, 160, 161).map(x => U(x, 8 bits))
    )

    val normalBorderColor   = Vec(Seq(0, 123, 187).map(x => U(x, 8 bits)))
    val gameOverBorderColor = Vec(Seq(230, 0, 51).map(x => U(x, 8 bits)))

    when(inBorder()) {
      val baseContentColor =
        Mux(extraIo.gameover, baseGameOverContentColor, baseNormalContentColor)
      result.zip(baseContentColor).foreach { case (r, c) =>
        r := c - gradient
      }
    } otherwise {
      // Border color
      val borderColor =
        Mux(extraIo.gameover, gameOverBorderColor, normalBorderColor)
      result.zip(borderColor).foreach { case (r, c) =>
        r := c
      }
    }
    result
  }

  io.info.rgb     := draw()
  io.info.visible := super.visible()
}
