package mips.bundles

import chisel3._
import chisel3.util._
import mips.Spec

class RfReadPort extends Bundle {
    val en = Input(Bool())
    val addr = Input(UInt(Spec.Width.Reg.addr.W))
    val data = Output(UInt(Spec.Width.Reg.data.W))
}