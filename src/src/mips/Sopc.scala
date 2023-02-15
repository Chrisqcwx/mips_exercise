package mips

import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles._
import mips.components._
import mips.bridges._

class Sopc extends Module {
    val io = IO(new Bundle {
        val cpuDebugPort = Output(new CpuDebugPort)
        val romDebugPort = Output(Bool())
    })

    val cpu = Module(new Cpu(debug = true))
    val rom = Module(new Rom(debug = true))
    val ram = Module(new Ram)

    io.cpuDebugPort := cpu.io.cpuDebugPort.get
    io.romDebugPort := rom.io.romDebugPort.get

    // cpu
    cpu.io.romReadPort.data := rom.io.romReadPort.data
    cpu.io.ramRWPort.dataRead := ram.io.dataRead

    // rom
    rom.io.romReadPort.en := cpu.io.romReadPort.en
    rom.io.romReadPort.addr := cpu.io.romReadPort.addr

    // ram
    ram.io.addr := cpu.io.ramRWPort.addr
    ram.io.en := cpu.io.ramRWPort.en
    ram.io.enWrite := cpu.io.ramRWPort.enWrite
    ram.io.dataWrite := cpu.io.ramRWPort.dataWrite
    ram.io.sel := cpu.io.ramRWPort.sel
}