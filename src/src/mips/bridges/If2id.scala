package mips.bridges

import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.IdInstNdPort
import chisel3.experimental.BundleLiterals._

class If2id extends Module {
    val io = IO(new Bundle{
        val input = Input(new IdInstNdPort)
        val output = Output(new IdInstNdPort)

        val stallIf = Input(Bool())
        val stallId = Input(Bool())
    })

    val pcReg = RegInit(Spec.zeroWord)
    val instReg = RegInit(Spec.zeroWord)

    when (
        io.stallIf === true.B &&
        io.stallId === false.B
    ) {
        pcReg := Spec.zeroWord
        instReg := Spec.zeroWord
    }.elsewhen(io.stallIf === false.B) {
        pcReg := io.input.pc
        instReg := io.input.inst
    }


    io.output.pc := pcReg
    io.output.inst := instReg
    
}