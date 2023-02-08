import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.{RegWriteNdPort, HiLoReadNdPort, HiLoWriteNdPort}
import chisel3.experimental.BundleLiterals._

class Ex2mem extends Module {
    val io = IO(new Bundle {
        // reg write
        val in_regWritePort = Input(new RegWriteNdPort)
        val out_regWritePort = Output(new RegWriteNdPort)
        // hilo
        val hiloWrite_ex = Input(new HiLoWriteNdPort)
        val hiloWrite_mem = Output(new HiLoWriteNdPort)
    })

    val bridgeReg_regWrite = RegNext(
        io.in_regWritePort,
        (new RegWriteNdPort).Lit(
            _.en -> false.B,
            _.addr -> Spec.Addr.nop,
            _.data -> Spec.zeroWord
        )
    )

    io.out_regWritePort := bridgeReg_regWrite

    val bridgeReg_hilo = RegNext(
        io.hiloWrite_ex,
        (new HiLoWriteNdPort).Lit(
            _.en -> false.B,
            _.hi -> Spec.zeroWord,
            _.lo -> Spec.zeroWord
        )
    )

    io.hiloWrite_mem := bridgeReg_hilo
}