package mips.bridges

import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.{RegWriteNdPort, HiLoReadNdPort, HiLoWriteNdPort}
import mips.bundles.{LLbitWriteNdPort}
import chisel3.experimental.BundleLiterals._

class Mem2wbIOPort extends Bundle {
    val regWrite = new RegWriteNdPort
    val hiloWrite = new HiLoWriteNdPort
    val llbit = new LLbitWriteNdPort
}

class Mem2wb extends BridgeModule(new Mem2wbIOPort) {

    def defaultValue = (new Mem2wbIOPort).Lit(
        _.regWrite -> RegWriteNdPort.defaultValue,
        _.hiloWrite -> HiLoWriteNdPort.defaultValue,
        _.llbit -> LLbitWriteNdPort.defaultValue
    )
}