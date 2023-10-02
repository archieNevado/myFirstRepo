import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import SearchParameters from "@coremedia/studio-client.cap-rest-client/content/search/SearchParameters";
import CollectionViewManagerInternal from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewManagerInternal";
import RepositoryListSorterImpl from "@coremedia/studio-client.main.editor-components/sdk/collectionview/sort/RepositoryListSorterImpl";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import { as } from "@jangaroo/runtime";
import CatalogCollectionViewExtension from "./CatalogCollectionViewExtension";

/**
 * Extends the default RepositoryListSorterImpl to override the resolving of children.
 * The parent/children relationship differs for the catalog documents and is implemented here.
 */
class CatalogRepositoryListSorter extends RepositoryListSorterImpl {
  #extension: CatalogCollectionViewExtension = null;

  constructor(extension: CatalogCollectionViewExtension) {
    super();
    this.#extension = extension;
  }

  override sort(folder: Content, children: Array<any>): Array<any> {
    const cvManager = (as(editorContext._.getCollectionViewManager(), CollectionViewManagerInternal));
    const sortValues = cvManager.getCollectionView().getSortStateManager().getCurrentSortCriteria();
    return this.triggerSolrSort(folder, children, sortValues);
  }

  protected override computeSearchParameters(folder: Content, sortValues: Array<any>): SearchParameters {
    const searchParameters = super.computeSearchParameters(folder, sortValues);
    return this.#extension.applySearchParameters(folder, [], searchParameters);
  }

  override getChildren(folder: Content): Array<any> {
    //get categories from the parent category
    if (!folder.isLoaded()) {
      folder.load();
      return undefined;
    }

    const linkedChildren: Array<any> = folder.getProperties().get("children");
    if (linkedChildren === undefined) {
      return undefined;
    }

    //get the children of the selected category
    const linkingChildren = folder.getReferrersWithNamedDescriptor("CMHasContexts", "contexts");
    if (linkingChildren === undefined) {
      return undefined;
    }

    return linkedChildren.concat(linkingChildren);
  }

  override filter(folder: Content, children: Array<any>): Array<any> {
    let returnUndefined = false;
    children = children.filter((item: Content): boolean => {
      const state = item.getState();
      if (state.readable === false) {
        return false;
      }

      const deleted = item.isDeleted();
      if (deleted === undefined) {
        returnUndefined = true;
      }
      return deleted === false;
    });
    return returnUndefined ? undefined : children;
  }

}

export default CatalogRepositoryListSorter;
