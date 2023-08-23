package mips.bridges

import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.{IdDecodeNdPort, BranchValidNdPort}
import chisel3.experimental.BundleLiterals._

class Id2exIOPort extends Bundle {
    val idDecode = new IdDecodeNdPort
    val inst = UInt(Spec.Width.Rom.data.W)
    val branchValid = new BranchValidNdPort
    val delay = Bool()
}

class Id2ex extends BridgeModule(new Id2exIOPort) {
    
    // def bundleFactory = new Id2exIOPort
    // val bundleFactory = defaultValue
    
    def defaultValue = (new Id2exIOPort).Lit(
        _.idDecode -> IdDecodeNdPort.defaultValue,
        _.inst -> Spec.zeroWord,
        _.branchValid -> BranchValidNdPort.defaultValue,
        _.delay -> false.B
    )

}