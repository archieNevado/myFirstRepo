package com.coremedia.blueprint.studio.styleguide.treenavigation {

import com.coremedia.blueprint.studio.styleguide.tabs.icons.IconsTreeStructure;

import ext.data.NodeInterface;
import ext.data.Session;
import ext.data.TreeModel;

public class IconsTreeModel extends TreeModel {

  private static var rootNode:NodeInterface;
  private static var itemIds:Array = [];


  public function IconsTreeModel(data:Object, session:Session = null) {
    super(data, session);
  }

  public static function getTreeComponents():NodeInterface {
    if (!rootNode) {
      rootNode = TreeNavigationHelper.createTree(rootNode, IconsTreeStructure.TREE, itemIds);
    }
    return rootNode;
  }

  public static function getItemIds():Array {
    return itemIds;
  }
}
}
