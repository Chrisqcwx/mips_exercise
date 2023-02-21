package mips.bundles

import chisel3._
// import chisel3.util._
import mips.Spec

class LLbitWriteNdPort extends Bundle {
    val en = Bool()
    val value = Bool()
}