package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentUserContext;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.catalog.VariantFilter;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.util.Assert.notNull;

/**
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated
public class ProductVariantImpl extends ProductBase implements ProductVariant {

  @NonNull
  @Override
  protected Map<String, Object> getDelegate() {
    if (delegate == null) {
      delegate = getDelegateFromCache();
      if (delegate == null) {
        throw new NotFoundException(getId() + " (sku not found in catalog)");
      }
    }

    return delegate;
  }

  @Nullable
  @Override
  Map<String, Object> getDelegateFromCache() {
    UserContext userContext = CurrentUserContext.get();
    CommerceCache commerceCache = getCommerceCache();

    ProductCacheKey cacheKey = new ProductCacheKey(getId(), getContext(), userContext, getCatalogWrapperService(),
            commerceCache);

    return commerceCache.get(cacheKey);
  }

  /**
   * @throws CommerceException
   */
  @Override
  public void load() {
    getDelegate();
  }

  @Override
  @Nullable
  public Product getParent() {
    String parentProductID = DataMapHelper.findString(getDelegate(), "parentCatalogEntryID").orElse(null);
    if (parentProductID != null) {
      CatalogAlias catalogAlias = getCatalogAlias();
      CommerceId commerceId = getCommerceIdProvider().formatProductTechId(catalogAlias, parentProductID);
      return (Product) getCommerceBeanFactory().createBeanFor(commerceId, getContext());
    }
    return null;
  }

  @Override
  @Nullable
  public Object getAttributeValue(@NonNull String attributeId) {
    notNull(attributeId);

    List<ProductAttribute> attributes = getDefiningAttributes();
    for (ProductAttribute attribute : attributes) {
      if (attributeId.equals(attribute.getId())) {
        return attribute.getValue();
      }
    }
    attributes = getDescribingAttributes();
    for (ProductAttribute attribute : attributes) {
      if (attributeId.equals(attribute.getId())) {
        return attribute.getValue();
      }
    }
    return null;
  }

  // Methods that are directed to the parent product (for sake of convenience)
  //...

  @NonNull
  @Override
  public List<String> getVariantAxisNames() {
    Product parent = getParent();
    return parent != null ? parent.getVariantAxisNames() : Collections.emptyList();
  }

  @Override
  @NonNull
  public List<ProductVariant> getVariants() {
    Product parent = getParent();
    return parent != null ? parent.getVariants() : Collections.emptyList();
  }

  @Override
  @NonNull
  public List<ProductVariant> getVariants(@NonNull List<VariantFilter> filters) {
    Product parent = getParent();
    return parent != null ? parent.getVariants(filters) : Collections.emptyList();
  }

  @Override
  @NonNull
  public List<ProductVariant> getVariants(@Nullable VariantFilter filter) {
    Product parent = getParent();
    return parent != null ? parent.getVariants(filter) : Collections.emptyList();
  }

  @Override
  @NonNull
  public List<Object> getVariantAxisValues(@NonNull String axisName, @NonNull List<VariantFilter> filters) {
    Product parent = getParent();
    return parent != null ? parent.getVariantAxisValues(axisName, filters) : Collections.emptyList();
  }

  @Override
  @NonNull
  public List<Object> getVariantAxisValues(@NonNull String axisName, @Nullable VariantFilter filter) {
    Product parent = getParent();
    return parent != null ? parent.getVariantAxisValues(axisName, filter) : Collections.emptyList();
  }

  public boolean isBuyable() {
    return Boolean.parseBoolean(DataMapHelper.findString(getDelegate(), "buyable").orElse(null));
  }

  @Override
  public String toString() {
    return "[SKU " + getId() + "]";
  }

  @Override
  protected void loadAttributes() {
    super.loadAttributes();
    Product parent = getParent();
    if (parent != null) {
      mergeParentAttributes(parent);
    }
  }

  private void mergeParentAttributes(@NonNull Product parent) {
    List<ProductAttribute> myDescribingAttributes = getDescribingAttributes();
    List<ProductAttribute> parentDescribingAttributes = parent.getDescribingAttributes();
    if (myDescribingAttributes.isEmpty()) {
      myDescribingAttributes.addAll(parentDescribingAttributes);
    } else {
      for (ProductAttribute attribute : parentDescribingAttributes) {
        if (!myDescribingAttributes.contains(attribute)) {
          myDescribingAttributes.add(attribute);
        }
      }
    }
  }
}
