package whackamole.drivers

import spinal.core._
import spinal.lib._

case class VgaConfig(
    hFrontPorch: Int = 16,
    hSync: Int = 96,
    hBackPorch: Int = 48,
    hActive: Int = 640,
    vFrontPorch: Int = 10,
    vSync: Int = 2,
    vBackPorch: Int = 33,
    vActive: Int = 480
) {
  def hTotal = hFrontPorch + hSync + hBackPorch + hActive
  def vTotal = vFrontPorch + vSync + vBackPorch + vActive

  def isHBlank(h: UInt)     = h >= hActive
  def isVBlank(v: UInt)     = v >= vActive
  def willBeHBlank(h: UInt) = h === hActive - 1
  def willBeVBlank(v: UInt) = v === vActive - 1

  def isHSync(h: UInt)        =
    h >= hActive + hFrontPorch && h < hActive + hFrontPorch + hSync
  def isVSync(v: UInt)        =
    v >= vActive + vFrontPorch && v < vActive + vFrontPorch + vSync
  def willStartHSync(h: UInt) = h === hActive + hFrontPorch - 1
  def willStartVSync(v: UInt) = v === vActive + vFrontPorch - 1

  def willEndHSync(h: UInt) = h === hActive + hFrontPorch + hSync - 1
  def willEndVSync(v: UInt) = v === vActive + vFrontPorch + vSync - 1

  def willHOverflow(h: UInt) = h === hTotal - 1
  def willVOverflow(v: UInt) = v === vTotal - 1

  def inDispArea(h: UInt, v: UInt) =
    h < hActive && v < vActive
}

case class VgaDriver(config: VgaConfig) extends Component {
  val hCounterBitWidth = log2Up(config.hTotal)
  val vCounterBitWidth = log2Up(config.vTotal)

  val io = new Bundle {
    val hSync      = out Bool ()
    val vSync      = out Bool ()
    val inDispArea = out Bool ()

    val hPos = out UInt (hCounterBitWidth bits)
    val vPos = out UInt (vCounterBitWidth bits)
  }

  val hCounter = Reg(UInt(hCounterBitWidth bits)) init (0)
  val vCounter = Reg(UInt(vCounterBitWidth bits)) init (0)

  val hSync = Reg(Bool) init (False)
  val vSync = Reg(Bool) init (False)

  when(config.willHOverflow(hCounter)) {
    vCounter := Mux(config.willVOverflow(vCounter), U(0), vCounter + 1).resized
    hCounter := U(0)
  } otherwise {
    hCounter := hCounter + 1
  }

  when(config.willStartHSync(hCounter)) {
    hSync := False
  } elsewhen (config.willEndHSync(hCounter)) {
    hSync := True
  }

  when(config.willStartVSync(vCounter)) {
    vSync := False
  } elsewhen (config.willEndVSync(vCounter)) {
    vSync := True
  }

  io.hPos := hCounter
  io.vPos := vCounter

  io.hSync := hSync
  io.vSync := vSync

  io.inDispArea := config.inDispArea(hCounter, vCounter)
}