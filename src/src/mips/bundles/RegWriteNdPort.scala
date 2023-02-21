package mips.bundles

import chisel3._
// import chisel3.util._
import mips.Spec

import chisel3.experimental.BundleLiterals._

class RegWriteNdPort extends Bundle {
    val en = Bool()
    val addr = UInt(Spec.Width.Reg.addr.W)
    val data = UInt(Spec.Width.Reg.data.W)
}

object RegWriteNdPort {
    val defaultValue = (new RegWriteNdPort).Lit(
        _.en -> false.B,
        _.addr -> Spec.Addr.nop,
        _.data -> Spec.zeroWord
    )
}