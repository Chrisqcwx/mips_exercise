package mips.bundles

import chisel3._
// import chisel3.util._
import mips.Spec

class CpuDebugPort extends Bundle {
    // val pc = Output(UInt(Spec.Width.Rom.addr.W))
    val regFileRegs = Output(Vec(Spec.Num.reg, UInt(Spec.Width.Reg.data.W)))
    val id_reg_data1 = Output(UInt(Spec.Width.Reg.data.W))
    val id_reg_data2 = Output(UInt(Spec.Width.Reg.data.W))
    val rf_write = Output(new RegWriteNdPort)
}