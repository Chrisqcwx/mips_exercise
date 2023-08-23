package mips.bridges

import chisel3._
import chisel3.util._
import mips.Spec
import chisel3.experimental.BundleLiterals._
import chisel3.experimental.FlatIO

import scala.reflect.runtime.universe._

abstract class BridgeModule[PortT <: Bundle](bundleFactory : PortT) extends Module {
    val io = FlatIO(new Bundle {
        val input = Input(bundleFactory)
        val output = Output(bundleFactory)
        val stallPrev = Input(Bool())
        val stallNext = Input(Bool())
    })



    // def bundleFactory : PortT

    def defaultValue : PortT

    val bridgeReg = RegInit(defaultValue)
    when (io.stallPrev && !io.stallNext) {
        bridgeReg := defaultValue
    }.elsewhen (!io.stallPrev) {
        bridgeReg := io.input
    }

    io.output := bridgeReg

}