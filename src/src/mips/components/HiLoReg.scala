import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.{HiLoReadNdPort, HiLoWriteNdPort}
import chisel3.experimental.BundleLiterals._

class HiLoReg extends Module {
    val io = IO(new Bundle {
        val write = Input(new HiLoWriteNdPort)
        val output= Output(new HiLoReadNdPort)
    })

    val hi = RegInit(0.U(Spec.Width.Reg.data.W))
    val lo = RegInit(0.U(Spec.Width.Reg.data.W))

    when (io.write.en === true.B) {
        hi := io.write.hi
        lo := io.write.lo
    }

    io.output.hi := hi
    io.output.lo := lo
}