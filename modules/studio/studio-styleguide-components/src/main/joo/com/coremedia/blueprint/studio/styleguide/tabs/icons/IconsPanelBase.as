package com.coremedia.blueprint.studio.styleguide.tabs.icons {
import com.coremedia.blueprint.studio.styleguide.*;
import com.coremedia.blueprint.studio.styleguide.templates.IconFontTemplate;
import com.coremedia.blueprint.studio.styleguide.templates.IconTemplate;
import com.coremedia.blueprint.studio.styleguide.treenavigation.IconsTreeModel;
import com.coremedia.blueprint.studio.styleguide.treenavigation.TreeNavigation;
import com.coremedia.blueprint.studio.styleguide.treenavigation.TreeNavigationHelper;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.impl.BeanFactoryImpl;

import ext.panel.Panel;
import ext.tree.TreePanel;

[ResourceBundle('com.coremedia.icons.CoreIcons')]
[ResourceBundle('com.coremedia.icons.CollaborationIcons')]
public class IconsPanelBase extends Panel {

  private var config:IconsPanelBase;
  private var activeVE:ValueExpression;

  private static const PAGE_QUERY_PARAMETER:String = 'icons';
  private static const START_PAGE_ITEM_ID:String = 'start_page-icons';
  private static const START_TEXT_ITEM_ID:String = "start-text-icons";

  public function IconsPanelBase(config:IconsPanelBase = null) {
    super(config);
    this.config = config;
  }

  override protected function initComponent():void {
    super.initComponent();

    var treePanel:TreePanel = queryById(TreeNavigation.ICONS_ITEM_ID) as TreePanel;
    if (treePanel) {
      TreeNavigationHelper.addItemClickedHandler(treePanel, activeVE, START_PAGE_ITEM_ID, START_TEXT_ITEM_ID, null, PAGE_QUERY_PARAMETER);
    }
  }

  /**
   * Calculates all displayable panels based upon the itemIds of the tree panel.
   *
   * @return Array
   */
  public function getIconPanels():Array {
    var items:Array = [];
    items.push(StyleguideUtils.createMainPanel(START_PAGE_ITEM_ID, START_TEXT_ITEM_ID, 'Explore all icons by using the side-bar.'));

    IconsTreeModel.getItemIds().forEach(function (leaf:Object):void {
      if (leaf.template) {
        if (leaf.template is IconTemplate) {
          if (leaf.itemId === IconsTreeStructure.TREE_ICONS_CORE) {
            items.push(createIconPanel(leaf, resourceManager.getResourceBundle(null, 'com.coremedia.icons.CoreIcons').content, 'CoreIcons'));
          }
          if (leaf.itemId === IconsTreeStructure.TREE_ICONS_COLLABORATION) {
            items.push(createIconPanel(leaf, resourceManager.getResourceBundle(null, 'com.coremedia.icons.CollaborationIcons').content, 'CollaborationIcons'));
          }
        }
      }
    });

    return items;
  }

  /**
   * Creates an Icon Panel
   * @param node Object
   * @param iconSet JavaScriptObject
   * @param iconSetName String
   * @return IconFontTemplate
   */
  private function createIconPanel(node:Object, iconSet:Object, iconSetName:String):IconFontTemplate {
    //noinspection JSUnusedGlobalSymbols
    var iconPanel:IconFontTemplate = IconFontTemplate({
      itemId: node.itemId,
      getInformationPanel: function ():Panel {
        return queryById(IconsPanel.INFORMATION_PANEL_ITEM_ID) as Panel;
      },
      categoryTitle: node.treeText,
      icons: iconSet,
      description: node.description,
      iconSetName: iconSetName
    });
    return iconPanel;
  }

  protected function getActiveVE():ValueExpression {
    if (!activeVE) {
      BeanFactoryImpl.initBeanFactory();
      activeVE = ValueExpressionFactory.createFromValue(START_PAGE_ITEM_ID);
    }
    return activeVE;
  }
}
}
