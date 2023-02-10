package mips.bridges

import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.{IdDecodeNdPort, BranchValidNdPort}
import chisel3.experimental.BundleLiterals._

class Id2ex extends Module {
    val io = IO(new Bundle {
        val input = Input(new IdDecodeNdPort)
        val output = Output( new IdDecodeNdPort)
        // stall
        val stallId = Input(Bool())
        val stallEx = Input(Bool())
        // branch
        val inBranchValid = Input(new BranchValidNdPort)
        val outBranchValid = Output(new BranchValidNdPort)
        val nextDelay = Input(Bool())
        val nowDelay = Output(Bool())
    })

    val bridgeRegIdDecode = RegInit(
        (new IdDecodeNdPort).Lit(
            _.aluop -> Spec.Op.AluOp.nop,
            _.alusel -> Spec.Op.AluSel.nop,
            _.reg_1 -> Spec.zeroWord,
            _.reg_2 -> Spec.zeroWord,
            _.en_write -> false.B,
            _.addr_write -> Spec.Addr.nop
        )
    )

    val bridegRegBranchValid = RegInit(
        (new BranchValidNdPort).Lit(
            _.inDelaySlot -> false.B,
            _.addr -> Spec.Addr.nop
        )
    )

    val bridgeRegDelay = RegInit(false.B)

    when(io.stallId === true.B && io.stallEx === false.B) {
        bridgeRegIdDecode := (new IdDecodeNdPort).Lit(
            _.aluop -> Spec.Op.AluOp.nop,
            _.alusel -> Spec.Op.AluSel.nop,
            _.reg_1 -> Spec.zeroWord,
            _.reg_2 -> Spec.zeroWord,
            _.en_write -> false.B,
            _.addr_write -> Spec.Addr.nop
        )
        bridegRegBranchValid := (new BranchValidNdPort).Lit(
            _.inDelaySlot -> false.B,
            _.addr -> Spec.Addr.nop
        )
    }.elsewhen(io.stallId === false.B) {
        bridgeRegIdDecode := io.input
        bridegRegBranchValid := io.inBranchValid
        bridgeRegDelay := io.nextDelay
    }

    io.output := bridgeRegIdDecode
    io.nowDelay := bridgeRegDelay
    io.outBranchValid := bridegRegBranchValid
}