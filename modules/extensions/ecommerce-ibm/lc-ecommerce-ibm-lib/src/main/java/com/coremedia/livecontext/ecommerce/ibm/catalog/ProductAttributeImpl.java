package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

class ProductAttributeImpl implements ProductAttribute {

  private Map<String, Object> delegate;

  ProductAttributeImpl(Map<String, Object> delegate) {
    this.delegate = delegate;
  }

  @Override
  public String getId() {
    return DataMapHelper.getValueForKey(delegate, "identifier", String.class);
  }

  @Override
  public String getType() {
    return DataMapHelper.getValueForKey(delegate, "dataType", String.class);
  }

  @Override
  public String getUnit() {
    return DataMapHelper.getValueForKey(delegate, "unit", String.class);
  }

  @Override
  public String getExternalId() {
    return DataMapHelper.getValueForKey(delegate, "uniqueID", String.class);
  }

  @Override
  public String getDisplayName() {
    return DataMapHelper.getValueForKey(delegate, "name", String.class);
  }

  @Override
  public String getDescription() {
    return DataMapHelper.getValueForKey(delegate, "description", String.class);
  }

  @Override
  public Object getValue() {
    Object value = null;
    //noinspection unchecked
    List<Map<String, Object>> valueForKey = DataMapHelper.getValueForKey(delegate, "values", List.class);
    if (valueForKey != null && !valueForKey.isEmpty()) {
      value = DataMapHelper.getValueForKey(valueForKey.get(0), "value");
    }
    return value;
  }

  @Override
  public List<Object> getValues() {
    //noinspection unchecked
    List<Map<String, Object>> valueForKey = DataMapHelper.getValueForKey(delegate, "values", List.class);
    if (valueForKey == null || valueForKey.isEmpty()) {
      return emptyList();
    }

    return valueForKey.stream()
            .map(item -> DataMapHelper.getValueForKey(item, "value"))
            .filter(Objects::nonNull)
            .collect(toList());
  }

  @Override
  public boolean isDefining() {
    return "Defining".equals(DataMapHelper.getValueForKey(delegate, "usage", String.class));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ProductAttributeImpl that = (ProductAttributeImpl) o;

    if (delegate != null) {
      return getId().equals(that.getId());
    }

    return that.delegate == null;
  }

  @Override
  public int hashCode() {
    return delegate != null ? getId().hashCode() : 0;
  }
}
