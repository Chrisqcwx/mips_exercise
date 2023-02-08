import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.{HiLoReadNdPort, HiLoWriteNdPort}
import chisel3.experimental.BundleLiterals._

class HiLoReg extends Module {
    val io = IO(new Bundle {
        val input = Input(new HiLoReadNdPort)
        val output= Output(new HiLoWriteNdPort)
    })

    
}