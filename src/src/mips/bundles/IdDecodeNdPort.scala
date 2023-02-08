package mips.bundles

import chisel3._
import chisel3.util._
import mips._

class IdDecodeNdPort extends Bundle {
    val aluop = (UInt(Spec.Width.Alu.op.W))
    val alusel = (UInt(Spec.Width.Alu.sel.W))
    val reg_1 = UInt(Params.wordLength.W)
    val reg_2 = UInt(Params.wordLength.W)
    val en_write = Bool()
    val addr_write = UInt(Spec.Width.Reg.addr.W)
}