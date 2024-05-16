import catalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/catalogHelper";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import ContentTreeModel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/tree/ContentTreeModel";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import { as } from "@jangaroo/runtime";
import CatalogStudioPluginBase from "../CatalogStudioPluginBase";
import CatalogTreeRelation from "./CatalogTreeRelation";

class RepositoryCatalogTreeModel extends ContentTreeModel {
  static readonly REPOSITORY_CATALOG_TREE_ID: string = "repositoryCatalogTreeId";

  override getNodeId(model: any): string {
    const content = as(model, Content);
    if (!content || content.isFolder()) {
      return null;
    }

    if (!CatalogHelper.getInstance().isActiveCoreMediaStore()) {
      return null;
    }

    const contentType = content.getType();
    if (contentType) {
      const typeBean = as(contentType, RemoteBean);
      if (typeBean && typeBean.isLoaded() && !contentType.isSubtypeOf(CatalogTreeRelation.CONTENT_TYPE_CATEGORY)) {
        return null;
      }
    }

    // otherwise, we really don't know if its a CMCategory, but we have to return something here synchronously...
    return super.getNodeId(model);
  }

  override getIdPathFromModel(model: any): Array<any> {
    const content = as(model, Content);
    if (!content) {
      // No path exists.
      return null;
    }
    if (!content.isLoaded()) {
      return undefined;
    }

    //the current active site has another store contentType
    if (!CatalogHelper.getInstance().isActiveCoreMediaStore()) {
      return null;
    }

    const contentType = content.getType();
    if (!(contentType.isSubtypeOf(CatalogTreeRelation.CONTENT_TYPE_PRODUCT) || contentType.isSubtypeOf(CatalogTreeRelation.CONTENT_TYPE_CATEGORY))) {
      return null;
    }

    // check if the content is part of the active site,
    // otherwise return null so that the content from the global tree is selected
    const siteId = editorContext._.getSitesService().getSiteIdFor(content);
    if (siteId !== editorContext._.getSitesService().getPreferredSiteId()) {
      return null;
    }

    return super.getIdPathFromModel(model);
  }

  override getNodeModel(nodeId: string): any {
    if (!CatalogHelper.getInstance().isActiveCoreMediaStore()) {
      return null;
    }

    const content = as(beanFactory._.getRemoteBean(nodeId), Content);
    if (content === null) {
      return null;
    }

    if (!content.isLoaded()) {
      content.load();
      return undefined;
    }

    if (!content.getType().isSubtypeOf(CatalogTreeRelation.CONTENT_TYPE_CATEGORY)) {
      return null;
    }

    return content && !content.isFolder() ? content : null;
  }

  override getRootId(): string {
    return this.getNodeId(this.#getCatalogRoot());
  }

  override getText(nodeId: string): string {
    if (nodeId === this.getNodeId(this.#getCatalogRoot())) {
      return "Corporate-Catalog";
    }
    return super.getText(nodeId);
  }

  override getIconCls(nodeId: string): string {
    const nodeModel = as(this.getNodeModel(nodeId), Content);
    if (nodeModel === this.#getCatalogRoot()) {
      return CoreIcons_properties.commerce_catalog;
    }
    return super.getIconCls(nodeId);
  }

  protected override getVisibleRootModels(): Array<any> {
    return [this.#getCatalogRoot()];
  }

  // To avoid a static cycle while doing MAKE: the modifier 'static' has ben removed
  //noinspection JSMethodCanBeStatic
  #getCatalogRoot(): Content {
    const storeExpression = catalogHelper.getActiveStoreExpression();
    return CatalogStudioPluginBase.getCatalogRootForStore(storeExpression);
  }

  override toString(): string {
    return "RepositoryCatalogTreeModel";
  }

  override getTreeId(): string {
    return RepositoryCatalogTreeModel.REPOSITORY_CATALOG_TREE_ID;
  }
}

export default RepositoryCatalogTreeModel;
