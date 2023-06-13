package whackamole

import spinal.core._
import spinal.lib._

case class Top() extends Component {
  val io = new Bundle {
    val rgb   = Vec(out UInt (8 bits), 3)
    val hSync = out Bool ()
    val vSync = out Bool ()
  }

  val vgaDriver = drivers.VgaDriver(drivers.VgaConfig())
  io.hSync := vgaDriver.io.hSync
  io.vSync := vgaDriver.io.vSync

  when (vgaDriver.io.inDispArea) {
    io.rgb(0) := vgaDriver.io.hPos.resized
    io.rgb(1) := vgaDriver.io.vPos.resized
    io.rgb(2) := (vgaDriver.io.hPos + vgaDriver.io.vPos).resized
  } otherwise {
    io.rgb(0) := 0x00
    io.rgb(1) := 0x00
    io.rgb(2) := 0x00
  }
}
