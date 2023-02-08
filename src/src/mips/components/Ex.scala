import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.{IdDecodeNdPort, RegWriteNdPort}
import chisel3.experimental.BundleLiterals._

class Ex extends Module {
    val io = IO(new Bundle {
        val idDecodePort = Input(new IdDecodeNdPort)
        val regWritePort = Output(new RegWriteNdPort)
    })

    // result

    val logic_out = Wire(UInt(Spec.Width.Reg.data.W))
    logic_out := Spec.zeroWord

    val shift_res = Wire(UInt(Spec.Width.Reg.data.W))
    shift_res := Spec.zeroWord

    

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
    }


}