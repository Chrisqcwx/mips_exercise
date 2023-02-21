package mips.bridges

import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.{RegWriteNdPort, HiLoReadNdPort, HiLoWriteNdPort}
import mips.bundles.{LLbitWriteNdPort}
import chisel3.experimental.BundleLiterals._

class Mem2wb extends Module {
    val io = IO(new Bundle {
        // reg file
        val in_regWritePort = Input(new RegWriteNdPort)
        val out_regWritePort = Output(new RegWriteNdPort)
        // hilo
        val hiloWrite_mem = Input(new HiLoWriteNdPort)
        val hiloWrite_wb = Output(new HiLoWriteNdPort)

        // stall
        val stallMem = Input(Bool())
        val stallWb  = Input(Bool())
        // llbit
        val llbitMem = Input(new LLbitWriteNdPort)
        val llbitWb = Output(new LLbitWriteNdPort)
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

    val bridgeReg_llbit = RegInit(
        (new LLbitWriteNdPort).Lit(
            _.en -> false.B,
            _.value -> false.B
        )
    )

    when (io.stallMem === true.B && io.stallWb === false.B) {
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
        bridgeReg_llbit := (new LLbitWriteNdPort).Lit(
            _.en -> false.B,
            _.value -> false.B
        )
    }.elsewhen (io.stallMem === false.B) {
        bridgeReg_regWrite := io.in_regWritePort
        bridgeReg_hilo := io.hiloWrite_mem
        bridgeReg_llbit := io.llbitMem
    }


    io.out_regWritePort := bridgeReg_regWrite
    io.hiloWrite_wb := bridgeReg_hilo
    io.llbitWb := bridgeReg_llbit
}