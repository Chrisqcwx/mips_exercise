package mips.bundles

import chisel3._
import mips.Spec

class BranchValidNdPort extends Bundle {
    val inDelaySlot = Bool()
    val addr = UInt(Spec.Width.Rom.addr.W)
}