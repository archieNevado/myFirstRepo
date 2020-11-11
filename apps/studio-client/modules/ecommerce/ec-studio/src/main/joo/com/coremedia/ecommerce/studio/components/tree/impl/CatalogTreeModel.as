package com.coremedia.ecommerce.studio.components.tree.impl {
import com.coremedia.cms.editor.sdk.collectionview.tree.CompoundChildTreeModel;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ecommerce.studio.augmentation.augmentationService;
import com.coremedia.ecommerce.studio.components.preferences.CatalogPreferencesBase;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ecommerce.studio.model.CategoryChildData;
import com.coremedia.ecommerce.studio.model.Marketing;
import com.coremedia.ecommerce.studio.model.MarketingSpot;
import com.coremedia.ecommerce.studio.model.Product;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.ecommerce.studio.tree.categoryTreeRelation;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.models.LazyLoadingTreeModel;
import com.coremedia.ui.models.NodeChildren;

import mx.resources.ResourceManager;

[ResourceBundle('com.coremedia.ecommerce.studio.ECommerceStudioPlugin')]
public class CatalogTreeModel implements CompoundChildTreeModel, LazyLoadingTreeModel {

  private var enabled:Boolean = true;
  public static const ID_PREFIX:String = "livecontext/";
  public static const CATALOG_TREE_ID:String = "catalogTreeId";

  public static const HYPERLINK_PREFIX:String = "hyperlink:";
  public static const HYPERLINK_SEPARATOR:String = "##";

  public function CatalogTreeModel() {
  }


  public function setEnabled(enabled:Boolean):void {
    this.enabled = enabled;
  }

  public function isEnabled():Boolean {
    return enabled;
  }

  public function isEditable(model:Object):Boolean {
    return false;
  }

  public function rename(model:Object, newName:String, oldName:String, callback:Function):void {
  }

  public function isRootVisible():Boolean {
    return true;
  }

  public function getRootId():String {
    if (!getStore()) {
      return null;
    }
    if (CatalogHelper.getInstance().isActiveCoreMediaStore()) {
      return null;
    }
    return getNodeId(getStore());
  }

  public function getText(nodeId:String):String {
    if (!getStore()) {
      return undefined
    }

    if (CatalogHelper.getInstance().isStoreId(nodeId)) {
      return computeStoreText();
    } else {
      var node:RemoteBean = getNodeModel(nodeId) as RemoteBean;
      if (node is Category) {
        return getCategoryName(Category(node));
      } else if (node is Product) {
        return Product(node).getName();
      } else if (node is Marketing) {
        return ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'StoreTree_marketing_root');
      } else if (node is MarketingSpot) {
        return MarketingSpot(node).getName();
      }
    }

    return undefined;
  }

  private static function getCategoryName(node:Category):String {
    //when multi-catalog is not configured there will be only one root category. Then the root category should be called.
    if (node.isLoaded()) {
      // 'Product Catalog' for the sake of backward compatibility.
      var isSingleRootCategory:Boolean = !node.getStore() || !node.getStore().getCatalogs() ||
              node.getStore().getCatalogs().length <= 1;
      return isSingleRootCategory && categoryTreeRelation.isRoot(node) ?
              ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'StoreTree_root_category') :
              node.getDisplayName();
    }
    return node.getUriPath();
  }

  public function getIconCls(nodeId:String):String {
    return computeIconCls(nodeId, undefined);
  }

  public function getTextCls(nodeId:String):String {
    return "";
  }

  public function getChildren(nodeId:String):NodeChildren {
    if (!getStore()) {
      return undefined
    }

    if (CatalogHelper.getInstance().isStoreId(nodeId)) {
      var store:Store = getNodeModel(nodeId) as Store;
      return getChildrenFor(store.getTopLevel(),
              store.getChildrenData(),
              ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'Category_icon'),
              nodeId);
    }
    if (CatalogHelper.isMarketingSpot(nodeId)) {
      return new NodeChildren([], {}, {});
    }
    if (CatalogHelper.isMarketing(nodeId)) {
      return new NodeChildren([], {}, {});
    }
    return getCategoryChildren(nodeId);
  }

  private function getCategoryChildren(nodeId:String):NodeChildren {
    var category:Category = getNodeModel(nodeId) as Category;
    var subCategories:Array = categoryTreeRelation.getChildrenOf(category);

    if (!subCategories) {
      return undefined;
    }

    //sorting will disable lazy loading
    if (subCategories.length > 0 && getSortCategoriesByName()) {
      if (!preloadChildren(subCategories)) {
        return undefined;
      }
      subCategories = sortSubcategories(subCategories);
    }

    return getChildrenFor(subCategories,
            category.getChildrenData(),
            ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'Category_icon'),
            nodeId);
  }

  private function sortSubcategories(subCategories:Array):Array {
    //don't change the original list of sub categories.
    subCategories = subCategories.slice();
    subCategories = subCategories.sort(
            function (a:Category, b:Category):int {
              var aDisplayName:String = a.getDisplayName();
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
  private function preloadChildren(subCategories:Array):Boolean {
    return subCategories.every(function (subCategory:Category):Boolean {
      subCategory.load();
      return subCategory.isLoaded();
    });
  }

  protected function getChildrenFor(children:Array, childData:Array, iconCls:String, parentNodeId:String):NodeChildren {
    if (!children) {
      return undefined;
    }
    if (!childData) {
      return undefined;
    }

    var childDataById:Object = computeDataByChildId(childData);
    var childIds:Array = [];
    var namesById:Object = {};
    var iconById:Object = {};

    children.forEach(function (child:RemoteBean):void {
      var childData:CategoryChildData = childDataById[child];

      var childId:String = calculateChildId(childData, parentNodeId);

      childIds.push(childId);

      if (child.isLoaded()) {
        namesById[childId] = childData.displayName;
        iconById[childId] = computeIconCls(childId, iconCls);
      } else {
        setEmptyNodeChildData(childId, namesById, iconById, null, null, null);
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
  private function calculateChildId(childData:CategoryChildData, parentId:String):String {
    var nodeId:String = getNodeId(childData.child);
    if (childData.hasOwnProperty("isVirtual") && childData.isVirtual) {
      return getHyperLinkId(nodeId, parentId);
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
  private static function getHyperLinkId(childId:String, parentId:String):String {
    return HYPERLINK_PREFIX + parentId + HYPERLINK_SEPARATOR + childId;
  }

  /**
   * Method that removes the hyperlink prefix and returns the id where the hyperlink points to
   * @param id to remove the hyperlink prefix.
   * @return the target id where the hyperlink points to.
   */
  private static function removeHyperLinkId(id:String):String {
    var i:int = id.indexOf(HYPERLINK_SEPARATOR);
    if (i !== -1) {
      return id.substring(i + HYPERLINK_SEPARATOR.length)
    }
    return id;
  }

  /**
   * @param id to check for the HYPERLINK_PREFIX
   * @return true whether the id is a hyperlink id
   */
  private static function isHyperLinkId(id:String):Boolean {
    return id.indexOf(HYPERLINK_PREFIX) !== -1;
  }

  private function computeIconCls(childId:String, defaultIconCls:String):String {

    if (CatalogHelper.isMarketing(removeHyperLinkId(childId))) {
      return ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'Marketing_icon');
    }
    if (removeHyperLinkId(childId) == getRootId()) {
      return ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'Store_icon');
    }
    var child:RemoteBean = beanFactory.getRemoteBean(removeHyperLinkId(childId));
    if (child is Category) {
      if (child.isLoaded()) {
        //is the child an augmented category?
        if (augmentationService.getContent(Category(child))) {
          if(isHyperLinkId(childId)){
            return ResourceManager.getInstance().getString('com.coremedia.icons.CoreIcons', 'augmented_link');
          }
          return ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'AugmentedCategory_icon');
        }
      }

      if(isHyperLinkId(childId)){
          return ResourceManager.getInstance().getString('com.coremedia.icons.CoreIcons', 'link');
      }
    }
    return defaultIconCls;
  }

  private function computeDataByChildId(childData:Array):Object {
    var childDataByNodeId:Object = {};
    childData.forEach(function (childData:CategoryChildData):void {
      var child:CatalogObject = childData.child as CatalogObject;
      if (child is Marketing) {
        childData.displayName = ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'StoreTree_marketing_root');
      } else if (child is Category) {
        childData.displayName = getCategoryName(Category(child));
      }
      childDataByNodeId[childData.child] = childData;
    });
    return childDataByNodeId;
  }



  /**
   * Creates an array that contains the tree path for the node with the given id.
   * @param nodeId The id to build the path for.
   * @return
   */
  public function getIdPath(nodeId:String):Array {
    if (!getStore()) {
      return undefined
    }
    return getIdPathFromModel(getNodeModel(nodeId));
  }

  public function getIdPathFromModel(model:Object):Array {
    if (!(model is CatalogObject)) {
      return null;
    }
    if (!getStore()) {
      return undefined
    }

    var path:Array = [];
    var node:RemoteBean = model as RemoteBean;
    var treeNode:RemoteBean;
    if (node is Product) {
      treeNode = Product(node).getCategory();
    } else if (node is MarketingSpot) {
      treeNode = getStore().getMarketing();
    } else {
      treeNode = node;
    }

    var category:Category = treeNode as Category;
    if (category) {
      //we have to reverse the path to root as we want from the root.
      var pathToRoot:Array = categoryTreeRelation.pathToRoot(treeNode);
      if (pathToRoot === undefined) {
        return undefined;
      } else if (!pathToRoot) {
        return null;
      }
      path = pathToRoot.reverse();
      //In this case "path" contains the root category at the top. So we need the store above it.
      treeNode = getStore();
    }
    path.unshift(treeNode);
    //add the store as top node if not happened already
    if (treeNode !== getStore()) {
      path.unshift(getStore());
    }
    return path.map(getNodeId);
  }


  private static function getStore():Store {
    return CatalogHelper.getInstance().getActiveStoreExpression().getValue();
  }

  private static function computeStoreText():String {
    var workspaceName:String;
    if (getStore().getCurrentWorkspace()) {
      workspaceName = getStore().getCurrentWorkspace().getName();
    }
    return getStore().getName() + (workspaceName ? ' - ' + workspaceName : '');

  }

  public function getNodeId(model:Object):String {
    var bean:RemoteBean = (model as RemoteBean);
    if (!bean || !(bean is CatalogObject) || (bean is Product) || (bean is MarketingSpot)) {
      return null;
    }
    return bean.getUriPath();
  }

  public function getNodeModel(nodeId:String):Object {
    nodeId = removeHyperLinkId(nodeId);
    if (!nodeId || nodeId.indexOf(ID_PREFIX) != 0) {
      return null;
    }
    return beanFactory.getRemoteBean(nodeId);
  }


  public function toString():String {
    return ID_PREFIX;
  }

  public function getTreeId():String {
    return CATALOG_TREE_ID;
  }

  public function loadNodeModelsById(nodeList:Array):Boolean {
    var reloadNecessary:Boolean = false;
    nodeList.forEach(function (nodeId:String):void {
      var category:RemoteBean = getNodeModel(nodeId) as RemoteBean;

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


  public function setEmptyNodeChildData(childId:String, textsByChildId:Object, iconsByChildId:Object, clsByChildId:Object, leafByChildId:Object, qtipsByChildId:Object):void {
    textsByChildId[childId] = " ";
    iconsByChildId[childId] = ResourceManager.getInstance().getString('com.coremedia.icons.CoreIcons', "tree_view_spinner") + " "  + "cm-spin";
  }

  internal function getSortCategoriesByName():Boolean {
    return editorContext.getPreferences().get(CatalogPreferencesBase.SORT_CATEGORIES_BY_NAME_KEY);
  }
}
}
