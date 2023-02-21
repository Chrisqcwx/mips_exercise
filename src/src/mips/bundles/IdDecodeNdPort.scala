package mips.bundles

import chisel3._
// import chisel3.util._
import mips.Spec
import chisel3.experimental.BundleLiterals._

class IdDecodeNdPort extends Bundle {
    val aluop = (UInt(Spec.Width.Alu.op.W))
    val alusel = (UInt(Spec.Width.Alu.sel.W))
    val reg1 = UInt(Spec.Width.Reg.data.W)
    val reg2 = UInt(Spec.Width.Reg.data.W)
    val enWrite = Bool()
    val addrWrite = UInt(Spec.Width.Reg.addr.W)
}

object IdDecodeNdPort {
    val defaultValue = (new IdDecodeNdPort).Lit(
        _.aluop -> Spec.Op.AluOp.nop,
        _.alusel -> Spec.Op.AluSel.nop,
        _.reg1 -> Spec.zeroWord,
        _.reg2 -> Spec.zeroWord,
        _.enWrite -> false.B,
        _.addrWrite -> Spec.Addr.nop
    )
}