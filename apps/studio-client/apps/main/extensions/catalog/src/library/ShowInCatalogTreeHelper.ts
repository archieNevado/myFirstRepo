import ShowInLibraryHelper from "@coremedia-blueprint/studio-client.main.ec-studio/library/ShowInLibraryHelper";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import EventUtil from "@coremedia/studio-client.client-core/util/EventUtil";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import { as } from "@jangaroo/runtime";
import { AnyFunction } from "@jangaroo/runtime/types";
import CatalogStudioPluginBase from "../CatalogStudioPluginBase";
import RepositoryCatalogTreeModel from "./RepositoryCatalogTreeModel";

class ShowInCatalogTreeHelper extends ShowInLibraryHelper {
  static readonly TREE_MODEL: RepositoryCatalogTreeModel = new RepositoryCatalogTreeModel();

  constructor(entities: Array<any>) {
    super(entities, ShowInCatalogTreeHelper.TREE_MODEL);
  }

  protected override tryShowInCatalogTree(entity: any): boolean {
    const content = as(entity, Content);
    if (content) {
      const sitesService = editorContext._.getSitesService();
      const siteId = sitesService.getSiteIdFor(content);
      if (siteId === undefined) {
        return undefined;
      }
      entity.siteId = siteId;
      if (sitesService.getPreferredSiteId() === siteId) {
        return super.tryShowInCatalogTree(entity);
      }
    }
    return false;
  }

  protected override switchSite(siteId: string, callback: AnyFunction): void {
    const site = editorContext._.getSitesService().getSite(siteId);
    CatalogStudioPluginBase.switchSite(site, (): void =>
      EventUtil.invokeLater(callback),
    );
  }

}

export default ShowInCatalogTreeHelper;
