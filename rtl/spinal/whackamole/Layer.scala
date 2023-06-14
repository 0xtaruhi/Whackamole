package whackamole

import spinal.core._
import spinal.lib._
import whackamole.graphics._
import scala.collection.mutable.ListBuffer
import whackamole.game.components._

case class Layer(config: GameConfig = GameConfig()) extends Component {
  val c  = config.graphicsConfig
  val io = new Bundle {
    val hPos              = in UInt (c.widthBits bits)
    val vPos              = in UInt (c.heightBits bits)
    val molesGraphicsInfo = in(GraphicsInfo())

    val rgb = out Vec (UInt(8 bits), 3)
  }

  val components = ListBuffer[GraphicsInfo]()

  def registerComponents(newComponents: Drawable*) {
    components ++= newComponents.map(_.io.info)

    for (c <- newComponents) {
      c.io.hPos := io.hPos
      c.io.vPos := io.vPos
    }
  }

  def registerComponents(newComponents: GraphicsInfo) {
    components += newComponents
  }

  val gameAreaBackground = GameAreaBackground(config)
  gameAreaBackground.io.startHPos := 0
  gameAreaBackground.io.startVPos := 0

  val scoreAreaBackground = ScoreAreaBackground(config)
  scoreAreaBackground.io.startHPos := config.gameAreaWidth
  scoreAreaBackground.io.startVPos := 0

  // Register components, so that they can be drawn.
  registerComponents(gameAreaBackground, scoreAreaBackground)
  registerComponents(io.molesGraphicsInfo)

  io.rgb.foreach(_ := 0)

  for (c <- components) {
    when(c.visible) {
      io.rgb := c.rgb
    }
  }
}
