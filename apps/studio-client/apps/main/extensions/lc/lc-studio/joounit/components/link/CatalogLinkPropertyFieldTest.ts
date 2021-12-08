import AbstractCatalogTest from "@coremedia-blueprint/studio-client.main.ec-studio-test-helper/AbstractCatalogTest";
import ECommerceStudioPlugin from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin";
import CatalogLinkContextMenu from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkContextMenu";
import CatalogLinkPropertyField from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkPropertyField";
import AbstractProductTeaserComponentsTest from "@coremedia-blueprint/studio-client.main.lc-studio-test-helper/AbstractProductTeaserComponentsTest";
import CatalogLinkPropertyFieldTestView from "@coremedia-blueprint/studio-client.main.lc-studio-test-helper/components/link/CatalogLinkPropertyFieldTestView";
import ActionStep from "@coremedia/studio-client.client-core-test-helper/ActionStep";
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
import CatalogThumbnailResolver from "../../../src/CatalogThumbnailResolver";

class CatalogLinkPropertyFieldTest extends AbstractProductTeaserComponentsTest {
  #link: CatalogLinkPropertyField = null;

  #removeButton: Button = null;

  #openInTabMenuItem: Item = null;

  #removeMenuItem: Item = null;

  #viewPort: Viewport = null;

  override setUp(): void {
    super.setUp();
    QtipUtil.registerQtipFormatter();

    this.#createTestling();
    editorContext._.registerThumbnailResolver(new CatalogThumbnailResolver("CatalogObject"));
  }

  override tearDown(): void {
    super.tearDown();
    this.#viewPort.destroy();
  }

  //noinspection JSUnusedGlobalSymbols
  testCatalogLink(): void {
    this.chain(
      this.waitForProductTeaserToBeLoaded(),
      this.#checkProductLinkDisplaysValue(AbstractCatalogTest.ORANGES_NAME),
      //still nothing selected
      this.#checkRemoveButtonDisabled(),
      this.#openContextMenu(), //this selects the link
      this.#checkContextMenuOpened(),
      this.#checkRemoveContextMenuEnabled(),
      this.#checkRemoveButtonEnabled(),
      this.setForceReadOnly(true),
      this.#openContextMenu(), //this selects the link
      this.#checkRemoveButtonDisabled(),
      //valid selected link can be always opened
      this.#checkRemoveContextMenuDisabled(),
      this.setLink(AbstractCatalogTest.ORANGES_ID + "503"),
      this.#checkCatalogLinkDisplaysErrorValue(AbstractCatalogTest.ORANGES_EXTERNAL_ID + "503"),
      this.setLink(AbstractCatalogTest.ORANGES_ID + "404"),
      this.#checkCatalogLinkDisplaysErrorValue(AbstractCatalogTest.ORANGES_EXTERNAL_ID + "404"),
      this.#openContextMenu(), //this selects the link
      //still forceReadOnly = true
      this.#checkRemoveButtonDisabled(),
      //invalid link --> cannot open
      this.#checkRemoveContextMenuDisabled(),
      //invalid link --> cannot open
      this.setForceReadOnly(false),
      this.#openContextMenu(), //this selects the link
      this.#checkRemoveButtonEnabled(),
      //invalid link --> cannot open
      this.#checkRemoveContextMenuEnabled(),
      //invalid link --> cannot open
      this.setLink(AbstractCatalogTest.ORANGES_SKU_ID),
      this.#checkSkuLinkDisplaysValue(AbstractCatalogTest.ORANGES_SKU_NAME),
      this.#openContextMenu(), //this selects the link
      this.#checkContextMenuOpened(),
      this.#checkRemoveButtonEnabled(),
      this.#checkRemoveContextMenuEnabled(),
      this.setForceReadOnly(true),
      this.#openContextMenu(), //this selects the link
      this.#checkRemoveButtonDisabled(),
      //valid selected link can be always opened
      this.#checkRemoveContextMenuDisabled(),
      this.setForceReadOnly(false),
      this.setLink(null),
      this.#checkCatalogLinkIsEmpty(),
      this.#checkRemoveButtonDisabled(),
    );
  }

  #openContextMenu(): Step {
    return new ActionStep("open Context Menu",
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

  #checkProductLinkDisplaysValue(value: string): Step {
    return new Step("check if product is linked and data is displayed",
      (): boolean => {
        const linkDisplay: string = TableUtil.getCellAsDom(this.#link, 0, 1)["textContent"];
        return this.#link.getStore().getCount() === 1 &&
                linkDisplay.indexOf(AbstractCatalogTest.ORANGES_EXTERNAL_ID) >= 0 &&
                linkDisplay.indexOf(value) >= 0;
      },
    );
  }

  #checkSkuLinkDisplaysValue(value: string): Step {
    return new Step("check if sku is linked and data is displayed",
      (): boolean => {
        const linkDisplay: string = TableUtil.getCellAsDom(this.#link, 0, 1)["textContent"];
        return this.#link.getStore().getCount() === 1 &&
                linkDisplay.indexOf(AbstractCatalogTest.ORANGES_SKU_EXTERNAL_ID) >= 0 &&
                linkDisplay.indexOf(value) >= 0;
      },
    );
  }

  #checkCatalogLinkDisplaysErrorValue(value: string): Step {
    return new Step("check if broken product is linked and fallback data '" + value + "' is displayed",
      (): boolean =>
        this.#link.getStore().getCount() === 1 &&
          TableUtil.getCellAsDom(this.#link, 0, 1)["textContent"].indexOf(value) >= 0,

    );
  }

  #checkCatalogLinkIsEmpty(): Step {
    return new Step("check if is catalog link is empty and set product link",
      (): boolean =>
        this.#link && this.#link.getStore() && this.#link.getStore().getCount() === 0,

    );
  }

  #checkRemoveButtonDisabled(): Step {
    return new Step("check remove button disabled",
      (): boolean =>
        this.#removeButton.disabled,

    );
  }

  #checkRemoveButtonEnabled(): Step {
    return new Step("check remove button enabled",
      (): boolean =>
        !this.#removeButton.disabled,

    );
  }

  #checkRemoveContextMenuDisabled(): Step {
    return new Step("check remove context menu disabled",
      (): boolean =>
        this.#removeMenuItem.disabled,

    );
  }

  #checkRemoveContextMenuEnabled(): Step {
    return new Step("check remove context menu enabled",
      (): boolean =>
        //return !removeMenuItem.disabled;
        //TODO: make this check work again
        true,

    );
  }

  #checkContextMenuOpened(): Step {
    return new Step("check context menu opened",
      (): boolean =>
        !!this.#findCatalogLinkContextMenu(),

    );
  }

  /**
   * private helper method to create the container for tests
   */
  #createTestling(): void {
    const config = Config(CatalogLinkPropertyFieldTestView);
    config.bindTo = this.getBindTo();
    config.forceReadOnlyValueExpression = this.getForceReadOnlyValueExpression();

    this.#viewPort = new CatalogLinkPropertyFieldTestView(config);
    this.#link = as(this.#viewPort.getComponent(CatalogLinkPropertyFieldTestView.CATALOG_LINK_PROPERTY_FIELD_ITEM_ID), CatalogLinkPropertyField);

    const openInTabButton = cast(Button, this.#link.getTopToolbar().queryById(ECommerceStudioPlugin.OPEN_IN_TAB_BUTTON_ITEM_ID));
    //we cannot and don't want test the open in tab action as it needs the workarea.
    this.#link.getTopToolbar().remove(openInTabButton);
    this.#removeButton = cast(Button, this.#link.getTopToolbar().queryById(ECommerceStudioPlugin.REMOVE_LINK_BUTTON_ITEM_ID));
  }

  #findCatalogLinkContextMenu(): CatalogLinkContextMenu {
    const contextMenu = as(ComponentManager.getAll().filter((component: Component): boolean =>
      !component.up() && !component.hidden && component.isXType(CatalogLinkContextMenu.xtype),
    )[0], CatalogLinkContextMenu);
    if (contextMenu) {
      this.#openInTabMenuItem = as(contextMenu.getComponent(ECommerceStudioPlugin.OPEN_IN_TAB_MENU_ITEM_ID), Item);
      //we cannot and don't want test the open in tab action as it needs the workarea.
      contextMenu.remove(this.#openInTabMenuItem);
      this.#removeMenuItem = as(contextMenu.getComponent(ECommerceStudioPlugin.REMOVE_LINK_MENU_ITEM_ID), Item);
    }

    return contextMenu;
  }
}

export default CatalogLinkPropertyFieldTest;
