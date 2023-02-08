import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.IdDecodeNdPort
import chisel3.experimental.BundleLiterals._

class Id2ex extends Module {
    val io = IO(new Bundle {
        val input = Input(new IdDecodeNdPort)
        val output = Output( new IdDecodeNdPort)
    })

    val bridgeReg = RegNext(
        io.input,
        (new IdDecodeNdPort).Lit(
            _.aluop -> Spec.Op.AluOp.nop,
            _.alusel -> Spec.Op.AluSel.nop,
            _.reg_1 -> Spec.zeroWord,
            _.reg_2 -> Spec.zeroWord,
            _.en_write -> false.B,
            _.addr_write -> Spec.Addr.nop
        )
    )
    
    io.output := bridgeReg
}