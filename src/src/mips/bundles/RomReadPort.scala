package mips.bundles

import chisel3._
import chisel3.util._
import mips.Spec

class RomReadPort extends Bundle {
    val en = Input(Bool())
    val addr = Input(UInt(Spec.Width.Rom.addr.W))
    val data = Output(UInt(Spec.Width.Rom.data.W))
}