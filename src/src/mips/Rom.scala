package mips

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFileInline
import mips.bundles.RomReadPort

class Rom(
  // romMemFile: String,
  // romInstNum: Int    = Params.romInstNum
  )extends Module {
  val io = IO(new RomReadPort)

  // val instMem = SyncReadMem(romInstNum, UInt(Spec.Width.Rom.data.W))
  // if (romMemFile.trim().nonEmpty) {
  //   loadMemoryFromFileInline(instMem, romMemFile)
  // }

  val instMem = RegInit(VecInit(
    Seq(
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
  ))

  io.data := Mux(
    io.en,
    instMem(
      //io.addr(Spec.Width.Rom.addr - 1, log2Floor(Spec.Width.Rom.data))
      io.addr/4.U
    ),
    Spec.zeroWord
  )
}
