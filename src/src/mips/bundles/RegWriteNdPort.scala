package mips.bundles

import chisel3._
// import chisel3.util._
import mips.Spec

class RegWriteNdPort extends Bundle {
    val en = Bool()
    val addr = UInt(Spec.Width.Reg.addr.W)
    val data = UInt(Spec.Width.Reg.data.W)
}