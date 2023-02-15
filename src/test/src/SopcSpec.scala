import chisel3._
import chisel3.util._
import chiseltest._
import chisel3.experimental.BundleLiterals._

import utest._
import mips._
import mips.Spec

object SopcSpec extends ChiselUtestTester {
  val tests = Tests {
    test("Sopc") {
      testCircuit(new Sopc, Seq(WriteVcdAnnotation)) {
        sopc =>
          def cpuDebugPort = sopc.io.cpuDebugPort
          def romOver = sopc.io.romDebugPort

          def print_rf[T <: Bits](data : Vec[T], printIdx: Seq[Int]): Unit = {
            
            // println(s"")
            println(s"regfile regs")
            var idx = 0
            for(idx <- printIdx) {
              println(s"reg ${idx}: 0x${(data(idx).peek().litValue.toString(16))}")
            }
            println("")
          }
          def print_reg(data1: UInt, data2: UInt): Unit = {
            println(s"data 1: 0x${(data1.peek().litValue.toString(16))}")
            println(s"data 2: 0x${(data2.peek().litValue.toString(16))}")
          }
          
          var canloop = true
          var i = 0
          while (canloop) {
            if (romOver.peek().litValue.toInt == 1 || i > 30) {
                canloop = false
            }
            else {
                i = i + 1
                println(i)
            }
            sopc.clock.step(1)
          }
        //   println((3.U).toString(2))

      }
    }
  }
}
