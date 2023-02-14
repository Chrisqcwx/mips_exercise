package mips

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFileInline
import mips.bundles.RomReadPort

class Rom(
  romMemFile: String,
  romInstNum: Int    = Params.romInstNum)
    extends Module {
  val io = IO(new RomReadPort)

  val instMem = SyncReadMem(romInstNum, UInt(Spec.Width.Rom.data.W))
  if (romMemFile.trim().nonEmpty) {
    loadMemoryFromFileInline(instMem, romMemFile)
  }

  io.data := Mux(
    io.en,
    instMem(
      io.addr(Spec.Width.Rom.addr - 1, log2Floor(Spec.Width.Rom.data))
    ),
    Spec.zeroWord
  )
}
