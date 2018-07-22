package com.coremedia.livecontext.ecommerce.hybris.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.Vendor;

import edu.umd.cs.findbugs.annotations.NonNull;

public class HybrisCommerceIdProvider extends BaseCommerceIdProvider {

  static final Vendor HYBRIS = Vendor.of("hybris");

  public HybrisCommerceIdProvider() {
    super(HYBRIS);
  }

  public static boolean isHybrisId(@NonNull CommerceId commerceId) {
    return HYBRIS.equals(commerceId.getVendor());
  }

  @NonNull
  public static CommerceIdBuilder commerceId(@NonNull CommerceBeanType beanType) {
    return BaseCommerceIdProvider.commerceId(HYBRIS, beanType);
  }
}
