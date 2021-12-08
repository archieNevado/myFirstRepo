import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import Ext from "@jangaroo/ext-ts";
import Button from "@jangaroo/ext-ts/button/Button";
import Container from "@jangaroo/ext-ts/container/Container";
import Item from "@jangaroo/ext-ts/menu/Item";
import Menu from "@jangaroo/ext-ts/menu/Menu";
import { as, bind, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import AnalyticsStudioPlugin_properties from "./AnalyticsStudioPlugin_properties";
import OpenAnalyticsDeepLinkUrlButton from "./OpenAnalyticsDeepLinkUrlButton";
import OpenAnalyticsUrlButtonBase from "./OpenAnalyticsUrlButtonBase";

class AnalyticsDeepLinkButtonContainerBase extends Container {
  constructor(config: Config<Container> = null) {
    super(config);
    this.addListener("beforerender", bind(this, this.#onBeforeRender));
  }

  #onBeforeRender(): void {
    if (this.items && this.items.length > 1) {
      this.#renderButtonMenu();
    }
  }

  #renderButtonMenu(): void {
    const menuItemsFromButtons = [];

    // iterate through all buttons and create new menuItems out of them
    this.items.each((item: OpenAnalyticsDeepLinkUrlButton): void => {
      const config = item.initialConfig;
      delete config.iconCls;
      const menuItem = this.createMenuItem(item);
      menuItem.addListener("enable", bind(this, this.updateAnalyticsReportButton));
      menuItem.addListener("disable", bind(this, this.updateAnalyticsReportButton));

      menuItemsFromButtons.push(menuItem);
    });

    // add menuItems to button menu
    const menuCfg = Config(Menu);
    menuCfg.items = menuItemsFromButtons;
    menuCfg["allowFunctions"] = true;
    menuCfg.width = 233;
    const buttonMenu = new Menu(menuCfg);

    // create menu button that holds the above menu
    const buttonCfg = Config(IconButton);
    buttonCfg.iconCls = CoreIcons_properties.analytics;
    buttonCfg.ui = ButtonSkin.WORKAREA.getSkin();
    buttonCfg.scale = "medium";
    buttonCfg.itemId = "analyticsReportButton";
    buttonCfg.text = AnalyticsStudioPlugin_properties.multi_analytics_button_text;
    buttonCfg.tooltip = AnalyticsStudioPlugin_properties.multi_analytics_button_tooltip;
    buttonCfg.disabled = true;
    buttonCfg.menu = buttonMenu;
    const newButton = new IconButton(buttonCfg);

    this.add(newButton);
  }

  updateAnalyticsReportButton(): void {
    const component = as(this.getComponent("analyticsReportButton"), Button);
    const allMenuItemsDisabled = component.menu.items.getRange().every((item: Item): boolean =>
      item.disabled,
    );
    component.setDisabled(allMenuItemsDisabled);
  }

  createMenuItem(item: OpenAnalyticsDeepLinkUrlButton): Item {
    // make sure button is properly initialized
    if (!item.urlValueExpression) {
      item.initUrlValueExpression();
    }

    // copy config - except 'xtype'!
    const buttonConfig = cast(OpenAnalyticsDeepLinkUrlButton, item.initialConfig);
    const menuConfig: Record<string, any> = {
      iconCls: CoreIcons_properties.analytics,
      handler: OpenAnalyticsUrlButtonBase.openInBrowser(item.urlValueExpression, buttonConfig.windowName),
      text: buttonConfig.tooltip,
      urlValueExpression: item.urlValueExpression,
      contentExpression: item.contentExpression,
    };
    Ext.apply(menuConfig, buttonConfig);
    delete menuConfig.xtype;
    delete menuConfig.xclass;

    const result = new Item(Config(Item, menuConfig));
    OpenAnalyticsUrlButtonBase.bindDisable(item.urlValueExpression, result);

    //hide single button
    item.setVisible(false);

    return result;
  }
}

export default AnalyticsDeepLinkButtonContainerBase;
