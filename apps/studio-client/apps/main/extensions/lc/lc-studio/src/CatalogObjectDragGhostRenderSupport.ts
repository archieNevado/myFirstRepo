import DragGhostRenderSupport from "@coremedia/studio-client.ext.interaction-components/DragGhostRenderSupport";
import { mixin } from "@jangaroo/runtime";
import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import AugmentationUtil from "@coremedia-blueprint/studio-client.main.ec-studio/helper/AugmentationUtil";
import ECommerceStudioPlugin_properties
  from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin_properties";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";

class CatalogObjectDragGhostRenderSupport implements DragGhostRenderSupport {

  readonly #commerceObject: CatalogObject;

  constructor(contentHubObject: CatalogObject) {
    this.#commerceObject = contentHubObject;
  }

  getDragGhostDisplayName(): string {
    return CatalogHelper.getInstance().getDisplayName(this.#commerceObject);
  }

  getDragGhostTypeIconCssClass(): string {
    return AugmentationUtil.getTypeCls(this.#commerceObject);
  }

  getDragGhostMultiSelectionTextTemplate(): string {
    return ECommerceStudioPlugin_properties.Catalog_DragDrop_multiSelect_text;
  }
}
mixin(CatalogObjectDragGhostRenderSupport, DragGhostRenderSupport);

export default CatalogObjectDragGhostRenderSupport;
