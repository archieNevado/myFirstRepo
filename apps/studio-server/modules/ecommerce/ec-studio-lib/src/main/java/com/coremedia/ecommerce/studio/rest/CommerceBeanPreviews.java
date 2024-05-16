package com.coremedia.ecommerce.studio.rest;

import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

@DefaultAnnotation(NonNull.class)
public class CommerceBeanPreviews {
  private final CommerceBean commerceBean;

  public CommerceBeanPreviews(CommerceBean commerceBean) {
    this.commerceBean = commerceBean;
  }

  public CommerceBean getCommerceBean() {
    return commerceBean;
  }

}
