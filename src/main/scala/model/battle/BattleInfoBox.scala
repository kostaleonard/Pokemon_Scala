package model.battle

import java.awt.{Color, Font, Graphics2D}
import java.awt.image.BufferedImage

import model.elementaltype._
import model.pokemon.Pokemon
import model.statuseffect._
import view.gui.menu.BasicMenu
import view.views.drawing.Drawable

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
  val PERSISTENT_EFFECT_FONT: Font = new Font(Font.MONOSPACED, Font.PLAIN, 16)
  val PERSISTENT_EFFECT_FONT_COLOR: Color = Color.WHITE
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

  /** Returns true if HP is displayed. */
  def isHPDisplayed: Boolean = displayHP

  /** Returns true if XP is displayed. */
  def isXPDisplayed: Boolean = displayXP

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
    g2d.fillRect(60, 40, getObjectWidth - 65, 14)
    g2d.setColor(BattleInfoBox.HP_FONT_COLOR)
    g2d.setFont(BattleInfoBox.HP_FONT)
    g2d.drawString("HP", 63, 53)
    g2d.setColor(BattleInfoBox.HP_BACKGROUND_COLOR)
    val hpFullWidth = getObjectWidth - 107
    g2d.fillRect(100, 42, hpFullWidth, 10)
    g2d.setColor(BattleInfoBox.HP_FILL_COLOR)
    val hpActualWidth = hpFullWidth * pokemon.getCurrentStats.getHP / pokemon.getStandardStats.getHP
    g2d.fillRect(100, 42, hpActualWidth, 10)
    if(pokemon.getEffectTracker.getNonPersistentEffects.size > 1)
      println("Warning, attempting to display more than 1 persistent effect.")
    pokemon.getEffectTracker.getPersistentEffects.foreach{
      case Burn =>
        g2d.setColor(FireType.getTypeColor)
        g2d.fillRect(20, 40, 36, 14)
        g2d.setFont(BattleInfoBox.PERSISTENT_EFFECT_FONT)
        g2d.setColor(BattleInfoBox.PERSISTENT_EFFECT_FONT_COLOR)
        g2d.drawString("BRN", 23, 53)
      case Paralyze =>
        g2d.setColor(ElectricType.getTypeColor)
        g2d.fillRect(20, 40, 36, 14)
        g2d.setFont(BattleInfoBox.PERSISTENT_EFFECT_FONT)
        g2d.setColor(BattleInfoBox.PERSISTENT_EFFECT_FONT_COLOR)
        g2d.drawString("PAR", 23, 53)
      case Sleep(_) =>
        g2d.setColor(NormalType.getTypeColor)
        g2d.fillRect(20, 40, 36, 14)
        g2d.setFont(BattleInfoBox.PERSISTENT_EFFECT_FONT)
        g2d.setColor(BattleInfoBox.PERSISTENT_EFFECT_FONT_COLOR)
        g2d.drawString("SLP", 23, 53)
      case Frozen =>
        g2d.setColor(IceType.getTypeColor)
        g2d.fillRect(20, 40, 36, 14)
        g2d.setFont(BattleInfoBox.PERSISTENT_EFFECT_FONT)
        g2d.setColor(BattleInfoBox.PERSISTENT_EFFECT_FONT_COLOR)
        g2d.drawString("FRZ", 23, 53)
      case Poison(_, _) =>
        g2d.setColor(PoisonType.getTypeColor)
        g2d.fillRect(20, 40, 36, 14)
        g2d.setFont(BattleInfoBox.PERSISTENT_EFFECT_FONT)
        g2d.setColor(BattleInfoBox.PERSISTENT_EFFECT_FONT_COLOR)
        g2d.drawString("PSN", 23, 53)
    }
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
      val expFullWidth = getObjectWidth - 39
      g2d.setColor(BattleInfoBox.HP_BACKGROUND_COLOR)
      g2d.fillRect(37, getObjectHeight - 12, expFullWidth, 10)
      //TODO make this more efficient by reusing references?
      val expActualWidth = expFullWidth min (expFullWidth * pokemon.getLevelTracker.getExperienceAtCurrentLevel /
        (pokemon.getLevelTracker.getTotalExperienceForNextLevel -
          pokemon.getLevelTracker.getTotalExperienceForCurrentLevel))
      g2d.setColor(BattleInfoBox.XP_FILL_COLOR)
      g2d.fillRect(37, getObjectHeight - 12, expActualWidth, 10)
    }
    g2d.dispose()
    canvasImage
  }

  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = None
}
