package mips.bridges

import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.IdInstNdPort
import chisel3.experimental.BundleLiterals._

class If2idIOPort extends Bundle {
    val idInst = new IdInstNdPort
}

class If2id extends BridgeModule[If2idIOPort] {
    
    def bundleFactory = new If2idIOPort
    
    def defaultValue = (new If2idIOPort).Lit(
        _.idInst -> IdInstNdPort.defaultValue
    )
    
}