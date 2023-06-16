package whackamole

import spinal.core._
import spinal.lib._
import whackamole.game.GameController
import whackamole.graphics.Drawable

case class GameTop(config: GameConfig = GameConfig()) extends Component {
  val io = new Bundle {
    val rgb      = Vec(out UInt (8 bits), 3)
    val hSync    = out Bool ()
    val vSync    = out Bool ()
    val keyPress = in Bool ()
    val keyIndex = in UInt (4 bits)
    val memAddr  = out UInt (18 bits)
    val memData  = in UInt (16 bits)
    val start    = in Bool ()
  }

  val updateCounter = CounterFreeRun(
    (config.frequency / config.updateFrequency).toBigInt()
  )
  val updateEn      = Reg(Bool(), False)
  updateEn := updateCounter.willOverflowIfInc

  val vgaDriver      = drivers.VgaDriver(drivers.VgaConfig())
  val gameController = GameController(config)
  val moles          = game.components.Moles(config)
  val roundDisp      = game.components.RoundDisp(config)
  val layer          = Layer()

  def braodcastVHPos(x: Drawable*) {
    for (drawable <- x) {
      drawable.io.hPos := vgaDriver.io.hPos.resized
      drawable.io.vPos := vgaDriver.io.vPos.resized
    }
  }

  // Memory related
  val memAddr = UInt(18 bits)
  when (roundDisp.extraIo.inArea) {
    memAddr := roundDisp.extraIo.reqAddr
  } elsewhen (moles.extraIo.inArea) {
    memAddr := moles.extraIo.memAddr
  } otherwise {
    memAddr := 0
  }
  io.memAddr := memAddr

  // VGA Driver
  io.hSync := vgaDriver.io.hSync
  io.vSync := vgaDriver.io.vSync

  // Game Controller
  gameController.io.start    := io.start
  gameController.io.updateEn := updateEn

  // Moles
  moles.io.startHPos            := 0
  moles.io.startVPos            := 0
  moles.extraIo.keyIndex        := io.keyIndex
  moles.extraIo.keyPress        := io.keyPress
  moles.extraIo.moleAppear      := gameController.io.moleAppear
  moles.extraIo.moleAppearIndex := gameController.io.moleIndex
  moles.extraIo.memData         := io.memData(15 downto 0)

  // Round Display
  roundDisp.extraIo.round   := (gameController.io.round + 1).resized
  roundDisp.extraIo.reqData := io.memData(7 downto 0)
  roundDisp.io.startHPos    := 500
  roundDisp.io.startVPos    := 150

  braodcastVHPos(moles, roundDisp)

  // Layer
  layer.io.hPos              := vgaDriver.io.hPos.resized
  layer.io.vPos              := vgaDriver.io.vPos.resized
  layer.io.molesGraphicsInfo := moles.io.info
  layer.io.roundGraphicsInfo := roundDisp.io.info

  when(vgaDriver.io.inDispArea) {
    io.rgb := layer.io.rgb
  } otherwise {
    io.rgb.foreach(_ := 0)
  }
}
