import CatalogObjectAction from "@coremedia-blueprint/studio-client.main.ec-studio/action/CatalogObjectAction";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import Config from "@jangaroo/runtime/Config";

class LiveContextCatalogObjectAction extends CatalogObjectAction {

  constructor(config: Config<CatalogObjectAction> = null) {
    super(config);
  }

  protected override isHiddenFor(catalogObjects: Array<any>): boolean {
    return CatalogHelper.getInstance().belongsToCoreMediaStore(catalogObjects) || super.isHiddenFor(catalogObjects);
  }

}

export default LiveContextCatalogObjectAction;
