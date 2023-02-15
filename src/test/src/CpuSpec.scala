// import chisel3._
// import chisel3.util._
// import chiseltest._
// import chisel3.experimental.BundleLiterals._

// import utest._
// import mips._
// import mips.Spec

// /**
//   * This is a trivial example of how to run this Specification
//   * From within sbt use:
//   * {{{
//   * testOnly gcd.GcdDecoupledTester
//   * }}}
//   * From a terminal shell use:
//   * {{{
//   * sbt 'testOnly gcd.GcdDecoupledTester'
//   * }}}
//   */
// object CpuSpec extends ChiselUtestTester {
//   val tests = Tests {
//     test("Cpu") {
//       testCircuit(new Cpu(debug=true), Seq(WriteVcdAnnotation)) {
//         cpu =>
//           def debugPort = cpu.io.cpuDebugPort.get

//           def print_rf[T <: Bits](data : Vec[T], printIdx: Seq[Int]): Unit = {
            
//             // println(s"")
//             println(s"regfile regs")
//             var idx = 0
//             for(idx <- printIdx) {
//               println(s"reg ${idx}: 0x${(data(idx).peek().litValue.toString(16))}")
//             }
//             println("")
//           }
//           def print_reg(data1: UInt, data2: UInt): Unit = {
//             println(s"data 1: 0x${(data1.peek().litValue.toString(16))}")
//             println(s"data 2: 0x${(data2.peek().litValue.toString(16))}")
//           }
//           val insts = Seq(
// "b00110100000000010000000000101111",
// "b00110100000000100000000000101111",
// "b00000000000000000000000001000000",
// "b00010000001000100000000000000100",
// "b00000000000000000000000001000000",
// "b00110100000000110000000000010001",
// "b00000000001000100010000000100101",
// "b00001000000000000000000000010100",
// "b00000000000000000000000001000000",
// "b00000000000000000000000001000000",
// "b00110100000000110000000000100010",
// "b00000000000000000000000001000000",
// "b00000000000000000000000001000000",
// "b00000000000000000000000001000000",
// "b00000000000000000000000001000000",
// "b00000000000000000000000001000000"
//           )

//           var timestep :Int = 0
//           var canloop = true
          
//           while (canloop) {
//             println(s"\n time step: ${timestep}\n")
            
//             val addr = cpu.io.romReadPort.addr.peek().litValue
//             val instIdx = addr.toInt/4
//             println(s"inst idx : ${instIdx}")
//             if (instIdx >= insts.length) {
//               canloop = false
//             }
//             else {
//               cpu.io.romReadPort.data.poke((insts(addr.toInt/4)).U(32.W))
//               // println(((insts(addr.toInt/4)).U(32.W))(31,26))
//               // println(Spec.Op.Inst.lui)
//               print_reg(debugPort.id_reg_data1, debugPort.id_reg_data2)
//               //print_rf(cpu.io.cpuDebugPort.regFileRegs, Seq(1,2,3,4))
//               cpu.clock.step(1)
//               timestep =  timestep + 1 
//             }
            
//           }

//           var i = 0
//           for (i <- 1 to 5) {
//             println(s"\n time step: ${timestep}\n")
            
//             cpu.io.romReadPort.data.poke(Spec.Op.Inst.ssnop)
//             print_reg(debugPort.id_reg_data1, debugPort.id_reg_data2)
//             //print_rf(dut.io.cpuDebugPort.regFileRegs, Seq(1,2,3,4))
//             cpu.clock.step(1)
//             timestep =  timestep + 1 
//           }
//           print_rf(debugPort.regFileRegs, Seq(1,2,3,4))

//       }
//     }
//   }
// }
