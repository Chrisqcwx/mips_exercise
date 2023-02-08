import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.RegWriteNdPort
import chisel3.experimental.BundleLiterals._

class Ex2mem extends Module {
    val io = IO(new Bundle {
        val in_regWritePort = Input(new RegWriteNdPort)
        val out_regWritePort = Output(new RegWriteNdPort)
    })

    val bridgeReg = RegNext(
        io.in_regWritePort,
        (new RegWriteNdPort).Lit(
            _.en -> false.B,
            _.addr -> Spec.Addr.nop,
            _.data -> Spec.zeroWord
        )
    )

    io.out_regWritePort := bridgeReg
}