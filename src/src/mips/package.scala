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

            object Alu {
                val op = 8
                val sel = 3
            }

            object Rom {
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

        object Op {
            object Inst {
                def and : UInt = "b100100".U(Spec.Width.inst.W)
                def or  : UInt = "b100101".U(Spec.Width.inst.W)
                def xor : UInt = "b100110".U(Spec.Width.inst.W)
                def nor : UInt = "b100111".U(Spec.Width.inst.W)
                def andi: UInt = "b001100".U(Spec.Width.inst.W)
                def ori : UInt = "b001101".U(Spec.Width.inst.W)
                def xori: UInt = "b001110".U(Spec.Width.inst.W)
                def lui : UInt = "b001111".U(Spec.Width.inst.W)

                def sll : UInt = "b000000".U(Spec.Width.inst.W)
                def sllv: UInt = "b000100".U(Spec.Width.inst.W)
                def srl : UInt = "b000010".U(Spec.Width.inst.W)
                def srlv: UInt = "b000110".U(Spec.Width.inst.W)
                def sra : UInt = "b000011".U(Spec.Width.inst.W)
                def srav: UInt = "b000111".U(Spec.Width.inst.W)

                def movz: UInt = "b001010".U(Spec.Width.inst.W)
                def movn: UInt = "b001011".U(Spec.Width.inst.W)
                def mfhi: UInt = "b010000".U(Spec.Width.inst.W)
                def mthi: UInt = "b010001".U(Spec.Width.inst.W)
                def mflo: UInt = "b010010".U(Spec.Width.inst.W)
                def mtlo: UInt = "b010011".U(Spec.Width.inst.W)

                def sync: UInt = "b001111".U(Spec.Width.inst.W)
                def pref: UInt = "b110011".U(Spec.Width.inst.W)
                def special_inst : UInt = "b000000".U(Spec.Width.inst.W)
                def regimm_inst  : UInt = "b000001".U(Spec.Width.inst.W)
                def special2_inst: UInt = "b011100".U(Spec.Width.inst.W)
                def ssnop : UInt = "b00000000000000000000000001000000".U(32.W)
                def nop: UInt = "b000000".U(Spec.Width.inst.W)
            }
            
            object AluOp {
                def and : UInt = "b00100100".U(Spec.Width.Alu.op.W)
                def or  : UInt = "b00100101".U(Spec.Width.Alu.op.W)
                def xor : UInt = "b00100110".U(Spec.Width.Alu.op.W)
                def nor : UInt = "b00100111".U(Spec.Width.Alu.op.W)
                def andi: UInt = "b01011001".U(Spec.Width.Alu.op.W)
                def ori : UInt = "b01011010".U(Spec.Width.Alu.op.W)
                def xori: UInt = "b01011011".U(Spec.Width.Alu.op.W)
                def lui : UInt = "b01011100".U(Spec.Width.Alu.op.W)

                def sll : UInt = "b01111100".U(Spec.Width.Alu.op.W)
                def sllv: UInt = "b00000100".U(Spec.Width.Alu.op.W)
                def srl : UInt = "b00000010".U(Spec.Width.Alu.op.W)
                def srlv: UInt = "b00000110".U(Spec.Width.Alu.op.W)
                def sra : UInt = "b00000011".U(Spec.Width.Alu.op.W)
                def srav: UInt = "b00000111".U(Spec.Width.Alu.op.W)

                def movz: UInt = "b00001010".U(Spec.Width.Alu.op.W)
                def movn: UInt = "b00001011".U(Spec.Width.Alu.op.W)
                def mfhi: UInt = "b00010000".U(Spec.Width.Alu.op.W)
                def mthi: UInt = "b00010001".U(Spec.Width.Alu.op.W)
                def mflo: UInt = "b00010010".U(Spec.Width.Alu.op.W)
                def mtlo: UInt = "b00010011".U(Spec.Width.Alu.op.W)

                def nop : UInt = "b00000000".U(Spec.Width.Alu.op.W)
            }

            object AluSel {
                def logic: UInt = "b001".U(Spec.Width.Alu.sel.W)
                def shift: UInt = "b010".U(Spec.Width.Alu.sel.W)
                def move : UInt = "b011".U(Spec.Width.Alu.sel.W)
                def nop  : UInt = "b000".U(Spec.Width.Alu.sel.W)
            }
        }
    }
}