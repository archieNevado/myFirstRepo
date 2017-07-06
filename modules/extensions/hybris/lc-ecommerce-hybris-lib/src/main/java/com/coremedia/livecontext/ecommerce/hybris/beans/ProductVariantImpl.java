package com.coremedia.livecontext.ecommerce.hybris.beans;

import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.hybris.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.VariantAttributeDocument;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class ProductVariantImpl extends ProductImpl implements ProductVariant {

  @Nullable
  @Override
  public Product getParent() {
    String baseProductCode = getDelegate().getBaseProduct().getCode();

    if (isNullOrEmpty(baseProductCode)) {
      return null;
    }

    return getCatalogService().findProductById(CommerceIdHelper.formatProductId(baseProductCode));
  }

  @Override
  public ProductDocument getDelegate() {
    return super.getDelegate();
  }

  @Override
  public String getReference() {
    return CommerceIdHelper.formatProductVariantId(getExternalId());
  }

  @Nonnull
  @Override
  public List<ProductAttribute> getDefiningAttributes() {
    List<ProductAttribute> productAttributes = new ArrayList<>();

    ProductDocument delegate = getDelegate();
    List<VariantAttributeDocument> variantAttributeDocuments = delegate.getVariantAttributes();
    for (VariantAttributeDocument variantAttributeDocument : variantAttributeDocuments) {
      ProductAttribute productAttribute = new ProductAttributeImpl(true, variantAttributeDocument);
      if (productAttribute.getValue() != null && !productAttribute.getValue().equals("null")) {
        productAttributes.add(productAttribute);
      } else {
        Product parent = getParent();
        if (parent != null && parent.isVariant()) {
          List<ProductAttribute> definingAttributes = parent.getDefiningAttributes();
          for (ProductAttribute definingAttribute : definingAttributes) {
            if (productAttribute.getId().equals(definingAttribute.getId())) {
              productAttributes.add(definingAttribute);
            }
          }
        }
      }
    }

    return productAttributes;
  }

  @Nonnull
  @Override
  public List<ProductAttribute> getDescribingAttributes() {
    List<ProductAttribute> productAttributes = new ArrayList<>();

    List<String> swatchColors = getDelegate().getSwatchColors();
    if (swatchColors != null) {
      for (String swatchColor : swatchColors) {
        ProductAttributeImpl productAttribute = new ProductAttributeImpl(false, "swatchColor", swatchColor);
        productAttributes.add(productAttribute);
      }
    }

    //TODO add more describing attributes than just swatchColor
    // add the defining attributes of the parents recursively
    productAttributes.addAll(getParent().getDescribingAttributes());

    return productAttributes;
  }

  @Nullable
  @Override
  public Object getAttributeValue(@Nonnull String s) {
    for (ProductAttribute productAttribute : getDefiningAttributes()) {
      if (productAttribute.getDisplayName().equals(s)) {
        return productAttribute.getValue();
      }
    }

    //TODO add for describing attributes
    return null;
  }

  @Nullable
  @Override
  public AvailabilityInfo getAvailabilityInfo() {
    return null;
  }

  @Override
  public BigDecimal getListPrice() {
    BigDecimal price = super.getListPrice();

    if (price == null) {
      Product parent = getParent();
      if (parent != null) {
        return parent.getListPrice();
      }
    }

    return price;
  }

  @Override
  public BigDecimal getOfferPrice() {
    BigDecimal price = super.getOfferPrice();

    if (price == null) {
      Product parent = getParent();
      if (parent != null) {
        return parent.getListPrice();
      }
    }

    return price;
  }
}
