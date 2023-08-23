import chisel3.stage._
import circt.stage.{CIRCTTarget, CIRCTTargetAnnotation}
import circt.stage
import mips._
import mips.components._

object Elaborate extends App {
  val useMFC    = false // use MLIR-based firrtl compiler
  val generator = Seq(chisel3.stage.ChiselGeneratorAnnotation(() => top))

  // def top = new DecoupledGcd(16)
  val debug = false
  def top = new fpu(debug=debug)
  // def top = new Fpga

  if (useMFC) {
    (new stage.ChiselStage)
    // .execute(args, generator)
      .execute(args, generator :+ CIRCTTargetAnnotation(CIRCTTarget.Verilog))
  } else {
    (new ChiselStage).execute(args, generator)
  }
}
