import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import Container from "@jangaroo/ext-ts/container/Container";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import { bind } from "@jangaroo/runtime";
import QuickSearch from "@coremedia/studio-client.quicksearch-models/QuickSearch";
import QuickSearchCommerceDataView from "./QuickSearchCommerceDataView";
import SearchState from "@coremedia/studio-client.library-services-api/SearchState";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import QuickSearchResultList from "@coremedia/studio-client.quicksearch-models/QuickSearchResultList";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import QuickSearchType from "@coremedia/studio-client.quicksearch-models/QuickSearchType";
import CatalogHelper from "../helper/CatalogHelper";
import CatalogModel from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogModel";
import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import CollectionViewConstants
  from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewConstants";
import QuickSearchDialog from "@coremedia/studio-client.main.quicksearch-components/components/QuickSearchDialog";
import QuickSearchResultsFooter
  from "@coremedia/studio-client.main.quicksearch-components/components/QuickSearchResultsFooter";

interface QuickSearchCommerceResultsConfig extends Config<Container>, Partial<Pick<QuickSearchCommerceResults,
  "quickSearch" |
  "searchType">> {
}

class QuickSearchCommerceResults extends Container implements QuickSearchResultList {
  declare Config: QuickSearchCommerceResultsConfig;

  static override readonly xtype: string = "com.coremedia.cms.editor.config.quickSearchCommerceResults";

  quickSearch: QuickSearch;

  searchType: QuickSearchType<CatalogObject>;

  getSearchModelTypeName(): string {
    return Content.name;
  }

  #showAll(): void {
    const searchState = new SearchState();
    searchState.searchText = this.quickSearch.getSearchText();
    searchState.folder = CatalogHelper.getInstance().getActiveStoreExpression().getValue();
    searchState.contentType = CatalogModel.TYPE_PRODUCT;
    editorContext._.getCollectionViewManager().openSearch(searchState, true, CollectionViewConstants.LIST_VIEW);
    QuickSearchDialog.exit();
  }

  constructor(config: Config<QuickSearchCommerceResults> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    super(ConfigUtils.apply(Config(QuickSearchCommerceResults, {
      items: [
        Config(QuickSearchCommerceDataView, {
          searchType: config.searchType,
          quickSearch: config.quickSearch,
        }),
        Config(QuickSearchResultsFooter, {
          searchType: config.searchType,
          quickSearch: config.quickSearch,
          showAll: bind(this$, this$.#showAll),
        }),
      ],
      layout: Config(VBoxLayout, {
        align: "stretch",
      }),
    }), config));
  }
}

export default QuickSearchCommerceResults;
