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
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.Map;

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
    if (wcMarketingSpot != null) {
      String resourceName = DataMapHelper.getValueForKey(wcMarketingSpot, "resourceName", String.class);
      if (resourceName != null) {
        String dependencyFieldIdentifier = DataMapHelper.getValueForKey(wcMarketingSpot,
                "espot".equals(resourceName) ? "MarketingSpotData[0].marketingSpotIdentifier" : "MarketingSpot[0].spotId", String.class);
        String valueForKey = StringUtils.isEmpty(dependencyFieldIdentifier) ?
                null : dependencyFieldIdentifier;
        if (!StringUtils.isEmpty(valueForKey)) {
          Cache.dependencyOn(valueForKey);
        }
      }
    }
  }

}