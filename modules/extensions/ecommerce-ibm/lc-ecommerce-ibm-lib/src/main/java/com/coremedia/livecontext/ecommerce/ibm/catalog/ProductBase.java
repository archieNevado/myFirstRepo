package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.Catalog;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.catalog.VariantFilter;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapTransformationHelper;
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
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Base class for product and product variant implementation.
 */
abstract class ProductBase extends AbstractIbmCommerceBean implements Product, CommerceObject {

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
        CatalogAlias catalogAlias = getId().getCatalogAlias();
        priceInfo = getCommerceCache().get(
                new StaticPricesByExternalIdCacheKey(getExternalId(), catalogAlias, getContext(),
                        UserContextHelper.getCurrentContext(), getCatalogWrapperService(), getCommerceCache()));
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
    //short description by WCS is pure text.
    return toRichtext(shortDescription);
  }

  @Override
  public Markup getLongDescription() {
    String longDescription = DataMapHelper.getValueForKey(getDelegate(), "longDescription", String.class);
    //long description by WCS is already html and encoded
    return toRichtext(longDescription, false);
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
    if (priceInfo == null) {
      return null;
    }

    Map<String, WcPrice> prices = priceInfo.getPrices();
    if (prices == null || prices.isEmpty()) {
      return null;
    }

    WcPrice listPrice = prices.get("Display");
    if (listPrice == null) {
      return null;
    }

    return convertStringToBigDecimal(listPrice.getPriceValue());
  }

  @Override
  public BigDecimal getOfferPrice() {
    UserContext userContext = UserContextHelper.getCurrentContext();

    if (UserContextHelper.getForUserName(userContext) != null && StoreContextHelper.isDynamicPricingEnabled(getContext())) {
      return getPersonalizedOfferPrice();
    }

    WcPrices priceInfo = getPriceInfo();
    if (priceInfo == null) {
      return null;
    }

    Map<String, WcPrice> prices = priceInfo.getPrices();
    if (prices == null || prices.isEmpty()) {
      return null;
    }

    WcPrice offerPrice = prices.get("Offer");
    if (offerPrice != null && !offerPrice.getPriceValue().isEmpty())  {
      return convertStringToBigDecimal(offerPrice.getPriceValue());
    } else {
      return getPersonalizedOfferPrice();
    }
  }

  @Nullable
  private BigDecimal getPersonalizedOfferPrice() {
    UserContext userContext = UserContextHelper.getCurrentContext();

    PersonalizedPriceByExternalIdCacheKey cacheKey = new PersonalizedPriceByExternalIdCacheKey(
            getExternalId(), getContext(), userContext, getCatalogWrapperService(), getCommerceCache());

    return getCommerceCache().find(cacheKey)
            .map(WcPrice::getPriceValue)
            .map(this::convertStringToBigDecimal)
            .orElse(null);
  }

  @Nullable
  private String getCmSeoSegment() {
    String cmLocalizedSeoSegment = DataMapHelper.getValueForKey(getDelegate(), "cm_seo_token_ntk", String.class);
    cmLocalizedSeoSegment = processCmLocalizedSeoSegment(cmLocalizedSeoSegment);

    if (cmLocalizedSeoSegment == null) {
      return null;
    }

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
        CommerceId commerceId = getCommerceIdProvider().formatProductId(getCatalogAlias(), getExternalId());
        ProductBase master = (ProductBase) getCatalogService().withStoreContext(newStoreContext).findProductById(commerceId, newStoreContext);
        if (master != null && !equals(master)) {
          cmLocalizedSeoSegment = master.getCmSeoSegment();
        }
      }
    }
    return cmLocalizedSeoSegment;
  }

  @Nullable
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

        CommerceId commerceId = getCommerceIdProvider().formatProductId(getCatalogAlias(), getExternalId());
        Product master = getCatalogService().withStoreContext(newStoreContext).findProductById(commerceId, newStoreContext);
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
    return DataMapHelper.findValueForKey(getDelegate(), "fullImage", String.class)
            .map(fullImage -> getAssetUrlProvider().getImageUrl(fullImage))
            .orElse(null);
  }

  @Override
  public String getThumbnailUrl() {
    return DataMapHelper.findValueForKey(getDelegate(), "thumbnail", String.class)
            .map(thumbnail -> getAssetUrlProvider().getImageUrl(thumbnail))
            .orElse(null);
  }

  @Override
  public CatalogPicture getCatalogPicture(){
    return findAssetService()
            .map(assetService -> assetService.getCatalogPicture(getDefaultImageUrl(), getReference()))
            .orElseGet(() -> new CatalogPicture("#", null));
  }

  @Override
  public Content getPicture() {
    List<Content> pictures = getPictures();
    return pictures != null && !pictures.isEmpty() ? pictures.get(0) : null;
  }

  @Override
  public List<Content> getPictures() {
    return findAssetService()
            .map(assetService -> assetService.findPictures(getReference()))
            .orElseGet(Collections::emptyList);
  }

  @Override
  public List<Content> getVisuals() {
    return findAssetService()
            .map(assetService -> assetService.findVisuals(getReference(), false))
            .orElseGet(Collections::emptyList);
  }

  @Override
  public List<Content> getDownloads() {
    return findAssetService()
            .map(assetService -> assetService.findDownloads(getReference()))
            .orElseGet(Collections::emptyList);
  }

  @Override
  public Category getCategory() {
    return doGetCategory();
  }

  @Override
  public List<Category> getCategories() {
    Category category = doGetCategory();
    return category == null ? Collections.emptyList() : Collections.singletonList(category);
  }

  @Override
  public Currency getCurrency() {
    return StoreContextHelper.getCurrency(getContext());
  }

  @Override
  public boolean isVariant() {
    return DataMapHelper.findValueForKey(getDelegate(), "parentCatalogEntryID", String.class)
            .isPresent();
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

    if (!values.isEmpty()) {
      return values;
    }

    List<ProductAttribute> definingAttributes = getDefiningAttributes();
    for (ProductAttribute attribute : definingAttributes) {
      if (attributeId.equals(attribute.getId())) {
        values.addAll(attribute.getValues());
        if (values.isEmpty()) {
          VariantFilter variantFilter = null;
          values.addAll(getVariantAxisValues(attribute.getId(), variantFilter));
        }
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

  @Nullable
  protected BigDecimal convertStringToBigDecimal(String value) {
    if (!NumberUtils.isNumber(value)) {
      return null;
    }

    return NumberUtils.createBigDecimal(value);
  }


  // --- internal ---------------------------------------------------

  @Nullable
  private Category doGetCategory() {
    Optional<Catalog> catalog = getCatalog();
    if (!catalog.isPresent()) {
      throw new IllegalStateException("Product '" + this + "' does not have a catalog category.");
    }
    String catalogId = catalog.get().getExternalId();
    List<String> parentCategoryIds = DataMapTransformationHelper.getParentCatGroupIdForSingleWrapper(getDelegate(), catalogId);
    if (parentCategoryIds.isEmpty()) {
      throw new IllegalStateException("Product '" + this + "' does not have have a parent category.");
    }

    String parentCategoryID = parentCategoryIds.get(0);
    if (parentCategoryID == null) {
      return null;
    }

    CommerceId commerceId = getCommerceIdProvider().formatCategoryTechId(getCatalogAlias(), parentCategoryID);
    return (Category) getCommerceBeanFactory().createBeanFor(commerceId, getContext());
  }
}
