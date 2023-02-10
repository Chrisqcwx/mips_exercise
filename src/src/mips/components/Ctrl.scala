package mips.components

import chisel3._
import chisel3.util._
import mips.Spec

class Ctrl extends Module {
    val io = IO(new Bundle {
        val stallreqId  = Input(Bool())
        val stallreqEx  = Input(Bool())
        val stallPc     = Output(Bool())
        val stallIf     = Output(Bool())
        val stallId     = Output(Bool())
        val stallEx     = Output(Bool())
        val stallMem    = Output(Bool())
        val stallWb     = Output(Bool())
    })

    val stall = Wire(UInt(6.W))
    when (io.stallreqEx === true.B) {
        stall := "b001111".U(6.W)
    }.elsewhen (io.stallreqId === true.B) {
        stall := "b000111".U(6.W)
    }.otherwise {
        stall := "b000000".U(6.W)
    }


    io.stallPc := stall(0)
    io.stallIf := stall(1)
    io.stallId := stall(2)
    io.stallEx := stall(3)
    io.stallMem:= stall(4)
    io.stallWb := stall(5)

}
