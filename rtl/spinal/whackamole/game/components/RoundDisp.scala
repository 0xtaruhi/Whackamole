/*
 * File: RoundDisp.scala
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Friday, 16th June 2023 7:04:08 pm
 * Last Modified: Saturday, 17th June 2023 10:44:59 am
 * Copyright: 2023 - 2023 Fudan University
 */
package whackamole.game.components

import spinal.core._
import spinal.lib._

import whackamole.graphics._
import whackamole.GameConfig

case class RoundDisp(config: GameConfig)
    extends Drawable(config.graphicsConfig) {
  def hSize: Int = 64
  def vSize: Int = 120

  val extraIo = new Bundle {
    val round   = in UInt (4 bits)
    val reqAddr = out UInt (18 bits)
    val reqData = in UInt (16 bits)
    val inArea  = out Bool ()
  }

  val curRoundNumber = Number64x32(
    color = (15, 35, 80),
    config = config.graphicsConfig
  )
  curRoundNumber.extraIo.number  := extraIo.round
  curRoundNumber.extraIo.reqData := extraIo.reqData
  curRoundNumber.io.hPos         := io.hPos
  curRoundNumber.io.vPos         := io.vPos
  curRoundNumber.io.startHPos    := io.startHPos
  curRoundNumber.io.startVPos    := io.startVPos

  val slash = Number64x32(
    color = (15, 35, 80),
    config = config.graphicsConfig
  )
  slash.extraIo.number  := 0xa
  slash.extraIo.reqData := extraIo.reqData
  slash.io.hPos         := io.hPos
  slash.io.vPos         := io.vPos
  slash.io.startHPos    := io.startHPos + 40
  slash.io.startVPos    := io.startVPos

  val totalRoundNumber = Number64x32(
    color = (15, 35, 80),
    config = config.graphicsConfig
  )
  totalRoundNumber.extraIo.number  := config.rounds.size
  totalRoundNumber.extraIo.reqData := extraIo.reqData
  totalRoundNumber.io.hPos         := io.hPos
  totalRoundNumber.io.vPos         := io.vPos
  totalRoundNumber.io.startHPos    := io.startHPos + 73
  totalRoundNumber.io.startVPos    := io.startVPos

  val rgb = Vec(UInt(8 bits), 3)

  when(curRoundNumber.extraIo.inArea) {
    extraIo.reqAddr := curRoundNumber.extraIo.reqAddr
    rgb             := curRoundNumber.io.info.rgb
  } elsewhen (slash.extraIo.inArea) {
    extraIo.reqAddr := slash.extraIo.reqAddr
    rgb             := slash.io.info.rgb
  } otherwise {
    extraIo.reqAddr := totalRoundNumber.extraIo.reqAddr
    rgb             := totalRoundNumber.io.info.rgb
  }

  def draw(): Vec[UInt] = rgb

  override def visible(): Bool = {
    curRoundNumber.io.info.visible || slash.io.info.visible || totalRoundNumber.io.info.visible
  }

  io.info.rgb     := draw()
  io.info.visible := visible()
  extraIo.inArea := curRoundNumber.extraIo.inArea || slash.extraIo.inArea || totalRoundNumber.extraIo.inArea
}
