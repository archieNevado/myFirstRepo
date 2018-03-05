package com.coremedia.livecontext.ecommerce.sfcc.beans;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceBean;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccCommerceIdProvider;
import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccConfigurationProperties;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.LocalizedProperty;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.MarkupTextDocument;
import com.coremedia.xml.Markup;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * Abstract commerce bean for Salesforce Commerce Cloud specific commerce beans.
 */
public abstract class AbstractSfccCommerceBean extends AbstractCommerceBean {

  private AbstractOCDocument delegate;
  private CommerceCache commerceCache;
  private SfccCommerceIdProvider commerceIdProvider;

  /**
   * Flag to determine if this document is fully loaded or not.
   */
  private boolean lightweight;

  protected AbstractSfccCommerceBean(@Nonnull SfccConfigurationProperties sfccConfigurationProperties) {
    setDefaultLocale(sfccConfigurationProperties.getDefaultLocale());
  }

  protected static Markup buildRichtextMarkup(String str) {
    return toRichtext(str, true);
  }

  protected static Markup buildRichtextMarkup(MarkupTextDocument markupText) {
    return toRichtext(markupText != null ? markupText.getMarkup() : "", false);
  }

  CommerceCache getCommerceCache() {
    return commerceCache;
  }

  @Autowired
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  public AbstractOCDocument getDelegate() {
    if (delegate == null) {
      load();
    }
    return delegate;
  }

  /**
   * Sets a delegate as an arbitrarily backing object.
   *
   * @param delegate the arbitrarily backing object
   */
  public void setDelegate(AbstractOCDocument delegate) {
    this.delegate = delegate;
  }

  @Override
  public Locale getLocale() {
    Locale currentStoreLocale = getContext().getLocale();

    if (currentStoreLocale == null) {
      return getDefaultLocale();
    }

    return currentStoreLocale;
  }

  @Override
  public String getExternalId() {
    return getDelegate().getId();
  }

  @Override
  public String getExternalTechId() {
    return getExternalId();
  }

  /**
   * Returns <code>true</code> if not all properties of the delegate have been loaded yet, <code>false</code> otherwise.
   *
   * @return
   */
  public boolean isLightweight() {
    return lightweight;
  }

  public void setLightweight(boolean lightweight) {
    this.lightweight = lightweight;
  }

  @Nonnull
  @Override
  public Map<String, Object> getCustomAttributes() {
    AbstractOCDocument delegate = getDelegate();
    return delegate != null ? delegate.customAttributes() : emptyMap();
  }

  /**
   * Returns a localized property value
   * using the locale of the current {@link com.coremedia.livecontext.ecommerce.common.StoreContext}.
   *
   * @param localizedProperty localized property
   * @return localized value or <code>null</code> if the provided property was <code>null</code>
   */
  public <T> T getLocalizedValue(LocalizedProperty<T> localizedProperty) {
    if (localizedProperty == null) {
      return null;
    }

    return localizedProperty.getValue(getLocale());
  }

  // --- Getters and setters ---

  public SfccCommerceIdProvider getCommerceIdProvider() {
    return commerceIdProvider;
  }

  @Autowired
  public void setCommerceIdProvider(SfccCommerceIdProvider sfccCommerceIdProvider) {
    this.commerceIdProvider = sfccCommerceIdProvider;
  }

  @Override
  @Autowired
  public void setCommerceBeanFactory(CommerceBeanFactory sfccCommerceBeanFactory) {
    super.setCommerceBeanFactory(sfccCommerceBeanFactory);
  }

  @Override
  @Autowired
  public void setCatalogService(CatalogService sfccCatalogService) {
    super.setCatalogService(sfccCatalogService);
  }

}
