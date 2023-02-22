package mips.components

import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.{RfReadPort, IdInstNdPort, IdDecodeNdPort, RegWriteNdPort}
import mips.bundles.{BranchSetNdPort, BranchValidNdPort}
import chisel3.experimental.BundleLiterals._

class Id extends Module {
    val io = IO(new Bundle {
        val idInstPort = Input(new IdInstNdPort)
        val inst = Output(UInt(Spec.Width.Rom.data.W))
        // to regfile
        val read_1 = Flipped(new RfReadPort)
        val read_2 = Flipped(new RfReadPort)
        // ex result
        val write_ex = Input(new RegWriteNdPort)
        // mem result
        val write_mem = Input(new RegWriteNdPort)
        // to ex
        val decode = Output(new IdDecodeNdPort)
        // stall require
        val stallReq = Output(Bool())
        val aluopEx = Input(UInt(Spec.Width.Alu.op.W))
        // branch
        val branchSet = Output(new BranchSetNdPort)
        val branchValid = Output(new BranchValidNdPort)
        val nowDelay = Input(Bool())
        val nextDelay = Output(Bool())
    })

    def op  = io.idInstPort.inst(31,26) // 指令码
    def op2 = io.idInstPort.inst(10,6)
    def op3 = io.idInstPort.inst(5,0)   // 功能码
    def op4 = io.idInstPort.inst(20,16)


    // val imm = Wire(SInt(Spec.Width.Reg.data.W))
    
    val imm = WireInit(Spec.zeroWord.asSInt)
    val imm_final = Wire(UInt(Spec.Width.Reg.data.W))
    // imm := Spec.zeroWord.asSInt

    val instValid = WireInit(false.B)

    // to regfile
    def reg_1_data = io.read_1.data
    def reg_2_data = io.read_2.data
    val reg_1_en = WireInit(false.B) //RegInit(false.B)
    val reg_1_addr = WireInit(io.idInstPort.inst(25,21)) //RegInit(Spec.Addr.nop)
    val reg_2_en = WireInit(false.B)
    val reg_2_addr = WireInit(io.idInstPort.inst(20,16))

    // reg_1_en := false.B
    // reg_2_en := false.B
    // reg_1_addr := io.idInstPort.inst(25,21)
    // reg_2_addr := io.idInstPort.inst(20,16)

    io.read_1.en := reg_1_en
    io.read_1.addr := reg_1_addr
    io.read_2.en := reg_2_en
    io.read_2.addr := reg_2_addr

    // to ex
    // val aluop = Wire(UInt(Spec.Width.Alu.op.W))   
    val aluop = WireInit(Spec.Op.AluOp.nop)
    val alusel = WireInit(Spec.Op.AluSel.nop)   
    val reg_1_o = WireInit(Spec.zeroWord)   
    val reg_2_o = WireInit(Spec.zeroWord)   
    val en_write = WireInit(false.B) 
    val addr_write = WireInit(io.idInstPort.inst(15,11))   

    // aluop := Spec.Op.AluOp.nop
    // alusel := Spec.Op.AluSel.nop
    // reg_1_o := Spec.zeroWord
    // reg_2_o := Spec.zeroWord
    // en_write := false.B
    // addr_write := io.idInstPort.inst(15,11)

    io.decode.aluop := aluop
    io.decode.alusel := alusel
    io.decode.reg1 := reg_1_o
    io.decode.reg2 := reg_2_o
    io.decode.enWrite := en_write
    io.decode.addrWrite := addr_write

    // branch

    io.branchValid.inDelaySlot := io.nowDelay

    def nextDelay = io.nextDelay
    def branchSetEn = io.branchSet.en
    def branchSetAddr = io.branchSet.addr
    def branchValidAddr = io.branchValid.addr

    nextDelay := false.B
    branchSetEn := false.B
    branchSetAddr := Spec.Addr.nop
    branchValidAddr := Spec.Addr.nop

    val pcAdd4 = WireInit(io.idInstPort.pc + 4.U)
    val pcAdd8 = WireInit(io.idInstPort.pc + 8.U)
    // pcAdd4 := io.idInstPort.pc + 4.U
    // pcAdd8 := io.idInstPort.pc + 8.U
    
    //val immSll2Signedext = WireInit(SInt(Spec.Width.Reg.data.W))
    val immSll2Signedext = WireInit(
        Cat(
            io.idInstPort.inst(15,0),
            0.U(2.W)
        ).asSInt
    )

    // decode
    
    switch (op) {
        is (Spec.Op.Inst.special_inst){
            switch (op2) {
                is (0.U(Spec.Width.inst2.W))
                {
                    switch (op3) {
                        
                        is (Spec.Op.Inst.or){
                            en_write := true.B
                            aluop := Spec.Op.AluOp.or
                            alusel := Spec.Op.AluSel.logic
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.and){
                            en_write := true.B
                            aluop := Spec.Op.AluOp.and
                            alusel := Spec.Op.AluSel.logic
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.xor){
                            en_write := true.B
                            aluop := Spec.Op.AluOp.xor
                            alusel := Spec.Op.AluSel.logic
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.nor){
                            en_write := true.B
                            aluop := Spec.Op.AluOp.nor
                            alusel := Spec.Op.AluSel.logic
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.sllv){
                            en_write := true.B
                            aluop := Spec.Op.AluOp.sll
                            alusel := Spec.Op.AluSel.shift
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.srlv){
                            en_write := true.B
                            aluop := Spec.Op.AluOp.srl
                            alusel := Spec.Op.AluSel.shift
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.srav){
                            en_write := true.B
                            aluop := Spec.Op.AluOp.sra
                            alusel := Spec.Op.AluSel.shift
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.sync){
                            en_write := false.B
                            aluop := Spec.Op.AluOp.nop
                            alusel := Spec.Op.AluSel.nop
                            instValid := true.B
                            reg_1_en := false.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.mfhi){
                            en_write := true.B
                            aluop := Spec.Op.AluOp.mfhi
                            alusel := Spec.Op.AluSel.move
                            instValid := true.B
                            reg_1_en := false.B
                            reg_2_en := false.B
                        }
                        is (Spec.Op.Inst.mflo){
                            en_write := true.B
                            aluop := Spec.Op.AluOp.mflo
                            alusel := Spec.Op.AluSel.move
                            instValid := true.B
                            reg_1_en := false.B
                            reg_2_en := false.B
                        }
                        is (Spec.Op.Inst.mthi){
                            en_write := false.B
                            aluop := Spec.Op.AluOp.mthi
                            
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := false.B
                        }
                        is (Spec.Op.Inst.mtlo){
                            en_write := false.B
                            aluop := Spec.Op.AluOp.mtlo
                            
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := false.B
                        }
                        is (Spec.Op.Inst.movn){
                            // en_write := true.B
                            when (io.decode.reg2 === 0.U(Spec.Width.Reg.data.W)) {
                                en_write := false.B
                            }.otherwise{
                                en_write := true.B
                            }
                            aluop := Spec.Op.AluOp.movn
                            alusel := Spec.Op.AluSel.move
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.movz){
                            // en_write := true.B
                            when (io.decode.reg2 === 0.U(Spec.Width.Reg.data.W)) {
                                en_write := true.B
                            }.otherwise{
                                en_write := false.B
                            }
                            aluop := Spec.Op.AluOp.movn
                            alusel := Spec.Op.AluSel.move
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.slt){
                            en_write := true.B
                            aluop := Spec.Op.AluOp.slt
                            alusel := Spec.Op.AluSel.arithmetic
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.sltu){
                            en_write := true.B
                            aluop := Spec.Op.AluOp.sltu
                            alusel := Spec.Op.AluSel.arithmetic
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.add){
                            en_write := true.B
                            aluop := Spec.Op.AluOp.add
                            alusel := Spec.Op.AluSel.arithmetic
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.addu){
                            en_write := true.B
                            aluop := Spec.Op.AluOp.addu
                            alusel := Spec.Op.AluSel.arithmetic
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.sub){
                            en_write := true.B
                            aluop := Spec.Op.AluOp.sub
                            alusel := Spec.Op.AluSel.arithmetic
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.subu){
                            en_write := true.B
                            aluop := Spec.Op.AluOp.subu
                            alusel := Spec.Op.AluSel.arithmetic
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.mult){
                            en_write := false.B
                            aluop := Spec.Op.AluOp.mult
            
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.multu){
                            en_write := false.B
                            aluop := Spec.Op.AluOp.multu
            
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := true.B
                        }
                        is (Spec.Op.Inst.jr) {
                            en_write := false.B
                            aluop := Spec.Op.AluOp.jr
                            alusel := Spec.Op.AluSel.jumpBranch
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := false.B

                            branchValidAddr := Spec.Addr.nop
                            branchSetEn := true.B
                            branchSetAddr := reg_1_o
                            nextDelay := true.B
                        }
                        is (Spec.Op.Inst.jalr) {
                            en_write := true.B
                            aluop := Spec.Op.AluOp.jalr
                            alusel := Spec.Op.AluSel.jumpBranch
                            instValid := true.B
                            reg_1_en := true.B
                            reg_2_en := false.B
                            addr_write := io.idInstPort.inst(15,11)

                            branchValidAddr := pcAdd8
                            branchSetEn := true.B
                            branchSetAddr := reg_1_o
                            nextDelay := true.B
                        }
                    }

                }
            }
        }
        is (Spec.Op.Inst.ori) {
            en_write := true.B
            aluop := Spec.Op.AluOp.or
            alusel := Spec.Op.AluSel.logic
            reg_1_en := true.B
            reg_2_en := false.B
            imm := io.idInstPort.inst(15,0).asSInt
            // imm := Cat(0.U(16.W), io.idInstPort.inst(15,0))
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.andi) {
            en_write := true.B
            aluop := Spec.Op.AluOp.and
            alusel := Spec.Op.AluSel.logic
            reg_1_en := true.B
            reg_2_en := false.B
            // imm := Cat(0.U(16.W), io.idInstPort.inst(15,0))
            imm := io.idInstPort.inst(15,0).asSInt
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.xori) {
            en_write := true.B
            aluop := Spec.Op.AluOp.xor
            alusel := Spec.Op.AluSel.logic
            reg_1_en := true.B
            reg_2_en := false.B
            imm := io.idInstPort.inst(15,0).asSInt
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.lui) {
            en_write := true.B
            aluop := Spec.Op.AluOp.or
            alusel := Spec.Op.AluSel.logic
            reg_1_en := true.B
            reg_2_en := false.B
            imm := (io.idInstPort.inst(15,0) << 16).asSInt
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.pref) {
            en_write := false.B
            aluop := Spec.Op.AluOp.nop
            alusel := Spec.Op.AluSel.nop
            reg_1_en := false.B
            reg_2_en := false.B
            instValid := true.B
        }
        is (Spec.Op.Inst.slti) {
            en_write := true.B
            aluop := Spec.Op.AluOp.slt
            alusel := Spec.Op.AluSel.arithmetic
            reg_1_en := true.B
            reg_2_en := false.B
            imm := io.idInstPort.inst(15,0).asSInt
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.sltiu) {
            en_write := true.B
            aluop := Spec.Op.AluOp.sltu
            alusel := Spec.Op.AluSel.arithmetic
            reg_1_en := true.B
            reg_2_en := false.B
            imm := io.idInstPort.inst(15,0).asSInt
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.addi) {
            en_write := true.B
            aluop := Spec.Op.AluOp.addi
            alusel := Spec.Op.AluSel.arithmetic
            reg_1_en := true.B
            reg_2_en := false.B
            imm := io.idInstPort.inst(15,0).asSInt
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.addiu) {
            en_write := true.B
            aluop := Spec.Op.AluOp.addiu
            alusel := Spec.Op.AluSel.arithmetic
            reg_1_en := true.B
            reg_2_en := false.B
            imm := io.idInstPort.inst(15,0).asSInt
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.special2_inst) {
            switch (op3) {
                is (Spec.Op.Inst.clz) {
                    en_write := true.B
                    aluop := Spec.Op.AluOp.clz
                    alusel := Spec.Op.AluSel.arithmetic
                    instValid := true.B
                    reg_1_en := true.B
                    reg_2_en := false.B
                }
                is (Spec.Op.Inst.clo) {
                    en_write := true.B
                    aluop := Spec.Op.AluOp.clo
                    alusel := Spec.Op.AluSel.arithmetic
                    instValid := true.B
                    reg_1_en := true.B
                    reg_2_en := false.B
                }
                is (Spec.Op.Inst.mul) {
                    en_write := true.B
                    aluop := Spec.Op.AluOp.mul
                    alusel := Spec.Op.AluSel.mul
                    instValid := true.B
                    reg_1_en := true.B
                    reg_2_en := true.B
                }
            }
        }
        is (Spec.Op.Inst.j) {
            en_write := false.B
            aluop := Spec.Op.AluOp.j
            alusel := Spec.Op.AluSel.jumpBranch
            instValid := true.B
            reg_1_en := false.B
            reg_2_en := false.B

            branchValidAddr := Spec.Addr.nop
            branchSetEn := true.B
            branchSetAddr := Cat(
                pcAdd4(31,28),
                io.idInstPort.inst(25,0),
                0.U(2.W)
            )
            nextDelay := true.B
        }
        is (Spec.Op.Inst.jal) {
            en_write := true.B
            aluop := Spec.Op.AluOp.jal
            alusel := Spec.Op.AluSel.jumpBranch
            instValid := true.B
            reg_1_en := false.B
            reg_2_en := false.B
            addr_write := 31.U

            branchValidAddr := pcAdd8
            branchSetEn := true.B
            branchSetAddr := Cat(
                pcAdd4(31,28),
                io.idInstPort.inst(25,0),
                0.U(2.W)
            )
            nextDelay := true.B
        }
        is (Spec.Op.Inst.beq) {
            en_write := false.B
            aluop := Spec.Op.AluOp.beq
            alusel := Spec.Op.AluSel.jumpBranch
            instValid := true.B
            reg_1_en := true.B
            reg_2_en := true.B

            when (reg_1_o === reg_2_o) {
                branchSetEn := true.B
                branchSetAddr := pcAdd4 + immSll2Signedext.asUInt
                nextDelay := true.B
            }
        }
        is (Spec.Op.Inst.bgtz) {
            en_write := false.B
            aluop := Spec.Op.AluOp.bgtz
            alusel := Spec.Op.AluSel.jumpBranch
            instValid := true.B
            reg_1_en := true.B
            reg_2_en := false.B

            when (
                reg_1_o(31) === 0.U(1.W) &&
                reg_1_o =/= Spec.zeroWord
            ) {
                branchSetEn := true.B
                branchSetAddr := pcAdd4 + immSll2Signedext.asUInt
                nextDelay := true.B
            }
        }
        is (Spec.Op.Inst.blez) {
            en_write := false.B
            aluop := Spec.Op.AluOp.blez
            alusel := Spec.Op.AluSel.jumpBranch
            instValid := true.B
            reg_1_en := true.B
            reg_2_en := false.B

            when (
                reg_1_o(31) === 1.U(1.W) ||
                reg_1_o === Spec.zeroWord
            ) {
                branchSetEn := true.B
                branchSetAddr := pcAdd4 + immSll2Signedext.asUInt
                nextDelay := true.B
            }
        }
        is (Spec.Op.Inst.bne) {
            en_write := false.B
            aluop := Spec.Op.AluOp.bne
            alusel := Spec.Op.AluSel.jumpBranch
            instValid := true.B
            reg_1_en := true.B
            reg_2_en := true.B

            when (reg_1_o =/= reg_2_o) {
                branchSetEn := true.B
                branchSetAddr := pcAdd4 + immSll2Signedext.asUInt
                nextDelay := true.B
            }
        }
        is (Spec.Op.Inst.regimm_inst) {
            switch (op4) {
                is (Spec.Op.Inst.bgez) {
                    en_write := false.B
                    aluop := Spec.Op.AluOp.bgez
                    alusel := Spec.Op.AluSel.jumpBranch
                    instValid := true.B
                    reg_1_en := true.B
                    reg_2_en := false.B

                    when (reg_1_o(31) === 0.U(1.W)) {
                        branchSetEn := true.B
                        branchSetAddr := pcAdd4 + immSll2Signedext.asUInt
                        nextDelay := true.B
                    }
                }
                is (Spec.Op.Inst.bgezal) {
                    en_write := true.B
                    aluop := Spec.Op.AluOp.bgezal
                    alusel := Spec.Op.AluSel.jumpBranch
                    instValid := true.B
                    reg_1_en := true.B
                    reg_2_en := false.B

                    addr_write := 31.U
                    branchValidAddr := pcAdd8

                    when (reg_1_o(31) === 0.U(1.W)) {
                        branchSetEn := true.B
                        branchSetAddr := pcAdd4 + immSll2Signedext.asUInt
                        nextDelay := true.B
                    }
                }
                is (Spec.Op.Inst.bltz) {
                    en_write := false.B
                    aluop := Spec.Op.AluOp.bltz
                    alusel := Spec.Op.AluSel.jumpBranch
                    instValid := true.B
                    reg_1_en := true.B
                    reg_2_en := false.B

                    when (reg_1_o(31) === 1.U(1.W)) {
                        branchSetEn := true.B
                        branchSetAddr := pcAdd4 + immSll2Signedext.asUInt
                        nextDelay := true.B
                    }
                }
                is (Spec.Op.Inst.bltzal) {
                    en_write := true.B
                    aluop := Spec.Op.AluOp.bltzal
                    alusel := Spec.Op.AluSel.jumpBranch
                    instValid := true.B
                    reg_1_en := true.B
                    reg_2_en := false.B

                    addr_write := 31.U
                    branchValidAddr := pcAdd8

                    when (reg_1_o(31) === 1.U(1.W)) {
                        branchSetEn := true.B
                        branchSetAddr := pcAdd4 + immSll2Signedext.asUInt
                        nextDelay := true.B
                    }
                }
            }
        }
        is (Spec.Op.Inst.lb) {
            en_write := true.B
            aluop := Spec.Op.AluOp.lb
            alusel := Spec.Op.AluSel.loadStore
            reg_1_en := true.B
            reg_2_en := false.B
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.lbu) {
            en_write := true.B
            aluop := Spec.Op.AluOp.lbu
            alusel := Spec.Op.AluSel.loadStore
            reg_1_en := true.B
            reg_2_en := false.B
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.lh) {
            en_write := true.B
            aluop := Spec.Op.AluOp.lh
            alusel := Spec.Op.AluSel.loadStore
            reg_1_en := true.B
            reg_2_en := false.B
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.lhu) {
            en_write := true.B
            aluop := Spec.Op.AluOp.lhu
            alusel := Spec.Op.AluSel.loadStore
            reg_1_en := true.B
            reg_2_en := false.B
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.lw) {
            en_write := true.B
            aluop := Spec.Op.AluOp.lw
            alusel := Spec.Op.AluSel.loadStore
            reg_1_en := true.B
            reg_2_en := false.B
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.lwl) {
            en_write := true.B
            aluop := Spec.Op.AluOp.lwl
            alusel := Spec.Op.AluSel.loadStore
            reg_1_en := true.B
            reg_2_en := true.B
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.lwr) {
            en_write := true.B
            aluop := Spec.Op.AluOp.lwr
            alusel := Spec.Op.AluSel.loadStore
            reg_1_en := true.B
            reg_2_en := true.B
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.ll) {
            en_write := true.B
            aluop := Spec.Op.AluOp.ll
            alusel := Spec.Op.AluSel.loadStore
            reg_1_en := true.B
            reg_2_en := false.B
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.sb) {
            en_write := false.B
            aluop := Spec.Op.AluOp.sb
            alusel := Spec.Op.AluSel.loadStore
            reg_1_en := true.B
            reg_2_en := true.B
            instValid := true.B
        }
        is (Spec.Op.Inst.sh) {
            en_write := false.B
            aluop := Spec.Op.AluOp.sh
            alusel := Spec.Op.AluSel.loadStore
            reg_1_en := true.B
            reg_2_en := true.B
            instValid := true.B
        }
        is (Spec.Op.Inst.sw) {
            en_write := false.B
            aluop := Spec.Op.AluOp.sw
            alusel := Spec.Op.AluSel.loadStore
            reg_1_en := true.B
            reg_2_en := true.B
            instValid := true.B
        }
        is (Spec.Op.Inst.swl) {
            en_write := false.B
            aluop := Spec.Op.AluOp.swl
            alusel := Spec.Op.AluSel.loadStore
            reg_1_en := true.B
            reg_2_en := true.B
            instValid := true.B
        }
        is (Spec.Op.Inst.swr) {
            en_write := false.B
            aluop := Spec.Op.AluOp.swr
            alusel := Spec.Op.AluSel.loadStore
            reg_1_en := true.B
            reg_2_en := true.B
            instValid := true.B
        }
        is (Spec.Op.Inst.sc) {
            en_write := true.B
            aluop := Spec.Op.AluOp.sc
            alusel := Spec.Op.AluSel.loadStore
            reg_1_en := true.B
            reg_2_en := true.B
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
    }

    imm_final := imm.asUInt

    when (io.idInstPort.inst(31,21)===0.U(11.W)){
        switch (op3) {
            is (Spec.Op.Inst.sll) {
                en_write := true.B
                aluop := Spec.Op.AluOp.sll
                alusel := Spec.Op.AluSel.shift
                reg_1_en := false.B
                reg_2_en := true.B
                imm_final := Cat(imm(Spec.Width.Reg.data-1,5), io.idInstPort.inst(10,6))
                addr_write := io.idInstPort.inst(15, 11)
                instValid := true.B
            }
            is (Spec.Op.Inst.srl) {
                en_write := true.B
                aluop := Spec.Op.AluOp.srl
                alusel := Spec.Op.AluSel.shift
                reg_1_en := false.B
                reg_2_en := true.B
                imm_final := Cat(imm(Spec.Width.Reg.data-1,5), io.idInstPort.inst(10,6))
                addr_write := io.idInstPort.inst(15, 11)
                instValid := true.B
            }
            is (Spec.Op.Inst.sra) {
                en_write := true.B
                aluop := Spec.Op.AluOp.sra
                alusel := Spec.Op.AluSel.shift
                reg_1_en := false.B
                reg_2_en := true.B
                imm_final := Cat(imm(Spec.Width.Reg.data-1,5), io.idInstPort.inst(10,6))
                addr_write := io.idInstPort.inst(15, 11)
                instValid := true.B
            }
        }
    }

    // operater num
    

    def deal_operator(en: Bool, data: UInt, addr:UInt, out: UInt) : Unit = {
        when (en === true.B && io.write_ex.en === true.B &&
            addr === io.write_ex.addr){
                out := io.write_ex.data
        }.elsewhen (en === true.B && io.write_mem.en === true.B &&
            addr === io.write_mem.addr){
                out := io.write_mem.data
        }.elsewhen (en === true.B){
            out := data
        }.elsewhen (en === false.B){
            out := imm_final
        }.otherwise {
            out := Spec.zeroWord
        }  
    }

    deal_operator(reg_1_en, reg_1_data, reg_1_addr, reg_1_o)
    deal_operator(reg_2_en, reg_2_data, reg_2_addr, reg_2_o)

    // inst
    io.inst := io.idInstPort.inst

    // stall
    

    def aluopEx = io.aluopEx

    val preIsLoad = WireInit(
        VecInit(
            Spec.Op.AluOp.lb,
            Spec.Op.AluOp.lbu,
            Spec.Op.AluOp.lh,
            Spec.Op.AluOp.lhu,
            Spec.Op.AluOp.lw,
            Spec.Op.AluOp.lwl,
            Spec.Op.AluOp.lwr,
            Spec.Op.AluOp.ll,
            Spec.Op.AluOp.sc
        ).contains(aluopEx)
    )

    val stallReqReg1 = WireInit(false.B)
    val stallReqReg2 = WireInit(false.B)

    def dealStallReqReg(
        stallReqReg : Bool, 
        enRead      : Bool, 
        addrRead    : UInt) : Unit = {

        when (
            preIsLoad === true.B &&
            enRead === true.B &&
            addrRead === io.write_ex.addr
        ) {
            stallReqReg := true.B
        }
    }

    dealStallReqReg(stallReqReg1, io.read_1.en, io.read_1.addr)
    dealStallReqReg(stallReqReg2, io.read_2.en, io.read_2.addr)


    io.stallReq := stallReqReg1 | stallReqReg2

}