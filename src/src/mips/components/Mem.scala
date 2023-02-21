package mips.components

import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.{RegWriteNdPort, HiLoReadNdPort, HiLoWriteNdPort}
import mips.bundles.{MemLSNdPort,RamRWPort}
import mips.bundles.{LLbitWriteNdPort}
import chisel3.experimental.BundleLiterals._

class Mem extends Module {
    val io = IO(new Bundle {
        // regfile
        val in_regWritePort = Input(new RegWriteNdPort)
        val out_regWritePort = Output(new RegWriteNdPort)

        // hilo
        val hiloWrite_ex = Input(new HiLoWriteNdPort)
        val hiloWrite = Output(new HiLoWriteNdPort)

        val memLS = Input(new MemLSNdPort)
        val ramRW = Flipped(new RamRWPort)
        // llbit
        val inLLbit = Input(Bool())
        val inLLbitWrite = Input(new LLbitWriteNdPort)
        val outLLbitWrite = Output(new LLbitWriteNdPort)
    })

    def in_en = io.in_regWritePort.en
    def in_addr = io.in_regWritePort.addr
    def in_data = io.in_regWritePort.data

    def out_en = io.out_regWritePort.en
    def out_addr = io.out_regWritePort.addr
    def out_data = io.out_regWritePort.data

    out_en := in_en
    out_addr := in_addr
    out_data := in_data

    def hilo_en_ex = io.hiloWrite_ex.en
    def hi_ex = io.hiloWrite_ex.hi
    def lo_ex = io.hiloWrite_ex.lo

    def hilo_en = io.hiloWrite.en
    def hi = io.hiloWrite.hi
    def lo = io.hiloWrite.lo

    hilo_en := hilo_en_ex
    hi := hi_ex
    lo := lo_ex

    def aluop = io.memLS.aluop
    def writeAddr = io.memLS.addr
    def writeData = io.memLS.data

    def ramEn = io.ramRW.en
    def ramEnWrite = io.ramRW.enWrite
    def ramAddr = io.ramRW.addr
    def ramWrite = io.ramRW.dataWrite
    def ramRead = io.ramRW.dataRead
    def ramSel = io.ramRW.sel

    ramEnWrite := false.B
    ramAddr := Spec.Addr.nop
    ramSel := "b1111".U(Spec.Width.Ram.sel.W)
    ramEn := false.B
    ramWrite := Spec.zeroWord

    // llbit

    val llbit = Wire(Bool())
    when (io.inLLbitWrite.en === true.B) {
        llbit := io.inLLbitWrite.value
    }.otherwise {
        llbit := io.inLLbit
    }

    io.outLLbitWrite := (new LLbitWriteNdPort).Lit(
        _.en -> false.B,
        _.value -> false.B
    )
    

    def fillSInt(data: UInt, fillLength: Int): UInt = {
        Cat(Fill(fillLength,data(data.getWidth-1)),data)
    }

    def fillUInt(data: UInt, fillLength: Int): UInt = {
        Cat(Fill(fillLength,0.U(1.W)),data)
    }

    switch (aluop) {
        is (Spec.Op.AluOp.lb) {
            ramAddr := writeAddr
            ramEnWrite := false.B
            ramEn := true.B
            val addrLast = writeAddr(1,0)
            ramSel := Reverse(UIntToOH(addrLast,Spec.Width.Ram.sel))
            switch (addrLast) {
                is ("b00".U(2.W)) {
                    out_data := fillSInt(ramRead(31,24),24)
                }
                is ("b01".U(2.W)) {
                    out_data := fillSInt(ramRead(23,16),24)
                }
                is ("b10".U(2.W)) {
                    out_data := fillSInt(ramRead(15,8),24)
                }
                is ("b11".U(2.W)) {
                    out_data := fillSInt(ramRead(7,0),24)
                }
            }
        }
        is (Spec.Op.AluOp.lbu) {
            ramAddr := writeAddr
            ramEnWrite := false.B
            ramEn := true.B
            val addrLast = writeAddr(1,0)
            ramSel := Reverse(UIntToOH(addrLast,Spec.Width.Ram.sel))
            switch (addrLast) {
                is ("b00".U(2.W)) {
                    out_data := fillUInt(ramRead(31,24),24)
                }
                is ("b01".U(2.W)) {
                    out_data := fillUInt(ramRead(23,16),24)
                }
                is ("b10".U(2.W)) {
                    out_data := fillUInt(ramRead(15,8),24)
                }
                is ("b11".U(2.W)) {
                    out_data := fillUInt(ramRead(7,0),24)
                }
            }
        }
        is (Spec.Op.AluOp.lh) {
            ramAddr := writeAddr
            ramEnWrite := false.B
            ramEn := true.B

            out_data := Spec.zeroWord
            val addrLast = writeAddr(1,0)
            switch (addrLast) {
                is ("b00".U(2.W)) {
                    ramSel := "b1100".U(Spec.Width.Ram.sel.W)
                    out_data := fillSInt(ramRead(31,16),16)
                }
                is ("b10".U(2.W)) {
                    ramSel := "b0011".U(Spec.Width.Ram.sel.W)
                    out_data := fillSInt(ramRead(15,0),16)
                }
            }
        }
        is (Spec.Op.AluOp.lhu) {
            ramAddr := writeAddr
            ramEnWrite := false.B
            ramEn := true.B

            out_data := Spec.zeroWord
            val addrLast = writeAddr(1,0)
            switch (addrLast) {
                is ("b00".U(2.W)) {
                    ramSel := "b1100".U(Spec.Width.Ram.sel.W)
                    out_data := fillUInt(ramRead(31,16),16)
                }
                is ("b10".U(2.W)) {
                    ramSel := "b0011".U(Spec.Width.Ram.sel.W)
                    out_data := fillUInt(ramRead(15,0),16)
                }
            }
        }
        is (Spec.Op.AluOp.lw) {
            ramAddr := writeAddr
            ramEnWrite := false.B
            ramEn := true.B
            ramSel := "b1111".U(Spec.Width.Ram.sel.W)
            out_data := ramRead
        }
        is (Spec.Op.AluOp.ll) {
            ramAddr := writeAddr
            ramEnWrite := false.B
            ramEn := true.B
            ramSel := "b1111".U(Spec.Width.Ram.sel.W)
            out_data := ramRead
            // set llbit
            io.outLLbitWrite := (new LLbitWriteNdPort).Lit(
                _.en -> true.B,
                _.value -> true.B
            )

        }
        is (Spec.Op.AluOp.lwl) {
            ramAddr := Cat(writeAddr(31,2),"b00".U(2.W))
            ramEnWrite := false.B
            ramEn := true.B
            ramSel := "b1111".U(Spec.Width.Ram.sel.W)

            val addrLast = writeAddr(1,0)
            switch (addrLast) {
                is ("b00".U(2.W)) {
                    out_data := ramRead
                }
                is ("b01".U(2.W)) {
                    out_data := Cat(ramRead(23,0),writeData(7,0))
                }
                is ("b10".U(2.W)) {
                    out_data := Cat(ramRead(15,0),writeData(15,0))
                }
                is ("b11".U(2.W)) {
                    out_data := Cat(ramRead(7,0),writeData(23,0))
                }
            }
        }
        is (Spec.Op.AluOp.lwr) {
            ramAddr := Cat(writeAddr(31,2),"b00".U(2.W))
            ramEnWrite := false.B
            ramEn := true.B
            ramSel := "b1111".U(Spec.Width.Ram.sel.W)

            val addrLast = writeAddr(1,0)
            switch (addrLast) {
                is ("b00".U(2.W)) {
                    out_data := Cat(writeData(31,8),ramRead(31,24))
                }
                is ("b01".U(2.W)) {
                    out_data := Cat(writeData(31,16),ramRead(31,16))
                }
                is ("b10".U(2.W)) {
                    out_data := Cat(writeData(31,24),ramRead(31,8))
                }
                is ("b11".U(2.W)) {
                    out_data := ramRead
                }
            }
        }
        is (Spec.Op.AluOp.sb) {
            ramAddr := writeAddr
            ramEnWrite := true.B
            ramEn := true.B
            ramWrite := Fill(4, writeData(7,0))

            val addrLast = writeAddr(1,0)
            ramSel := Reverse(UIntToOH(addrLast,Spec.Width.Ram.sel))
        }
        is (Spec.Op.AluOp.sh) {
            ramAddr := writeAddr
            ramEnWrite := true.B
            ramEn := true.B
            ramWrite := Fill(2, writeData(15,0))

            ramSel := "b0000".U(Spec.Width.Ram.sel.W)
            val addrLast = writeAddr(1,0)
            switch (addrLast) {
                is ("b00".U(2.W)) {
                    ramSel := "b1100".U(Spec.Width.Ram.sel.W)
                }
                is ("b10".U(2.W)) {
                    ramSel := "b0011".U(Spec.Width.Ram.sel.W)
                }
            }
        }
        is (Spec.Op.AluOp.sw) {
            ramAddr := writeAddr
            ramEnWrite := true.B
            ramEn := true.B
            ramWrite := writeData

            ramSel := "b1111".U(Spec.Width.Ram.sel.W)
        }
        is (Spec.Op.AluOp.swl) {
            ramAddr := Cat(writeAddr(31,2),"b00".U(2.W))
            ramEnWrite := true.B
            ramEn := true.B
            ramSel := "b0000".U(Spec.Width.Ram.sel.W)

            val addrLast = writeAddr(1,0)
            switch (addrLast) {
                is ("b00".U(2.W)) {
                    ramWrite := writeData
                    ramSel := "b1111".U(Spec.Width.Ram.sel.W)
                }
                is ("b01".U(2.W)) {
                    ramWrite := Cat(0.U(8.W),writeData(31,8))
                    ramSel := "b0111".U(Spec.Width.Ram.sel.W)
                }
                is ("b10".U(2.W)) {
                    ramWrite := Cat(0.U(16.W),writeData(31,16))
                    ramSel := "b0011".U(Spec.Width.Ram.sel.W)
                }
                is ("b11".U(2.W)) {
                    ramWrite := Cat(0.U(24.W),writeData(31,24))
                    ramSel := "b0001".U(Spec.Width.Ram.sel.W)
                }
            }
        }
        is (Spec.Op.AluOp.swr) {
            ramAddr := Cat(writeAddr(31,2),"b00".U(2.W))
            ramEnWrite := true.B
            ramEn := true.B
            ramSel := "b0000".U(Spec.Width.Ram.sel.W)

            val addrLast = writeAddr(1,0)
            switch (addrLast) {
                is ("b00".U(2.W)) {
                    ramWrite := Cat(writeData(7,0),0.U(24.W))
                    ramSel := "b1000".U(Spec.Width.Ram.sel.W)
                }
                is ("b01".U(2.W)) {
                    ramWrite := Cat(writeData(15,0),0.U(16.W))
                    ramSel := "b1100".U(Spec.Width.Ram.sel.W)
                }
                is ("b10".U(2.W)) {
                    ramWrite := Cat(writeData(23,0),0.U(8.W))
                    ramSel := "b1110".U(Spec.Width.Ram.sel.W)
                }
                is ("b11".U(2.W)) {
                    ramWrite := writeData
                    ramSel := "b1111".U(Spec.Width.Ram.sel.W)
                }
            }
        }
        is (Spec.Op.AluOp.sc) {
            when (llbit === true.B) {
                ramAddr := writeAddr
                ramEnWrite := true.B
                ramEn := true.B
                ramWrite := writeData
                ramSel := "b1111".U(Spec.Width.Ram.sel.W)
                out_data := 1.U(Spec.Width.Reg.data.W)

                io.outLLbitWrite := (new LLbitWriteNdPort).Lit(
                    _.en -> true.B,
                    _.value -> false.B
                )
            }.otherwise {
                out_data := Spec.zeroWord
            }
        }
    }
}