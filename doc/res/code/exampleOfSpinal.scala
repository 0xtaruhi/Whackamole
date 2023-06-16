import spinal.core._
import spinal.lib._

case class Adder(width: Int) extends Component {
  val io = new Bundle {
    val a = in UInt (width bits)
    val b = in UInt (width bits)
    val c = out Bool (width bits)
  }

  io.c := io.a + io.b
}
