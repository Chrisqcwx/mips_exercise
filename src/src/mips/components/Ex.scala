import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.{IdDecodeNdPort, RegWriteNdPort, HiLoReadNdPort, HiLoWriteNdPort}
import chisel3.experimental.BundleLiterals._

class Ex extends Module {
    val io = IO(new Bundle {
        // from id
        val idDecodePort = Input(new IdDecodeNdPort)
        // from hilo
        val hiloRead = Input(new HiLoReadNdPort)
        // from mem
        val hiloWrite_mem = Input(new HiLoWriteNdPort)
        // from wb
        val hiloWrite_wb = Input(new HiLoWriteNdPort)

        val regWritePort = Output(new RegWriteNdPort)
        
        val hiloWrite = Output(new HiLoWriteNdPort)
    })

    // result

    val logic_out = Wire(UInt(Spec.Width.Reg.data.W))
    logic_out := Spec.zeroWord

    val shift_res = Wire(UInt(Spec.Width.Reg.data.W))
    shift_res := Spec.zeroWord

    val move_res = Wire(UInt(Spec.Width.Reg.data.W))
    move_res := Spec.zeroWord

    val hi = Wire(UInt(Spec.Width.Reg.data.W))
    hi := Spec.zeroWord

    val lo = Wire(UInt(Spec.Width.Reg.data.W))
    lo := Spec.zeroWord



    when (io.hiloWrite_mem.en === true.B) {
        hi := io.hiloWrite_mem.hi
        lo := io.hiloWrite_mem.lo
    }.elsewhen (io.hiloWrite_wb.en === true.B) {
        hi := io.hiloWrite_wb.hi
        lo := io.hiloWrite_wb.lo
    }.otherwise{
        hi := io.hiloRead.hi
        lo := io.hiloRead.lo
    }



    // aluop

    def aluop = io.idDecodePort.aluop
    def reg_1_data = io.idDecodePort.reg_1
    def reg_2_data = io.idDecodePort.reg_2

    // logic

    switch(aluop) {
        is (Spec.Op.AluOp.or) {
            logic_out := (reg_1_data | reg_2_data)
        }
        is (Spec.Op.AluOp.and) {
            logic_out := (reg_1_data & reg_2_data)
        }
        is (Spec.Op.AluOp.nor) {
            logic_out := ~ (reg_1_data | reg_2_data)
        }
        is (Spec.Op.AluOp.xor) {
            logic_out := (reg_1_data ^ reg_2_data)
        }
    }

    // shift

    switch(aluop) {
        is (Spec.Op.AluOp.sll) {
            shift_res := reg_2_data << reg_1_data(4,0)
        }
        is (Spec.Op.AluOp.srl) {
            shift_res := reg_2_data >> reg_1_data(4,0)
        }
        is (Spec.Op.AluOp.sra) {
            shift_res := (reg_2_data.asSInt() >> reg_1_data(4,0)).asUInt()
        }
    }

    // move

    switch(aluop) {
        is (Spec.Op.AluOp.mfhi) {
            move_res := hi
        }
        is (Spec.Op.AluOp.mflo) {
            move_res := lo
        }
        is (Spec.Op.AluOp.movz) {
            move_res := reg_1_data
        }
        is (Spec.Op.AluOp.movn) {
            move_res := reg_1_data
        }

    }


    // alusel

    def alusel = io.idDecodePort.alusel
    def en_write = io.regWritePort.en
    def addr_write = io.regWritePort.addr
    def data_write = io.regWritePort.data

    en_write := io.idDecodePort.en_write
    addr_write := io.idDecodePort.addr_write
    data_write := Spec.zeroWord

    switch (alusel) {
        is (Spec.Op.AluSel.logic) {
            data_write := logic_out
        }
        is (Spec.Op.AluSel.shift) {
            data_write := shift_res
        }
        is (Spec.Op.AluSel.move) {
            data_write := move_res
        }
    }

    // write hilo

    def hiloWrite_en = io.hiloWrite.en
    hiloWrite_en := false.B

    def hiloWrite_hi = io.hiloWrite.hi
    hiloWrite_hi := Spec.zeroWord

    def hiloWrite_lo = io.hiloWrite.lo
    hiloWrite_lo := Spec.zeroWord

    when(aluop === Spec.Op.AluOp.mthi) {
        hiloWrite_en := true.B
        hiloWrite_hi := reg_1_data
        hiloWrite_lo := lo
    }.elsewhen(aluop === Spec.Op.AluOp.mtlo) {
        hiloWrite_en := true.B
        hiloWrite_hi := hi
        hiloWrite_lo := reg_1_data
    }
}