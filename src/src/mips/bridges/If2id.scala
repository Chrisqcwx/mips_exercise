import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.IdInstNdPort
import chisel3.experimental.BundleLiterals._

class If2id extends Module {
    val io = IO(new Bundle{
        val input = Input(new IdInstNdPort)
        val output = Output(new IdInstNdPort)
    })

    val bridgeReg = RegNext(
        io.input,
        (new IdInstNdPort).Lit(
            _.pc -> Spec.zeroWord,
            _.inst -> Spec.zeroWord
        )
    )

    io.output := bridgeReg
}