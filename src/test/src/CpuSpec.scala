import chisel3._
import chiseltest._
import chisel3.experimental.BundleLiterals._

import utest._
import mips.Spec

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly gcd.GcdDecoupledTester
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly gcd.GcdDecoupledTester'
  * }}}
  */
object GCDSpec extends ChiselUtestTester {
  val tests = Tests {
    test("Cpu") {
      testCircuit(new Cpu(), Seq(WriteVcdAnnotation)) {
        cpu =>
          def debugPort = cpu.io.cpuDebugPort

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
          val insts = Seq(
            "b00111100000000010000000000000000",
"b00111100000000101111111111111111",
"b00111100000000110000010100000101",
"b00111100000001000000000000000000",
"b00000000010000010010000000001010",
"b00000000011000010010000000001011",
"b00000000011000100010000000001011",
"b00000000010000110010000000001010",
"b00000000000000000000000000010001",
"b00000000010000000000000000010001",
"b00000000011000000000000000010001",
"b00000000000000000010000000010000",
"b00000000011000000000000000010011",
"b00000000010000000000000000010011",
"b00000000001000000000000000010011"
          )
          
          for (timestep <- 1 to insts.length) {
            println(s"\n time step: ${timestep}\n")
            
            val addr = cpu.io.romReadPort.addr.peek().litValue
            cpu.io.romReadPort.data.poke((insts(addr.toInt/4)).U(32.W))
            // println(((insts(addr.toInt/4)).U(32.W))(31,26))
            // println(Spec.Op.Inst.lui)
            print_reg(debugPort.id_reg_data1, debugPort.id_reg_data2)
            //print_rf(cpu.io.cpuDebugPort.regFileRegs, Seq(1,2,3,4))
            cpu.clock.step(1)
          }

          

          for (timestep <- insts.length + 1 to insts.length + 5) {
            println(s"\n time step: ${timestep}\n")
            
            cpu.io.romReadPort.data.poke(0.U(32.W))
            print_reg(debugPort.id_reg_data1, debugPort.id_reg_data2)
            //print_rf(dut.io.cpuDebugPort.regFileRegs, Seq(1,2,3,4))
            cpu.clock.step(1)
          }
          print_rf(cpu.io.cpuDebugPort.regFileRegs, Seq(1,2,3,4))
      }
    }
  }
}
