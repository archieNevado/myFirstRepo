package com.coremedia.blueprint.studio.styleguide {
import ext.form.field.DisplayField;
import ext.panel.Panel;

public class StyleguideUtils {

  /**
   * Creates a Main Panel
   *
   * @param mainPanelItemId
   * @param welcomeTextItemId
   * @param welcomeText
   * @return Panel
   */
  public static function createMainPanel(mainPanelItemId:String, welcomeTextItemId:String, welcomeText:String):Panel {
    var mainPanel:Panel = Panel({
      itemId: mainPanelItemId,
      ui: StyleguideSkin.PANEL_TEMPLATE.getSkin(),
      title: 'Welcome',
      items: []
    });

    var displayField1:DisplayField = DisplayField({
      ui: StyleguideSkin.DISPLAY_FIELD_TEXT_HUGE.getSkin(),
      value: 'This is Coremedia\'s Living StyleGuide.'
    });
    mainPanel.items.push(displayField1);

    var displayField2:DisplayField = DisplayField({
      ui: StyleguideSkin.DISPLAY_FIELD_TEXT_NORMAL.getSkin(),
      value: welcomeText,
      itemId: welcomeTextItemId
    });
    mainPanel.items.push(displayField2);

    return mainPanel;
  }
}
}
