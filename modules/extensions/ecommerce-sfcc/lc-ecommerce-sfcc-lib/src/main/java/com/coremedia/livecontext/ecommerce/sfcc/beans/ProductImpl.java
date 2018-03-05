package com.coremedia.livecontext.ecommerce.sfcc.beans;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.catalog.VariantFilter;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;
import com.coremedia.livecontext.ecommerce.sfcc.catalog.ProductCacheKey;
import com.coremedia.livecontext.ecommerce.sfcc.catalog.ProductVariantsCacheKey;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccCommerceIdProvider;
import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccConfigurationProperties;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.MarkupTextDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.MasterDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.MediaFileDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.VariantDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.VariationAttributeDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.ProductsResource;
import com.coremedia.livecontext.ecommerce.sfcc.pricing.PriceServiceImpl;
import com.coremedia.xml.Markup;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@Named("sfccCommerceBeanFactory:product")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductImpl extends AbstractSfccCommerceBean implements Product {

  private ProductsResource resource;
  private PriceServiceImpl priceService;

  private List<VariantDocument> variants;

  protected List<ProductAttribute> definingAttributes;

  public ProductImpl(@Nonnull SfccConfigurationProperties sfccConfigurationProperties) {
    super(sfccConfigurationProperties);
  }

  @Override
  public ProductDocument getDelegate() {
    return (ProductDocument) super.getDelegate();
  }

  @Override
  public void load() {
    CommerceId productId = getId();
    ProductCacheKey cacheKey = new ProductCacheKey(productId, getContext(), resource, getCommerceCache());
    ProductDocument delegate = getCommerceCache().get(cacheKey);
    if (delegate == null) {
      throw new NotFoundException("Commerce object not found with id " + productId);
    }

    setDelegate(delegate);
  }

  public void invalidate() {
    getCommerceCache().getCache().invalidate(CommerceIdFormatterHelper.format(getId()));
    load();
  }

  @Override
  public Currency getCurrency() {
    return getContext().getCurrency();
  }

  @Override
  public String getName() {
    return getLocalizedValue(getDelegate().getName());
  }

  @Override
  public Markup getShortDescription() {
    MarkupTextDocument markupText = getLocalizedValue(getDelegate().getShortDescription());
    return buildRichtextMarkup(markupText);
  }

  @Override
  public Markup getLongDescription() {
    MarkupTextDocument markupText = getLocalizedValue(getDelegate().getLongDescription());
    return buildRichtextMarkup(markupText);
  }

  @Override
  public String getTitle() {
    return getLocalizedValue(getDelegate().getPageTitle());
  }

  @Override
  public String getMetaDescription() {
    return getLocalizedValue(getDelegate().getPageDescription());
  }

  @Override
  public String getMetaKeywords() {
    return getLocalizedValue(getDelegate().getPageKeywords());
  }

  @Override
  public BigDecimal getListPrice() {
    if (priceService == null){
      return null;
    }

    StoreContext storeContext = getContext();

    String storeId = storeContext.getStoreId();
    Currency currency = storeContext.getCurrency();

    return priceService.findListPriceForProduct(getExternalId(), storeId, currency)
            .orElse(null);
  }

  @Override
  public BigDecimal getOfferPrice() {
    if (priceService == null){
      return null;
    }

    StoreContext storeContext = getContext();

    String storeId = storeContext.getStoreId();
    Currency currency = storeContext.getCurrency();

    return priceService.findOfferPriceForProduct(getExternalId(), storeId, currency)
            .orElse(null);
  }

  @Override
  public String getSeoSegment() {
    try {
      return URLEncoder.encode(getName(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return getExternalId();
    }
  }

  @Override
  public Category getCategory() {
    String categoryId = getDelegate().getPrimaryCategoryId();

    MasterDocument master = getDelegate().getMaster();
    if (isVariant() && master != null) {
      // For variants we need to fetch the master product and get the primary category id of that master
      String masterId = master.getMasterId();

      categoryId = getResource().getProductById(masterId, getContext())
              .map(ProductDocument::getPrimaryCategoryId)
              .orElse(categoryId);
    }

    if (categoryId == null) {
      // Trigger a reload
      invalidate();
      categoryId = getDelegate().getPrimaryCategoryId();
    }

    if (categoryId == null) {
      return null;
    }

    CommerceId commerceId = SfccCommerceIdProvider.commerceId(CATEGORY).withExternalId(categoryId).build();

    return (Category) getCommerceBeanFactory().createBeanFor(commerceId, getContext());
  }

  @Nonnull
  @Override
  public List<Category> getCategories() {
    return emptyList();
  }

  @Override
  public String getDefaultImageAlt() {
    MediaFileDocument image = getDelegate().getImage();
    return image != null ? getLocalizedValue(image.getAlt()) : null;
  }

  @Override
  public String getDefaultImageUrl() {
    MediaFileDocument image = getDelegate().getImage();

    if (image == null && isLightweight()) {
      invalidate(); // Force fetching all properties
      image = getDelegate().getImage();
    }

    return image != null ? image.getAbsUrl() : null;
  }

  @Override
  public String getThumbnailUrl() {
    return getDefaultImageUrl();
  }

  @Nonnull
  @Override
  public List<ProductAttribute> getDefiningAttributes() {
    if (definingAttributes == null) {
      List<VariationAttributeDocument> variationAttributes = getDelegate().getVariationAttributes();

      if (variationAttributes == null || variationAttributes.isEmpty()) {
        return Collections.emptyList();
      }

      List<ProductAttribute> result = variationAttributes.stream()
              .map(variationAttribute -> new ProductAttributeImpl(variationAttribute, true))
              .filter(Objects::nonNull)
              .collect(toList());

      definingAttributes = Collections.unmodifiableList(result);
    }

    return definingAttributes;
  }

  @Nonnull
  @Override
  public List<ProductAttribute> getDescribingAttributes() {
    return emptyList();
  }

  @Nonnull
  @Override
  public List<String> getVariantAxisNames() {
    List<ProductAttribute> definingAttributes = getDefiningAttributes();
    return definingAttributes.stream()
            .map(ProductAttribute::getId)
            .filter(Objects::nonNull)
            .collect(toList());
  }

  @Nonnull
  @Override
  public List<Object> getVariantAxisValues(@Nonnull String axisName, @Nonnull List<VariantFilter> filters) {
    List<Object> result = new ArrayList<>();

    List<ProductVariant> availableProducts = getVariants(filters);
    for (ProductVariant productVariant : availableProducts) {
      Object attributeValue = productVariant.getAttributeValue(axisName);
      if (attributeValue != null && !result.contains(attributeValue)) {
        result.add(attributeValue);
      }
    }

    return result;
  }

  @Nonnull
  @Override
  public List<Object> getVariantAxisValues(@Nonnull String axisName, @Nullable VariantFilter filter) {
    if (filter == null) {
      return getVariantAxisValues(axisName, emptyList());
    }

    List<VariantFilter> filters = newArrayList(filter);

    return getVariantAxisValues(axisName, filters);
  }

  @Nonnull
  @Override
  public List<Object> getAttributeValues(@Nonnull String s) {
    return getVariantAxisValues(s, emptyList());
  }

  @Nonnull
  @Override
  public List<ProductVariant> getVariants() {
    if (variants == null) {
      variants = getDelegate().getVariants();
      if (variants == null) {
        variants = getCommerceCache().get(
                new ProductVariantsCacheKey(getId(), getContext(), resource, getCommerceCache()));
      }
      if (variants == null) {
        variants = emptyList();
      }
    }

    List<ProductVariant> result = new ArrayList<>();
    for (VariantDocument variantDocument : variants) {
      CommerceIdProvider idProvider = CurrentCommerceConnection.get().getIdProvider();
      CommerceId commerceId = idProvider.formatProductVariantId(null, variantDocument.getProductId());

      ProductVariant productVariant = getCatalogService().findProductVariantById(commerceId, getContext());

      if (productVariant != null) {
        result.add(productVariant);
      }
    }

    return result;
  }

  @Nonnull
  @Override
  public List<ProductVariant> getVariants(@Nonnull List<VariantFilter> filters) {
    List<ProductVariant> allVariants = getVariants();

    if (filters.isEmpty()) {
      return allVariants;
    }

    return allVariants.stream()
            .filter(variant -> isVariantIncludedinAllFilters(variant, filters))
            .collect(toList());
  }

  private static boolean isVariantIncludedinAllFilters(ProductVariant productVariant, @Nonnull Collection<VariantFilter> filters) {
    return filters.stream().allMatch(filter -> filter.matches(productVariant));
  }

  @Nonnull
  @Override
  public List<ProductVariant> getVariants(@Nullable VariantFilter filter) {
    if (filter == null) {
      return getVariants(emptyList());
    }

    return getVariants(singletonList(filter));
  }

  @Nonnull
  @Override
  public Map<ProductVariant, AvailabilityInfo> getAvailabilityMap() {
    return emptyMap();
  }

  @Override
  public float getTotalStockCount() {
    return 0;
  }

  @Override
  public boolean isAvailable() {
    return getDelegate().isInStock();
  }

  @Override
  public boolean isVariant() {
    return getDelegate().getType().isVariant();
  }

  @Nonnull
  @Override
  public CatalogPicture getCatalogPicture() {
    return findAssetService()
            .map(assetService -> assetService.getCatalogPicture(getDefaultImageUrl(), getId()))
            .orElseGet(() -> new CatalogPicture("#", null));
  }

  @Nullable
  @Override
  public Content getPicture() {
    return getPictures().stream().findFirst().orElse(null);
  }

  @Nonnull
  @Override
  public List<Content> getPictures() {
    return findAssetService()
            .map(assetService -> assetService.findPictures(getReference()))
            .orElseGet(Collections::emptyList);
  }

  @Nonnull
  @Override
  public List<Content> getVisuals() {
    return findAssetService()
            .map(assetService -> assetService.findVisuals(getReference()))
            .orElseGet(Collections::emptyList);
  }

  @Nonnull
  @Override
  public List<Content> getDownloads() {
    return findAssetService()
            .map(assetService -> assetService.findDownloads(getReference()))
            .orElseGet(Collections::emptyList);
  }

  @VisibleForTesting
  ProductsResource getResource() {
    return resource;
  }

  @Autowired
  public void setResource(ProductsResource ocapiProductsResource) {
    this.resource = ocapiProductsResource;
  }

  @Autowired(required = false)
  public void setPriceService(PriceServiceImpl sfccPriceService) {
    this.priceService = sfccPriceService;
  }
}
