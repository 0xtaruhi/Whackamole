/*
 * File: Keypad.scala
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Wednesday, 14th June 2023 9:53:59 pm
 * Last Modified: Saturday, 17th June 2023 10:44:27 am
 * Copyright: 2023 - 2023 Fudan University
 */

package whackamole.drivers

import spinal.core._
import spinal.lib._

class JitterFilter(
    resetLevel: Boolean = false
) extends Component {
  val io = new Bundle {
    val input  = in Bool ()
    val output = out Bool ()
    val clear  = in Bool ()
  }

  val counter = Counter(1000)

  val prevInput = RegNext(io.input, False)
  val same      = io.input === prevInput

  val output = RegInit(Bool(resetLevel))

  when(!io.clear) {
    when(same) {
      when(!counter.willOverflowIfInc) {
        counter.increment()
      } otherwise {
        output := io.input
      }
    } otherwise {
      counter.clear()
    }
  } otherwise {
    output := Bool(resetLevel)
    counter.clear()
  }

  io.output := output
}

object JitterFilter {
  def apply(input: Bool): Bool = apply(input, False)

  def apply[T <: Data](input: T): T = apply(input, False)

  def apply(input: Bool, clear: Bool): Bool = {
    val filter = new JitterFilter
    filter.io.input := input
    filter.io.clear := clear
    filter.io.output
  }

  def apply[T <: Data](input: T, clear: Bool): T = {
    val filters = input.asBits.asBools.map(apply(_, clear))
    filters.asBits().asInstanceOf[T]
  }
}

case class KeypadDriver() extends Component {
  val io = new Bundle {
    val keyInput = in Bits (4 bits)
    val pressed  = out Bool ()
    val keyIndex = out UInt (4 bits)
  }

  val rowIdx  = Reg(UInt(2 bits)) init (0)
  val counter = CounterFreeRun(50000)

  val clear = counter.willOverflow
  val filterdInput = JitterFilter(io.keyInput, clear)

  when(counter.willOverflow) {
    rowIdx := rowIdx + 1
  }

  val colIdx = OHToUInt(filterdInput)
  io.pressed := filterdInput =/= B"0000"

  io.keyIndex := (rowIdx @@ colIdx)
}
