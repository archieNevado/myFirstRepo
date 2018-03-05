package com.coremedia.livecontext.ecommerce.sfcc.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.Vendor;

import javax.annotation.Nonnull;

public class SfccCommerceIdProvider extends BaseCommerceIdProvider {

  static final Vendor SFCC = Vendor.of("sfcc");

  public SfccCommerceIdProvider() {
    super(SFCC);
  }

  public static boolean isSfccId(@Nonnull CommerceId commerceId) {
    return SFCC.equals(commerceId.getVendor());
  }

  @Nonnull
  public static CommerceIdBuilder commerceId(@Nonnull CommerceBeanType beanType) {
    return BaseCommerceIdProvider.commerceId(SFCC, beanType);
  }
}
