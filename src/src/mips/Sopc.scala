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
    })

    val cpu = Module(new Cpu(debug = true))
    val rom = Module(new Rom)
    val ram = Module(new Ram)

    io.cpuDebugPort := cpu.io.cpuDebugPort.get

    // cpu
    cpu.io.romReadPort.data := rom.io.data
    cpu.io.ramRWPort.dataRead := ram.io.dataRead

    // rom
    rom.io.en := cpu.io.romReadPort.en
    rom.io.addr := cpu.io.romReadPort.addr

    // ram
    ram.io.addr := cpu.io.ramRWPort.addr
    ram.io.en := cpu.io.ramRWPort.en
    ram.io.enWrite := cpu.io.ramRWPort.enWrite
    ram.io.dataWrite := cpu.io.ramRWPort.dataWrite
    ram.io.sel := cpu.io.ramRWPort.sel
}