import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import MarketingSpot from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/MarketingSpot";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import LivecontextStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.lc-studio/LivecontextStudioPlugin_properties";
import LiveContextCatalogObjectAction from "@coremedia-blueprint/studio-client.main.lc-studio/action/LiveContextCatalogObjectAction";
import ActionConfigUtil from "@coremedia/studio-client.ext.cap-base-components/actions/ActionConfigUtil";
import { as, bind, cast, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import ManagementCenterUtil from "../mgmtcenter/ManagementCenterUtil";
import OpenInManagementCenterAction from "./OpenInManagementCenterAction";

interface OpenInManagementCenterActionBaseConfig extends Config<LiveContextCatalogObjectAction> {
}

/**
 * This action is intended to be used from within EXML, only.
 *
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 * @deprecated
 */
class OpenInManagementCenterActionBase extends LiveContextCatalogObjectAction {
  declare Config: OpenInManagementCenterActionBaseConfig;

  /**
   * @param config the configuration object
   */
  constructor(config: Config<OpenInManagementCenterAction> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    super(cast(OpenInManagementCenterAction, ActionConfigUtil.extendConfiguration(resourceManager.getResourceBundle(null, LivecontextStudioPlugin_properties).content, config, "openInManagementCenter", { handler: bind(this$, this$.#doExecute) })));
  }

  protected override isDisabledFor(catalogObjects: Array<any>): boolean {
    if (!ManagementCenterUtil.isSupportedBrowser(catalogObjects)) {
      return true;
    }

    //the action should be enabled only if there is only one catalog object and it is a product
    if (catalogObjects.length !== 1) {
      return true;
    }
    const catalogObject: CatalogObject = catalogObjects[0];
    if (is(catalogObject, Product) || is(catalogObject, Category) || is(catalogObject, MarketingSpot)) {
      if (catalogObject.getState().exists) {
        if (is(catalogObject, Category)) {
          if (cast(Category, catalogObject).getParent() === null) {
            //for the root category we don't have any view on the WCS.
            return true;
          }
        }
        return false;
      }
    }
    return true;
  }

  protected override isHiddenFor(catalogObjects: Array<any>): boolean {
    //we need a catalog object to access the store object.
    if (catalogObjects.length === 0) {
      return false;
    }
    const catalogObject = as(catalogObjects[0], CatalogObject);
    if (!catalogObject) {
      return true;
    }

    const store = catalogObject.getStore();
    if (!store) {
      return true;
    }

    return store.getVendorName() !== "IBM" || super.isHiddenFor(catalogObjects);
  }

  #doExecute(): void {
    if (!ManagementCenterUtil.isSupportedBrowser(this.getCatalogObjects())) {
      return;
    }
    const catalogObject: CatalogObject = this.getCatalogObjects()[0];
    //currently we display only products and categories
    if (is(catalogObject, Product)) {
      ManagementCenterUtil.openProduct(cast(Product, catalogObject));
    } else if (is(catalogObject, Category)) {
      ManagementCenterUtil.openCategory(cast(Category, catalogObject));
    } else if (is(catalogObject, MarketingSpot)) {
      ManagementCenterUtil.openMarketingSpot(cast(MarketingSpot, catalogObject));
    }
  }
}

export default OpenInManagementCenterActionBase;
