package whackamole

import whackamole.graphics._

case class GameConfig(
  val graphicsConfig: GraphicsConfig = GraphicsConfig(),
  val gameAreaWidth: Int = 480,
  val moleHalfGap: Int = 10,
  val gameAreaBorderThickness: Int = 10,
  val scoreAreaBorderThickness: Int = 10
)
