import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.RegWriteNdPort
import chisel3.experimental.BundleLiterals._

class Mem0 extends Module {
    val io = IO(new Bundle {
        val in_regWritePort = Input(new RegWriteNdPort)
        val out_regWritePort = Output(new RegWriteNdPort)
    })

    def in_en = io.in_regWritePort.en
    def in_addr = io.in_regWritePort.addr
    def in_data = io.in_regWritePort.data

    def out_en = io.out_regWritePort.en
    def out_addr = io.out_regWritePort.addr
    def out_data = io.out_regWritePort.data

    out_en := in_en
    out_addr := in_addr
    out_data := in_data
}