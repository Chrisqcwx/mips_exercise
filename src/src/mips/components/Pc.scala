import chisel3._
import chisel3.util._
import mips.Spec

class Pc extends Module {
    val io = IO(new Bundle {
        val pc = Output(UInt(Spec.Width.Rom.addr.W))
        val ce = Output(Bool())
        // val tmp = Output(UInt(11.W))
    })

    val pc = RegInit(Spec.zeroWord)
    pc := pc + 4.U
    io.pc := pc

    val ce = RegInit(Spec.zeroWord)
    ce := true.B
    io.ce := ce

    // val tmp1 = RegInit(3.U(2.W))
    // when(io.pc =/= 0.U(Spec.Width.Rom.addr.W)){
    //     tmp1 := 0.U(2.W)
    // }
    // val tmp2 = RegInit(5.U(3.W))
    // io.tmp := Cat(tmp1, tmp2, tmp1)

}