package com.coremedia.blueprint.studio.styleguide {
import com.coremedia.blueprint.studio.styleguide.tabs.icons.IconsTreeStructure;
import com.coremedia.blueprint.studio.styleguide.tabs.skins.SkinsTreeStructure;
import com.coremedia.blueprint.studio.styleguide.treenavigation.TreeNavigation;
import com.coremedia.blueprint.studio.styleguide.treenavigation.TreeNavigationHelper;
import com.coremedia.ui.util.UrlUtil;

import ext.panel.Panel;
import ext.tab.TabPanel;

public class StyleguideBase extends Panel {

  private static const PAGE_DOCUMENTATION:String = 'documentation';
  private static const PAGE_ICONS:String = 'icons';
  private static const PAGE_SKINS:String = 'skins';

  private var config:Styleguide;
  private var selectedPage:String;

  public function StyleguideBase(config:Styleguide = null) {
    super(config);
    this.config = config;
  }

  override protected function afterRender():void {
    super.afterRender();
    selectPage();
    selectContentToView();
  }

  private function selectPage(page:String = null):void {
    if (selectedPage) {
      // Page has already been selected
      return;
    }
    selectedPage = !page ? UrlUtil.getHashParam('page') : page;
    if (selectedPage) {
      var tabPanel:TabPanel = queryById(Styleguide.MENU_ITEM_ID) as TabPanel;
      if (tabPanel) {
        var itemId:String = "sg-menu-" + selectedPage;
        var tab:Panel = tabPanel.queryById(itemId) as Panel;
        if (tab) {
          tabPanel.setActiveTab(tab);
        }
      }
    }
    else {
      selectedPage = 'no page selected';
    }
  }

  private function selectContentToView():void {
    var show:String = UrlUtil.getHashParam('show');
    var success:Boolean = false;
    if (show && selectedPage === PAGE_ICONS) {
      success = TreeNavigationHelper.clickTreeItem(IconsTreeStructure.SHOW[show],
              queryById(TreeNavigation.ICONS_ITEM_ID));
    }
    else if (show && selectedPage === PAGE_SKINS) {
      success = TreeNavigationHelper.clickTreeItem(SkinsTreeStructure.SHOW[show],
              queryById(TreeNavigation.SKINS_ITEM_ID));
    }
    else if (show && selectedPage === PAGE_DOCUMENTATION) {
      success = TreeNavigationHelper.clickTreeItem(SkinsTreeStructure.SHOW[show],
              queryById(TreeNavigation.DOCUMENTATION_ITEM_ID));
    }
    if (!success) {
      if (selectedPage && show) {
        window.console.warn('Failed to show page ', selectedPage, ' and tree item ', show, '. Falling back to start page.');
      }
      selectPage(PAGE_ICONS);
      TreeNavigationHelper.clickTreeItem(IconsTreeStructure.TREE_ICONS_CORE,
              queryById(TreeNavigation.SKINS_ITEM_ID));
    }
  }
}
}
