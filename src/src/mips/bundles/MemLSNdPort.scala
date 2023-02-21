package mips.bundles

import chisel3._
// import chisel3.util._
import mips.Spec
import chisel3.experimental.BundleLiterals._

class MemLSNdPort extends Bundle {
    val aluop = (UInt(Spec.Width.Alu.op.W))
    val addr = UInt(Spec.Width.Rom.addr.W)
    val data = UInt(Spec.Width.Rom.data.W)
}

object MemLSNdPort {
    val defaultValue = (new MemLSNdPort).Lit(
        _.aluop -> Spec.Op.AluOp.nop,
        _.addr -> Spec.Addr.nop,
        _.data -> Spec.zeroWord
    )
}