import chisel3.stage._
import circt.stage.{CIRCTTarget, CIRCTTargetAnnotation}
import circt.stage
import mips._
import mips.components._

object Elaborate extends App {
  val useMFC    = true // use MLIR-based firrtl compiler
  val generator = Seq(chisel3.stage.ChiselGeneratorAnnotation(() => top))

  // def top = new DecoupledGcd(16)
  val debug = false
  def top = new Cpu(debug=debug)

  if (useMFC) {
    (new stage.ChiselStage)
      .execute(args, generator :+ CIRCTTargetAnnotation(CIRCTTarget.Verilog))
  } else {
    (new ChiselStage).execute(args, generator)
  }
}
