package mips.bundles

import chisel3._
import chisel3.util._
import mips.Spec

class IdInstNdPort extends Bundle {
    val pc = (UInt(Spec.Width.Rom.addr.W))
    val inst = (UInt(Spec.Width.Rom.data.W))
}