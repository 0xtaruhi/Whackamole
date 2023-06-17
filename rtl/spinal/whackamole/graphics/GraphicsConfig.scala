/*
 * File: GraphicsConfig.scala
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Tuesday, 13th June 2023 6:51:13 pm
 * Last Modified: Saturday, 17th June 2023 10:47:09 am
 * Copyright: 2023 - 2023 Fudan University
 */
package whackamole.graphics

import spinal.core._

case class GraphicsConfig(
  val width: Int = 640,
  val height: Int = 480
) {
  def widthBits = log2Up(width)
  def heightBits = log2Up(height)
}
