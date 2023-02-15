package mips

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFileInline
import mips.bundles.RomReadPort

class Rom(
  debug: Boolean = false
  // romMemFile: String,
  // romInstNum: Int    = Params.romInstNum
  )extends Module {
  // val io = IO(new RomReadPort)

  val io = IO(new Bundle {
    val romReadPort = new RomReadPort
    val romDebugPort = if(debug) Some(Output(Bool())) else None
  })

  // val instMem = SyncReadMem(romInstNum, UInt(Spec.Width.Rom.data.W))
  // if (romMemFile.trim().nonEmpty) {
  //   loadMemoryFromFileInline(instMem, romMemFile)
  // }

  val insts = Seq(
"b00110100000000010000000000101111",
"b00110100000000100000000000101111",
"b00000000000000000000000001000000",
"b00010000001000100000000000000100",
"b00000000000000000000000001000000",
"b00110100000000110000000000010001",
"b00000000001000100010000000100101",
"b00001000000000000000000000010100",
"b00000000000000000000000001000000",
"b00000000000000000000000001000000",
"b00110100000000110000000000100010",
"b00000000000000000000000001000000",
"b00000000000000000000000001000000",
"b00000000000000000000000001000000",
"b00000000000000000000000001000000",
"b00000000000000000000000001000000"
  ).map {str => str.U(32.W)}

  val instMem = RegInit(VecInit(
    insts
  ))

  // io.data := Mux(
  //   io.en,
  //   instMem(
  //     //io.addr(Spec.Width.Rom.addr - 1, log2Floor(Spec.Width.Rom.data))
  //     io.addr/4.U
  //   ),
  //   Spec.zeroWord
  // )

  def readData = io.romReadPort.data
  def over = io.romDebugPort.get

  def readRegIdx = io.romReadPort.addr/4.U

  readData := Spec.zeroWord
  when (
    io.romReadPort.en === true.B && 
    readRegIdx < insts.length.U
  ) {
    readData := instMem(readRegIdx)
  }

  over := (readRegIdx >= insts.length.U)
}
