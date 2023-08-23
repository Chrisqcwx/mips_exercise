import chisel3._
import chisel3.util._
import chisel3.util.experimental.forceName
import chisel3.experimental.FlatIO

class fpu(debug: Boolean = false) extends Module {
  val io = IO(new Bundle{
    val op = Input(Bool())
    val A_ = Input(UInt(32.W))
    val B_ = Input(UInt(32.W))
    val C_ = Output(UInt(32.W))
    val deltaE = if(debug) Some(Output(SInt(8.W))) else None
  })

  io.C_ := 0.U

  val sA = WireDefault(io.A_(31))
  val expA = WireDefault(Cat(false.B, io.A_(30, 23))) // 9位
  val manA = WireDefault(io.A_(22, 0))
  val isNorm_A = WireDefault(expA.orR) // 是否规格化
  // 规格化补1
  val normMan_A = WireDefault(Cat(false.B, false.B, true.B, manA)) // 26位
  // 阶码和位数变补码
  val comExp_A = WireDefault(expA.asSInt - 127.S) // 9位
  val comMan_A = WireDefault(Mux( // 26位
    sA,
    ~normMan_A + 1.U,
    normMan_A
  ).asSInt)

  val unNormMan_A = WireDefault(Cat(false.B, false.B, false.B, manA))
  when (!isNorm_A) {
    comExp_A := - 126.S
    comMan_A := Mux( // 26位
      sA,
      ~unNormMan_A + 1.U,
      unNormMan_A
    ).asSInt
  }

  

  val sB = WireDefault(io.B_(31)) ^ io.op
  val expB = WireDefault(Cat(false.B, io.B_(30, 23)))
  val manB = WireDefault(io.B_(22, 0))
  val isNorm_B = WireDefault(expB.orR)
  val normMan_B = WireDefault(Cat(false.B, false.B, true.B, manB)) 
  val comExp_B = WireDefault(expB.asSInt - 127.S)
  val comMan_B = WireDefault(Mux(
    sB,
    ~normMan_B + 1.U,
    normMan_B
  ).asSInt)

  val unNormMan_B = WireDefault(Cat(false.B, false.B, false.B, manB))
  when (!isNorm_B) {
    comExp_B := - 126.S
    comMan_B := Mux( // 26位
      sB,
      ~unNormMan_B + 1.U,
      unNormMan_B
    ).asSInt
  }

  val deltaExp = WireDefault(comExp_A - comExp_B).suggestName("deltaExp")

  if (debug) {
    io.deltaE.get := deltaExp
  }

  /**
    * 双规格化数运算
    */
  val shiftExp = WireDefault(comExp_A).suggestName("shiftExp")
  val shiftManA = WireDefault(comMan_A).suggestName("shiftManA")
  val shiftManB = WireDefault(comMan_B)
  val deltaExpSign = WireDefault(deltaExp(7)).suggestName("deltaExpSign")
  // 对阶后求和
  val shiftSum = WireDefault(shiftManA + shiftManB).suggestName("shiftSum")
  // 恢复
  val restoreSum = WireDefault(0.S(26.W)).suggestName("restoreSum")
  val realMan = WireDefault(0.U(23.W)).suggestName("realMan") //  Wire(UInt(23.W))
  val comResExp = WireDefault(0.S(9.W)).suggestName("comResExp") 
  val realExp = WireDefault((comResExp + 127.S).asUInt).suggestName("realExp")
  val realSign = WireDefault(false.B).suggestName("realSign")
  io.C_ := Cat(realSign, realExp(7, 0), realMan)

  val clzCounter = Module(new Clz)
  val clzNum = WireDefault(clzCounter.io.output - 8.U).suggestName("clzNum") // 空的6位+2个符号位
  clzCounter.io.input := 0.U
  
  // when(isNorm_A || isNorm_B)
   {
    when (deltaExpSign) {
      // A 阶码小
      shiftExp := comExp_B
      shiftManA := comMan_A >> (~deltaExp.asUInt+1.U)
    }.otherwise{
      // A 阶码大 B -> A
      shiftExp := comExp_A
      shiftManB := comMan_B >> deltaExp.asUInt
    }
    
    realSign := shiftSum(25)
   
    when (shiftSum(25) === shiftSum(24)) {
      // 左规
      when(shiftSum(25)) {
        // 负数
        clzCounter.io.input := ~ shiftSum.asUInt
      }.otherwise {
        // 正数
        clzCounter.io.input := shiftSum.asUInt
      }
      restoreSum := shiftSum << clzNum
      comResExp := shiftExp - Cat(false.B, clzNum).asSInt
      
    }.otherwise{
      // 右规
      restoreSum := shiftSum >> 1
      comResExp := shiftExp + 1.S
    }

    when(shiftSum(25)) {
      realMan := (~restoreSum.asUInt + 1.U)(22, 0)
    }.otherwise{
      realMan := restoreSum(22, 0)
    }

    when (realExp(8) || !realExp.orR) {
      // -> 非规格化
      restoreSum := shiftSum << (shiftExp + 126.S).asUInt;
      io.C_ := Cat(
        realSign, 
        0.U(8.W), 
        realMan
      )
    }
  }

  // naming
  forceName(sA, "sA_")
  forceName(sB, "sB_")
  forceName(expA, "expA_")
  forceName(expB, "expB_")
  forceName(manA, "mamA_")
  forceName(manB, "manB_")

  forceName(isNorm_A, "isNorm_A_")
  forceName(normMan_A, "normMan_A_")
  forceName(comExp_A, "comExp_A_")
  forceName(comMan_A, "comMan_A_")
  forceName(isNorm_B, "isNorm_B_")
  forceName(normMan_B, "normMan_B_")
  forceName(comExp_B, "comExp_B_")
  forceName(comMan_B, "comMan_B_")

  forceName(unNormMan_A, "unNormMan_A_")
  forceName(unNormMan_B, "unNormMan_B_")
  forceName(deltaExp, "deltaExp_")
  forceName(deltaExpSign, "deltaExpSign_")

  forceName(shiftExp, "shiftExp_")
  forceName(shiftManA, "shiftManA_")
  forceName(shiftManB, "shiftManB_")
  forceName(shiftSum, "shiftSum_")

  forceName(restoreSum, "restoreSum_")
  forceName(realMan, "realMan_")
  forceName(comResExp, "comResExp_")
  forceName(realExp, "realExp_")
  forceName(realSign, "realSign_")

  forceName(clzNum, "clzNum_")
  forceName(io.A_, "A")
  forceName(io.B_, "B")
  forceName(io.C_, "C")
  forceName(io.op, "op")
}
