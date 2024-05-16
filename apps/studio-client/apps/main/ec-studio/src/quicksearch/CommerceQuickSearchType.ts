import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import int from "@jangaroo/runtime/int";
import RemoteServiceMethod from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethod";
import RemoteServiceMethodResponse
  from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethodResponse";
import SearchResult from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/SearchResult";
import SearchParameters from "@coremedia/studio-client.cap-rest-client/content/search/SearchParameters";
import sitesService from "@coremedia/studio-client.multi-site-models/global/sitesService";
import RemoteBeanUtil from "@coremedia/studio-client.client-core/data/RemoteBeanUtil";
import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import QuickSearch from "@coremedia/studio-client.quicksearch-models/QuickSearch";
import QuickSearchType from "@coremedia/studio-client.quicksearch-models/QuickSearchType";
import CatalogModel from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogModel";
import CatalogHelper from "../helper/CatalogHelper";
import QuickSearchTypeResult from "@coremedia/studio-client.quicksearch-models/QuickSearchTypeResult";

interface CommerceQuickSearchTypeConfig extends Config<QuickSearchType<CatalogObject>> {
}

/**
 * @public
 */
class CommerceQuickSearchType extends QuickSearchType<CatalogObject> {
  declare Config: CommerceQuickSearchTypeConfig;

  static readonly RESULT_LIST_TYPE_COMMERCE: string = "commerce";

  constructor(config: Config<CommerceQuickSearchType> = null) {
    super(CatalogObject.name, ConfigUtils.apply(Config<CommerceQuickSearchType>({}), config));
  }

  override getPriority(): int {
    return 50;
  }

  override async isEnabled(): Promise<boolean> {
    const preferredSite = sitesService._.getPreferredSite();
    if (!preferredSite) {
      return false;
    }

    const store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();
    return (!store || !CatalogHelper.getInstance().isCoreMediaStore(store));
  }

  override async search(quickSearch: QuickSearch, text: string): Promise<QuickSearchTypeResult> {
    const preferredSite = sitesService._.getPreferredSite();
    const store = await CatalogHelper.getInstance().getActiveStoreExpression().loadValue();

    let hits = [];
    let total = 0;
    if (preferredSite && store && !CatalogHelper.getInstance().isCoreMediaStore(store)) {
      const catalogSearch = new RemoteServiceMethod("livecontext/search/" + store.getSiteId(), "GET");

      const searchParameters = new SearchParameters();
      searchParameters.query = text;
      searchParameters["searchType"] = CatalogModel.TYPE_PRODUCT;
      searchParameters.limit = this.getLimit();
      searchParameters["siteId"] = preferredSite.getId();

      const response = await new Promise<RemoteServiceMethodResponse>((resolve, reject) => catalogSearch.request(searchParameters, resolve, reject));
      const responseObject = response.getResponseJSON();
      if (responseObject) {
        hits = responseObject["hits"];
        total = responseObject["total"];
        await Promise.all(hits.map(bean => bean.load()));
      }
    }

    return new QuickSearchTypeResult(this, hits, total);
  }
}

export default CommerceQuickSearchType;
