package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public abstract class AbstractWcWrapperService {

  private static final String PARAM_CATALOG_ID = "catalogId";
  private static final String PARAM_CONTRACT_ID = "contractId";
  private static final String PARAM_LANG_ID = "langId";
  private static final String PARAM_CURRENCY = "currency";
  protected static final String PARAM_FOR_USER = "forUser";
  protected static final String PARAM_FOR_USER_ID = "forUserId";

  private WcRestConnector restConnector;
  private WcLanguageMappingService wcLanguageMappingService;
  private CatalogAliasTranslationService catalogAliasTranslationService;

  @Autowired
  public void setRestConnector(WcRestConnector connector) {
    this.restConnector = connector;
  }

  public WcRestConnector getRestConnector() {
    return restConnector;
  }

  public WcRestConnector getCatalogConnector() {
    return restConnector;
  }

  @Required
  public void setCatalogAliasTranslationService(CatalogAliasTranslationService catalogAliasTranslationService) {
    this.catalogAliasTranslationService = catalogAliasTranslationService;
  }

  protected WcLanguageMappingService getWcLanguageMappingService() {
    return wcLanguageMappingService;
  }

  @Autowired
  void setWcLanguageMappingService(WcLanguageMappingService wcLanguageMappingService) {
    this.wcLanguageMappingService = wcLanguageMappingService;
  }

  public Map getLanguageMapping() {
    return wcLanguageMappingService.getLanguageMapping();
  }

  /**
   * Adds the given parameters to a map.
   */
  @Nonnull
  public Map<String, String[]> createParametersMap(@Nullable CatalogAlias catalogAlias, @Nullable Locale locale,
                                                   @Nullable Currency currency, @Nullable Integer userId,
                                                   @Nullable String userName, @Nullable String[] contractIds,
                                                   @Nonnull StoreContext storeContext) {
    Map<String, String[]> parameters = new TreeMap<>();

    if (catalogAlias != null) {
      Optional<CatalogId> catalogId = getCatalogId(catalogAlias, storeContext);
      catalogId.ifPresent(catalogId1 -> parameters.put(PARAM_CATALOG_ID, new String[]{catalogId1.value()}));
    }

    if (locale != null) {
      String languageId = getLanguageId(locale);
      parameters.put(PARAM_LANG_ID, new String[]{languageId});
    }

    if (currency != null) {
      parameters.put(PARAM_CURRENCY, new String[]{currency.toString()});
    }

    if (userId != null) {
      parameters.put(PARAM_FOR_USER_ID, new String[]{String.valueOf(userId)});
    } else if (userName != null) {
      parameters.put(PARAM_FOR_USER, new String[]{userName});
    } else if (contractIds != null) {
      parameters.put(PARAM_CONTRACT_ID, contractIds);
    }

    return parameters;
  }

  @Nonnull
  public Map<String, String[]> createParametersMap(@Nullable CatalogAlias catalogAlias, @Nullable Locale locale,
                                                   @Nullable Currency currency, StoreContext storeContext) {
    return createParametersMap(catalogAlias, locale, currency, null, null, null, storeContext);
  }

  @Nonnull
  public Map<String, String[]> createParametersMap(@Nullable CatalogAlias catalogAlias, @Nullable Locale locale,
                                                   @Nullable Currency currency, @Nullable String[] contractIds,
                                                   StoreContext storeContext) {
    return createParametersMap(catalogAlias, locale, currency, null, null, contractIds, storeContext);
  }

  /**
   * Gets IBM specific language Id for a given locale String.
   * If a certain mapping does not exist or locale String is invalid, the default "-1" for "en" is returned.
   *
   * @param locale e.g. "en_US" "en" "de"
   */
  @Nonnull
  public String getLanguageId(@Nullable Locale locale) {
    return getWcLanguageMappingService().getLanguageId(locale);
  }

  @Nonnull
  private Optional<CatalogId> getCatalogId(@Nonnull CatalogAlias catalogAlias, @Nonnull StoreContext storeContext) {
    String siteId = storeContext.getSiteId();
    return catalogAliasTranslationService.getCatalogIdForAlias(catalogAlias, siteId);
  }
}
