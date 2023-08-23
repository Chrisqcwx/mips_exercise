import chisel3._
import chisel3.util._
import chiseltest._
import chisel3.experimental.BundleLiterals._

import utest._
import mips._
import mips.Spec
import java.sql.ResultSet

object FpuSpec extends ChiselUtestTester {
  val tests = Tests {
    test("FPU") {
      testCircuit(new fpu(debug = true), Seq(WriteVcdAnnotation)) {
        f =>
          def res = f.io.C_
          // -0.75
          // exp: -1
          f.io.A_ poke "b1_0111_1110_100_0000_0000_0000_0000_0000".U(32.W)
          // exp: 2
          f.io.B_ poke "b0_1000_0001_010_0000_0000_0000_0000_0000".U(32.W)
          //             0_1000_0010_011
          f.io.op poke false.B

          f.clock.step(1)
          // 1; 1.011 - -1; 1.011 = => 3; 1.00001
          val resStr = (res.peek().litValue.toString(16))
          println(resStr)

      }
    }
  }
}
