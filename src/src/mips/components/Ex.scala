package mips.components

import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.{IdDecodeNdPort, RegWriteNdPort, HiLoReadNdPort, HiLoWriteNdPort}
import mips.bundles.{BranchValidNdPort}
import mips.bundles.{MemLSNdPort}

class Ex extends Module {
    val io = IO(new Bundle {
        // from id
        val idDecodePort = Input(new IdDecodeNdPort)
        val inst = Input(UInt(Spec.Width.Rom.data.W))

        // from hilo
        val hiloRead = Input(new HiLoReadNdPort)
        // from mem
        val hiloWrite_mem = Input(new HiLoWriteNdPort)
        // from wb
        val hiloWrite_wb = Input(new HiLoWriteNdPort)

        val regWritePort = Output(new RegWriteNdPort)
        
        val hiloWrite = Output(new HiLoWriteNdPort)
        // stall requirement
        val stallReq = Output(Bool())
        // branch
        val branchValid = Input(new BranchValidNdPort)
        // mem load / save
        val memLS = Output(new MemLSNdPort)
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

    val arithmetic = Wire(UInt(Spec.Width.Reg.data.W))
    arithmetic := Spec.zeroWord
    

    val mul_res = Wire(UInt(Spec.Width.Reg.doubleData.W))
    mul_res := 0.U(Spec.Width.Reg.doubleData.W)
    


    // hilo
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

    
    // *******************************************
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

    // arithmetic

    val reg_2_data_mux = Wire(UInt(Spec.Width.Reg.data.W))
    when(
        aluop === Spec.Op.AluOp.sub ||
        aluop === Spec.Op.AluOp.subu ||
        aluop === Spec.Op.AluOp.slt
    ) {
        reg_2_data_mux := (~reg_2_data) + 1.U
    }.otherwise {
        reg_2_data_mux := reg_2_data
    }

    val result_sum = Wire(UInt(Spec.Width.Reg.data.W))
    result_sum := reg_1_data + reg_2_data_mux

    // 溢出
    
    val over_sum = Wire(Bool())
    when ((
        reg_1_data(31) === reg_2_data_mux(31)) &&
        reg_1_data(31) =/= result_sum(31)
    ) {
        over_sum := true.B
    }.otherwise {
        over_sum := false.B
    }

    val reg1_lt_reg2 = Wire(UInt(Spec.Width.Reg.data.W))

    // reg1 < reg2
    when (aluop === Spec.Op.AluOp.slt) {
        when(
            reg_1_data.asSInt() < reg_2_data.asSInt()
        ) {
            reg1_lt_reg2 := 1.U(Spec.Width.Reg.data.W)
        }.otherwise {
            reg1_lt_reg2 := 0.U(Spec.Width.Reg.data.W)
        }
    }.otherwise {
        when(
            reg_1_data < reg_2_data
        ) {
            reg1_lt_reg2 := 1.U(Spec.Width.Reg.data.W)
        }.otherwise {
            reg1_lt_reg2 := 0.U(Spec.Width.Reg.data.W)
        }
    }


    //io.tmp_clz := 32.U(32.W)
    // def count_clzo (data: UInt, eq: UInt): Unit = {
    //     val i = 32
    //     var cnt = 32.U(32.W)
    //     def count_clzo_single(idx: Int): Unit = {
    //         when (data(idx) === eq) {
    //             if (idx == 0) {
    //                 io.tmp_clz := 32.U(32.W)
    //             } else {
    //                 count_clz_single(idx-1)
    //             }
    //         }.otherwise {
    //             io.tmp_clz := (31-idx).U(32.W)
    //         }
    //     }
    //     count_clzo_single(31)
    // }
    //count_clzo(io.idDecodePort.reg_1, 0.U) 
    // io.tmp_clz := PriorityMux(
    //     Seq.range(31,-1,-1).map{index =>
    //         (io.idDecodePort.reg_1(index)===1.U(1.W)) -> (31-index).U(32.W)
    //     } ++ Seq(
    //         true.B -> 32.U(32.W)
    //     )
    // )

    // arithmetic

    switch (aluop) {
        is (Spec.Op.AluOp.slt, Spec.Op.AluOp.sltu) {
            arithmetic := reg1_lt_reg2
        }
        is (
            Spec.Op.AluOp.add, Spec.Op.AluOp.addu, 
            Spec.Op.AluOp.addi, Spec.Op.AluOp.addiu,
            Spec.Op.AluOp.sub, Spec.Op.AluOp.subu
        ) {
            arithmetic := result_sum
        }
        is (Spec.Op.AluOp.clz) {
            arithmetic := PriorityMux(
                Seq.range(Spec.Width.Reg.data-1,-1,-1).map{index =>
                    (io.idDecodePort.reg_1(index)===1.U(1.W)) -> (Spec.Width.Reg.data-1-index).U(Spec.Width.Reg.data.W)
                } ++ Seq(
                    true.B -> Spec.Width.Reg.data.U(Spec.Width.Reg.data.W)
                )
            )
        }
        is (Spec.Op.AluOp.clo) {
            arithmetic := PriorityMux(
                Seq.range(Spec.Width.Reg.data-1,-1,-1).map{index =>
                    (io.idDecodePort.reg_1(index)===0.U(1.W)) -> (Spec.Width.Reg.data-1-index).U(Spec.Width.Reg.data.W)
                } ++ Seq(
                    true.B -> Spec.Width.Reg.data.U(Spec.Width.Reg.data.W)
                )
            )
        }
    }

    // mul
    switch (aluop) {
        is (Spec.Op.AluOp.mul, Spec.Op.AluOp.mult) {
            mul_res := (reg_1_data.asSInt() * reg_2_data.asSInt()).asUInt()
        }
        is (Spec.Op.AluOp.multu) {
            mul_res := reg_1_data * reg_2_data
        }
    }
    

    // alusel

    def alusel = io.idDecodePort.alusel
    def en_write = io.regWritePort.en
    def addr_write = io.regWritePort.addr
    def data_write = io.regWritePort.data

    
    when (
        (aluop === Spec.Op.AluOp.add ||
        aluop === Spec.Op.AluOp.addi ||
        aluop === Spec.Op.AluOp.sub) && over_sum
    ) {
        en_write := false.B
    }.otherwise {
        en_write := io.idDecodePort.en_write
    }

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
        is (Spec.Op.AluSel.arithmetic) {
            data_write := arithmetic
        }
        is (Spec.Op.AluSel.mul) {
            data_write := mul_res
        }
        is (Spec.Op.AluSel.jumpBranch) {
            data_write := io.branchValid.addr
        }
    }

    // write hilo

    def hiloWrite_en = io.hiloWrite.en
    hiloWrite_en := false.B

    def hiloWrite_hi = io.hiloWrite.hi
    hiloWrite_hi := Spec.zeroWord

    def hiloWrite_lo = io.hiloWrite.lo
    hiloWrite_lo := Spec.zeroWord

    

    switch (aluop) {
        is(Spec.Op.AluOp.mthi) {
            hiloWrite_en := true.B
            hiloWrite_hi := reg_1_data
            hiloWrite_lo := lo
        }
        is(Spec.Op.AluOp.mtlo) {
            hiloWrite_en := true.B
            hiloWrite_hi := hi
            hiloWrite_lo := reg_1_data
        }    
        is(Spec.Op.AluOp.mult, Spec.Op.AluOp.multu) {
            hiloWrite_en := true.B
            hiloWrite_hi := mul_res(Spec.Width.Reg.doubleData-1, Spec.Width.Reg.data)
            hiloWrite_lo := mul_res(Spec.Width.Reg.data-1, 0)
        }
    }

    // stall
    io.stallReq := false.B

    // mem
    def inst = io.inst

    io.memLS.aluop := aluop
    io.memLS.addr := reg_1_data + Cat(Fill(16,inst(15)), inst(15,0))
    io.memLS.data := reg_2_data

}