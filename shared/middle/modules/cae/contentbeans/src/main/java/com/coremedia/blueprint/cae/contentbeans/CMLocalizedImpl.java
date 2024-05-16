package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMLocalized;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.objectserver.beans.ContentBeanCollections;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * Generated extension class for immutable beans of document type "CMLocalized".
 */
public abstract class CMLocalizedImpl extends CMLocalizedBase {
  private SitesService sitesService;
  private ContentBeanCollections contentBeanCollections;

  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  public void setContentBeanCollections(ContentBeanCollections contentBeanCollections) {
    this.contentBeanCollections = contentBeanCollections;
  }

  protected SitesService getSitesService() {
    return sitesService;
  }

  @PostConstruct
  protected void initialize() {
    if (contentBeanCollections == null) {
      throw new IllegalStateException("Required property not set: contentBeanCollections");
    }
    if (sitesService == null) {
      throw new IllegalStateException("Required property not set: sitesService");
    }
  }

  @Override
  public Locale getLocale() {
    return getSitesService().getContentSiteAspect(getContent()).getLocale();
  }

  @Override
  public String getLang() {
    Locale locale = getLocale();
    return locale != null ? locale.getLanguage() : null;
  }

  @Override
  public String getCountry() {
    Locale locale = getLocale();
    return locale != null ? locale.getCountry() : null;
  }

  public CMLocalized getVariant(Locale locale) {
    return getVariantsByLocale().get(locale);
  }

  @Override
  public Map<Locale, ? extends CMLocalized> getVariantsByLocale() {
    return getVariantsByLocale(CMLocalized.class);
  }

  protected <T extends CMLocalized> Map<Locale, T> getVariantsByLocale(Class<T> type) {
    Map<Locale, Content> variantsByLocale = getSitesService().getContentSiteAspect(getContent()).getVariantsByLocale();
    return contentBeanCollections.contentBeanMap(variantsByLocale, type);
  }

  @Override
  public Collection<? extends CMLocalized> getLocalizations() {
    return getVariantsByLocale().values();
  }
}
