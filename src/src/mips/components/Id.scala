import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.{RfReadPort, IdInstNdPort, IdDecodeNdPort, RegWriteNdPort}
import chisel3.experimental.BundleLiterals._

class Id extends Module {
    val io = IO(new Bundle {
        val idInstPort = Input(new IdInstNdPort)
        // to regfile
        val read_1 = Flipped(new RfReadPort)
        val read_2 = Flipped(new RfReadPort)
        // ex result
        val write_ex = Input(new RegWriteNdPort)
        // mem result
        val write_mem = Input(new RegWriteNdPort)
        // to ex
        val decode = Output(new IdDecodeNdPort)
    })

    def op  = io.idInstPort.inst(31,26) // 指令码
    def op2 = io.idInstPort.inst(10,6)
    def op3 = io.idInstPort.inst(5,0)   // 功能码
    def op4 = io.idInstPort.inst(20,16)


    val imm = Wire(UInt(Spec.Width.Reg.data.W))
    val imm_final = Wire(UInt(Spec.Width.Reg.data.W))
    imm := Spec.zeroWord

    val instValid = Wire(Bool())
    instValid := false.B

    // to regfile
    def reg_1_data = io.read_1.data
    def reg_2_data = io.read_2.data
    val reg_1_en = Wire(Bool()) //RegInit(false.B)
    val reg_1_addr = Wire(UInt(Spec.Width.Reg.addr.W)) //RegInit(Spec.Addr.nop)
    val reg_2_en = Wire(Bool())
    val reg_2_addr = Wire(UInt(Spec.Width.Reg.addr.W))

    reg_1_en := false.B
    reg_2_en := false.B
    reg_1_addr := io.idInstPort.inst(25,21)
    reg_2_addr := io.idInstPort.inst(20,16)

    io.read_1.en := reg_1_en
    io.read_1.addr := reg_1_addr
    io.read_2.en := reg_2_en
    io.read_2.addr := reg_2_addr

    // to ex
    val aluop = Wire(UInt(Spec.Width.Alu.op.W))   //  RegInit(Spec.Op.AluOp.nop)
    val alusel = Wire(UInt(Spec.Width.Alu.sel.W))   //  RegInit(Spec.Op.AluSel.nop)
    val reg_1_o = Wire(UInt(Spec.Width.Reg.data.W))   //  RegInit(Spec.zeroWord)
    val reg_2_o = Wire(UInt(Spec.Width.Reg.data.W))   //  RegInit(Spec.zeroWord)
    val en_write = Wire(Bool())   //  RegInit(false.B)
    val addr_write = Wire(UInt(Spec.Width.Reg.addr.W))   //  RegInit(Spec.Addr.nop)

    aluop := Spec.Op.AluOp.nop
    alusel := Spec.Op.AluSel.nop
    reg_1_o := Spec.zeroWord
    reg_2_o := Spec.zeroWord
    en_write := false.B
    addr_write := io.idInstPort.inst(15,11)

    io.decode.aluop := aluop
    io.decode.alusel := alusel
    io.decode.reg_1 := reg_1_o
    io.decode.reg_2 := reg_2_o
    io.decode.en_write := en_write
    io.decode.addr_write := addr_write

    // decode
    
    switch (op) {
        is (Spec.Op.Inst.special_inst){
            switch (op2) {
                is (0.U(5.W))
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
                            when (io.decode.reg_2 === 0.U(Spec.Width.Reg.data.W)) {
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
                            when (io.decode.reg_2 === 0.U(Spec.Width.Reg.data.W)) {
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
            imm := Cat(0.U(16.W), io.idInstPort.inst(15,0))
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.andi) {
            en_write := true.B
            aluop := Spec.Op.AluOp.and
            alusel := Spec.Op.AluSel.logic
            reg_1_en := true.B
            reg_2_en := false.B
            imm := Cat(0.U(16.W), io.idInstPort.inst(15,0))
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.xori) {
            en_write := true.B
            aluop := Spec.Op.AluOp.xor
            alusel := Spec.Op.AluSel.logic
            reg_1_en := true.B
            reg_2_en := false.B
            imm := Cat(0.U(16.W), io.idInstPort.inst(15,0))
            addr_write := io.idInstPort.inst(20,16)
            instValid := true.B
        }
        is (Spec.Op.Inst.lui) {
            en_write := true.B
            aluop := Spec.Op.AluOp.or
            alusel := Spec.Op.AluSel.logic
            reg_1_en := true.B
            reg_2_en := false.B
            imm := Cat(io.idInstPort.inst(15,0), 0.U(16.W))
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
    }

    imm_final := imm

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

}