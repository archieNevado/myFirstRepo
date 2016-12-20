package com.coremedia.blueprint.studio.styleguide.treenavigation {

import com.coremedia.blueprint.studio.styleguide.tabs.documentation.DocumentationTreeStructure;

import ext.data.NodeInterface;
import ext.data.Session;
import ext.data.TreeModel;

public class DocumentationTreeModel extends TreeModel {

  private static var rootNode:NodeInterface;
  private static var itemIds:Array = [];


  public function DocumentationTreeModel(data:Object, session:Session = null) {
    super(data, session);
  }

  public static function getTreeComponents():NodeInterface {
    if (!rootNode) {
      rootNode = TreeNavigationHelper.createTree(rootNode, DocumentationTreeStructure.TREE, itemIds);
    }
    return rootNode;
  }

  public static function getItemIds():Array {
    return itemIds;
  }
}
}
