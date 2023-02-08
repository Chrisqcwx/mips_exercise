import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.{RegWriteNdPort, HiLoReadNdPort, HiLoWriteNdPort}
import chisel3.experimental.BundleLiterals._

class Mem2wb extends Module {
    val io = IO(new Bundle {
        // reg file
        val in_regWritePort = Input(new RegWriteNdPort)
        val out_regWritePort = Output(new RegWriteNdPort)
        // hilo
        val hiloWrite_mem = Input(new HiLoWriteNdPort)
        val hiloWrite_wb = Output(new HiLoWriteNdPort)
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

    val bridgeReg_hilo = RegNext(
        io.hiloWrite_mem,
        (new HiLoWriteNdPort).Lit(
            _.en -> false.B,
            _.hi -> Spec.zeroWord,
            _.lo -> Spec.zeroWord
        )
    )

    io.hiloWrite_wb := bridgeReg_hilo
}