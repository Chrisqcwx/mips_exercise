package mips.bundles

import chisel3._
import mips.Spec
import chisel3.experimental.BundleLiterals._

class BranchValidNdPort extends Bundle {
    val inDelaySlot = Bool()
    val addr = UInt(Spec.Width.Rom.addr.W)
}

object BranchValidNdPort {
    val defaultValue = (new BranchValidNdPort).Lit(
        _.inDelaySlot -> false.B,
        _.addr -> Spec.Addr.nop
    )
}