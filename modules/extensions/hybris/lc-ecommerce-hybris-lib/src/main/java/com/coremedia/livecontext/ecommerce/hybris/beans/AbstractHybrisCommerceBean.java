package com.coremedia.livecontext.ecommerce.hybris.beans;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceBean;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.livecontext.ecommerce.asset.AssetUrlProvider;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.hybris.common.CommerceBeanHelper;
import com.coremedia.livecontext.ecommerce.hybris.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.AbstractHybrisDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.CatalogResource;
import com.coremedia.xml.Markup;
import org.springframework.beans.factory.annotation.Required;

import java.util.Locale;

import static com.google.common.base.Strings.isNullOrEmpty;

public abstract class AbstractHybrisCommerceBean extends AbstractCommerceBean {

  private AbstractHybrisDocument delegate;

  private CatalogResource catalogResource;

  private CommerceBeanHelper commerceBeanHelper;

  private CommerceCache commerceCache;

  CommerceCache getCommerceCache() {
    return commerceCache;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  public CommerceBeanHelper getCommerceBeanHelper() {
    return commerceBeanHelper;
  }

  @Required
  public void setCommerceBeanHelper(CommerceBeanHelper commerceBeanHelper) {
    this.commerceBeanHelper = commerceBeanHelper;
  }

  public AbstractHybrisDocument getDelegate() {
    if (delegate == null) {
      load();
    }

    return delegate;
  }

  public AssetUrlProvider getAssetUrlProvider() {
    return DefaultConnection.get().getAssetUrlProvider();
  }

  /**
   * Sets a delegate as an arbitrarily backing object.
   *
   * @param delegate the arbitrarily backing object
   */
  public void setDelegate(AbstractHybrisDocument delegate) {
    this.delegate = delegate;
  }

  @Override
  public String getExternalId() {
    // do not call the getDelegate() method because it has consequences regarding a untimely loading of beans
    return CommerceIdHelper.convertToExternalId(getId());
  }

  @Override
  public String getExternalTechId() {
    return getExternalId();
  }

  public CatalogResource getCatalogResource() {
    return catalogResource;
  }

  public void setCatalogResource(CatalogResource catalogResource) {
    this.catalogResource = catalogResource;
  }

  protected void loadCached(AbstractCommerceCacheKey cacheKey) {
    AbstractHybrisDocument document = (AbstractHybrisDocument) getCommerceCache().get(cacheKey);

    if (document == null) {
      throw new NotFoundException("Commerce object not found with id '" + getId() + "'.");
    }

    setDelegate(document);
  }

  protected static Markup buildRichtextMarkup(String str) {
    return toRichtext(str, false);
  }

  @Override
  public Locale getLocale() {
    return getContext().getLocale();
  }
}
