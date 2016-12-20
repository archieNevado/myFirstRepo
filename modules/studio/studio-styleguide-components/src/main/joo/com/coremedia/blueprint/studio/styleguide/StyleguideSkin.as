package com.coremedia.blueprint.studio.styleguide {
import com.coremedia.ui.util.Enum;

/**
 * Declares all usable skins defined in the studio-styleguide (not meant to be used for studio-app).
 */
public class StyleguideSkin extends Enum {

  /* BUTTONS */
  private static const BUTTONS:String = 'buttons';
  public static const BUTTON_LIGHT:StyleguideSkin = new StyleguideSkin("sg-light", BUTTONS);
  public static const BUTTON_DARK:StyleguideSkin = new StyleguideSkin("sg-dark", BUTTONS);
  public static const BUTTON_ICON:StyleguideSkin = new StyleguideSkin("sg-icon", BUTTONS);

  /* CONTAINER */
  private static const CONTAINER:String = 'container';
  public static const CONTAINER_LIGHT:StyleguideSkin = new StyleguideSkin("sg-light", CONTAINER);
  public static const CONTAINER_DARK:StyleguideSkin = new StyleguideSkin("sg-dark", CONTAINER);
  public static const CONTAINER_MEDIUM:StyleguideSkin = new StyleguideSkin("sg-medium", CONTAINER);
  public static const CONTAINER_BACKGROUND_BLUE:StyleguideSkin = new StyleguideSkin("sg-background-blue", CONTAINER);
  public static const CONTAINER_PATTERN:StyleguideSkin = new StyleguideSkin("sg-pattern", CONTAINER);

  /* TEXTAREA */
  private static const TEXTAREA:String = 'text-area';
  public static const TEXTAREA_CODE:StyleguideSkin = new StyleguideSkin("sg-code", TEXTAREA);

  /* SLIDERS */
  private static const SLIDERS:String = 'slider';
  public static const SLIDER_DEFAULT:StyleguideSkin = new StyleguideSkin("sg-slider", SLIDERS);

  /* PANELS */
  private static const PANELS:String = 'slider';
  public static const PANEL_HEADLINE:StyleguideSkin = new StyleguideSkin("sg-headline", PANELS);
  public static const PANEL_TEMPLATE:StyleguideSkin = new StyleguideSkin("sg-template", PANELS);
  public static const PANEL_PLACEHOLDER:StyleguideSkin = new StyleguideSkin("sg-placeholder", PANELS);
  public static const PANEL_BASE:StyleguideSkin = new StyleguideSkin("sg-base", PANELS);
  public static const PANEL_BASE_LIGHT:StyleguideSkin = new StyleguideSkin("sg-base-light", PANELS);
  public static const PANEL_SIMPLE:StyleguideSkin = new StyleguideSkin("sg-simple", PANELS);
  public static const PANEL_TREE:StyleguideSkin = new StyleguideSkin("sg-tree", PANELS);
  public static const PANEL_DARK:StyleguideSkin = new StyleguideSkin("sg-dark", PANELS);
  public static const PANEL_MEDIUM:StyleguideSkin = new StyleguideSkin("sg-medium", PANELS);

  /* SLIDERS */
  private static const TAB_PANELS:String = 'tab-panels';
  public static const TAB_PANEL_DEFAULT:StyleguideSkin = new StyleguideSkin("sg-tabpanel", TAB_PANELS);

  /* DISPLAY FIELD */
  private static const DISPLAY_FIELDS:String = 'display-fields';
  public static const DISPLAY_FIELD_DOCUMENTATION:StyleguideSkin = new StyleguideSkin("sg-documentation", DISPLAY_FIELDS);
  public static const DISPLAY_FIELD_CODE:StyleguideSkin = new StyleguideSkin("sg-code", DISPLAY_FIELDS);
  public static const DISPLAY_FIELD_TEXT_NORMAL:StyleguideSkin = new StyleguideSkin("sg-text-normal", DISPLAY_FIELDS);
  public static const DISPLAY_FIELD_TEXT_INVERTED:StyleguideSkin = new StyleguideSkin("sg-text-inverted", DISPLAY_FIELDS);
  public static const DISPLAY_FIELD_TEXT_HUGE:StyleguideSkin = new StyleguideSkin("sg-text-huge", DISPLAY_FIELDS);

  /* CUSTOMS */
  private static const CUSTOMS:String = 'customs';
  public static const CUSTOM_LINE_HORIZONTAL:StyleguideSkin = new StyleguideSkin("sg-line-horizontal", CUSTOMS);
  public static const CUSTOM_LINE_HORIZONTAL_LIGHT:StyleguideSkin = new StyleguideSkin("sg-line-horizontal-light", CUSTOMS);

  /**
   * An array containing all ButtonSkin enums.
   */
  [ArrayElementType("com.coremedia.blueprint.studio.styleguide.StyleguideSkin")]
  public static const skins:Array = collectValues(StyleguideSkin);

  private var skin:String;
  private var skinGroup:String;


  function StyleguideSkin(skin:String, skinGroup:String) {
    this.skin = skin;
    this.skinGroup = skinGroup;
  }

  public function getSkin():String {
    return skin;
  }

  public function getSkinGroup():String {
    return skinGroup;
  }

  override public function toString():String {
    return getSkin();
//    return super.toString().toLowerCase().replace(/_/g, "-");
  }
}
}
