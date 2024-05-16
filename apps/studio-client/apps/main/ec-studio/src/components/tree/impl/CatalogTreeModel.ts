import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import CategoryChildData from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CategoryChildData";
import Marketing from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Marketing";
import MarketingSpot from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/MarketingSpot";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import LazyLoadingTreeModel from "@coremedia/studio-client.ext.ui-components/models/LazyLoadingTreeModel";
import NodeChildren from "@coremedia/studio-client.ext.ui-components/models/NodeChildren";
import CompoundChildTreeModel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/tree/CompoundChildTreeModel";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import { as, bind, cast, is, mixin } from "@jangaroo/runtime";
import int from "@jangaroo/runtime/int";
import { AnyFunction } from "@jangaroo/runtime/types";
import ECommerceStudioPlugin_properties from "../../../ECommerceStudioPlugin_properties";
import augmentationService from "../../../augmentation/augmentationService";
import CatalogHelper from "../../../helper/CatalogHelper";
import categoryTreeRelation from "../../../tree/categoryTreeRelation";
import CatalogPreferencesBase from "../../preferences/CatalogPreferencesBase";

class CatalogTreeModel implements CompoundChildTreeModel, LazyLoadingTreeModel {

  #enabled: boolean = true;

  static readonly ID_PREFIX: string = "livecontext/";

  static readonly CATALOG_TREE_ID: string = "catalogTreeId";

  static readonly HYPERLINK_PREFIX: string = "hyperlink:";

  static readonly HYPERLINK_SEPARATOR: string = "##";

  constructor() {
  }

  setEnabled(enabled: boolean): void {
    this.#enabled = enabled;
  }

  isEnabled(): boolean {
    return this.#enabled;
  }

  isEditable(model: any): boolean {
    return false;
  }

  rename(model: any, newName: string, oldName: string, callback: AnyFunction): void {
  }

  isRootVisible(): boolean {
    return true;
  }

  getRootId(): string {
    if (!CatalogTreeModel.#getStore()) {
      return null;
    }
    if (CatalogHelper.getInstance().isActiveCoreMediaStore()) {
      return null;
    }
    return this.getNodeId(CatalogTreeModel.#getStore());
  }

  getText(nodeId: string): string {
    if (!CatalogTreeModel.#getStore()) {
      return undefined;
    }

    if (CatalogHelper.getInstance().isStoreId(nodeId)) {
      return CatalogTreeModel.#computeStoreText();
    } else {
      const node = as(this.getNodeModel(nodeId), RemoteBean);
      if (is(node, Category)) {
        return CatalogTreeModel.#getCategoryName(cast(Category, node));
      } else if (is(node, Product)) {
        return cast(Product, node).getName();
      } else if (is(node, Marketing)) {
        return ECommerceStudioPlugin_properties.StoreTree_marketing_root;
      } else if (is(node, MarketingSpot)) {
        return cast(MarketingSpot, node).getName();
      }
    }

    return undefined;
  }

  static #getCategoryName(node: Category): string {
    //when multi-catalog is not configured there will be only one root category. Then the root category should be called.
    if (node.isLoaded()) {
      // 'Product Catalog' for the sake of backward compatibility.
      const isSingleRootCategory: boolean = !node.getStore() || !node.getStore().getCatalogs() ||
              node.getStore().getCatalogs().length <= 1;
      return isSingleRootCategory && categoryTreeRelation.isRoot(node) ?
        ECommerceStudioPlugin_properties.StoreTree_root_category :
        node.getDisplayName();
    }
    return node.getUriPath();
  }

  getIconCls(nodeId: string): string {
    return this.#computeIconCls(nodeId, undefined);
  }

  getTextCls(nodeId: string): string {
    return "";
  }

  getChildren(nodeId: string): NodeChildren {
    if (!CatalogTreeModel.#getStore()) {
      return undefined;
    }

    if (CatalogHelper.getInstance().isStoreId(nodeId)) {
      const store = as(this.getNodeModel(nodeId), Store);
      return this.getChildrenFor(store.getTopLevel(),
        store.getChildrenData(),
        ECommerceStudioPlugin_properties.Category_icon,
        nodeId);
    }
    if (CatalogHelper.isMarketingSpot(nodeId)) {
      return new NodeChildren([], {}, {});
    }
    if (CatalogHelper.isMarketing(nodeId)) {
      return new NodeChildren([], {}, {});
    }
    return this.#getCategoryChildren(nodeId);
  }

  #getCategoryChildren(nodeId: string): NodeChildren {
    const category = as(this.getNodeModel(nodeId), Category);
    let subCategories = categoryTreeRelation.getChildrenOf(category);

    if (!subCategories) {
      return undefined;
    }

    //sorting will disable lazy loading
    if (subCategories.length > 0 && this.getSortCategoriesByName()) {
      if (!this.#preloadChildren(subCategories)) {
        return undefined;
      }
      subCategories = this.#sortSubcategories(subCategories);
    }

    return this.getChildrenFor(subCategories,
      category.getChildrenData(),
      ECommerceStudioPlugin_properties.Category_icon,
      nodeId);
  }

  #sortSubcategories(subCategories: Array<any>): Array<any> {
    //don't change the original list of sub categories.
    subCategories = subCategories.slice();
    subCategories = subCategories.sort(
      (a: Category, b: Category): int => {
        const aDisplayName = a.getDisplayName();
        //todo: in tests somehow displayName is undefined...
        if (!aDisplayName) {
          return -1;
        }
        return aDisplayName.localeCompare(b.getDisplayName());
      });
    return subCategories;
  }

  /**
   * We children are preloaded, this fixes the problem that raises for breadcrumbs:
   * If you select a leaf category the first time in the search mode, the node is not
   * found in the tree since it has been not loaded yet.
   * As a result, the BindTreeSelectionPlugin selected the default node, which is the content root.
   * @param subCategories
   * @return true if all children are loaded.
   */
  #preloadChildren(subCategories: Array<any>): boolean {
    return subCategories.every((subCategory: Category): boolean => {
      subCategory.load();
      return subCategory.isLoaded();
    });
  }

  protected getChildrenFor(children: Array<any>, childData: Array<any>, iconCls: string, parentNodeId: string): NodeChildren {
    if (!children) {
      return undefined;
    }
    if (!childData) {
      return undefined;
    }

    const childDataById = this.#computeDataByChildId(childData);
    const childIds = [];
    const namesById: Record<string, any> = {};
    const iconById: Record<string, any> = {};

    children.forEach((child: RemoteBean): void => {
      const childData: CategoryChildData = childDataById[String(child)];

      const childId = this.#calculateChildId(childData, parentNodeId);

      childIds.push(childId);

      if (child.isLoaded()) {
        namesById[childId] = childData.displayName;
        iconById[childId] = this.#computeIconCls(childId, iconCls);
      } else {
        this.setEmptyNodeChildData(childId, namesById, iconById, null, null, null);
      }
    });

    return new NodeChildren(childIds, namesById, iconById);
  }

  /**
   * Some Catalog Trees (Hybris) have duplicate nodes. This is solved by a "hyperlink" concept.
   * <p>When we ask a node for its children and we detect a duplicate "hyperlink" via the property
   * {@code isVirtual} we add the node with a hyperlink prefix. Like that we ensure that we only have unique
   * node ids.
   * When clicking on a hyperlink the selection jumps to the original node (see {@link com.coremedia.ui.plugins.BindTreeSelectionPluginBase#treeSelectionChanged}).
   * @param childData for the child that will be created
   * @return the Id that either has the hyperlink prefix or not, depending if the child is marked as "virtual"
   */
  #calculateChildId(childData: CategoryChildData, parentId: string): string {
    const nodeId = this.getNodeId(childData.child);
    if (childData.hasOwnProperty("isVirtual") && childData.isVirtual) {
      return CatalogTreeModel.#getHyperLinkId(nodeId, parentId);
    }
    return nodeId;
  }

  /**
   * Use this method to add a hyperlink prefix, including the parent id to your id.
   * Like that you get a unique id for your hyperlink node. Use {@link CatalogTreeModel#removeHyperLinkId}
   * to receive the target id from your hyperlink ID.
   * @param childId to add the hyperlink prefix to
   * @return a unique hyperlink id, that still holds the information about the target node.
   */
  static #getHyperLinkId(childId: string, parentId: string): string {
    return CatalogTreeModel.HYPERLINK_PREFIX + parentId + CatalogTreeModel.HYPERLINK_SEPARATOR + childId;
  }

  /**
   * Method that removes the hyperlink prefix and returns the id where the hyperlink points to
   * @param id to remove the hyperlink prefix.
   * @return the target id where the hyperlink points to.
   */
  static #removeHyperLinkId(id: string): string {
    const i = id.indexOf(CatalogTreeModel.HYPERLINK_SEPARATOR);
    if (i !== -1) {
      return id.substring(i + CatalogTreeModel.HYPERLINK_SEPARATOR.length);
    }
    return id;
  }

  /**
   * @param id to check for the HYPERLINK_PREFIX
   * @return true whether the id is a hyperlink id
   */
  static #isHyperLinkId(id: string): boolean {
    return id.indexOf(CatalogTreeModel.HYPERLINK_PREFIX) !== -1;
  }

  #computeIconCls(childId: string, defaultIconCls: string): string {

    if (CatalogHelper.isMarketing(CatalogTreeModel.#removeHyperLinkId(childId))) {
      return ECommerceStudioPlugin_properties.Marketing_icon;
    }
    if (CatalogTreeModel.#removeHyperLinkId(childId) == this.getRootId()) {
      return ECommerceStudioPlugin_properties.Store_icon;
    }
    const child = beanFactory._.getRemoteBean(CatalogTreeModel.#removeHyperLinkId(childId));
    if (is(child, Category)) {
      if (child.isLoaded()) {
        //is the child an augmented category?
        if (augmentationService.getContent(cast(Category, child))) {
          if (CatalogTreeModel.#isHyperLinkId(childId)) {
            return CoreIcons_properties.augmented_link;
          }
          return ECommerceStudioPlugin_properties.AugmentedCategory_icon;
        }
      }

      if (CatalogTreeModel.#isHyperLinkId(childId)) {
        return CoreIcons_properties.link;
      }
    }
    return defaultIconCls;
  }

  #computeDataByChildId(childData: Array<any>): any {
    const childDataByNodeId: Record<string, any> = {};
    childData.forEach((childData: CategoryChildData): void => {
      const child = as(childData.child, CatalogObject);
      if (is(child, Marketing)) {
        childData.displayName = ECommerceStudioPlugin_properties.StoreTree_marketing_root;
      } else if (is(child, Category)) {
        childData.displayName = CatalogTreeModel.#getCategoryName(cast(Category, child));
      }
      childDataByNodeId[String(childData.child)] = childData;
    });
    return childDataByNodeId;
  }

  /**
   * Creates an array that contains the tree path for the node with the given id.
   * @param nodeId The id to build the path for.
   * @return
   */
  getIdPath(nodeId: string): Array<any> {
    if (!CatalogTreeModel.#getStore()) {
      return undefined;
    }
    return this.getIdPathFromModel(this.getNodeModel(nodeId));
  }

  getIdPathFromModel(model: any): Array<any> {
    if (!is(model, CatalogObject)) {
      return null;
    }
    if (!CatalogTreeModel.#getStore()) {
      return undefined;
    }

    let path = [];
    const node = as(model, RemoteBean);
    let treeNode: RemoteBean;
    if (is(node, Product)) {
      treeNode = cast(Product, node).getCategory();
    } else if (is(node, MarketingSpot)) {
      treeNode = CatalogTreeModel.#getStore().getMarketing();
    } else {
      treeNode = node;
    }

    const category = as(treeNode, Category);
    if (category) {
      //we have to reverse the path to root as we want from the root.
      const pathToRoot = categoryTreeRelation.pathToRoot(treeNode);
      if (pathToRoot === undefined) {
        return undefined;
      } else if (!pathToRoot) {
        return null;
      }
      path = pathToRoot.reverse();
      //In this case "path" contains the root category at the top. So we need the store above it.
      treeNode = CatalogTreeModel.#getStore();
    }
    path.unshift(treeNode);
    //add the store as top node if not happened already
    if (treeNode !== CatalogTreeModel.#getStore()) {
      path.unshift(CatalogTreeModel.#getStore());
    }
    return path.map(bind(this, this.getNodeId));
  }

  static #getStore(): Store {
    return CatalogHelper.getInstance().getActiveStoreExpression().getValue();
  }

  static #computeStoreText(): string {
    return CatalogTreeModel.#getStore().getName();
  }

  getNodeId(model: any): string {
    const bean = (as(model, RemoteBean));
    if (!bean || !is(bean, CatalogObject) || is(bean, Product) || is(bean, MarketingSpot)) {
      return null;
    }
    return bean.getUriPath();
  }

  getNodeModel(nodeId: string): any {
    nodeId = CatalogTreeModel.#removeHyperLinkId(nodeId);
    if (!nodeId || nodeId.indexOf(CatalogTreeModel.ID_PREFIX) != 0) {
      return null;
    }
    return beanFactory._.getRemoteBean(nodeId);
  }

  toString(): string {
    return CatalogTreeModel.ID_PREFIX;
  }

  getTreeId(): string {
    return CatalogTreeModel.CATALOG_TREE_ID;
  }

  loadNodeModelsById(nodeList: Array<any>): boolean {
    let reloadNecessary = false;
    nodeList.forEach((nodeId: string): void => {
      const category = as(this.getNodeModel(nodeId), RemoteBean);

      //" " is used as a placeholder text, for an entirely empty String the folder would show "Root" as text.
      //we check for loaded content that still has placeholder data shown, in that case we need to manually trigger "reload" of the tree
      if (category.isLoaded()) {
        reloadNecessary = true;
      } else {
        category.load();
      }
    });

    return !reloadNecessary;
  }

  setEmptyNodeChildData(childId: string, textsByChildId: any, iconsByChildId: any, clsByChildId: any, leafByChildId: any, qtipsByChildId: any): void {
    textsByChildId[childId] = " ";
    iconsByChildId[childId] = CoreIcons_properties.tree_view_spinner + " " + "cm-spin";
  }

  getSortCategoriesByName(): boolean {
    return editorContext._.getPreferences().get(CatalogPreferencesBase.SORT_CATEGORIES_BY_NAME_KEY);
  }
}
mixin(CatalogTreeModel, CompoundChildTreeModel, LazyLoadingTreeModel);

export default CatalogTreeModel;
