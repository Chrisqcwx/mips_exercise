package mips.bridges

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
        // stall
        val stallEx = Input(Bool())
        val stallMem = Input(Bool())
        // mem load sava
        val memLSEx = Input(new MemLSNdPort)
        val memLSMem = Output(new MemLSNdPort)
    })

    val bridgeReg_regWrite = RegInit(
        (new RegWriteNdPort).Lit(
            _.en -> false.B,
            _.addr -> Spec.Addr.nop,
            _.data -> Spec.zeroWord
        )
    )

    val bridgeReg_hilo = RegInit(
        (new HiLoWriteNdPort).Lit(
            _.en -> false.B,
            _.hi -> Spec.zeroWord,
            _.lo -> Spec.zeroWord
        )
    )

    val bridgeReg_memLS = RegInit(
        (new MemLSNdPort).Lit(
            _.aluop -> Spec.Op.AluOp.nop,
            _.hi -> Spec.zeroWord,
            _.lo -> Spec.zeroWord
        )
    )

    when(io.stallEx === true.B && io.stallMem === false.B) {
        bridgeReg_regWrite := (new RegWriteNdPort).Lit(
            _.en -> false.B,
            _.addr -> Spec.Addr.nop,
            _.data -> Spec.zeroWord
        )
        bridgeReg_hilo := (new HiLoWriteNdPort).Lit(
            _.en -> false.B,
            _.hi -> Spec.zeroWord,
            _.lo -> Spec.zeroWord
        )
        bridgeReg_memLS := (new MemLSNdPort).Lit(
            _.aluop -> Spec.Op.AluOp.nop,
            _.hi -> Spec.zeroWord,
            _.lo -> Spec.zeroWord
        )
    }.elsewhen(io.stallEx === false.B) {
        bridgeReg_regWrite := io.in_regWritePort
        bridgeReg_hilo := io.hiloWrite_ex
        bridgeReg_memLS := io.memLSEx
    }

    io.out_regWritePort := bridgeReg_regWrite
    io.hiloWrite_mem := bridgeReg_hilo
    io.memLSMem := bridgeReg_memLS
}