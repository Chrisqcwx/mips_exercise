package mips.bundles

import chisel3._
import chisel3.util._
import mips.Spec

class HiLoReadNdPort extends Bundle {
    val hi = Input(UInt(Spec.Width.Reg.data.W))
    val lo = Output(UInt(Spec.Width.Reg.data.W))
}