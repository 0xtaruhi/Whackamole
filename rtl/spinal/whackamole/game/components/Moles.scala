package whackamole.game.components

import spinal.core._
import spinal.lib._
import whackamole.graphics._
import whackamole._

import whackamole.GameConstants._

case class Mole(config: GameConfig = GameConfig())
    extends Drawable(config.graphicsConfig) {

  val extraIo = new Bundle {
    val press   = in Bool ()
    val appear  = in Bool ()
    val memAddr = out UInt (18 bits)
    val memData = in UInt (16 bits)
    val inArea  = out Bool ()
    val hit     = out Bool ()
  }

  // The following register is used to indicate whether
  // the mole has been hit before.
  // It will be reset to False when the appear signal is asserted.
  val prevHit = RegInit(False)
  when(extraIo.appear.rise()) {
    prevHit := False
  }

  when(!prevHit && extraIo.appear && extraIo.press) {
    extraIo.hit := True
    prevHit     := True
  } otherwise {
    extraIo.hit := False
  }

  def hSize: Int =
    (config.gameAreaWidth - 2 * config.gameAreaBorderThickness) / 4 - 2 * config.moleHalfGap
  def vSize: Int = hSize

  def draw(): Vec[UInt] = {
    val result = Vec(UInt(8 bits), 3)
    result(0) := extraIo.memData(4 downto 0) @@ U"b000"
    result(1) := extraIo.memData(9 downto 5) @@ U"b000"
    result(2) := extraIo.memData(14 downto 10) @@ U"b000"
    when(extraIo.press) {
      result(0) := 0
    }
    result
  }

  val inArea = super.visible
  extraIo.inArea := inArea

  val addrCounter = Counter(moleHeight * moleWidth, inArea)
  val addrPrefix  = UInt(4 bits)

  addrPrefix := Mux(
    extraIo.appear,
    U(moleshowAddrPrefix, 4 bits),
    U(molehideAddrPrefix, 4 bits)
  )

  extraIo.memAddr := addrPrefix @@ addrCounter.value.resize(14 bits)

  override def visible(): Bool = {
    inArea && extraIo.memData(15)
  }

  io.info.rgb     := draw()
  io.info.visible := visible()
}

case class Moles(config: GameConfig = GameConfig())
    extends Drawable(config.graphicsConfig) {

  val extraIo = new Bundle {
    val keyPress        = in Bool ()
    val keyIndex        = in UInt (4 bits)
    val moleAppear      = in Bool ()
    val moleAppearIndex = in UInt (4 bits)
    val memAddr         = out UInt (18 bits)
    val memData         = in UInt (16 bits)
    val curPosAppear    = out Bool ()
    val inArea          = out Bool ()
    val hit             = out Bool ()
  }

  val moles = Seq.fill(16)(Mole())

  val moleAppearVecs = Vec(Bool(), 16)
  for (i <- 0 until 16) {
    moleAppearVecs(i) := extraIo.moleAppear && extraIo.moleAppearIndex === i
  }

  val rawGameAreaWidth = config.gameAreaWidth - 2 * config.moleHalfGap

  for {
    i <- 0 until 4
    j <- 0 until 4
  } {
    val moleIdx = i * 4 + j
    moles(moleIdx).extraIo.appear := moleAppearVecs(moleIdx)
    moles(
      moleIdx
    ).io.startHPos := j * (rawGameAreaWidth / 4) + config.moleHalfGap + config.gameAreaBorderThickness
    moles(
      moleIdx
    ).io.startVPos := i * (rawGameAreaWidth / 4) + config.moleHalfGap + config.gameAreaBorderThickness
  }

  for (mole <- moles) {
    mole.io.hPos := hPos
    mole.io.vPos := vPos
  }

  when(extraIo.keyPress) {
    for (i <- 0 until moles.size) {
      when(U(i) === extraIo.keyIndex) {
        moles(i).extraIo.press := True
      } otherwise {
        moles(i).extraIo.press := False
      }
    }
  } otherwise {
    moles.foreach(_.extraIo.press := False)
  }

  extraIo.hit := moles.map(_.extraIo.hit).orR

  def hSize: Int = config.gameAreaWidth - 2 * config.gameAreaBorderThickness
  def vSize: Int =
    config.graphicsConfig.height - 2 * config.gameAreaBorderThickness

  val visibleBits = moles.map(_.io.info.visible).asBits
  val inAreaBits  = moles.map(_.extraIo.inArea).asBits
  extraIo.inArea := inAreaBits.orR

  def draw(): Vec[UInt] = {
    val result = Vec(UInt(8 bits), 3)
    result := MuxOH(
      visibleBits,
      moles.map(_.io.info.rgb)
    )
    result
  }

  override def visible(): Bool = {
    moles.map(_.io.info.visible).orR
  }

  extraIo.memAddr := MuxOH(inAreaBits, moles.map(_.extraIo.memAddr))
  moles.foreach { x => x.extraIo.memData := extraIo.memData }

  io.info.rgb     := draw()
  io.info.visible := visibleBits.orR
}
