package view.gui.menu

import view.Drawable

import scala.collection.mutable.ArrayBuffer

object Menu{
  val DEFAULT_WIDTH = 300
  val DEFAULT_HEIGHT = 300
}

abstract class Menu[A <: MenuItem] extends Drawable {
  protected val menuItems: ArrayBuffer[A] = ArrayBuffer.empty[A]
  protected var width: Int = Menu.DEFAULT_WIDTH
  protected var height: Int = Menu.DEFAULT_HEIGHT
  protected var selectedMenuItem = 0
  protected var isActive = true
  //TODO do we need visibility settings?
  //protected var isVisible = true

  /** Returns the menu items. */
  def getMenuItems: ArrayBuffer[A] = menuItems

  /** Adds a menu item to the menu. */
  def appendMenuItem(menuItem: A): Unit = menuItems.append(menuItem)

  /** Removes a menu item from the menu. */
  def removeMenuItem(index: Int): A = menuItems.remove(index)

  /** If the menu is active, calls the selected menu item's GuiAction function. */
  def makeSelection(): Unit = if(isActive) menuItems(selectedMenuItem).guiAction.functionToCall()

  /** Sets the selected menu item to the next selectable menu item. */
  def scrollDown(): Unit = if(isActive && selectedMenuItem < menuItems.length - 1){
    val nextSelectableIndex = menuItems.indices.find{ i => i > selectedMenuItem && menuItems(i).isSelectable }
    nextSelectableIndex.map(index => selectedMenuItem = index)
  }

  /** Sets the selected menu item to the previous selectable menu item. */
  def scrollUp(): Unit = if(isActive && selectedMenuItem > 0){
    val nextSelectableIndex = menuItems.indices.reverse.find{ i => i < selectedMenuItem && menuItems(i).isSelectable }
    nextSelectableIndex.map(index => selectedMenuItem = index)
  }
}
