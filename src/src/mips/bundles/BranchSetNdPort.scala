package mips.bundles

import chisel3._
import mips.Spec

class BranchSetNdPort extends Bundle {
    val en = Bool()
    val addr = UInt(Spec.Width.Rom.addr.W)
}