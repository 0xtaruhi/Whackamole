package whackamole

import spinal.core._
import spinal.lib._

case class Top() extends Component {
  val io = new Bundle {
    val led = out Bits(8 bits)    
  }
  val ledReg = Reg(Bits(8 bits)) init(1)
  
  ledReg := ledReg.rotateLeft(1)
  io.led := ledReg
}
