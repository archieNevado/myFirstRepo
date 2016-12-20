package com.coremedia.blueprint.studio.styleguide.tabs.icons {
import com.coremedia.blueprint.studio.styleguide.templates.IconTemplate;
import com.coremedia.blueprint.studio.styleguide.treenavigation.TreeNavigationHelper;

import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

[ResourceBundle("com.coremedia.blueprint.studio.styleguide.Styleguide")]
public class IconsTreeStructure {

  private static var res:IResourceManager = ResourceManager.getInstance();
  private static const BUNDLE:String = 'com.coremedia.blueprint.studio.styleguide.Styleguide';

  // TREE Structure
  public static const TREE:Array = [];
  public static const SHOW:Array = [];

  public static const TREE_ICONS_CATEGORY:Object = createTreeCategory('icons', res.getString(BUNDLE, 'icons_title'));
  public static const TREE_ICONS_CORE:String = addTreeNode("core-icons", res.getString(BUNDLE, 'core_icons_title'), res.getString(BUNDLE, 'core_icons_description'));
  public static const TREE_ICONS_COLLABORATION:String = addTreeNode("collaboration-icons", res.getString(BUNDLE, 'collaboration_icons_title'), res.getString(BUNDLE, 'collaboration_icons_description'));


  private static function createTreeCategory(name:String, categoryTitle:String):Object {
    return TreeNavigationHelper.createTreeCategory(name, TREE, SHOW, categoryTitle, IconTemplate({config: {skins: []}}));
  }

  private static function addTreeNode(name:String, categoryTitle:String, description:String):String {
    return TreeNavigationHelper.addTreeNode(name, categoryTitle, description, TREE_ICONS_CATEGORY, SHOW);
  }
}
}
