package com.coremedia.livecontext.ecommerce.ibm.inventory;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmService;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityService;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;

public class AvailabilityServiceImpl extends AbstractIbmService implements AvailabilityService {

    private WcAvailabilityWrapperService availabilityWrapperService;
    private CommerceCache commerceCache;
    private CommerceBeanFactory commerceBeanFactory;

    @Override
    @Nullable
    public AvailabilityInfo getAvailabilityInfo(@Nonnull ProductVariant productVariant) {
        Map<String, Object> inventoryAvailability = commerceCache.get(
                new AvailabilityByIdsCacheKey(productVariant.getExternalTechId(), productVariant.getContext(), availabilityWrapperService, commerceCache));

        Map<ProductVariant, AvailabilityInfo> productVariantAvailabilityMap = getProductVariantAvailabilityMap(inventoryAvailability, productVariant.getContext());
        return productVariantAvailabilityMap.get(productVariant);
    }

    @Override
    @Nonnull
    public Map<ProductVariant, AvailabilityInfo> getAvailabilityInfo(@Nonnull List<ProductVariant> productVariants) {
        if (productVariants.isEmpty()){
            return Collections.emptyMap();
        }

        //we assume that all variants are belonging to the same storecontext
        StoreContext context = productVariants.get(0).getContext();
        String skuIds = getListOfCommaSeperatedProductIds(productVariants);

        Map<String, Object> inventoryAvailability = commerceCache.get(
                new AvailabilityByIdsCacheKey(skuIds, context, availabilityWrapperService, commerceCache));

        return getProductVariantAvailabilityMap(inventoryAvailability, context);
    }

    private String getListOfCommaSeperatedProductIds(List<ProductVariant> productVariants) {
        List<String> productVariantIds = new ArrayList<>();

        for (Product product : productVariants) {
            productVariantIds.add(product.getExternalTechId());
        }

        Collections.sort(productVariantIds);
        return StringUtils.join(productVariantIds, ",");
    }

    private Map<ProductVariant, AvailabilityInfo> getProductVariantAvailabilityMap(Map<String, Object> wcInventoryAvailabilityList, StoreContext context) {
        if (wcInventoryAvailabilityList == null || wcInventoryAvailabilityList.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<ProductVariant, AvailabilityInfo> result = new HashMap<>();
        List<Map<String, Object>> inventoryAvailabilityList = DataMapHelper
                .findValue(wcInventoryAvailabilityList, "InventoryAvailability", List.class)
                .orElse(null);

        for (Map<String, Object> inventoryAvailability : inventoryAvailabilityList) {
            //TODO: Online Store only??
            if (DataMapHelper.findStringValue(inventoryAvailability, "onlineStoreId").isPresent()) {
              String productId = DataMapHelper.findStringValue(inventoryAvailability, "productId").orElse(null);
              CommerceId commerceId = getCommerceIdProvider().formatProductVariantTechId(context.getCatalogAlias(), productId);
              ProductVariant sku = (ProductVariant) commerceBeanFactory.createBeanFor(commerceId, context);
              result.put(sku, new AvailabilityInfoImpl(inventoryAvailability));
            }
        }
        return result;
    }

    public CommerceCache getCommerceCache() {
        return commerceCache;
    }

    public void setCommerceCache(CommerceCache commerceCache) {
        this.commerceCache = commerceCache;
    }

    public WcAvailabilityWrapperService getAvailabilityWrapperService() {
        return availabilityWrapperService;
    }

    public void setAvailabilityWrapperService(WcAvailabilityWrapperService availabilityWrapperService) {
        this.availabilityWrapperService = availabilityWrapperService;
    }

    public CommerceBeanFactory getCommerceBeanFactory() {
        return commerceBeanFactory;
    }

    public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
        this.commerceBeanFactory = commerceBeanFactory;
    }

  @Nonnull
  @Override
  public AvailabilityService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, AvailabilityService.class);
  }
}
