package whackamole

import whackamole.graphics._

import spinal.core._

case class RoundInfo(
    val molesNum: Int,
    val points: Int,
    val appearTime: TimeNumber = 0.8 sec,
    val minTimeBeforeAppear: TimeNumber = 1 sec
) {
  def maxFloatingTime     = 3 sec
  def maxTimeBeforeAppear = minTimeBeforeAppear + maxFloatingTime
}

case class GameConfig(
    val frequency: HertzNumber = 2 MHz,
    val updateFrequency: HertzNumber = 1000 Hz,
    val graphicsConfig: GraphicsConfig = GraphicsConfig(),
    val gameAreaWidth: Int = 480,
    val moleHalfGap: Int = 10,
    val gameAreaBorderThickness: Int = 10,
    val scoreAreaBorderThickness: Int = 10,
    val rounds: Seq[RoundInfo] = Seq(
      RoundInfo(5, 1, 5 sec, 1 sec),
      RoundInfo(5, 3, 5 sec, 1 sec),
      RoundInfo(10, 7, 5 sec, 1 sec)
    ),
    val roundGapTime: TimeNumber = 3 sec,
    val scoreDigits: Int = 2
) {
  val maxScoreIncr          = rounds.map(_.points).max
  val maxChildRound         = rounds.map(_.molesNum).max
  val maxCyclesBeforeAppear =
    rounds
      .map(a => toUpdateCyclesNum(a.maxTimeBeforeAppear))
      .max

  val maxFloatingCycles =
    rounds.map(_.maxFloatingTime).map(toUpdateCyclesNum).max

  val maxAppearCycles = 
    rounds.map(_.appearTime).map(toUpdateCyclesNum).max

  def toCyclesNum(time: TimeNumber): BigInt = {
    (frequency * time).toBigInt()
  }

  def toUpdateCyclesNum(time: TimeNumber): BigInt = {
    (updateFrequency * time).toBigInt()
  }
}
