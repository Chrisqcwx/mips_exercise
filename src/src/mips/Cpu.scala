package mips

import chisel3._
// import chisel3.util._
import mips.Spec
import mips.bundles._
import mips.components._
import mips.bridges._
// import mips._

// import chisel3.experimental.BundleLiterals._

class Cpu extends Module {
    val io = IO(new Bundle {
        val romReadPort = Flipped(new RomReadPort)

        // debug
        val cpuDebugPort = Output(new CpuDebugPort)
    })

    // io.tmp := false.B

    

    val pc = Module(new Pc)
    val if2id = Module(new If2id)
    val id = Module(new Id)
    val regfile = Module(new Regfile)
    val id2ex = Module(new Id2ex)
    val ex = Module(new Ex)
    val ex2mem = Module(new Ex2mem)
    val mem = Module(new components.Mem)
    val mem2wb = Module(new Mem2wb)
    val hilo = Module(new HiLoReg)
    val ctrl = Module(new Ctrl)

    // output

    io.romReadPort.en := pc.io.ce
    io.romReadPort.addr := pc.io.pc

    // pc

    pc.io.stall := ctrl.io.stallPc
    pc.io.branchSet := id.io.branchSet

    // if2id    

    if2id.io.input.pc := pc.io.pc
    if2id.io.input.inst := io.romReadPort.data
    if2id.io.stallIf := ctrl.io.stallIf
    if2id.io.stallId := ctrl.io.stallId

    // id

    id.io.idInstPort := if2id.io.output
    id.io.read_1.data := regfile.io.read_1.data
    id.io.read_2.data := regfile.io.read_2.data
    id.io.write_ex := ex.io.regWritePort
    id.io.write_mem := mem.io.out_regWritePort
    id.io.nowDelay := id2ex.io.nowDelay


    // regfile
    
    regfile.io.read_1.en := id.io.read_1.en
    regfile.io.read_2.en := id.io.read_2.en
    regfile.io.read_1.addr := id.io.read_1.addr
    regfile.io.read_2.addr := id.io.read_2.addr
    regfile.io.write := mem2wb.io.out_regWritePort
    

    // id2ex

    id2ex.io.input := id.io.decode
    id2ex.io.stallId := ctrl.io.stallId
    id2ex.io.stallEx := ctrl.io.stallEx
    id2ex.io.nextDelay := id.io.nextDelay
    id2ex.io.inBranchValid := id.io.branchValid

    
    // ex

    ex.io.idDecodePort := id2ex.io.output
    ex.io.hiloRead := hilo.io.output
    ex.io.hiloWrite_mem := mem.io.hiloWrite
    ex.io.hiloWrite_wb := mem2wb.io.hiloWrite_wb
    ex.io.branchValid := id2ex.io.outBranchValid

    
    // ex2mem

    ex2mem.io.in_regWritePort := ex.io.regWritePort
    ex2mem.io.hiloWrite_ex := ex.io.hiloWrite
    ex2mem.io.stallEx := ctrl.io.stallEx
    ex2mem.io.stallMem := ctrl.io.stallMem

    
    // mem

    mem.io.in_regWritePort := ex2mem.io.out_regWritePort
    mem.io.hiloWrite_ex := ex2mem.io.hiloWrite_mem
    
    // mem2wb

    mem2wb.io.in_regWritePort := mem.io.out_regWritePort
    mem2wb.io.hiloWrite_mem := mem.io.hiloWrite
    mem2wb.io.stallMem := ctrl.io.stallMem
    mem2wb.io.stallWb := ctrl.io.stallWb

    // hilo
    hilo.io.write := mem2wb.io.hiloWrite_wb
    
    // ctrl

    ctrl.io.stallreqId := id.io.stallReq
    ctrl.io.stallreqEx := ex.io.stallReq
    
    // test
    // io.tmp := regfile.io.write.en
    
    // debug
    io.cpuDebugPort.regFileRegs := regfile.io.regFileRegs
    io.cpuDebugPort.id_reg_data1 := id.io.decode.reg_1
    io.cpuDebugPort.id_reg_data2 := id.io.decode.reg_2
    io.cpuDebugPort.rf_write := mem2wb.io.out_regWritePort
}