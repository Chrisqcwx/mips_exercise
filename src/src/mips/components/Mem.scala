package mips.components

import chisel3._
// import chisel3.util._
import mips.Spec
import mips.bundles.{RegWriteNdPort, HiLoReadNdPort, HiLoWriteNdPort}
// import chisel3.experimental.BundleLiterals._

class Mem extends Module {
    val io = IO(new Bundle {
        // regfile
        val in_regWritePort = Input(new RegWriteNdPort)
        val out_regWritePort = Output(new RegWriteNdPort)

        // hilo
        val hiloWrite_ex = Input(new HiLoWriteNdPort)
        val hiloWrite = Output(new HiLoWriteNdPort)
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

    def hilo_en_ex = io.hiloWrite_ex.en
    def hi_ex = io.hiloWrite_ex.hi
    def lo_ex = io.hiloWrite_ex.lo

    def hilo_en = io.hiloWrite.en
    def hi = io.hiloWrite.hi
    def lo = io.hiloWrite.lo

    hilo_en := hilo_en_ex
    hi := hi_ex
    lo := lo_ex
}