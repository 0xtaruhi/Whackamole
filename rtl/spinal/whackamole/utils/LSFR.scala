package whackamole.utils

import spinal.core._
import spinal.lib._

class LSFR(
    val width: Int,
    val taps: Seq[Int],
    val init: Int,
    val withUpdateEn: Boolean = false
) extends Component {
  val io = new Bundle {
    val value    = out UInt (width bits)
    val updateEn = withUpdateEn generate (in Bool ())
  }

  val value = Reg(UInt(width bits)) init (init)
  val next  = taps.map(value(_)).reduce(_ ^ _)

  if (withUpdateEn) {
    when(io.updateEn) {
      value := value(value.getWidth - 2 downto 0) @@ next
    }
  } else {
    value := value(value.getWidth - 2 downto 0) @@ next
  }

  io.value := value
}

object LSFR {
  def apply(
      width: Int,
      taps: Seq[Int],
      init: Int
  ): UInt = {
    val lsfr = new LSFR(width, taps, init)
    lsfr.io.value
  }

  def apply(width: Int, taps: Seq[Int]): UInt = apply(width, taps, 0)

  def apply(updateEn: Bool, width: Int, taps: Seq[Int], init: Int): UInt = {
    val lsfr = new LSFR(width, taps, init, true)
    lsfr.io.updateEn := updateEn
    lsfr.io.value
  }

  def apply(updateEn: Bool, width: Int, taps: Seq[Int]): UInt =
    apply(updateEn, width, taps, 0)
}
