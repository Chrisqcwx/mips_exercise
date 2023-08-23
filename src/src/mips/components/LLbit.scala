package mips.components

import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.LLbitWriteNdPort
import chisel3.experimental.FlatIO

class LLbit extends Module {
    val io = FlatIO(new Bundle {
        // 异常
        val flush = Input(Bool())
        val write = Input(new LLbitWriteNdPort)
        val outLLbit = Output(Bool())
    })

    val llbit = RegInit(false.B)

    when (io.flush === true.B) {
        llbit := false.B
    }.elsewhen (io.write.en === true.B) {
        llbit := io.write.value
    }

    io.outLLbit := llbit
}