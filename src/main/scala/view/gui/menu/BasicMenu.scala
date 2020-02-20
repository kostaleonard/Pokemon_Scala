package view.gui.menu

import java.awt.image.BufferedImage
import java.awt.{Color, Font, Graphics2D}

import scala.collection.mutable.ArrayBuffer

object BasicMenu{
  val DEFAULT_TITLE_FONT = new Font(Font.MONOSPACED, Font.BOLD, 50)
  val DEFAULT_TITLE_FONT_COLOR: Color = Color.BLACK
  val DEFAULT_MENUITEM_FONT = new Font(Font.MONOSPACED, Font.BOLD, 30)
  val DEFAULT_MENUITEM_FONT_COLOR: Color = Color.BLACK
  val DEFAULT_MENU_BACKGROUND_COLOR = new Color(80, 80, 80)
  val DEFAULT_HIGHLIGHT_COLOR: Color = Color.YELLOW
  val DEFAULT_ACCENT_COLOR: Color = Color.BLUE.brighter()
  val DEFAULT_BORDER_COLOR: Color = Color.BLACK
  val DEFAULT_BORDER_THICKNESS = 3
  val DEFAULT_TITLE_SEPARATOR_THICKNESS = 2
  val DEFAULT_NONSELECTABLE_MENUITEM_COLOR: Color = Color.GRAY.brighter()
}

class BasicMenu extends Menu[BasicMenuItem] {
  override protected val menuItems: ArrayBuffer[BasicMenuItem] = ArrayBuffer.empty[BasicMenuItem]
  width = Menu.DEFAULT_WIDTH
  height = Menu.DEFAULT_HEIGHT
  protected var titleFont: Font = BasicMenu.DEFAULT_TITLE_FONT
  protected var titleFontColor: Color = BasicMenu.DEFAULT_TITLE_FONT_COLOR
  protected var menuItemFont: Font = BasicMenu.DEFAULT_MENUITEM_FONT
  protected var menuItemFontColor: Color = BasicMenu.DEFAULT_MENUITEM_FONT_COLOR
  protected var menuBackgroundColor: Color = BasicMenu.DEFAULT_MENU_BACKGROUND_COLOR
  protected var highlightColor: Color = BasicMenu.DEFAULT_HIGHLIGHT_COLOR
  protected var accentColor: Color = BasicMenu.DEFAULT_ACCENT_COLOR
  protected var borderColor: Color = BasicMenu.DEFAULT_BORDER_COLOR
  protected var borderThickness: Int = BasicMenu.DEFAULT_BORDER_THICKNESS
  protected var titleSeparatorThickness: Int = BasicMenu.DEFAULT_TITLE_SEPARATOR_THICKNESS
  protected var nonSelectableMenuItemColor: Color = BasicMenu.DEFAULT_NONSELECTABLE_MENUITEM_COLOR
  protected var titleDisplayed = true
  /** Will supersede this.height */
  protected var wrapContentHeight = true
  /** Will supersede this.width */
  protected var wrapContentWidth = false
  protected var titleString = "MENU"
  protected var canvasImage: BufferedImage = new BufferedImage(getObjectWidth, getObjectHeight,
    BufferedImage.TYPE_INT_ARGB)

  /** Changes the menu's title string. */
  def setTitleString(title: String): Unit = titleString = title

  /** Sets whether the title is displayed. */
  def setTitleDisplayed(b: Boolean): Unit = {
    titleDisplayed = b
    canvasImage = new BufferedImage(getObjectWidth, getObjectHeight, BufferedImage.TYPE_INT_ARGB)
  }

  /** Sets the menu's width. */
  def setWidth(w: Int): Unit ={
    width = w
    canvasImage = new BufferedImage(getObjectWidth, getObjectHeight, BufferedImage.TYPE_INT_ARGB)
  }

  /** Sets the menu's height. */
  def setHeight(h: Int): Unit = {
    height = h
    canvasImage = new BufferedImage(getObjectWidth, getObjectHeight, BufferedImage.TYPE_INT_ARGB)
  }

  /** Returns the width of the menu. */
  override def getObjectWidth: Int = if(wrapContentWidth) getWrappedWidth else width

  /** Returns the height of the menu. */
  override def getObjectHeight: Int = if(wrapContentHeight) getWrappedHeight else height

  /** Adds a menu item to the menu. */
  override def appendMenuItem(menuItem: BasicMenuItem): Unit = {
    super.appendMenuItem(menuItem)
    canvasImage = new BufferedImage(getObjectWidth, getObjectHeight, BufferedImage.TYPE_INT_ARGB)
  }

  /** Removes a menu item from the menu. */
  override def removeMenuItem(index: Int): BasicMenuItem = {
    val result = super.removeMenuItem(index)
    canvasImage = new BufferedImage(getObjectWidth, getObjectHeight, BufferedImage.TYPE_INT_ARGB)
    result
  }

  /** Returns the width of the menu when the menu is wrapped to fit the content width. */
  def getWrappedWidth: Int = {
    val buffer = borderThickness * 4
    val g2d = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB).getGraphics.asInstanceOf[Graphics2D]
    val titleStringWidth = g2d.getFontMetrics(titleFont).stringWidth(titleString)
    val menuItemFontMetrics = g2d.getFontMetrics(menuItemFont)
    val allStringsInMenuItems = menuItems.map(_.text)
    val longestStringWidth = if(menuItems.isEmpty) 0 else allStringsInMenuItems.map(str => menuItemFontMetrics.stringWidth(str)).max
    (titleStringWidth max longestStringWidth) + buffer
  }

  /** Returns the height of the menu when the menu is wrapped to fit the content height. */
  def getWrappedHeight: Int = {
    val buffer = 0 //TODO borderThickness * 2?
    val g2d = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB).getGraphics.asInstanceOf[Graphics2D]
    val titleStringHeight = if(titleDisplayed) g2d.getFontMetrics(titleFont).getHeight else 0
    val menuItemStringHeight = g2d.getFontMetrics(menuItemFont).getHeight * menuItems.length
    titleStringHeight + menuItemStringHeight + buffer
  }

  /** Determines whether the menu height wraps the content or uses its static value. */
  def setWrapContentHeight(b: Boolean): Unit = wrapContentHeight = b

  /** Determines whether the menu width wraps the content or uses its static value. */
  def setWrapContentWidth(b: Boolean): Unit = wrapContentWidth = b

  /** Returns the menu's BufferedImage. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.setColor(borderColor)
    g2d.fillRect(0, 0, getObjectWidth, getObjectHeight)
    g2d.setColor(menuBackgroundColor)
    g2d.fillRect(borderThickness, borderThickness, getObjectWidth - 2 * borderThickness,
      getObjectHeight - 2 * borderThickness)
    val titleHeight = if(titleDisplayed) g2d.getFontMetrics(titleFont).getHeight else 0
    if(titleDisplayed) {
      g2d.setColor(titleFontColor)
      g2d.setFont(titleFont)
      g2d.drawString(titleString, borderThickness * 2, (titleHeight * 3) / 4)
    }
    g2d.setColor(borderColor)
    g2d.fillRect(0, titleHeight, getObjectWidth, titleSeparatorThickness)
    val heightStartMenuItems = titleHeight
    val menuItemHeight = g2d.getFontMetrics(menuItemFont).getHeight
    g2d.setFont(menuItemFont)
    menuItems.indices.foreach { i =>
      val menuItem = menuItems(i)
      val heightStartThisMenuItem = heightStartMenuItems + menuItemHeight * i
      if (selectedMenuItem == i && isActive) {
        g2d.setColor(highlightColor)
        if (i == 0) g2d.fillRect(borderThickness, heightStartThisMenuItem + borderThickness,
            getObjectWidth - 2 * borderThickness, menuItemHeight - borderThickness)
        else if (i == menuItems.length - 1) g2d.fillRect(borderThickness, heightStartThisMenuItem,
          getObjectWidth - 2 * borderThickness, menuItemHeight - borderThickness)
        else g2d.fillRect(borderThickness, heightStartThisMenuItem, getObjectWidth - 2 * borderThickness,
          menuItemHeight)
      }
      if (menuItem.isSelectable) g2d.setColor(menuItemFontColor)
      else g2d.setColor(nonSelectableMenuItemColor)
      g2d.drawString(menuItem.text, borderThickness * 2, heightStartThisMenuItem + (menuItemHeight * 3) / 4)
    }
    g2d.dispose()
    canvasImage
  }

  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = None
}
