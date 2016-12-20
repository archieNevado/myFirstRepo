package com.coremedia.blueprint.studio.styleguide.treenavigation {

import com.coremedia.blueprint.studio.styleguide.tabs.skins.SkinsTreeStructure;

import ext.data.NodeInterface;
import ext.data.Session;
import ext.data.TreeModel;

public class SkinsTreeModel extends TreeModel {

  private static var rootNode:NodeInterface;
  private static var itemIds:Array = [];


  public function SkinsTreeModel(data:Object, session:Session = null) {
    super(data, session);
  }

  public static function getTreeComponents():NodeInterface {
    if (!rootNode) {
      rootNode = TreeNavigationHelper.createTree(rootNode, SkinsTreeStructure.TREE, itemIds, 'skins');
    }
    return rootNode;
  }

  public static function getItemIds():Array {
    return itemIds;
  }
}
}
