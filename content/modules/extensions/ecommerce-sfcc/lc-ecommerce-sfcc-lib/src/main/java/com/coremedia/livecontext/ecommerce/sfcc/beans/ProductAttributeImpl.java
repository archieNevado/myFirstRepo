package com.coremedia.livecontext.ecommerce.sfcc.beans;

import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.VariationAttributeDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.VariationAttributeValueDocument;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public class ProductAttributeImpl implements ProductAttribute {

  private Boolean isDefining;
  protected VariationAttributeDocument delegate;

  ProductAttributeImpl(VariationAttributeDocument delegate, boolean isDefining) {
    super();
    this.delegate = delegate;
    this.isDefining = isDefining;
  }

  public VariationAttributeDocument getDelegate() {
    return delegate;
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public String getDisplayName() {
    return delegate.getName().getDefaultValue();
  }

  @Override
  public String getType() {
    return delegate.getOcType();
  }

  @Override
  public String getUnit() {
    return delegate.getOcType();
  }

  @Override
  public String getDescription() {
    return getDisplayName();
  }

  @Override
  public String getExternalId() {
    return getId();
  }

  @Override
  public Object getValue() {
    return getValues().stream().findFirst().orElse(null);
  }

  @Override
  public List<Object> getValues() {
    List<VariationAttributeValueDocument> values = delegate.getValues();

    if (values == null || values.isEmpty()) {
      return Collections.emptyList();
    }

    List<Object> result = values.stream()
            .map(VariationAttributeValueDocument::getValue)
            .filter(Objects::nonNull)
            .collect(toList());

    return Collections.unmodifiableList(result);
  }

  @Override
  public boolean isDefining() {
    return this.isDefining;
  }
}
