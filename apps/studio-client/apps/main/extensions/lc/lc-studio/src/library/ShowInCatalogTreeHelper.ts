import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import augmentationService from "@coremedia-blueprint/studio-client.main.ec-studio/augmentation/augmentationService";
import CatalogTreeModel from "@coremedia-blueprint/studio-client.main.ec-studio/components/tree/impl/CatalogTreeModel";
import ShowInLibraryHelper from "@coremedia-blueprint/studio-client.main.ec-studio/library/ShowInLibraryHelper";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import StringUtil from "@jangaroo/ext-ts/String";
import { as, cast, is } from "@jangaroo/runtime";
import { AnyFunction } from "@jangaroo/runtime/types";

class ShowInCatalogTreeHelper extends ShowInLibraryHelper {

  static readonly TREE_MODEL: CatalogTreeModel = new CatalogTreeModel();

  constructor(entities: Array<any>) {
    super(entities, ShowInCatalogTreeHelper.TREE_MODEL);
  }

  protected override tryShowInCatalogTree(entity: any): boolean {
    const content = as(entity, Content);
    let catalogObject: CatalogObject = null;
    if (content) {
      catalogObject = augmentationService.getCatalogObject(content);
      if (catalogObject === undefined) {
        return undefined;
      }
    } else if (is(entity, CatalogObject)) {
      catalogObject = cast(CatalogObject, entity);
    }
    return this.#tryShowCatalogObject(catalogObject, entity);
  }

  #tryShowCatalogObject(catalogObject: CatalogObject, entity: any): boolean {
    if (catalogObject) {
      const sitesService = editorContext._.getSitesService();
      // catalog objects immediately know their site id
      const catalogObjectSiteId = catalogObject.getSiteId();
      if (sitesService.getPreferredSiteId() === catalogObjectSiteId) {
        return super.tryShowInCatalogTree(catalogObject);
      }
      entity.siteId = catalogObjectSiteId;
    }
    return false;
  }

  protected override openDialog(msg: string, buttons: any, entity: any, callback: AnyFunction): void {
    if (is(entity, CatalogObject)) {
      delete buttons["no"];
      const siteName = editorContext._.getSitesService().getSiteName(entity.getSiteId());
      msg = StringUtil.format(ShowInLibraryHelper.RESOURCE_BUNDLE.Catalog_show_in_catalog_tree_fails_for_CatalogObject, siteName);
    }
    super.openDialog(msg, buttons, entity, callback);
  }
}

export default ShowInCatalogTreeHelper;
