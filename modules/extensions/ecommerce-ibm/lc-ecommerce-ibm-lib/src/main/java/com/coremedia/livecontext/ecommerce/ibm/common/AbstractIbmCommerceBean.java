package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceBean;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.livecontext.ecommerce.asset.AssetUrlProvider;
import com.coremedia.xml.Markup;
import org.springframework.beans.factory.annotation.Required;

import java.util.Locale;

public abstract class AbstractIbmCommerceBean extends AbstractCommerceBean {

  private CommerceCache commerceCache;

  protected CommerceCache getCommerceCache() {
    return commerceCache;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  @Override
  public Locale getLocale() {
    return StoreContextHelper.getLocale(getContext());
  }

  public String getStoreId() {
    return StoreContextHelper.getStoreId(getContext());
  }

  public String getStoreName() {
    return StoreContextHelper.getStoreName(getContext());
  }

  /**
   * Sets a delegate as an arbitrarily backing object.
   * Its up to the concrete catalog implementation if a backing object is set from outside or whether the bean impl
   * handles it for itself privately. If a catalog service impl decides to set it from outside then it can use this
   * method. The bean impl must know how to handle (or cast) the given delegate parameter.
   *
   * @param delegate the arbitrarily backing object
   */
  public abstract void setDelegate(Object delegate);

  protected AssetUrlProvider getAssetUrlProvider() {
    return DefaultConnection.get().getAssetUrlProvider();
  }

  protected static Markup toRichtext(String str) {
    return toRichtext(str, true);
  }
}
