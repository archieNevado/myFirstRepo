package com.coremedia.livecontext.ecommerce.sfcc.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

@DefaultAnnotation(NonNull.class)
public class SfccCommerceIdProvider extends BaseCommerceIdProvider {

  static final Vendor SFCC = Vendor.of("sfcc");

  public SfccCommerceIdProvider() {
    super(SFCC);
  }

  public static boolean isSfccId(CommerceId commerceId) {
    return SFCC.equals(commerceId.getVendor());
  }

  public static CommerceIdBuilder commerceId(CommerceBeanType beanType) {
    return BaseCommerceIdProvider.commerceId(SFCC, beanType);
  }
}
