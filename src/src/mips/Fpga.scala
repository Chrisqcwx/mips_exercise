package mips

import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles._
import mips.components._
import mips.bridges._
import scala.collection.immutable
import chisel3.experimental.FlatIO

class Fpga extends Module {
    val io = FlatIO(new Bundle {
        val led = Output(UInt(8.W))
        val en  = Output(UInt(8.W))
        val clllllk = Output(Bool())
    })

    val cpuDebugPort = Wire(new CpuDebugPort)
        val romDebugPort = Wire(Bool())
    val display = Module(new DisplayAll)
    io.en := display.io.en
    io.led := display.io.led

    display.io.show := VecInit.fill(8)(true.B) 
    display.io.nums := VecInit.tabulate(8)((i:Int)=>cpuDebugPort.regFileRegs(1)((8-i)*4-1,(7-i)*4))

    val cpu = Module(new Cpu(debug = true))
    val rom = Module(new Rom(debug = true))
    val ram = Module(new Ram)
    val cpuclock = Module(new CPUCounter)
    io.clllllk := cpuclock.io


    cpuDebugPort := cpu.io.cpuDebugPort.get
    romDebugPort := rom.io.romDebugPort.get

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

class DisplayNum extends Module {
    val io = IO(new Bundle {
        val num = Input(UInt(4.W))
        val en  = Input(Bool())
        val led = Output(UInt(8.W))
    })

    def led = io.led

    led := "b11111111".U(8.W)
    when(io.en) {
        led := MuxLookup(
            io.num,
            "b11111111".U(8.W),
            immutable.ArraySeq(
                0.U -> "b10000_001".U(8.W),
                1.U -> "b11001_111".U(8.W),
                2.U -> "b10010_010".U(8.W),
                3.U -> "b10000_110".U(8.W),
                4.U -> "b11001_100".U(8.W),
                5.U -> "b10100_100".U(8.W), 
                6.U -> "b10100_000".U(8.W),
                7.U -> "b10001_111".U(8.W),
                8.U -> "b10000_000".U(8.W),
                9.U -> "b10001_100".U(8.W),
                10.U -> "b10001_000".U(8.W),
                11.U -> "b11100_000".U(8.W),
                12.U -> "b11110_010".U(8.W),
                13.U -> "b11000_010".U(8.W),
                14.U -> "b10110_000".U(8.W),
                15.U -> "b10111_000".U(8.W)
            )
        )
    }
}

class CPUCounter extends Module {
    val io = IO(Output(Bool()))

    val cpuclock = RegInit(false.B)
    io := cpuclock

    val cnt = RegInit(0.U(32.W))
    def cntmax = 49999999
    val cntEnd = WireInit(cnt===cntmax.U(32.W))
    when(cntEnd) {
        cnt := 0.U
        cpuclock := ~cpuclock
    }.otherwise {
        cnt := cnt + 1.U
    }
}

class DisplayAll extends Module {
    val io = IO(new Bundle {
        val nums = Input(Vec(8,UInt(4.W)))
        val show = Input(Vec(8,Bool()))
        val led = Output(UInt(8.W))
        val en  = Output(UInt(8.W))
    })

    val cnt = RegInit(0.U(24.W))
    def cntmax = 99999
    val cntEnd = WireInit(cnt===cntmax.U(24.W))
    when(cntEnd) {
        cnt := 0.U
    }.otherwise {
        cnt := cnt + 1.U
    }

    val _en = RegInit("b0111_1111".U(8.W))
    when (cntEnd) {
        _en := Cat(_en(6,0),_en(7))
    }

    io.en := _en

    val _leds = Wire(Vec(8,UInt(8.W)))
    for (i <- 0 to 7) {
        val dni = Module(new DisplayNum)
        dni.io.num := io.nums(i)
        dni.io.en := io.show(i)
        _leds(i.U) := dni.io.led
    }

    io.led := MuxLookup(
            _en,
            "b11111111".U(8.W),
            immutable.ArraySeq(
                "b0111_1111".U -> _leds(0),
                "b1011_1111".U -> _leds(1),
                "b1101_1111".U -> _leds(2),
                "b1110_1111".U -> _leds(3),
                "b111_10111".U -> _leds(4),
                "b111_11011".U -> _leds(5),
                "b111_11101".U -> _leds(6),
                "b111_11110".U -> _leds(7)
            )
        )
    
}