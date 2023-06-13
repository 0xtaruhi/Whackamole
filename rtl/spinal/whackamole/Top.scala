package whackamole

import spinal.core._
import spinal.lib._

case class Top(config: GameConfig = GameConfig()) extends Component {
  val io = new Bundle {
    val rgb   = Vec(out UInt (8 bits), 3)
    val hSync = out Bool ()
    val vSync = out Bool ()
  }

  val vgaDriver = drivers.VgaDriver(drivers.VgaConfig())
  io.hSync := vgaDriver.io.hSync
  io.vSync := vgaDriver.io.vSync

  val layer = Layer()
  layer.io.hPos := vgaDriver.io.hPos.resized
  layer.io.vPos := vgaDriver.io.vPos.resized

  when (vgaDriver.io.inDispArea) {
    io.rgb := layer.io.rgb
  } otherwise {
    io.rgb.foreach(_ := 0)
  }
}
