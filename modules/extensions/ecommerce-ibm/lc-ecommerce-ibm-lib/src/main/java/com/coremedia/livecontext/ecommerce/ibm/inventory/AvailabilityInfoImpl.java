package com.coremedia.livecontext.ecommerce.ibm.inventory;

import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;

import java.util.Map;
import java.util.Objects;


public class AvailabilityInfoImpl implements AvailabilityInfo {
  private final Map<String, Object> delegate;

  public AvailabilityInfoImpl(Map<String, Object> delegate) {
    this.delegate = delegate;
  }

  @Override
  public float getQuantity() {
    return DataMapHelper.getValueForKey(delegate, "availableQuantity", 0.0f);
  }

  @Override
  public String getInventoryStatus() {
    return DataMapHelper.getValueForKey(delegate, "inventoryStatus", String.class);
  }

  @Override
  public String getUnitOfMeasure() {
    return DataMapHelper.getValueForKey(delegate, "unitOfMeasure", String.class);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AvailabilityInfoImpl that = (AvailabilityInfoImpl) o;

    return !(delegate != null ? !java.util.Objects.equals(DataMapHelper.getValueForKey(delegate, "productId", String.class), DataMapHelper.getValueForKey(that.delegate, "productId", String.class)) : that.delegate != null);

  }

  @Override
  public int hashCode() {
    return delegate != null ? Objects.hash(DataMapHelper.getValueForKey(delegate, "productId", String.class)) : 0;
  }
}
