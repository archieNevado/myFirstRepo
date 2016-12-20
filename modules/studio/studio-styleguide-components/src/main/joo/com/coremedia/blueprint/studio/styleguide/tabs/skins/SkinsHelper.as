package com.coremedia.blueprint.studio.styleguide.tabs.skins {
import com.coremedia.blueprint.studio.styleguide.StyleguideSkin;
import com.coremedia.blueprint.studio.styleguide.templates.SkinTemplate;
import com.coremedia.ui.skins.BaseSkin;
import com.coremedia.ui.skins.ButtonSkin;
import com.coremedia.ui.skins.CheckboxSkin;
import com.coremedia.ui.skins.ContainerSkin;
import com.coremedia.ui.skins.DisplayFieldSkin;
import com.coremedia.ui.skins.TableViewSkin;
import com.coremedia.ui.skins.IconDisplayFieldSkin;
import com.coremedia.ui.skins.PanelSkin;
import com.coremedia.ui.skins.SliderSkin;
import com.coremedia.ui.skins.TabBarSkin;
import com.coremedia.ui.skins.TextfieldSkin;
import com.coremedia.ui.skins.ToolbarSkin;

import ext.Ext;
import ext.dom.Element;
import ext.panel.Panel;

public class SkinsHelper {

  public static const SCALE_SMALL:String = 'small';
  public static const SCALE_MEDIUM:String = 'medium';
  public static const SCALE_LARGE:String = 'large';

  // Skin category DARK background. Use with getId() oder getSkinGroup()
  public static const DARK_CONTAINER:Array = [
    ButtonSkin.TOOLBAR.getId(),
    ButtonSkin.TOOLBAR_GROUPED.getId(),
    ButtonSkin.INVERTED.getId(),
    ButtonSkin.SIMPLE_INVERTED.getId(),
    ButtonSkin.FOOTER_PRIMARY.getId(),
    ButtonSkin.FOOTER_SECONDARY.getId(),
    ButtonSkin.USER_MENU.getId(),
    CheckboxSkin.DEFAULT.getId(),
    CheckboxSkin.INVERTED.getId(),
    PanelSkin.EMBEDDED.getId(),
    PanelSkin.SPECIAL_WELCOME_INNER.getId(),
    PanelSkin.SPECIAL_WELCOME_OUTER.getId(),
    ContainerSkin.DEFAULT.getId(),
    ContainerSkin.GRID_200.getId(),
    TableViewSkin.DEFAULT.getId(),
    IconDisplayFieldSkin.DEFAULT.getSkinGroup()
  ];

  // Skin category MEDIUM background. Use with getId() oder getSkinGroup()
  public static const MEDIUM_CONTAINER:Array = [
    DisplayFieldSkin.DEFAULT.getSkinGroup(),
    TabBarSkin.DEFAULT.getSkinGroup(),
    SliderSkin.DEFAULT.getSkinGroup(),
    TextfieldSkin.DEFAULT.getSkinGroup(),
    IconDisplayFieldSkin.DEFAULT.getId(),
    IconDisplayFieldSkin.INFO.getId()
  ];

  // Buttons backgrounds (not for toggleable)
  private static const DARK_BUTTONS:Array = [
    ToolbarSkin.SIDE.getId(),
    ToolbarSkin.MAIN_NAVIGATION.getId(),
    ToolbarSkin.WORKAREA.getId(),
    ToolbarSkin.WIDGET_HEADER.getId(),
    ToolbarSkin.WIDGET_HEADER_HIGHLIGHTED.getId(),
    ToolbarSkin.WINDOW_HEADER.getId()
  ];

  // SMALL BUTTONS
  private static const SMALL_BUTTONS:Array = [
    ButtonSkin.DEFAULT.getId(),
    ButtonSkin.TOOLBAR.getId(),
    ButtonSkin.TOOLBAR_GROUPED.getId(),
    ButtonSkin.INVERTED.getId(),
    ButtonSkin.SIMPLE.getId(),
    ButtonSkin.SIMPLE_INVERTED.getId(),
    ButtonSkin.FOOTER_PRIMARY.getId(),
    ButtonSkin.FOOTER_SECONDARY.getId(),
    ButtonSkin.INLINE_PRIMARY.getId(),
    ButtonSkin.INLINE_SECONDARY.getId(),
    ButtonSkin.VIVID.getId(),
    ButtonSkin.USER_MENU.getId(),
    ButtonSkin.BREADCRUMB.getId(),
    ButtonSkin.LINK.getId(),
    ButtonSkin.VIVID_TOOLBAR.getId(),
    ButtonSkin.UPLOAD.getId(),
    ButtonSkin.PREVIEW_TOOLBAR.getId(),
    ButtonSkin.PREVIEW_TOOLBAR_GROUPED.getId()
  ];

  // MEDIUM BUTTONS
  private static const MEDIUM_BUTTONS:Array = [
    ButtonSkin.DEFAULT.getId(),
    ButtonSkin.INVERTED.getId(),
    ButtonSkin.INVERTED_TOGGLE.getId(),
    ButtonSkin.WORKAREA.getId(),
    ButtonSkin.LOGO.getId()
  ];

  // LARGE BUTTONS
  private static const LARGE_BUTTONS:Array = [
    ButtonSkin.DEFAULT.getId()
  ];


  // MISSING BUTTONS
  private static const MISSING_BTN_SCALES:Array = [];


  public static function hasButtonScale(skinEnum:BaseSkin, scale:String):Boolean {
    var largeBtn:int = LARGE_BUTTONS.indexOf(skinEnum.getId());
    var mediumBtn:int = MEDIUM_BUTTONS.indexOf(skinEnum.getId());
    var smallBtn:int = SMALL_BUTTONS.indexOf(skinEnum.getId());
    if (largeBtn >= 0 && scale === SCALE_LARGE) {
      return true;
    } else if (mediumBtn >= 0 && scale === SCALE_MEDIUM) {
      return true;
    } else if (smallBtn >= 0 && scale === SCALE_SMALL) {
      return true;
    }

    if (largeBtn < 0 && mediumBtn < 0 && smallBtn < 0 && MISSING_BTN_SCALES.indexOf(skinEnum.getId()) < 0) {
      window.console.error('The scale ', scale, ' of the button skin', skinEnum.getSkin(), 'is not specified within the SkinsHelper.');
      MISSING_BTN_SCALES.push(skinEnum.getId());
    }
    return false;
  }

  public static function getContainerUi(skinEnum:BaseSkin):String {
    if (DARK_CONTAINER.indexOf(skinEnum.getId()) >= 0) {
      return StyleguideSkin.CONTAINER_DARK.getSkin();
    } else if (MEDIUM_CONTAINER.indexOf(skinEnum.getId()) >= 0) {
      return StyleguideSkin.CONTAINER_MEDIUM.getSkin();
    }
    return StyleguideSkin.CONTAINER_LIGHT.getSkin();
  }

  public static function getContainerUiForSkinGroup(skinGroup:String):String {
    if (DARK_CONTAINER.indexOf(skinGroup) >= 0) {
      return StyleguideSkin.CONTAINER_DARK.getSkin();
    } else if (MEDIUM_CONTAINER.indexOf(skinGroup) >= 0) {
      return StyleguideSkin.CONTAINER_MEDIUM.getSkin();
    }
    return StyleguideSkin.CONTAINER_LIGHT.getSkin();
  }

  public static function getToolbarButtonUi(config:SkinTemplate):String {
    if (DARK_BUTTONS.indexOf(config.skinEnum.getId()) >= 0) {
      return StyleguideSkin.BUTTON_DARK.getSkin();
    } else if (MEDIUM_CONTAINER.indexOf(config.skinEnum.getId()) >= 0) {
      return StyleguideSkin.CONTAINER_MEDIUM.getSkin();
    }
    return StyleguideSkin.BUTTON_LIGHT.getSkin();
  }


  public static function toggleBackground(panel:SkinCategoryPanel):void {
    findElementAndToggleClass(panel, 'div', "x-container-sg-dark", "y-container-sg-dark");
    findElementAndToggleClass(panel, 'div', "x-container-sg-medium", "y-container-sg-medium");
//    findElementAndToggleClass(panel, 'a', "x-btn-sg-dark-medium", "y-btn-sg-dark-medium");
  }

  private static function findElementAndToggleClass(panel:Panel, tagType:String, oldSelector:String, newSelector:String):void {
    var childrenX:Array = panel.getEl().query(tagType + "[class*='" + oldSelector + "']");
    var childrenY:Array = panel.getEl().query(tagType + "[class*='" + newSelector + "']");
    if (!toggleClass(childrenX, oldSelector, newSelector)) {
      toggleClass(childrenY, newSelector, oldSelector);
    }
  }

  private static function toggleClass(items:Array, oldCls:String, newCls:String):Boolean {
    if (items && items.length > 0) {
      items.forEach(function (child:Element):void {
        var targetElement:Element = Ext.get(child);
        targetElement.removeCls(oldCls);
        targetElement.addCls(newCls);
      });
      return true;
    }
    return false;
  }

  public static function hideBackgroundToggleButton(skins:Array):Boolean {
    if (skins) {
      var hide:Boolean = true;
      skins.forEach(function (skinEnum:BaseSkin):Boolean {
        if (DARK_CONTAINER.indexOf(skinEnum.getId()) >= 0 || MEDIUM_CONTAINER.indexOf(skinEnum.getId()) >= 0) {
          hide = false;
          return false;
        }
        if (DARK_CONTAINER.indexOf(skinEnum.getSkinGroup) >= 0 || MEDIUM_CONTAINER.indexOf(skinEnum.getSkinGroup()) >= 0) {
          hide = false;
          return false;
        }
      });
      return hide;
    }
    return true;
  }
}
}
