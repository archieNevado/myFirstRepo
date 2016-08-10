package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.pricing.PersonalizedPriceByExternalIdCacheKey;
import com.coremedia.livecontext.ecommerce.ibm.pricing.StaticPricesByExternalIdCacheKey;
import com.coremedia.livecontext.ecommerce.ibm.pricing.WcPrice;
import com.coremedia.livecontext.ecommerce.ibm.pricing.WcPrices;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.xml.Markup;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Base class for product and product variant implementation.
 */
abstract class ProductBase extends AbstractIbmCommerceBean implements Product {

  private static final Logger LOG = LoggerFactory.getLogger(ProductBase.class);

  private static final java.lang.String USAGE_DEFINING = "Defining";

  protected Map<String, Object> delegate;

  private WcPrices priceInfo;
  private List<ProductAttribute> definingAttributes;
  private List<ProductAttribute> describingAttributes;

  private WcCatalogWrapperService catalogWrapperService;

  protected abstract Map<String, Object> getDelegate();

  @Override
  public void setDelegate(Object delegate) {
    this.delegate = (Map<String, Object>) delegate;
  }

  abstract Map<String, Object> getDelegateFromCache();

  public WcCatalogWrapperService getCatalogWrapperService() {
    return catalogWrapperService;
  }

  @Required
  public void setCatalogWrapperService(WcCatalogWrapperService catalogWrapperService) {
    this.catalogWrapperService = catalogWrapperService;
  }

  protected WcPrices getPriceInfo() {
    if (priceInfo == null) {
      WcPrices wcPrices = new WcPrices();
      wcPrices.setDataMap(getDelegate());
      if (wcPrices.getPrices().size() > 1) {
        priceInfo = wcPrices;
      }
      if (priceInfo == null) {
        //TODO: would be nice to delegate a priceService here
        priceInfo = (WcPrices) getCommerceCache().get(
                new StaticPricesByExternalIdCacheKey(getExternalId(), getContext(), UserContextHelper.getCurrentContext(), getCatalogWrapperService(), getCommerceCache()));
      }
    }
    return priceInfo;
  }

  @Override
  public String getExternalId() {
    return DataMapHelper.getValueForKey(getDelegate(), "partNumber", String.class);
  }

  @Override
  public String getExternalTechId() {
    return DataMapHelper.getValueForKey(getDelegate(), "uniqueID", String.class);
  }

  @Override
  public String getName() {
    return DataMapHelper.getValueForKey(getDelegate(), "name", String.class);
  }

  @Override
  public Markup getShortDescription() {
    String shortDescription = DataMapHelper.getValueForKey(getDelegate(), "shortDescription", String.class);
    return toRichtext(shortDescription);
  }

  @Override
  public Markup getLongDescription() {
    String longDescription = DataMapHelper.getValueForKey(getDelegate(), "longDescription", String.class);
    return toRichtext(longDescription);
  }

  @Override
  public String getTitle() {
    return DataMapHelper.getValueForKey(getDelegate(), "title", String.class);
  }

  @Override
  public String getMetaDescription() {
    return DataMapHelper.getValueForKey(getDelegate(), "metaDescription", String.class);
  }

  @Override
  public String getMetaKeywords() {
    return DataMapHelper.getValueForKey(getDelegate(), "metaKeyword", String.class);
  }

  @Override
  public BigDecimal getListPrice() {
    WcPrices priceInfo = getPriceInfo();
    if (priceInfo != null) {
      Map<String, WcPrice> prices = priceInfo.getPrices();
      if (prices != null && !prices.isEmpty()) {
        WcPrice listPrice = prices.get("Display");
        if (listPrice != null) {
          return convertStringToBigDecimal(listPrice.getPriceValue());
        }
      }
    }
    return null;
  }

  @Override
  public BigDecimal getOfferPrice() {
    UserContext userContext = UserContextHelper.getCurrentContext();
    if (userContext != null && UserContextHelper.getForUserName(userContext) != null && StoreContextHelper.isDynamicPricingEnabled(getContext())) {
      return getPersonalizedOfferPrice();
    } else {
      WcPrices priceInfo = getPriceInfo();
      if (priceInfo != null) {
        Map<String, WcPrice> prices = priceInfo.getPrices();
        if (prices != null && !prices.isEmpty()) {
          WcPrice offerPrice = prices.get("Offer");
          if (offerPrice != null) {
            return convertStringToBigDecimal(offerPrice.getPriceValue());
          } else {
            return getPersonalizedOfferPrice();
          }
        }
      }
    }
    return null;
  }

  private BigDecimal getPersonalizedOfferPrice() {
    WcPrice offerPrice = (WcPrice) getCommerceCache().get(new PersonalizedPriceByExternalIdCacheKey(getExternalId(), getContext(), UserContextHelper.getCurrentContext(), getCatalogWrapperService(), getCommerceCache()));
    if (offerPrice != null) {
      String value = offerPrice.getPriceValue();
      if (value != null) {
        return convertStringToBigDecimal(value);
      }
    }
    return null;
  }

  private String getCmSeoSegment() {
    String cmLocalizedSeoSegment = DataMapHelper.getValueForKey(getDelegate(), "cm_seo_token_ntk", String.class);
    cmLocalizedSeoSegment = processCmLocalizedSeoSegment(cmLocalizedSeoSegment);

    if (cmLocalizedSeoSegment != null) {
      String[] localizedSeoSegments = cmLocalizedSeoSegment.split(";");
      List<String> localizedSeoSegmentList = Arrays.asList(localizedSeoSegments);
      if (localizedSeoSegmentList.size() > 1) {
        String storeId = getStoreId();
        for (String seoSegment : localizedSeoSegmentList) {
          if (seoSegment.startsWith(storeId)) {
            return seoSegment.substring(storeId.length() + 1);
          }
        }
        return localizedSeoSegmentList.get(0).substring(cmLocalizedSeoSegment.indexOf("_") + 1);
      } else {
        return cmLocalizedSeoSegment.substring(cmLocalizedSeoSegment.indexOf("_") + 1);
      }
    } else {
      return null;
    }
  }

  private String processCmLocalizedSeoSegment(String cmLocalizedSeoSegment) {
    if (isBlank(cmLocalizedSeoSegment)) {
      if (getDefaultLocale() == null) {
        LOG.warn("Default locale does not set for commerce beans.");
      }
      if (!getLocale().equals(getDefaultLocale())) {
        LOG.debug("Product {} does not have a cm seo segment for the current locale {}. Return the cm seo segment for the default locale {}.",
                getName(), getLocale(), getDefaultLocale());
        StoreContext newStoreContext = StoreContextHelper.getCurrentContextFor(getDefaultLocale());

        ProductBase master = (ProductBase) getCatalogService().withStoreContext(newStoreContext).findProductById(CommerceIdHelper.formatProductId(getExternalId()));
        if (master != null && !equals(master)) {
          cmLocalizedSeoSegment = master.getCmSeoSegment();
        }
      }
    }
    return cmLocalizedSeoSegment;
  }

  @Override
  public String getSeoSegment() {
    String localizedSeoSegment = getCmSeoSegment();
    if (isBlank(localizedSeoSegment)) {
      localizedSeoSegment = DataMapHelper.getValueForKey(getDelegate(), "seo_token_ntk", String.class);
      localizedSeoSegment = processLocalizedSeoSegment(localizedSeoSegment);

      if (localizedSeoSegment == null) {
        localizedSeoSegment = "";
      } else {
        String[] localizedSeoSegments = localizedSeoSegment.split(";");
        List<String> localizedSeoSegmentList = Arrays.asList(localizedSeoSegments);
        if (localizedSeoSegmentList.size() > 1) {
          localizedSeoSegment = localizedSeoSegmentList.get(0);
        }
      }
    }

    return localizedSeoSegment;
  }

  private String processLocalizedSeoSegment(String localizedSeoSegment) {
    if (isBlank(localizedSeoSegment)) {
      if (getDefaultLocale() == null) {
        LOG.warn("Default locale does not set for commerce beans.");
      }
      if (!getLocale().equals(getDefaultLocale())) {
        LOG.debug("Product {} does not have a seo segment for the current locale {}. Return the seo segment for the default locale {}.",
                getName(), getLocale(), getDefaultLocale());
        StoreContext newStoreContext = StoreContextHelper.getCurrentContextFor(getDefaultLocale());

        Product master = getCatalogService().withStoreContext(newStoreContext).findProductById(CommerceIdHelper.formatProductId(getExternalId()));
        if (master != null && !equals(master)) {
          localizedSeoSegment = master.getSeoSegment();
        }
      }
    }
    return localizedSeoSegment;
  }

  @Override
  public String getDefaultImageAlt() {
    return DataMapHelper.getValueForKey(getDelegate(), "fullImageAltDescription", String.class);
  }

  @Override
  public String getDefaultImageUrl() {
    String fullImage = DataMapHelper.getValueForKey(getDelegate(), "fullImage", String.class);
    return null == fullImage ? null : getAssetUrlProvider().getImageUrl(fullImage);
  }

  @Override
  public String getThumbnailUrl() {
    String thumbnail = DataMapHelper.getValueForKey(getDelegate(), "thumbnail", String.class);
    return null == thumbnail ? null : getAssetUrlProvider().getImageUrl(thumbnail);
  }

  @Override
  public CatalogPicture getCatalogPicture(){
    AssetService assetService = getAssetService();
    if(null != assetService) {
      return assetService.getCatalogPicture(getDefaultImageUrl());
    }
    return new CatalogPicture("#", null);
  }

  @Override
  public Content getPicture() {
    List<Content> pictures = getPictures();
    return pictures != null && !pictures.isEmpty() ? pictures.get(0) : null;
  }

  @Override
  public List<Content> getPictures() {
    AssetService assetService = getAssetService();
    if(assetService != null) {
      return assetService.findPictures(getReference());
    }
    return Collections.emptyList();
  }

  @Override
  public List<Content> getVisuals() {
    AssetService assetService = getAssetService();
    if(null != assetService) {
      return assetService.findVisuals(getReference(), false);
    }
    return Collections.emptyList();
  }

  @Override
  public List<Content> getDownloads() {
    AssetService assetService = getAssetService();
    if(null != assetService) {
      return assetService.findDownloads(getReference());
    }
    return Collections.emptyList();
  }

  @Override
  public Category getCategory() {
    return doGetCategory();
  }

  @Override
  public List<Category> getCategories() {
    Category category = doGetCategory();
    return category==null ? Collections.<Category>emptyList() : Collections.singletonList(category);
  }

  @Override
  public Currency getCurrency() {
    return StoreContextHelper.getCurrency(getContext());
  }

  @Override
  public boolean isVariant() {
    return DataMapHelper.getValueForKey(getDelegate(), "parentCatalogEntryID", String.class) != null;
  }

  @Nonnull
  @Override
  public List<ProductAttribute> getDefiningAttributes() {
    if (definingAttributes == null) {
      loadAttributes();
    }
    return definingAttributes;
  }

  @Nonnull
  @Override
  public List<ProductAttribute> getDescribingAttributes() {
    if (describingAttributes == null) {
      loadAttributes();
    }
    return describingAttributes;
  }

  @Override
  @Nonnull
  public List<Object> getAttributeValues(@Nonnull String attributeId) {

    List<Object> values = new ArrayList<>();

    List<ProductAttribute> describingAttributes = getDescribingAttributes();
    for (ProductAttribute attribute : describingAttributes) {
      if (attributeId.equals(attribute.getId())) {
        values.addAll(attribute.getValues());
        break;
      }
    }

    if (!values.isEmpty()) return values;

    List<ProductAttribute> definingAttributes = getDefiningAttributes();
    for (ProductAttribute attribute : definingAttributes) {
      if (attributeId.equals(attribute.getId())) {
        values.addAll(attribute.getValues());
        break;
      }
    }

    return values;
  }

  protected void loadAttributes() {
    // load attributes in local member variables if available in delegate
    definingAttributes = new ArrayList<>();
    describingAttributes = new ArrayList<>();
    List<Map<String, Object>> wcAttributes = DataMapHelper.getValueForKey(getDelegate(), "attributes", List.class);
    if (wcAttributes != null && ! wcAttributes.isEmpty()) {
      for (Map<String, Object> wcAttribute : wcAttributes) {
        ProductAttribute pa = new ProductAttributeImpl(wcAttribute);
        if (USAGE_DEFINING.equals(DataMapHelper.getValueForKey(wcAttribute, "usage", String.class))) {
          definingAttributes.add(pa);
        } else {
          describingAttributes.add(pa);
        }
      }
    }
  }

  protected BigDecimal convertStringToBigDecimal(String value) {
    if (NumberUtils.isNumber(value)) {
      return NumberUtils.createBigDecimal(value);
    }
    return null;
  }


  // --- internal ---------------------------------------------------

  private Category doGetCategory() {
    String parentCategoryID = DataMapHelper.getValueForPath(getDelegate(), "parentCatalogGroupID[0]", String.class);
    return (Category) getCommerceBeanFactory().createBeanFor(
            CommerceIdHelper.formatCategoryTechId(parentCategoryID), getContext());
  }
}