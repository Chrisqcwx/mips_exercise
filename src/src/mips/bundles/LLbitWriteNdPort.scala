package mips.bundles

import chisel3._
// import chisel3.util._
import mips.Spec
import chisel3.experimental.BundleLiterals._

class LLbitWriteNdPort extends Bundle {
    val en = Bool()
    val value = Bool()
}

object LLbitWriteNdPort {
    val defaultValue = (new LLbitWriteNdPort).Lit(
        _.en -> false.B,
        _.value -> false.B
    )
}