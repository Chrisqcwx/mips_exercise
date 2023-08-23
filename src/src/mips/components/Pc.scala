package mips.components

import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.BranchSetNdPort
import chisel3.experimental.FlatIO

class Pc extends Module {
    val io = FlatIO(new Bundle {
        val pc = Output(UInt(Spec.Width.Rom.addr.W))
        val ce = Output(Bool())

        val stall = Input(Bool())
        // branch
        val branchSet = Input(new BranchSetNdPort)
    })

    val ce = RegInit(true.B)
    io.ce := ce

    val pc = RegInit(Spec.zeroWord)

    when (ce === false.B) {
        pc := Spec.zeroWord
    }.elsewhen (io.stall === false.B) {
        when (io.branchSet.en === true.B) {
            pc := io.branchSet.addr
        }.otherwise {
            pc := pc + 4.U
        }
    }


    io.pc := pc

}