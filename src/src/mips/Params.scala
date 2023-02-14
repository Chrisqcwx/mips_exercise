import chisel3._

package object mips {

    object Params {
        final val wordLength = 32
        final val regReadNum = 2
        final val romInstNum = 1024
    }

    object Spec{

        def zeroWord : UInt = 0.U(Params.wordLength.W)

        

        object Width{
            val inst = 6
            val inst2 = 5

            object Alu {
                val op = 8
                val sel = 3
            }

            object Rom {
                
                val addr = Params.wordLength
                val data = Params.wordLength
            }

            object Ram {
                val sel  = 4
                val addr = Params.wordLength
                val data = Params.wordLength
            }

            object Reg {
                val addr = 5
                val data = Params.wordLength
                val doubleData = data * 2
            }
        }

        object Num{
            val reg = 32
        }

        object Addr {
            val nop = 0.U(Width.Reg.addr.W)
        }

        object RamFlag {
            val write = true.B
            val read = false.B
        }

        object Op {
            object Inst {
                def and     : UInt = "b100100".U(Spec.Width.inst.W)
                def or      : UInt = "b100101".U(Spec.Width.inst.W)
                def xor     : UInt = "b100110".U(Spec.Width.inst.W)
                def nor     : UInt = "b100111".U(Spec.Width.inst.W)
                def andi    : UInt = "b001100".U(Spec.Width.inst.W)
                def ori     : UInt = "b001101".U(Spec.Width.inst.W)
                def xori    : UInt = "b001110".U(Spec.Width.inst.W)
                def lui     : UInt = "b001111".U(Spec.Width.inst.W)

                def sll     : UInt = "b000000".U(Spec.Width.inst.W)
                def sllv    : UInt = "b000100".U(Spec.Width.inst.W)
                def srl     : UInt = "b000010".U(Spec.Width.inst.W)
                def srlv    : UInt = "b000110".U(Spec.Width.inst.W)
                def sra     : UInt = "b000011".U(Spec.Width.inst.W)
                def srav    : UInt = "b000111".U(Spec.Width.inst.W)

                def movz    : UInt = "b001010".U(Spec.Width.inst.W)
                def movn    : UInt = "b001011".U(Spec.Width.inst.W)
                def mfhi    : UInt = "b010000".U(Spec.Width.inst.W)
                def mthi    : UInt = "b010001".U(Spec.Width.inst.W)
                def mflo    : UInt = "b010010".U(Spec.Width.inst.W)
                def mtlo    : UInt = "b010011".U(Spec.Width.inst.W)

                // R special
                def add     : UInt = "b100000".U(Spec.Width.inst.W)
                def addu    : UInt = "b100001".U(Spec.Width.inst.W)
                def sub     : UInt = "b100010".U(Spec.Width.inst.W)
                def subu    : UInt = "b100011".U(Spec.Width.inst.W)
                def slt     : UInt = "b101010".U(Spec.Width.inst.W)
                def sltu    : UInt = "b101011".U(Spec.Width.inst.W)
                // I
                def addi    : UInt = "b001000".U(Spec.Width.inst.W)
                def addiu   : UInt = "b001001".U(Spec.Width.inst.W)
                def slti    : UInt = "b001010".U(Spec.Width.inst.W)
                def sltiu   : UInt = "b001011".U(Spec.Width.inst.W)
                // R
                def clz     : UInt = "b100000".U(Spec.Width.inst.W)
                def clo     : UInt = "b100001".U(Spec.Width.inst.W)
                // R
                def mul     : UInt = "b000010".U(Spec.Width.inst.W)
                def mult    : UInt = "b011000".U(Spec.Width.inst.W)
                def multu   : UInt = "b011001".U(Spec.Width.inst.W)
                // R
                def jr      : UInt = "b001000".U(Spec.Width.inst.W)
                def jalr    : UInt = "b001001".U(Spec.Width.inst.W)
                // J
                def j       : UInt = "b000010".U(Spec.Width.inst.W)
                def jal     : UInt = "b000011".U(Spec.Width.inst.W)
                // I
                def beq     : UInt = "b000100".U(Spec.Width.inst.W)
                def bgtz    : UInt = "b000111".U(Spec.Width.inst.W)
                def blez    : UInt = "b000110".U(Spec.Width.inst.W)
                def bne     : UInt = "b000101".U(Spec.Width.inst.W)
                def bgez    : UInt = "b00001".U(Spec.Width.inst2.W)
                def bgezal  : UInt = "b10001".U(Spec.Width.inst2.W)
                def bltz    : UInt = "b00000".U(Spec.Width.inst2.W)
                def bltzal  : UInt = "b10000".U(Spec.Width.inst2.W)
                // I
                def lb      : UInt = "b100000".U(Spec.Width.inst.W)
                def lbu     : UInt = "b100100".U(Spec.Width.inst.W)
                def lh      : UInt = "b100001".U(Spec.Width.inst.W)
                def lhu     : UInt = "b100101".U(Spec.Width.inst.W)
                def ll      : UInt = "b110000".U(Spec.Width.inst.W)
                def lw      : UInt = "b100011".U(Spec.Width.inst.W)
                def lwl     : UInt = "b100010".U(Spec.Width.inst.W)
                def lwr     : UInt = "b100110".U(Spec.Width.inst.W)
                // I
                def sb      : UInt = "b101000".U(Spec.Width.inst.W)
                def sc      : UInt = "b111000".U(Spec.Width.inst.W)
                def sh      : UInt = "b101001".U(Spec.Width.inst.W)
                def sw      : UInt = "b101011".U(Spec.Width.inst.W)
                def swl     : UInt = "b101010".U(Spec.Width.inst.W)
                def swr     : UInt = "b101110".U(Spec.Width.inst.W)
                

                def sync: UInt = "b001111".U(Spec.Width.inst.W)
                def pref: UInt = "b110011".U(Spec.Width.inst.W)
                def special_inst : UInt = "b000000".U(Spec.Width.inst.W)
                def regimm_inst  : UInt = "b000001".U(Spec.Width.inst.W)
                def special2_inst: UInt = "b011100".U(Spec.Width.inst.W)
                def ssnop : UInt = "b00000000000000000000000001000000".U(32.W)
                def nop: UInt = "b000000".U(Spec.Width.inst.W)
            }
            
            object AluOp {
                def and     : UInt = "b00100100".U(Spec.Width.Alu.op.W)
                def or      : UInt = "b00100101".U(Spec.Width.Alu.op.W)
                def xor     : UInt = "b00100110".U(Spec.Width.Alu.op.W)
                def nor     : UInt = "b00100111".U(Spec.Width.Alu.op.W)
                def andi    : UInt = "b01011001".U(Spec.Width.Alu.op.W)
                def ori     : UInt = "b01011010".U(Spec.Width.Alu.op.W)
                def xori    : UInt = "b01011011".U(Spec.Width.Alu.op.W)
                def lui     : UInt = "b01011100".U(Spec.Width.Alu.op.W)

                def sll     : UInt = "b01111100".U(Spec.Width.Alu.op.W)
                def sllv    : UInt = "b00000100".U(Spec.Width.Alu.op.W)
                def srl     : UInt = "b00000010".U(Spec.Width.Alu.op.W)
                def srlv    : UInt = "b00000110".U(Spec.Width.Alu.op.W)
                def sra     : UInt = "b00000011".U(Spec.Width.Alu.op.W)
                def srav    : UInt = "b00000111".U(Spec.Width.Alu.op.W)

                def movz    : UInt = "b00001010".U(Spec.Width.Alu.op.W)
                def movn    : UInt = "b00001011".U(Spec.Width.Alu.op.W)
                def mfhi    : UInt = "b00010000".U(Spec.Width.Alu.op.W)
                def mthi    : UInt = "b00010001".U(Spec.Width.Alu.op.W)
                def mflo    : UInt = "b00010010".U(Spec.Width.Alu.op.W)
                def mtlo    : UInt = "b00010011".U(Spec.Width.Alu.op.W)

                def add     : UInt = "b00100000".U(Spec.Width.Alu.op.W)
                def addu    : UInt = "b00100001".U(Spec.Width.Alu.op.W)
                def sub     : UInt = "b00100010".U(Spec.Width.Alu.op.W)
                def subu    : UInt = "b00100011".U(Spec.Width.Alu.op.W)
                def slt     : UInt = "b00101010".U(Spec.Width.Alu.op.W)
                def sltu    : UInt = "b00101011".U(Spec.Width.Alu.op.W)
                def addi    : UInt = "b01010101".U(Spec.Width.Alu.op.W)
                def addiu   : UInt = "b01010110".U(Spec.Width.Alu.op.W)
                def slti    : UInt = "b01010111".U(Spec.Width.Alu.op.W)
                def sltiu   : UInt = "b01011000".U(Spec.Width.Alu.op.W)
                def clz     : UInt = "b10110000".U(Spec.Width.Alu.op.W)
                def clo     : UInt = "b10110001".U(Spec.Width.Alu.op.W)
                def mul     : UInt = "b10101001".U(Spec.Width.Alu.op.W)
                def mult    : UInt = "b00011000".U(Spec.Width.Alu.op.W)
                def multu   : UInt = "b00011001".U(Spec.Width.Alu.op.W)

                def j       : UInt = "b01001111".U(Spec.Width.Alu.op.W)
                def jal     : UInt = "b01010000".U(Spec.Width.Alu.op.W)
                def jalr    : UInt = "b00001001".U(Spec.Width.Alu.op.W)
                def jr      : UInt = "b00001000".U(Spec.Width.Alu.op.W)

                def beq     : UInt = "b01010001".U(Spec.Width.Alu.op.W)
                def bgez    : UInt = "b01010001".U(Spec.Width.Alu.op.W)
                def bgezal  : UInt = "b01010001".U(Spec.Width.Alu.op.W)
                def bgtz    : UInt = "b01010001".U(Spec.Width.Alu.op.W)
                def blez    : UInt = "b01010001".U(Spec.Width.Alu.op.W)
                def bltz    : UInt = "b01010001".U(Spec.Width.Alu.op.W)
                def bltzal  : UInt = "b01010001".U(Spec.Width.Alu.op.W)
                def bne     : UInt = "b01010001".U(Spec.Width.Alu.op.W)

                def lb      : UInt = "b11100000".U(Spec.Width.Alu.op.W)
                def lbu     : UInt = "b11100100".U(Spec.Width.Alu.op.W)
                def lh      : UInt = "b11100001".U(Spec.Width.Alu.op.W)
                def lhu     : UInt = "b11100101".U(Spec.Width.Alu.op.W)
                def ll      : UInt = "b11110000".U(Spec.Width.Alu.op.W)
                def lw      : UInt = "b11100011".U(Spec.Width.Alu.op.W)
                def lwl     : UInt = "b11100010".U(Spec.Width.Alu.op.W)
                def lwr     : UInt = "b11100110".U(Spec.Width.Alu.op.W)
                
                def sb      : UInt = "b11101000".U(Spec.Width.Alu.op.W)
                def sc      : UInt = "b11111000".U(Spec.Width.Alu.op.W)
                def sh      : UInt = "b11101001".U(Spec.Width.Alu.op.W)
                def sw      : UInt = "b11101011".U(Spec.Width.Alu.op.W)
                def swl     : UInt = "b11101010".U(Spec.Width.Alu.op.W)
                def swr     : UInt = "b11101110".U(Spec.Width.Alu.op.W)


                def nop     : UInt = "b00000000".U(Spec.Width.Alu.op.W)
            }

            object AluSel {
                def logic       : UInt = "b001".U(Spec.Width.Alu.sel.W)
                def shift       : UInt = "b010".U(Spec.Width.Alu.sel.W)
                def move        : UInt = "b011".U(Spec.Width.Alu.sel.W)
                def arithmetic  : UInt = "b100".U(Spec.Width.Alu.sel.W)
                def mul         : UInt = "b101".U(Spec.Width.Alu.sel.W)
                def jumpBranch  : UInt = "b110".U(Spec.Width.Alu.sel.W)
                def loadStore   : UInt = "b111".U(Spec.Width.Alu.sel.W)
                def nop         : UInt = "b000".U(Spec.Width.Alu.sel.W)
            }
        }
    }
}