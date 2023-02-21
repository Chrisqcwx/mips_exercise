package mips.bundles

import chisel3._
// import chisel3.util._
import mips.Spec
import chisel3.experimental.BundleLiterals._

class HiLoWriteNdPort extends Bundle {
    val en = Bool()
    val hi = UInt(Spec.Width.Reg.data.W)
    val lo = UInt(Spec.Width.Reg.data.W)
}

object HiLoWriteNdPort {
    val defaultValue = (new HiLoWriteNdPort).Lit(
        _.en -> false.B,
        _.hi -> Spec.zeroWord,
        _.lo -> Spec.zeroWord
    )
}