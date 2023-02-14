package mips.bundles

import chisel3._
// import chisel3.util._
import mips.Spec

class RamRWPort extends Bundle {
    val en = Input(Bool())
    val enWrite = Input(Bool())
    val sel = Input(UInt(Spec.Width.Ram.sel.W))
    val addr = Input(UInt(Spec.Width.Ram.addr.W))
    val dataWrite = Input(UInt(Spec.Width.Ram.data.W))
    val dataRead = Output(UInt(Spec.Width.Ram.data.W))
}