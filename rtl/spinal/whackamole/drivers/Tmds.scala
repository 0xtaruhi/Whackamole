package whackamole.drivers

import spinal.core._
import spinal.lib._

case class TmdsInterface() extends Bundle with IMasterSlave {
  val dispEn  = Bool()
  val control = Bits(2 bits)
  val dataIn  = Bits(8 bits)
  val dataOut = Bits(10 bits)

  override def asMaster(): Unit = {
    out(dispEn, control, dataIn)
    in(dataOut)
  }
}

class TmdsEncoder extends Component {
  val io = new Bundle {
    val tmds = slave(TmdsInterface())
  }

  def getOnesNum(data: BitVector): UInt = {
    val b          = data.asBools
    val resultSize = log2Up(data.getWidth + 1)
    b.map(_.asUInt(resultSize bits)).reduceBalancedTree(_ + _)
  }

  val qM    = Bits(9 bits)
  val calQM = new Area {
    val dataInOnesNum = getOnesNum(io.tmds.dataIn)
    val xnorFlag      =
      dataInOnesNum > 4 || (dataInOnesNum === 4 && io.tmds.dataIn(0))

    val qMVec = Vec(Bool(), 9)
    qMVec(0) := io.tmds.dataIn(0)
    qMVec(8) := !xnorFlag

    for (i <- 1 until 8) {
      qMVec(i) := io.tmds.dataIn(i) ^ qMVec(i - 1) ^ xnorFlag
    }
    qM := qMVec.asBits
  }

  val invertqM = ~qM

  val dispDisableData = io.tmds.control.mux(
    B"00" -> B"11_0101_0100",
    B"01" -> B"00_1010_1011",
    B"10" -> B"01_0101_0100",
    B"11" -> B"10_1010_1011"
  )

  val dispEnableData     = Bits(10 bits)
  val qMOnesNum          = getOnesNum(qM)
  val qMZerosNum         = qM.getWidth - qMOnesNum
  val qMOnesZerosNumDiff = (qMOnesNum - qMZerosNum).asSInt

  val qMCounter = RegInit(S(0, 10 bits))
  when(io.tmds.dispEn) {
    when(qMCounter === 0 || qMOnesNum === qMZerosNum) {
      dispEnableData(9)          := !qM(8)
      dispEnableData(8)          := qM(8)
      dispEnableData(7 downto 0) := Mux(
        qM(8),
        qM(7 downto 0),
        invertqM(7 downto 0)
      )

      qMCounter := qMCounter + Mux(
        qM(8),
        qMOnesZerosNumDiff,
        -qMOnesZerosNumDiff
      )
    } otherwise {
      val qMCounterGt0  = qMCounter > 0
      val qMOnesGtZeros = qMOnesNum > qMZerosNum

      val tempXnor = !(qMCounterGt0 ^ qMOnesGtZeros)
      when(tempXnor) {
        dispEnableData(9)          := True
        dispEnableData(7 downto 0) := invertqM(7 downto 0)
      } otherwise {
        dispDisableData(9)         := False
        dispEnableData(7 downto 0) := qM(7 downto 0)
      }
      dispEnableData(9) := qM(8)
      qMCounter := qMCounter + Mux(
        tempXnor,
        qM(8).asSInt(10 bits) |<< 1,
        -(invertqM(8).asSInt(10 bits) |<< 1)
      ) - Mux(tempXnor, qMOnesZerosNumDiff, -qMOnesZerosNumDiff)
    }
  } otherwise {
    dispEnableData := 0
    qMCounter      := 0
  }

  io.tmds.dataOut := RegNext(
    Mux(io.tmds.dispEn, dispEnableData, dispDisableData)
  ).init(0)
}

object TmdsEncoder {
  def apply(): TmdsEncoder = {
    new TmdsEncoder
  }

  def apply(tmds: TmdsInterface): Unit = {
    val encoder = new TmdsEncoder
    encoder.io.tmds <> tmds
  }

  def apply(dispEn: Bool, control: Bits, dataIn: Bits): Bits = {
    val encoder = new TmdsEncoder
    encoder.io.tmds.dispEn  := dispEn
    encoder.io.tmds.control := control
    encoder.io.tmds.dataIn  := dataIn
    encoder.io.tmds.dataOut
  }
}
