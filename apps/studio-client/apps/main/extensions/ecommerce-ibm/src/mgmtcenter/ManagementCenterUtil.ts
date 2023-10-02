import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import MarketingSpot from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/MarketingSpot";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import StoreImpl from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/StoreImpl";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import UrlUtil from "@coremedia/studio-client.client-core/util/UrlUtil";
import Ext from "@jangaroo/ext-ts";
import ComponentManager from "@jangaroo/ext-ts/ComponentManager";
import { as, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import int from "@jangaroo/runtime/int";
import ManagementCenterFrame from "./ManagementCenterFrame";
import ManagementCenterWindow from "./ManagementCenterWindow";

class ManagementCenterUtil {
  static readonly DEFAULT_WIDTH: number = 1000;

  static readonly DEFAULT_HEIGHT: number = 600;

  static readonly #EXTERNAL_MODE: string = "externalWcs";

  static #managementWindow: ManagementCenterWindow = null;

  static #managementFrame: ManagementCenterFrame = null;

  static #myWindow: Window = null;

  static #url: string = null;

  static #fetchUrl(): void {
    const store = (as(CatalogHelper.getInstance().getActiveStoreExpression().getValue(), StoreImpl));
    if (!store) {
      ManagementCenterUtil.#url = undefined;
      return;
    }

    if (!store.isLoaded()) {
      store.load(ManagementCenterUtil.#fetchUrl);
    }

    ManagementCenterUtil.#url = store.getVendorUrl();
  }

  static getUrl(): string {
    ManagementCenterUtil.#fetchUrl();
    return ManagementCenterUtil.#url;
  }

  static #isExternal(): boolean {
    return ManagementCenterUtil.isSupportedBrowser() && UrlUtil.getHashParam(ManagementCenterUtil.#EXTERNAL_MODE);
  }

  static #isNewWindow(): boolean {
    if (ManagementCenterUtil.#isExternal()) {
      return !ManagementCenterUtil.#myWindow;
    } else {
      return !ManagementCenterUtil.#managementWindow;
    }
  }

  /**
   * Displays a given product in the Management Center by sending the required product information
   * to the management window.
   * @param product The product to display.
   */
  static openProduct(product?: Product): void {
    if (product) {
      ManagementCenterUtil.#openManagementCenterViewInternal();
      const partNumber = product.getExternalId();
      const productId = product.getExternalTechId();
      const store = product.getStore();
      const msg = "product:" + store.getStoreId() + ":0:" + partNumber + ":" + productId;
      ManagementCenterUtil.#openManagementCenterViewInternal(msg);
    }
  }

  static openCategory(category?: Category): void {
    if (category) {
      ManagementCenterUtil.#openManagementCenterViewInternal();
      const partNumber = category.getExternalId();
      const categoryId = category.getExternalTechId();
      const store = category.getStore();
      const msg = "category:" + store.getStoreId() + ":0:" + partNumber + ":" + categoryId;
      ManagementCenterUtil.#openManagementCenterViewInternal(msg);
    }
  }

  static openMarketingSpot(espot?: MarketingSpot): void {
    if (espot) {
      ManagementCenterUtil.#openManagementCenterViewInternal();
      const partNumber = espot.getExternalId();
      const store = espot.getStore();
      const msg = "espot:" + store.getStoreId() + ":0:" + partNumber + ":";
      ManagementCenterUtil.#openManagementCenterViewInternal(msg);
    }
  }

  static openManagementCenterView(): void {
    ManagementCenterUtil.#openManagementCenterViewInternal();
  }

  /**
   * Open the (singleton) Management Center from the {@see ext.WindowManager}.
   */
  static #openManagementCenterViewInternal(msg?: string): void {
    const wasNewWindow = ManagementCenterUtil.#isNewWindow();
    if (ManagementCenterUtil.#isExternal()) {
      ManagementCenterUtil.#openExternal();
    } else {
      ManagementCenterUtil.#openEmbedded();
    }

    if (wasNewWindow && !ManagementCenterUtil.#isExternal()) {
      ManagementCenterUtil.#managementFrame.on("afterrender", (): void =>
        ManagementCenterUtil.#myWindow.focus(),
      );
      window.setTimeout((): void =>
        ManagementCenterUtil.#openManagementCenterViewInternal(msg)
      , 7000); // wait 7s when frame was opened initially
      return;
    } else {
      ManagementCenterUtil.#myWindow.focus();
    }

    if (ManagementCenterUtil.#isExternal() && !Ext.isChrome) {
      ManagementCenterUtil.#myWindow.location.replace(ManagementCenterUtil.getUrl() + "#" + msg);
    } else {
      //we have to access the contentWindow anew otherwise the management center integration will not work.
      if (!ManagementCenterUtil.#isExternal()) {
        ManagementCenterUtil.#myWindow = ManagementCenterUtil.#managementFrame.getContentWindow();
      }
      ManagementCenterUtil.#myWindow["postMessage"](msg, "*"); // todo: set a real postOrigin instead of "*"
    }
  }

  static #openExternal(): void {
    if (!ManagementCenterUtil.#myWindow || ManagementCenterUtil.#myWindow.closed) {
      ManagementCenterUtil.#myWindow = window.open(ManagementCenterUtil.getUrl(), "_blank", ManagementCenterUtil.#getWindowOptions());
    }
  }

  static #openEmbedded(): void {
    ManagementCenterUtil.#managementWindow = as(ComponentManager.get(ManagementCenterWindow.MANAGEMENT_CENTER_WINDOW_ID), ManagementCenterWindow);
    if (!ManagementCenterUtil.#managementWindow) {
      const windowConfig = Config(ManagementCenterWindow);
      windowConfig.minWidth = ManagementCenterUtil.DEFAULT_WIDTH;
      windowConfig.width = ManagementCenterUtil.DEFAULT_WIDTH;
      windowConfig.minHeight = ManagementCenterUtil.DEFAULT_HEIGHT;
      windowConfig.height = ManagementCenterUtil.DEFAULT_HEIGHT;
      ManagementCenterUtil.#managementWindow = new ManagementCenterWindow(windowConfig);
      ManagementCenterUtil.#managementFrame = as(ManagementCenterUtil.#managementWindow.queryById(ManagementCenterWindow.MANAGEMENT_CENTER_FRAME_ID), ManagementCenterFrame);
      ManagementCenterUtil.#managementFrame.on("afterrender", (): void => {
        ManagementCenterUtil.#myWindow = ManagementCenterUtil.#managementFrame.getContentWindow();
      });
    }
    ManagementCenterUtil.#managementWindow.show();
  }

  static #getWindowOptions(): string {
    const widthOption = "width=" + ManagementCenterUtil.DEFAULT_WIDTH;
    const heightOption = "height=" + ManagementCenterUtil.DEFAULT_HEIGHT;
    return widthOption + "," + heightOption + ",location=yes, resizable=yes";
  }

  static isSupportedBrowser(catalogObjects: Array<any> = null): boolean {
    let store: Store;
    if (catalogObjects && catalogObjects.length > 0) {
      store = cast(CatalogObject, catalogObjects[0]).getStore();
    } else {
      store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();
    }
    if (store) {
      if (store.getVendorName() !== "IBM") {
        return false;
      }
      const wcsVersion = ManagementCenterUtil.#parseWcsVersion(as(store.getVendorVersion(), String));
      if (wcsVersion[0] > 7 || wcsVersion[0] == 7 && wcsVersion[1] > 7) {
        return Ext.isIE || Ext.isGecko || Ext.isChrome;
      }
    }
    return Ext.isIE || Ext.isGecko;
  }

  static #parseWcsVersion(version: string): Array<int> {
    return cast(Array, (version || "").split(".").map(ManagementCenterUtil.#toInt));
  }

  static #toInt(s: string): int {
    return parseInt(s, 10);
  }
}

export default ManagementCenterUtil;
