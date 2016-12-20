package com.coremedia.blueprint.studio.styleguide.treenavigation {
import com.coremedia.icons.CoreIcons_properties;
import com.coremedia.ui.data.ValueExpression;

import ext.Component;
import ext.data.Model;
import ext.data.NodeInterface;
import ext.form.field.DisplayField;
import ext.panel.Panel;
import ext.tree.TreePanel;
import ext.tree.TreeView;

public class TreeNavigationHelper {

  /**
   * Create the tree and collects all itemIds of the tree nodes.
   *
   * @param root [Object]
   * @param items [Array]
   * @param collectItemIds [Array]
   * @param hasOwnProperty [String]
   * @return The tree object
   */
  public static function createTree(root:NodeInterface, items:Array, collectItemIds:Array, hasOwnProperty:String = null):NodeInterface {
    root = NodeInterface({
      text: 'Styleguide Components',
      expanded: false,
      children: []
    });

    items.forEach(function (category:Object):void {
      root.children.push(createNode(category, collectItemIds, hasOwnProperty));
    });
    return root;
  }

  private static function createNode(category:Object, container:Array, checkProperty:String = null):Object {
    if (category.children.length == 0) {
      return createLeaf(category, category, CoreIcons_properties.INSTANCE.arrow_right, container, checkProperty);
    }
    var node:Object = {
      text: category.treeText,
      description: category.description,
      id: category.itemId,
      itemId: category.itemId,
      expanded: true,
      children: []
    };
    category.children.forEach(function (child:Object):void {
      node['children'].push(createLeaf(child, category, CoreIcons_properties.INSTANCE.arrow_right, container, checkProperty));
    });
    return node;
  }

  private static function createLeaf(child:Object, category:Object, iconCls:String, container:Array, checkProperty:String):NodeInterface {
    var categoryTemplate:Object = category.template;
    var childTemplate:Object = child.template;
    var template:Object = childTemplate ? childTemplate : categoryTemplate;
    var enabled:Boolean = true;
    container.push({
      itemId: child.itemId,
      template: template,
      treeText: child.treeText,
      description: child.description,
      objectType: 'Leaf',
      name: category.categoryName
    });
    if (template && checkProperty && template.config && !template.config.hasOwnProperty(checkProperty)) {
      enabled = false;
    }
    return createChild(child, true, enabled, iconCls);
  }

  private static function createChild(child:Object, leaf:Boolean, enabled:Boolean, iconCls:String):NodeInterface {
    // http://fontawesome.io/cheatsheet/
    return NodeInterface({
      text: child.treeText,
      description: child.description,
      id: child.itemId,
      itemId: child.itemId,
      leaf: leaf,
      iconCls: iconCls,
      cls: enabled ? "" : "x-grid-cell--sg-disabled",
      disabled: !enabled,
      name: child.name
    });
  }

  /**
   * Add <b>itemclick</b> handler on treePanel to display corresponding panel when node on tree has been clicked.
   *
   * @param treePanel TreePanel
   * @param ve ValueExpression
   * @param startPageItemId String
   * @param welcomeTextItemId String
   * @param getInformationPanel Function
   * @param page String
   */
  public static function addItemClickedHandler(treePanel:TreePanel, ve:ValueExpression, startPageItemId:String, welcomeTextItemId:String, getInformationPanel:Function = null, page:String = null):void {
    treePanel.mon(treePanel, 'itemclick', function (view:TreeView, record:Model):void {
      var hasChildren:Boolean = (!!record.data.children && record.data.children.length > 0);
      var text:DisplayField;
      if (record.data.disabled) {
        ve.setValue(startPageItemId);
        text = treePanel.queryById(welcomeTextItemId) as DisplayField;
        if (text) {
          text.setValue("Examples for " + record.data.text + " coming soon...");
        }
      }
      else {
        if (record.data.itemId && !hasChildren) {
          ve.setValue(record.data.itemId);
          changeHash(page, record.data.name);
          if (getInformationPanel) {
            var informationPanel:Panel = getInformationPanel();
            informationPanel.expand(true);
          }
        }
        else {
          ve.setValue(startPageItemId);
          text = treePanel.queryById(welcomeTextItemId) as DisplayField;
          if (text) {
            text.setValue('This category will show you available examples for: ' + record.data.text);
          }
        }
      }
    });
  }

  /**
   * Execute event: Click on tree item.
   *
   * @param id String Id of tree item
   * @param tree TreePanel The tree
   */
  public static function clickTreeItem(id:String, tree:Component):Boolean {
    if (!tree || !(tree instanceof TreePanel)) {
      return false;
    }
    var treePanel:TreePanel = tree as TreePanel;
    var node:NodeInterface;
    if (treePanel) {
      if (id) {
        node = treePanel.getRootNode().getTreeStore().getNodeById(id);
        if (node) {
          treePanel.getSelectionModel().select(node);
          treePanel.fireEvent("itemClick", treePanel, node);
          return true;
        }
      }
    }
    return false;
  }

  private static function changeHash(page:String, show:String):void {
    var searchOriginal:String = window.location.hash;
    var hash:String = replaceHashParam('page', page, searchOriginal);
    hash = replaceHashParam('show', show, hash);
    if (searchOriginal !== hash) {
      window.location.hash = hash;
    }
  }

  private static function replaceHashParam(param:String, newval:String, search:String = null):String {
    var regex:RegExp = new RegExp("([?;&])" + param + "[^&;]*[;&]?");
    if (!search) {
      search = window.location.hash;
    }
    var query:String = search.replace(regex, "$1").replace(/&$/, '');

    return (query.length > 2 ? query + "&" : "#") + (newval ? param + "=" + newval : '');
  }

  public static function createTreeCategory(name:String, container:Array, showContainer:Array, treeText:String, template:Object = null):Object {
    var category:Object = {
      itemId: 'tree_' + name,
      treeText: treeText,
      objectType: 'TreeCategory',
      children: [],
      categoryName: name,
      name: name
    };
    if (template) {
      category['template'] = template;
    }
    container.push(category);
    showContainer[name] = category.itemId;
    return category;
  }

  public static function addTreeNode(name:String, treeText:String, description:String, category:Object, showContainer:Array, template:Object = null):String {
    var itemId:String = category.itemId + '-' + name;
    var child:Object = {
      itemId: itemId,
      treeText: treeText,
      objectType: 'TreeNode',
      description: description,
      name: name
    };
    if (template) {
      child['template'] = template;
    }
    category.children.push(child);
    showContainer[name] = child.itemId;
    return itemId;
  }
}
}
