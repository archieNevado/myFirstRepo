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

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;

public class MarketingSpotCacheKey extends AbstractIbmDocumentCacheKey<Map<String, Object>> {

  private WcMarketingSpotWrapperService wrapperService;

  public MarketingSpotCacheKey(@Nonnull CommerceId id,
                               StoreContext storeContext,
                               UserContext userContext,
                               WcMarketingSpotWrapperService wrapperService,
                               CommerceCache commerceCache) {
    super(id, storeContext, userContext, CONFIG_KEY_MARKETING_SPOT, commerceCache);
    this.wrapperService = wrapperService;
    if (!BaseCommerceBeanType.MARTETING_SPOT.equals(id.getCommerceBeanType())) {
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

  @Nonnull
  private static Optional<String> findDependency(@Nonnull Map<String, Object> wcMarketingSpot) {
    return getDependencyFieldIdentifierKey(wcMarketingSpot)
            .flatMap(key -> getDependencyFieldIdentifier(wcMarketingSpot, key));
  }

  @Nonnull
  private static Optional<String> getDependencyFieldIdentifierKey(@Nonnull Map<String, Object> wcMarketingSpot) {
    return DataMapHelper.findStringValue(wcMarketingSpot, "resourceName")
            .map(resourceName -> "espot".equals(resourceName)
                    ? "MarketingSpotData[0].marketingSpotIdentifier"
                    : "MarketingSpot[0].spotId");
  }

  @Nonnull
  private static Optional<String> getDependencyFieldIdentifier(@Nonnull Map<String, Object> wcMarketingSpot,
                                                               @Nonnull String dependencyFieldIdentifierKey) {
    return DataMapHelper.findStringValue(wcMarketingSpot, dependencyFieldIdentifierKey)
            .filter(value -> !value.isEmpty());
  }
}
