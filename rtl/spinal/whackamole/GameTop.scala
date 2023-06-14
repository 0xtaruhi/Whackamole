package whackamole

import spinal.core._
import spinal.lib._
import whackamole.game.GameController

case class GameTop(config: GameConfig = GameConfig()) extends Component {
  val io = new Bundle {
    val rgb      = Vec(out UInt (8 bits), 3)
    val hSync    = out Bool ()
    val vSync    = out Bool ()
    val keyPress = in Bool ()
    val keyIndex = in UInt (4 bits)
  }

  val updateCounter = CounterFreeRun(
    (config.frequency / config.updateFrequency).toBigInt()
  )
  val updateEn      = Reg(Bool(), False)
  updateEn := updateCounter.willOverflowIfInc

  val vgaDriver = drivers.VgaDriver(drivers.VgaConfig())
  io.hSync := vgaDriver.io.hSync
  io.vSync := vgaDriver.io.vSync

  val gameController = GameController(config)
  gameController.io.start    := True
  gameController.io.updateEn := updateEn

  val moles = game.components.Moles(config)
  moles.io.startHPos            := 0
  moles.io.startVPos            := 0
  moles.io.hPos                 := vgaDriver.io.hPos.resized
  moles.io.vPos                 := vgaDriver.io.vPos.resized
  moles.extraIo.keyIndex        := io.keyIndex
  moles.extraIo.keyPress        := io.keyPress
  moles.extraIo.moleAppear      := gameController.io.moleAppear
  moles.extraIo.moleAppearIndex := gameController.io.moleIndex

  val layer = Layer()
  layer.io.hPos              := vgaDriver.io.hPos.resized
  layer.io.vPos              := vgaDriver.io.vPos.resized
  layer.io.molesGraphicsInfo := moles.io.info

  when(vgaDriver.io.inDispArea) {
    io.rgb := layer.io.rgb
  } otherwise {
    io.rgb.foreach(_ := 0)
  }
}
