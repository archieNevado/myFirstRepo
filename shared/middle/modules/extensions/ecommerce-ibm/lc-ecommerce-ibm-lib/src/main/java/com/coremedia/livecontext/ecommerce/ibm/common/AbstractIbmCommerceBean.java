package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceBean;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.asset.AssetUrlProvider;
import com.coremedia.livecontext.ecommerce.ibm.catalog.CatalogServiceImpl;
import com.coremedia.xml.Markup;
import org.springframework.beans.factory.annotation.Required;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Locale;

public abstract class AbstractIbmCommerceBean extends AbstractCommerceBean {

  private CommerceCache commerceCache;

  private IbmCommerceIdProvider commerceIdProvider;

  private AssetUrlProvider assetUrlProvider;

  protected CommerceCache getCommerceCache() {
    return commerceCache;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  protected IbmCommerceIdProvider getCommerceIdProvider() {
    return commerceIdProvider;
  }

  @NonNull
  @Override
  protected CatalogServiceImpl getCatalogService() {
    return (CatalogServiceImpl) super.getCatalogService();
  }

  @Required
  public void setCommerceIdProvider(IbmCommerceIdProvider commerceIdProvider) {
    this.commerceIdProvider = commerceIdProvider;
  }

  protected AssetUrlProvider getAssetUrlProvider() {
    return assetUrlProvider;
  }

  @Required
  public void setAssetUrlProvider(AssetUrlProvider assetUrlProvider) {
    this.assetUrlProvider = assetUrlProvider;
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

  @Nullable
  protected static Markup toRichtext(@Nullable String str) {
    return toRichtext(str, true);
  }
}
