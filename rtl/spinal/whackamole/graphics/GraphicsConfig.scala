package whackamole.graphics

import spinal.core._

case class GraphicsConfig(
  val width: Int = 640,
  val height: Int = 480
) {
  def widthBits = log2Up(width)
  def heightBits = log2Up(height)
}
