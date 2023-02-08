import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.RomReadPort

class Rom extends Module {
    val io = IO(new Bundle {
        val romReadPort = new RomReadPort
    })

    
}