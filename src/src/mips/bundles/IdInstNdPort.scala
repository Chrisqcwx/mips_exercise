package mips.bundles

import chisel3._
// import chisel3.util._
import mips.Spec
import chisel3.experimental.BundleLiterals._

class IdInstNdPort extends Bundle {
    val pc = (UInt(Spec.Width.Rom.addr.W))
    val inst = (UInt(Spec.Width.Rom.data.W))
}

object IdInstNdPort {
    val defaultValue = (new IdInstNdPort).Lit(
        _.pc -> Spec.Addr.nop,
        _.inst -> Spec.zeroWord
    )
}