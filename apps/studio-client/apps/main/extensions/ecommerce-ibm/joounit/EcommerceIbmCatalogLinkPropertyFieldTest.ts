import AbstractCatalogTest from "@coremedia-blueprint/studio-client.main.ec-studio-test-helper/AbstractCatalogTest";
import ECommerceStudioPlugin from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin";
import CatalogLinkContextMenu from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkContextMenu";
import CatalogLinkPropertyField from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkPropertyField";
import AbstractProductTeaserComponentsTest from "@coremedia-blueprint/studio-client.main.lc-studio-test-helper/AbstractProductTeaserComponentsTest";
import CatalogLinkPropertyFieldTestView from "@coremedia-blueprint/studio-client.main.lc-studio-test-helper/components/link/CatalogLinkPropertyFieldTestView";
import CatalogThumbnailResolver from "@coremedia-blueprint/studio-client.main.lc-studio/CatalogThumbnailResolver";
import Step from "@coremedia/studio-client.client-core-test-helper/Step";
import QtipUtil from "@coremedia/studio-client.ext.ui-components/util/QtipUtil";
import TableUtil from "@coremedia/studio-client.ext.ui-components/util/TableUtil";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Component from "@jangaroo/ext-ts/Component";
import ComponentManager from "@jangaroo/ext-ts/ComponentManager";
import Button from "@jangaroo/ext-ts/button/Button";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import Item from "@jangaroo/ext-ts/menu/Item";
import { as, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import EcommerceIbmStudioPlugin from "../src/EcommerceIbmStudioPlugin";
import ManagementCenterUtil from "../src/mgmtcenter/ManagementCenterUtil";

class EcommerceIbmCatalogLinkPropertyFieldTest extends AbstractProductTeaserComponentsTest {
  #link: CatalogLinkPropertyField = null;

  #openWcsButton: Button = null;

  #openInTabMenuItem: Item = null;

  #openWcsMenuItem: Item = null;

  #viewPort: Viewport = null;

  override setUp(): void {
    super.setUp();
    QtipUtil.registerQtipFormatter();
    editorContext._.registerThumbnailResolver(new CatalogThumbnailResolver("CatalogObject"));

    const conf = Config(CatalogLinkPropertyFieldTestView);
    conf.bindTo = this.getBindTo();
    conf.forceReadOnlyValueExpression = this.getForceReadOnlyValueExpression();

    this.#createTestling(conf);
  }

  override tearDown(): void {
    super.tearDown();
    this.#viewPort.destroy();
  }

  protected override createPlugin(): void {
    new EcommerceIbmStudioPlugin();
  }

  //noinspection JSUnusedGlobalSymbols
  testCatalogLink(): void {
    this.chain(
      this.waitForProductTeaserToBeLoaded(),
      //still nothing selected
      this.#checkOpenWcsButtonDisabled(),
      this.#openContextMenu(), //this selects the link
      this.#checkOpenWcsButtonEnabled(),
      this.#checkOpenWcsContextMenuEnabled(),
      this.setForceReadOnly(true),
      this.#openContextMenu(), //this selects the link
      //valid selected link can be always opened
      this.#checkOpenWcsButtonEnabled(),
      this.#checkOpenWcsContextMenuEnabled(),
      this.setLink(AbstractCatalogTest.ORANGES_ID + "503"),
      this.setLink(AbstractCatalogTest.ORANGES_ID + "404"),
      this.#openContextMenu(), //this selects the link
      //invalid link --> cannot open
      this.#checkOpenWcsButtonDisabled(),
      //invalid link --> cannot open
      this.#checkOpenWcsContextMenuDisabled(),
      this.setForceReadOnly(false),
      this.#openContextMenu(), //this selects the link
      //invalid link --> cannot open
      this.#checkOpenWcsButtonDisabled(),
      //invalid link --> cannot open
      this.#checkOpenWcsContextMenuDisabled(),
      this.setLink(AbstractCatalogTest.ORANGES_SKU_ID),
      this.#openContextMenu(), //this selects the link
      this.#checkOpenWcsButtonEnabled(),
      this.#checkOpenWcsContextMenuEnabled(),
      this.setForceReadOnly(true),
      this.#openContextMenu(), //this selects the link
      //valid selected link can be always opened
      this.#checkOpenWcsButtonEnabled(),
      this.#checkOpenWcsContextMenuEnabled(),
      this.setForceReadOnly(false),
      this.setLink(null),
      this.#checkOpenWcsButtonDisabled(),
    );
  }

  #openContextMenu(): Step {
    return new Step("open Context Menu",
      (): boolean =>
        true
      ,
      (): void => {
        const empty: boolean = this.#link.getView().getRow(0) === undefined;
        const event: Record<string, any> = {
          type: "contextmenu",

          getXY: (): Array<any> =>
            (empty ? TableUtil.getMainBody(this.#link) : TableUtil.getCell(this.#link, 0, 1)).getXY()
          ,
          preventDefault: (): void =>{
            //do nothing
          },
          getTarget: (): HTMLElement =>
            TableUtil.getCellAsDom(this.#link, 0, 1),

        };
        if (empty) {
          this.#link.fireEvent("contextmenu", event);
        } else {
          this.#link.fireEvent("rowcontextmenu", this.#link, null, null, 0, event);
        }
      },
    );
  }

  #checkOpenWcsButtonDisabled(): Step {
    return new Step("check open Wcs button disabled",
      (): boolean =>
        this.#openWcsButton.disabled,

    );
  }

  #checkOpenWcsButtonEnabled(): Step {
    return new Step("check open Wcs button enabled",
      (): boolean =>
        !ManagementCenterUtil.isSupportedBrowser() || !this.#openWcsButton.disabled,

    );
  }

  #checkOpenWcsContextMenuDisabled(): Step {
    return new Step("check open Wcs context menu disabled",
      (): boolean =>
        !ManagementCenterUtil.isSupportedBrowser() || this.#openWcsMenuItem.disabled,

    );
  }

  #checkOpenWcsContextMenuEnabled(): Step {
    return new Step("check open Wcs context menu enabled",
      (): boolean =>
        !ManagementCenterUtil.isSupportedBrowser() || !this.#openWcsMenuItem.disabled,

    );
  }

  /**
   * private helper method to create the container for tests
   */
  #createTestling(config: Config<CatalogLinkPropertyFieldTestView>): void {
    this.#viewPort = new CatalogLinkPropertyFieldTestView(config);
    this.#link = as(this.#viewPort.getComponent(CatalogLinkPropertyFieldTestView.CATALOG_LINK_PROPERTY_FIELD_ITEM_ID), CatalogLinkPropertyField);
    const openInTabButton = cast(Button, this.#link.getTopToolbar().find("itemId", ECommerceStudioPlugin.OPEN_IN_TAB_BUTTON_ITEM_ID)[0]);
    //we cannot and don't want test the open in tab action as it needs the workarea.
    this.#link.getTopToolbar().remove(openInTabButton);
    this.#openWcsButton = cast(Button, this.#link.getTopToolbar().find("itemId", EcommerceIbmStudioPlugin.OPEN_IN_MCENTER_BUTTON_ITEM_ID)[0]);
  }

  #findCatalogLinkContextMenu(): CatalogLinkContextMenu {
    const contextMenu = as(ComponentManager.getAll().filter((component: Component): boolean =>
      !component.up() && !component.hidden && component.isXType(CatalogLinkContextMenu.xtype),
    )[0], CatalogLinkContextMenu);
    if (contextMenu) {
      this.#openInTabMenuItem = as(contextMenu.getComponent(ECommerceStudioPlugin.OPEN_IN_TAB_MENU_ITEM_ID), Item);
      //we cannot and don't want test the open in tab action as it needs the workarea.
      contextMenu.remove(this.#openInTabMenuItem);
      this.#openWcsMenuItem = as(contextMenu.getComponent(EcommerceIbmStudioPlugin.OPEN_IN_MCENTER_MENU_ITEM_ID), Item);
    }

    return contextMenu;
  }
}

export default EcommerceIbmCatalogLinkPropertyFieldTest;
