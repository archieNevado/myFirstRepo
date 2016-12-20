package com.coremedia.blueprint.studio.styleguide.tabs.documentation {
import com.coremedia.blueprint.studio.styleguide.StyleguideUtils;
import com.coremedia.blueprint.studio.styleguide.templates.HtmlWrapperPanel;
import com.coremedia.blueprint.studio.styleguide.treenavigation.DocumentationTreeModel;
import com.coremedia.blueprint.studio.styleguide.treenavigation.TreeNavigation;
import com.coremedia.blueprint.studio.styleguide.treenavigation.TreeNavigationHelper;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.impl.BeanFactoryImpl;

import ext.panel.Panel;
import ext.tree.TreePanel;

public class DocumentationPanelBase extends Panel {

  private var config:DocumentationPanelBase;
  private var activeVE:ValueExpression;

  private static const PAGE_QUERY_PARAMETER:String = 'documentation';
  private static const START_PAGE_ITEM_ID:String = 'start_page-documentation';
  private static const START_TEXT_ITEM_ID:String = "start-text-documentation";

  public function DocumentationPanelBase(config:DocumentationPanelBase = null) {
    super(config);
    this.config = config;
  }

  override protected function initComponent():void {
    super.initComponent();

    var treePanel:TreePanel = queryById(TreeNavigation.DOCUMENTATION_ITEM_ID) as TreePanel;
    if (treePanel) {
      TreeNavigationHelper.addItemClickedHandler(treePanel, activeVE, START_PAGE_ITEM_ID, START_TEXT_ITEM_ID, null, PAGE_QUERY_PARAMETER);
    }
  }

  /**
   * Calculates all displayable panels based upon the itemIds of the tree panel.
   *
   * @return Array
   */
  public function getDocumentationPanels():Array {
    var items:Array = [];
    items.push(StyleguideUtils.createMainPanel(START_PAGE_ITEM_ID, START_TEXT_ITEM_ID, 'Explore all docs by using the side-bar.'));

    DocumentationTreeModel.getItemIds().forEach(function (leaf:Object):void {
      items.push(createHTMLWrapper(leaf));
    });

    return items;
  }

  /**
   * Creates an Html Wrapper Panel
   * @param node Object
   * @return HtmlWrapperPanel
   */
  private static function createHTMLWrapper(node:Object):HtmlWrapperPanel {
    return HtmlWrapperPanel({
      itemId: node.itemId,
      path: node.name,
      renderAsType: HtmlWrapperPanel.PANEL
    });
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
