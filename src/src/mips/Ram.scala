package mips

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFileInline
import mips.bundles.RamRWPort
import mips.{Params, Spec}

class Ram extends Module {
    val io = IO(new Bundle {
        val ramPort = new RamRWPort
    })

    val memory = RegInit(VecInit(
        Seq.fill(Params.ramMemNum)(0.U(Params.wordLength.W))
    ))

    RegInit(VecInit(Seq.fill(Spec.Num.reg)(0.U(Spec.Width.Reg.data.W))))

    def en = io.ramPort.en
    def enWrite = io.ramPort.enWrite
    def dataRead = io.ramPort.dataRead
    def dataWrite = io.ramPort.dataWrite
    def addr = io.ramPort.addr
    def sel = io.ramPort.sel

    // write
    val regIndex = addr(Params.ramMemNumLog2+1,2)
    when (en === true.B && enWrite === true.B) {
        // val write = Wire(UInt(Params.wordLength.W))
        def getByte(idx: Int) : UInt = {
            val wire = Wire(UInt(8.W))
            when (sel(idx) === true.B) {
                wire := dataWrite((idx+1)*8-1, 8*idx)
            
            }.otherwise {
                wire := memory(regIndex)((idx+1)*8-1, 8*idx)
            }
            wire
            //byte
            //byte
        }
        memory(regIndex) := Cat(Seq.range(3,-1,-1).map { idx =>
            getByte(idx)
        })
    }

    // read
    dataRead := Spec.zeroWord
    when (en === true.B && enWrite === false.B) {
        dataRead := memory(regIndex)
    }
}