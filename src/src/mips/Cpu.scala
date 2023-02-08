import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles._
import chisel3.experimental.BundleLiterals._

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
    val mem = Module(new Mem0)
    val mem2wb = Module(new Mem2wb)

    // pc

    io.romReadPort.en := pc.io.ce
    io.romReadPort.addr := pc.io.pc

    // if2id    

    if2id.io.input.pc := pc.io.pc
    if2id.io.input.inst := io.romReadPort.data

    // id

    id.io.idInstPort := if2id.io.output
    id.io.read_1.data := regfile.io.read_1.data
    id.io.read_2.data := regfile.io.read_2.data
    id.io.write_ex := ex.io.regWritePort
    id.io.write_mem := mem.io.out_regWritePort


    // regfile
    
    regfile.io.read_1.en := id.io.read_1.en
    regfile.io.read_2.en := id.io.read_2.en
    regfile.io.read_1.addr := id.io.read_1.addr
    regfile.io.read_2.addr := id.io.read_2.addr
    regfile.io.write := mem2wb.io.out_regWritePort
    

    // id2ex

    id2ex.io.input := id.io.decode

    
    // ex

    ex.io.idDecodePort := id2ex.io.output

    
    // ex2mem

    ex2mem.io.in_regWritePort := ex.io.regWritePort

    
    // mem

    mem.io.in_regWritePort := ex2mem.io.out_regWritePort

    
    // mem2wb

    mem2wb.io.in_regWritePort := mem.io.out_regWritePort

    
    
    // test
    // io.tmp := regfile.io.write.en
    
    // debug
    io.cpuDebugPort.regFileRegs := regfile.io.regFileRegs
    io.cpuDebugPort.id_reg_data1 := id.io.decode.reg_1
    io.cpuDebugPort.id_reg_data2 := id.io.decode.reg_2
    io.cpuDebugPort.rf_write := mem2wb.io.out_regWritePort
}