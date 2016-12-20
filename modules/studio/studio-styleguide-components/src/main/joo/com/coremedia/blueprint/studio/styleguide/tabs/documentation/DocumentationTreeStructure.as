package com.coremedia.blueprint.studio.styleguide.tabs.documentation {
import com.coremedia.blueprint.studio.styleguide.treenavigation.TreeNavigationHelper;

import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

[ResourceBundle("com.coremedia.blueprint.studio.styleguide.Styleguide")]
public class DocumentationTreeStructure {

  private static var res:IResourceManager = ResourceManager.getInstance();
  private static const BUNDLE:String = 'com.coremedia.blueprint.studio.styleguide.Styleguide';

  // TREE Structure
  public static const TREE:Array = [];
  public static const SHOW:Array = [];

  public static const DISPLAY_SKIN:String = addTree('styleguide_doc_display_skin', res.getString(BUNDLE, 'styleguide_doc_display_skin'));
  public static const USE_SKIN:String = addTree('styleguide_doc_use_skin', res.getString(BUNDLE, 'styleguide_doc_use_skin'));
  public static const USE_STYLEGUIDE:String = addTree('styleguide_use_styleguide', res.getString(BUNDLE, 'styleguide_use_styleguide'));
  public static const ADD_ICONS:String = addTree('styleguide_doc_add_icon', res.getString(BUNDLE, 'styleguide_doc_add_icon'));
  public static const USE_BEM:String = addTree('styleguide_doc_bem_plugin', res.getString(BUNDLE, 'styleguide_doc_bem_plugin'));
  public static const MIXINS:String = addTree('styleguide_doc_mixins', res.getString(BUNDLE, 'styleguide_doc_mixins'));

  private static function addTree(path:String, treeText:String):String {
    TreeNavigationHelper.createTreeCategory(path, TREE, SHOW, treeText);
    return path;
  }

}
}
