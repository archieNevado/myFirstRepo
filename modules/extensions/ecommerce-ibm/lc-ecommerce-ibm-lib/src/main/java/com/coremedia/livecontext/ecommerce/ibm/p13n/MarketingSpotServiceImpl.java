package com.coremedia.livecontext.ecommerce.ibm.p13n;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.catalog.CatalogServiceImpl;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpotService;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.collections4.Transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.MARKETING_SPOT;
import static com.coremedia.livecontext.ecommerce.ibm.common.IbmCommerceIdProvider.commerceId;
import static java.util.Arrays.asList;

public class MarketingSpotServiceImpl implements MarketingSpotService {

  private CommerceCache commerceCache;
  private CommerceBeanFactory commerceBeanFactory;
  private WcMarketingSpotWrapperService marketingSpotWrapperService;
  private boolean useExternalIdForBeanCreation;

  @Override
  @NonNull
  public List<MarketingSpot> findMarketingSpots(@NonNull StoreContext storeContext) {
    Map<String, Object> wcMarketingSpot = commerceCache.get(
            new MarketingSpotsCacheKey(storeContext, UserContextHelper.getCurrentContext(),
                    marketingSpotWrapperService, commerceCache));

    return createMarketingSpotBeansFor(wcMarketingSpot, storeContext);
  }

  @Nullable
  @Override
  public MarketingSpot findMarketingSpotById(@NonNull CommerceId id, @NonNull StoreContext storeContext) {
    UserContext userContext = UserContextHelper.getCurrentContext();
    Map<String, Object> wcMarketingSpot = commerceCache.get(
            new MarketingSpotCacheKey(id, storeContext, userContext, marketingSpotWrapperService, commerceCache));

    return createMarketingSpotBeanFor(wcMarketingSpot, false, storeContext);
  }

  @VisibleForTesting
  static CommerceId toMarketingSpotId(String externalId) {
    return commerceId(MARKETING_SPOT).withExternalId(externalId).build();
  }

  @VisibleForTesting
  static CommerceId toMarketingSpotTechId(String externalTechId) {
    return commerceId(MARKETING_SPOT).withTechId(externalTechId).build();
  }

  @Override
  @NonNull
  public SearchResult<MarketingSpot> searchMarketingSpots(@NonNull String searchTerm,
                                                          @Nullable Map<String, String> searchParams,
                                                          @NonNull StoreContext storeContext) {
    Map<String, Object> wcMarketingSpots = marketingSpotWrapperService.searchMarketingSpots(searchTerm, searchParams,
            storeContext, UserContextHelper.getCurrentContext());

    List<MarketingSpot> spots = createMarketingSpotBeansFor(wcMarketingSpots, storeContext);

    SearchResult<MarketingSpot> result = new SearchResult<>();
    result.setSearchResult(spots);
    result.setTotalCount(spots.size());
    result.setPageNumber(1);
    result.setPageSize(1);
    return result;
  }

  @Nullable
  protected MarketingSpot createMarketingSpotBeanFor(@Nullable Map<String, Object> marketingSpotWrapper, boolean reloadById,
                                                     @NonNull StoreContext storeContext) {
    if (marketingSpotWrapper == null) {
      return null;
    }

    // results may come from different REST handlers identified by resourceName field ('spot' or 'espot')
    // must be distinguished when retrieving data
    CommerceId id = useExternalIdForBeanCreation ?
            toMarketingSpotId(getStringValueForKey(marketingSpotWrapper,
                    isESpotResult(marketingSpotWrapper) ? "MarketingSpotData[0].eSpotName" : "MarketingSpot[0].spotName")) :
            toMarketingSpotTechId(getStringValueForKey(marketingSpotWrapper,
                    isESpotResult(marketingSpotWrapper) ? "MarketingSpotData[0].marketingSpotIdentifier" : "MarketingSpot[0].spotId"));

    final MarketingSpotImpl spot = (MarketingSpotImpl) commerceBeanFactory.createBeanFor(id, storeContext);
    Transformer transformer = null;
    if (reloadById) {
      transformer = new Transformer() {
        private Map<String, Object> delegateFromCache;

        @Override
        public Object transform(Object input) {
          if (null == delegateFromCache) {
            delegateFromCache = spot.getDelegateFromCache();
          }

          //noinspection SuspiciousMethodCalls
          return delegateFromCache.get(input);
        }
      };
    }

    spot.setDelegate(CatalogServiceImpl.asLazyMap(marketingSpotWrapper, transformer));

    return spot;
  }

  protected List<MarketingSpot> createMarketingSpotBeansFor(Map<String, Object> marketingSpotWrappers,
                                                            @NonNull StoreContext storeContext) {
    if (marketingSpotWrappers == null || marketingSpotWrappers.isEmpty()) {
      return Collections.emptyList();
    }

    List<MarketingSpot> result = new ArrayList<>();

    List<Map<String, Object>> marketingSpotWrapperList = getInnerElements(marketingSpotWrappers);
    if (marketingSpotWrapperList != null) {
      for (Map<String, Object> wrapper : marketingSpotWrapperList) {
        Map<String, Object> outerWrapper = new HashMap<>();
        // these properties are required by the model bean in order to distinguish which REST handler provided
        // the data and which keys to use for reading
        outerWrapper.put("resourceId", marketingSpotWrappers.get("resourceId"));
        outerWrapper.put("resourceName", marketingSpotWrappers.get("resourceName"));
        outerWrapper.put(isESpotResult(outerWrapper) ? "MarketingSpotData" : "MarketingSpot", asList(wrapper));
        result.add(createMarketingSpotBeanFor(outerWrapper, true, storeContext));
      }
    }

    return Collections.unmodifiableList(result);
  }

  private static boolean isESpotResult(Map<String, Object> marketingSpotWrappers) {
    return "espot".equals(getStringValueForKey(marketingSpotWrappers, "resourceName"));
  }

  private static List<Map<String, Object>> getInnerElements(Map<String, Object> wcMarketingSpot) {
    // results may come from different REST handlers identified by resourceName field ('spot' or 'espot')
    // must be distinguished when retrieving data
    // noinspection unchecked
    return getListValueForKey(wcMarketingSpot, isESpotResult(wcMarketingSpot) ? "MarketingSpotData" : "MarketingSpot");
  }

  @Nullable
  private static List getListValueForKey(@NonNull Map<String, Object> map, @NonNull String key) {
    return DataMapHelper.findValue(map, key, List.class).orElse(null);
  }

  @Nullable
  private static String getStringValueForKey(@NonNull Map<String, Object> map, @NonNull String key) {
    return DataMapHelper.findStringValue(map, key).orElse(null);
  }

  public CommerceCache getCommerceCache() {
    return commerceCache;
  }

  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  public CommerceBeanFactory getCommerceBeanFactory() {
    return commerceBeanFactory;
  }

  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }

  public WcMarketingSpotWrapperService getMarketingSpotWrapperService() {
    return marketingSpotWrapperService;
  }

  public void setMarketingSpotWrapperService(WcMarketingSpotWrapperService marketingSpotWrapperService) {
    this.marketingSpotWrapperService = marketingSpotWrapperService;
  }

  @SuppressWarnings("unused")
  public boolean isUseExternalIdForBeanCreation() {
    return useExternalIdForBeanCreation;
  }

  @SuppressWarnings("unused")
  public void setUseExternalIdForBeanCreation(boolean useExternalIdForBeanCreation) {
    this.useExternalIdForBeanCreation = useExternalIdForBeanCreation;
  }

}
