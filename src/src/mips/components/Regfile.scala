import chisel3._
import chisel3.util._
import mips.Spec
import mips.bundles.{RfReadPort, RegWriteNdPort}
import chisel3.experimental.BundleLiterals._

class Regfile extends Module {
    val io = IO(new Bundle{
        val write = Input(new RegWriteNdPort)
        val read_1 = new RfReadPort
        val read_2 = new RfReadPort

        // debug
        val regFileRegs = Output(Vec(Spec.Num.reg, UInt(Spec.Width.Reg.data.W)))
    })

    val regs = RegInit(VecInit(Seq.fill(Spec.Num.reg)(0.U(Spec.Width.Reg.data.W))))

    // write
    when (io.write.en && (io.write.addr =/= 0.U(Spec.Width.Reg.addr.W))) {
        regs(io.write.addr) := io.write.data
    }

    // read
    def deal_rdata(readPort: RfReadPort, writePort: RegWriteNdPort): Unit = {
        def rdata = readPort.data
        when (readPort.addr === 0.U(Spec.Width.Reg.addr.W)){
            rdata := Spec.zeroWord
        }.elsewhen((readPort.addr === writePort.addr) && 
            (writePort.en === true.B) && (readPort.en === true.B)){
                rdata := io.write.data
        }.elsewhen(readPort.en === true.B){
            rdata := regs(readPort.addr)
        }.otherwise{
            rdata := Spec.zeroWord
        }
    }

    deal_rdata(io.read_1, io.write)
    deal_rdata(io.read_2, io.write)

    // def rdata1 = io.read_1.data

    // when (io.read_1.addr === 0.U(Spec.Width.Reg.addr.W)){
    //     rdata1 := Spec.zeroWord
    // }.elsewhen((io.read_1.addr === io.write.addr) && 
    //     (io.write.en === true.B) && (io.read_1.en === true.B)){
    //         rdata1 := io.write.data
    // }.elsewhen(io.write.en === true.B){
    //     rdata1 := regs(io.read_1.addr)
    // }.otherwise{
    //     rdata1 := Spec.zeroWord
    // }

    // def rdata2 = io.read_2.data

    // when (io.read_2.addr === 0.U(Spec.Width.Reg.addr.W)){
    //     rdata2 := Spec.zeroWord
    // }.elsewhen((io.read_2.addr === io.write.addr) && 
    //     (io.write.en === true.B) && (io.read_2.en === true.B)){
    //         rdata2 := io.write.data
    // }.elsewhen(io.write.en === true.B){
    //     rdata2 := regs(io.read_2.addr)
    // }.otherwise{
    //     rdata2 := Spec.zeroWord
    // }

    
    // debug
    io.regFileRegs := regs
}