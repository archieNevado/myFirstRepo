package com.coremedia.blueprint.studio.styleguide.tabs.skins {
import com.coremedia.blueprint.studio.styleguide.StyleguideUtils;
import com.coremedia.blueprint.studio.styleguide.templates.HtmlWrapperPanel;
import com.coremedia.blueprint.studio.styleguide.templates.SkinTemplate;
import com.coremedia.blueprint.studio.styleguide.treenavigation.SkinsTreeModel;
import com.coremedia.blueprint.studio.styleguide.treenavigation.TreeNavigation;
import com.coremedia.blueprint.studio.styleguide.treenavigation.TreeNavigationHelper;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.impl.BeanFactoryImpl;

import ext.Component;
import ext.form.field.DisplayField;
import ext.panel.Panel;
import ext.tree.TreePanel;

public class SkinsPanelBase extends Panel {

  private var config:SkinsPanel;
  private var activeVE:ValueExpression;
  private var text:DisplayField;

  private static const PAGE_QUERY_PARAMETER:String = 'skins';
  private static const START_PAGE_ITEM_ID:String = 'start_page-skins';
  private static const START_TEXT_ITEM_ID:String = "start-text-skins";

  public function SkinsPanelBase(config:SkinsPanel = null) {
    super(config);
    this.config = config;
  }

  override protected function initComponent():void {
    super.initComponent();

    var treePanel:TreePanel = queryById(TreeNavigation.SKINS_ITEM_ID) as TreePanel;
    if (treePanel) {
      TreeNavigationHelper.addItemClickedHandler(treePanel, activeVE, START_PAGE_ITEM_ID, START_TEXT_ITEM_ID, function ():Panel {
        return queryById(SkinsPanel.INFORMATION_PANEL_ITEM_ID) as Panel;
      }, PAGE_QUERY_PARAMETER);
    }
  }

  /**
   * Calculates all displayable panels based upon the itemIds of the tree panel.
   *
   * @return Array
   */
  public function getSkinPanels():Array {
    var items:Array = [];
    items.push(StyleguideUtils.createMainPanel(START_PAGE_ITEM_ID, START_TEXT_ITEM_ID, 'Explore all skins by using the side-bar.'));

    SkinsTreeModel.getItemIds().forEach(function (leaf:Object):void {
      if (leaf.template) {
        if (leaf.template is SkinTemplate) {
          var config:Object = leaf.template.config;
          if (config && config.skins && config.skins['values'].length > 0) {
            items.push(createSkinCategoryPanel(config, leaf.treeText, leaf.itemId, leaf.template));
          }
        }
        else if (leaf.template is String) {
          items.push(createHtmlWrapperPanel(leaf.template as String));
        }
      }
    });

    return items;
  }

  /**
   * Calculates all displayable skins for the skin enums
   *
   * @return Array
   */
  public function getSkinsList():Array {
    var items:Array = [];
    items.push(Component({
      padding: 10,
      html: 'Please select a skin.'
    }));
    SkinsTreeModel.getItemIds().forEach(function (leaf:Object):void {
      if (leaf.template) {
        var config:Object = leaf.template.config;
        if (config) {
          // itemId should be the same as for the skinCategoryPanel.
          // So that the click on the tree could change the info panel as well...
          var info:SkinsInformationPanel = SkinsInformationPanel({
            itemId: leaf.itemId,
            skinCategoryItemId: leaf.itemId,
            skinGroup: config.skinGroup,
            skinsEnum: config.skins['values']
          });
          items.push(info);
        }
      }
    });

    return items;
  }

  /**
   * Creates a skin category panel
   * @param node Object
   * @param categoryTitle String
   * @param itemId String
   * @param example SkinTemplate
   * @return com.coremedia.blueprint.studio.styleguide.tabs.skins.SkinCategoryPanel
   */
  private static function createSkinCategoryPanel(node:Object, categoryTitle:String, itemId:String, example:SkinTemplate):SkinCategoryPanel {
    example.skinGroup = node.skinGroup;
    var skinCategoryPanel:SkinCategoryPanel = SkinCategoryPanel({
      itemId: itemId,
      skinsEnum: node.skins['values'],
      skinGroup: node.skinGroup,
      example: example,
      hideToggleButton: SkinsHelper.hideBackgroundToggleButton(node.skins['values']),
      categoryTitle: categoryTitle
    });
    return skinCategoryPanel;
  }

  private static function createHtmlWrapperPanel(path:String):HtmlWrapperPanel {
    return HtmlWrapperPanel({
      path: path
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
