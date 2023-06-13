package whackamole

import spinal.core._
import spinal.lib._
import whackamole.graphics._
import scala.collection.mutable.ListBuffer
import whackamole.game.components._

case class Layer(config: GameConfig = GameConfig()) extends Component {
  val c = config.graphicsConfig
  val io         = new Bundle {
    val hPos = in UInt (c.widthBits bits)
    val vPos = in UInt (c.heightBits bits)

    val rgb = out Vec (UInt(8 bits), 3)
  }

  val components = ListBuffer[Drawable]()

  def registerComponents(newComponents: Drawable*) {
    components ++= newComponents

    for (c <- newComponents) {
      c.io.info.hPos := io.hPos
      c.io.info.vPos := io.vPos
    }
  }

  val gameAreaBackground = GameAreaBackground(config)
  gameAreaBackground.io.startHPos := 0
  gameAreaBackground.io.startVPos := 0

  val scoreAreaBackground = ScoreAreaBackground(config)
  scoreAreaBackground.io.startHPos := config.gameAreaWidth
  scoreAreaBackground.io.startVPos := 0

  val moles = Moles(config)
  moles.io.startHPos := 0
  moles.io.startVPos := 0
  
  // Register components, so that they can be drawn.
  registerComponents(gameAreaBackground, scoreAreaBackground, moles)

  io.rgb.foreach(_ := 0)

  for (c <- components) {
    when (c.io.info.visible) {
      io.rgb := c.io.info.rgb
    }
  }
}
