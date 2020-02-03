package model.battle

import java.awt.{Color, Font, Graphics2D}
import java.awt.image.BufferedImage

import model.pokemon.Pokemon
import view.Drawable
import view.gui.menu.BasicMenu

object BattleInfoBox {
  val SINGLE_OPPONENT_OFFSET_X = 20
  val SINGLE_OPPONENT_OFFSET_Y = 10
  val SINGLE_PLAYER_OFFSET_X = 540
  val SINGLE_PLAYER_OFFSET_Y = 355
  val BASE_SIZE_X = 400
  val BASE_SIZE_Y = 60
  val HP_SIZE = 14
  val XP_SIZE = 14
  val DEFAULT_BACKGROUND_COLOR: Color = BasicMenu.DEFAULT_MENU_BACKGROUND_COLOR
  val DEFAULT_BORDER_COLOR: Color = BasicMenu.DEFAULT_BORDER_COLOR
  val BORDER_THICKNESS = 2
  val DEFAULT_FONT: Font = BasicMenu.DEFAULT_MENUITEM_FONT
  val DEFAULT_FONT_COLOR: Color = BasicMenu.DEFAULT_MENUITEM_FONT_COLOR
  val HP_FONT: Font = new Font(Font.MONOSPACED, Font.BOLD, 16)
  val HP_FONT_COLOR: Color = Color.YELLOW.darker()
  val HP_BACKGROUND_COLOR: Color = Color.GRAY
  val HP_FILL_COLOR: Color = Color.GREEN.darker()
  val XP_FONT_COLOR: Color = Color.YELLOW.darker()
  val XP_BACKGROUND_COLOR: Color = Color.GRAY
  val XP_FILL_COLOR: Color = Color.CYAN.darker()
}

class BattleInfoBox(pokemon: Pokemon, isPlayerPokemon: Boolean) extends Drawable {
  protected val displayHP: Boolean = isPlayerPokemon
  protected val displayXP: Boolean = isPlayerPokemon
  protected val width: Int = BattleInfoBox.BASE_SIZE_X
  protected val height: Int = BattleInfoBox.BASE_SIZE_Y +
    (if(displayHP) BattleInfoBox.HP_SIZE else 0) +
    (if(displayXP) BattleInfoBox.XP_SIZE else 0)
  protected val canvasImage: BufferedImage = new BufferedImage(getObjectWidth, getObjectHeight,
    BufferedImage.TYPE_INT_ARGB)

  /** Returns the object's width. */
  override def getObjectWidth: Int = width

  /** Returns the object's height. */
  override def getObjectHeight: Int = height

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.setColor(BattleInfoBox.DEFAULT_BACKGROUND_COLOR)
    g2d.fillRect(0, 0, getObjectWidth, getObjectHeight)
    g2d.setColor(BattleInfoBox.DEFAULT_BORDER_COLOR)
    g2d.fillRect(0, 0, getObjectWidth, BattleInfoBox.BORDER_THICKNESS)
    g2d.fillRect(0, 0, BattleInfoBox.BORDER_THICKNESS, getObjectHeight)
    g2d.fillRect(0, getObjectHeight - BattleInfoBox.BORDER_THICKNESS, getObjectWidth, BattleInfoBox.BORDER_THICKNESS)
    g2d.fillRect(getObjectWidth - BattleInfoBox.BORDER_THICKNESS, 0, BattleInfoBox.BORDER_THICKNESS, getObjectHeight)
    g2d.setFont(BattleInfoBox.DEFAULT_FONT)
    g2d.setColor(BattleInfoBox.DEFAULT_FONT_COLOR)
    g2d.drawString("%s".format(pokemon.getName), BattleInfoBox.BORDER_THICKNESS * 2, 30)
    val levelWidth = g2d.getFontMetrics(BattleInfoBox.DEFAULT_FONT).stringWidth(pokemon.getLevel.toString)
    g2d.drawString("Lv%d".format(pokemon.getLevel), BattleInfoBox.BASE_SIZE_X - 42 - levelWidth, 30)
    g2d.setColor(pokemon.getGenderColor)
    g2d.drawString(" " * pokemon.getName.length + pokemon.getGenderSign, BattleInfoBox.BORDER_THICKNESS * 2, 30)
    g2d.setColor(Color.BLACK)
    g2d.fillRect(20, 40, getObjectWidth - 25, 14)
    g2d.setColor(BattleInfoBox.HP_FONT_COLOR)
    g2d.setFont(BattleInfoBox.HP_FONT)
    g2d.drawString("HP", 23, 53)
    g2d.setColor(BattleInfoBox.HP_BACKGROUND_COLOR)
    g2d.fillRect(60, 42, getObjectWidth - 67, 10)
    g2d.setColor(BattleInfoBox.HP_FILL_COLOR)
    //TODO fill HP bar proportional to actual HP
    g2d.fillRect(60, 42, getObjectWidth - 67, 10)
    if(displayHP) {
      g2d.setColor(BattleInfoBox.DEFAULT_FONT_COLOR)
      val hpString = "%d/%d".format(pokemon.getCurrentStats.getHP, pokemon.getStandardStats.getHP)
      val hpWidth = g2d.getFontMetrics(BattleInfoBox.HP_FONT).stringWidth(hpString)
      g2d.drawString(hpString, BattleInfoBox.BASE_SIZE_X - hpWidth - 6, 68)
    }
    if(displayXP) {
      g2d.setColor(Color.BLACK)
      g2d.fillRect(0, getObjectHeight - 14, getObjectWidth, 14)
      g2d.setColor(BattleInfoBox.HP_FONT_COLOR)
      g2d.drawString("EXP", BattleInfoBox.BORDER_THICKNESS * 2, 87)
      g2d.setColor(BattleInfoBox.HP_BACKGROUND_COLOR)
      g2d.fillRect(37, getObjectHeight - 12, getObjectWidth - 39, 10)
      //TODO fill EXP bar proportional to actual EXP
      g2d.setColor(BattleInfoBox.XP_FILL_COLOR)
      g2d.fillRect(37, getObjectHeight - 12, 100, 10)
    }
    g2d.dispose()
    canvasImage
  }

  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = None

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = {}
}
