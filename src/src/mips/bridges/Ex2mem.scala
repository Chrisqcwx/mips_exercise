package mips.bridges

import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.{RegWriteNdPort, HiLoReadNdPort, HiLoWriteNdPort}
import mips.bundles.{MemLSNdPort}
import chisel3.experimental.BundleLiterals._

class Ex2memIOPort extends Bundle {
    val regWrite = new RegWriteNdPort
    val hiloWrite = new HiLoWriteNdPort
    val memLS = new MemLSNdPort
}

class Ex2mem extends BridgeModule(new Ex2memIOPort) {

    // def bundleFactory = new Ex2memIOPort

    def defaultValue = (new Ex2memIOPort).Lit(
        _.regWrite -> RegWriteNdPort.defaultValue,
        _.hiloWrite -> HiLoWriteNdPort.defaultValue,
        _.memLS -> MemLSNdPort.defaultValue
    )
}