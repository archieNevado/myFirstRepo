package com.coremedia.blueprint.studio.styleguide.tabs.skins {
import com.coremedia.blueprint.studio.styleguide.templates.SkinTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.skins.ButtonsTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.skins.CheckboxesTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.skins.ContainerTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.skins.DisplayFieldsTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.skins.FieldSetsTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.skins.TableViewsTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.skins.IconDisplayFieldTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.skins.MenusTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.skins.PanelsTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.skins.SlidersTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.skins.SplitterTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.skins.TabBarsTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.skins.TextFieldsTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.skins.ToolbarsTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.skins.TipsTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.skins.WindowsTemplate;
import com.coremedia.blueprint.studio.styleguide.treenavigation.TreeNavigationHelper;
import com.coremedia.ui.skins.ButtonSkin;
import com.coremedia.ui.skins.CheckboxSkin;
import com.coremedia.ui.skins.ContainerSkin;
import com.coremedia.ui.skins.DisplayFieldSkin;
import com.coremedia.ui.skins.FieldSetSkin;
import com.coremedia.ui.skins.TableViewSkin;
import com.coremedia.ui.skins.IconDisplayFieldSkin;
import com.coremedia.ui.skins.MenuSkin;
import com.coremedia.ui.skins.PanelSkin;
import com.coremedia.ui.skins.SliderSkin;
import com.coremedia.ui.skins.SplitterSkin;
import com.coremedia.ui.skins.TabBarSkin;
import com.coremedia.ui.skins.TextfieldSkin;
import com.coremedia.ui.skins.TipSkin;
import com.coremedia.ui.skins.ToolbarSkin;
import com.coremedia.ui.skins.WindowSkin;

import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

[ResourceBundle("com.coremedia.blueprint.studio.styleguide.Styleguide")]
public class SkinsTreeStructure {

  private static var res:IResourceManager = ResourceManager.getInstance();
  private static const BUNDLE:String = 'com.coremedia.blueprint.studio.styleguide.Styleguide';

  // TREE Structure
  public static const TREE:Array = [];
  public static const SHOW:Array = [];

  //noinspection JSUnusedGlobalSymbols
  public static const BUTTONS:Object = createTreeCategory(ButtonSkin.SKIN_GROUP,
          res.getString(BUNDLE, 'buttons_title'),
          ButtonsTemplate({
            config: {
              skins: ButtonSkin
            }
          }));

  //noinspection JSUnusedGlobalSymbols
  public static const CHECKBOXES:Object = createTreeCategory(CheckboxSkin.SKIN_GROUP, res.getString(BUNDLE, 'checkboxes_title'), CheckboxesTemplate({
    config: {
      skins: CheckboxSkin
    }
  }));

  //noinspection JSUnusedGlobalSymbols
  public static const CONTAINER:Object = createTreeCategory(ContainerSkin.SKIN_GROUP, res.getString(BUNDLE, 'container_title'), ContainerTemplate({
    config: {
      skins: ContainerSkin
    }
  }));

  //noinspection JSUnusedGlobalSymbols
  public static const DISPLAY_FIELDS:Object = createTreeCategory(DisplayFieldSkin.SKIN_GROUP, res.getString(BUNDLE, 'displayfields_title'), DisplayFieldsTemplate({
    config: {
      skins: DisplayFieldSkin
    }
  }));

  //noinspection JSUnusedGlobalSymbols
  public static const FIELDSETS:Object = createTreeCategory(FieldSetSkin.SKIN_GROUP, res.getString(BUNDLE, 'fieldsets_title'), FieldSetsTemplate({
    config: {
      skins: FieldSetSkin
    }
  }));

  //noinspection JSUnusedGlobalSymbols
  public static const TABLEVIEWS:Object = createTreeCategory(TableViewSkin.SKIN_GROUP, res.getString(BUNDLE, 'tableviews_title'), TableViewsTemplate({
    config: {
      skins: TableViewSkin
    }
  }));

  //noinspection JSUnusedGlobalSymbols
  public static const ICONDISPLAYFIELDS:Object = createTreeCategory(IconDisplayFieldSkin.SKIN_GROUP, res.getString(BUNDLE, 'icon_displayfield_title'), IconDisplayFieldTemplate({
    config: {
      skins: IconDisplayFieldSkin
    }
  }));

  //noinspection JSUnusedGlobalSymbols
  public static const MENUS:Object = createTreeCategory(MenuSkin.SKIN_GROUP, res.getString(BUNDLE, 'menus_title'), MenusTemplate({
    config: {
      skins: MenuSkin
    }
  }));

  //noinspection JSUnusedGlobalSymbols
  public static const PANELS:Object = createTreeCategory(PanelSkin.SKIN_GROUP, res.getString(BUNDLE, 'panels_title'), PanelsTemplate({
    config: {
      skins: PanelSkin
    }
  }));

  //noinspection JSUnusedGlobalSymbols
  public static const SLIDERS:Object = createTreeCategory(SliderSkin.SKIN_GROUP, res.getString(BUNDLE, 'sliders_title'), SlidersTemplate({
    config: {
      skins: SliderSkin
    }
  }));

  //noinspection JSUnusedGlobalSymbols
  public static const SPLITTERS:Object = createTreeCategory(SplitterSkin.SKIN_GROUP, res.getString(BUNDLE, 'splitter_title'), SplitterTemplate({
    config: {
      skins: SplitterSkin
    }
  }));

  //noinspection JSUnusedGlobalSymbols
  public static const TABBARS:Object = createTreeCategory(TabBarSkin.SKIN_GROUP, res.getString(BUNDLE, 'tabbars_title'), TabBarsTemplate({
    config: {
      skins: TabBarSkin,
      hideToggleButton: false
    }
  }));

  //noinspection JSUnusedGlobalSymbols
  public static const TEXTFIELDS:Object = createTreeCategory(TextfieldSkin.SKIN_GROUP, res.getString(BUNDLE, 'textfields_title'), TextFieldsTemplate({
    config: {
      skins: TextfieldSkin
    }
  }));

  //noinspection JSUnusedGlobalSymbols
  public static const TIPS:Object = createTreeCategory(TipSkin.SKIN_GROUP, res.getString(BUNDLE, 'tips_title'), TipsTemplate({
    config: {
      skins: TipSkin
    }
  }));

  //noinspection JSUnusedGlobalSymbols
  public static const TOOLBARS:Object = createTreeCategory(ToolbarSkin.SKIN_GROUP, res.getString(BUNDLE, 'toolbars_title'), ToolbarsTemplate({
    config: {
      skins: ToolbarSkin
    }
  }));

  //noinspection JSUnusedGlobalSymbols
  public static const WINDOWS:Object = createTreeCategory(WindowSkin.SKIN_GROUP, res.getString(BUNDLE, 'windows_title'), WindowsTemplate({
    config: {
      skins: WindowSkin
    }
  }));

  // Internal functions

  private static function createTreeCategory(name:String, categoryTitle:String, template:SkinTemplate = null):Object {
    if (template) {
      template['config']['skinGroup'] = name;
      template['config']['categoryTitle'] = categoryTitle;
    }
    return TreeNavigationHelper.createTreeCategory(name, TREE, SHOW, categoryTitle, template);
  }

}
}
