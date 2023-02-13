package mips.bundles

import chisel3._
// import chisel3.util._
import mips.Spec

class MemLSNdPort extends Bundle {
    val aluop = (UInt(Spec.Width.Alu.op.W))
    val addr = UInt(Spec.Width.Rom.addr.W)
    val data = UInt(Spec.Width.Rom.data.W)
}