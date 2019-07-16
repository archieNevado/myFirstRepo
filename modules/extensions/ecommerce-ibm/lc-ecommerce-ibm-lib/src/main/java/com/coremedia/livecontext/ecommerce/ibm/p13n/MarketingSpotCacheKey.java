package com.coremedia.livecontext.ecommerce.ibm.p13n;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmDocumentCacheKey;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Map;
import java.util.Optional;

public class MarketingSpotCacheKey extends AbstractIbmDocumentCacheKey<Map<String, Object>> {

  private WcMarketingSpotWrapperService wrapperService;

  public MarketingSpotCacheKey(@NonNull CommerceId id, @NonNull StoreContext storeContext, UserContext userContext,
                               WcMarketingSpotWrapperService wrapperService, CommerceCache commerceCache) {
    super(id, storeContext, userContext, CONFIG_KEY_MARKETING_SPOT, commerceCache);
    this.wrapperService = wrapperService;

    if (!BaseCommerceBeanType.MARKETING_SPOT.equals(id.getCommerceBeanType())) {
      throw new InvalidIdException(id + " is not a marketing spot id.");
    }
  }

  @Override
  public Map<String, Object> computeValue(Cache cache) {
    return wrapperService.findMarketingSpotById(getCommerceId(), storeContext, userContext);
  }

  @Override
  public void addExplicitDependency(Map<String, Object> wcMarketingSpot) {
    if (wcMarketingSpot == null) {
      return;
    }

    findDependency(wcMarketingSpot).ifPresent(Cache::dependencyOn);
  }

  @NonNull
  private static Optional<String> findDependency(@NonNull Map<String, Object> wcMarketingSpot) {
    return getDependencyFieldIdentifierKey(wcMarketingSpot)
            .flatMap(key -> getDependencyFieldIdentifier(wcMarketingSpot, key));
  }

  @NonNull
  private static Optional<String> getDependencyFieldIdentifierKey(@NonNull Map<String, Object> wcMarketingSpot) {
    return DataMapHelper.findString(wcMarketingSpot, "resourceName")
            .map(resourceName -> "espot".equals(resourceName)
                    ? "MarketingSpotData[0].marketingSpotIdentifier"
                    : "MarketingSpot[0].spotId");
  }

  @NonNull
  private static Optional<String> getDependencyFieldIdentifier(@NonNull Map<String, Object> wcMarketingSpot,
                                                               @NonNull String dependencyFieldIdentifierKey) {
    return DataMapHelper.findString(wcMarketingSpot, dependencyFieldIdentifierKey)
            .filter(value -> !value.isEmpty());
  }
}
