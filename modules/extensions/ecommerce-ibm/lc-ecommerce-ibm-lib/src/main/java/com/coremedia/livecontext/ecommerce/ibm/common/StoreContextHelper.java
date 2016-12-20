package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.base.livecontext.util.LocaleHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.login.WcCredentials;
import com.coremedia.livecontext.ecommerce.ibm.login.WcPreviewToken;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CATALOG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.COMMERCE_SYSTEM_IS_UNAVAILABLE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CONFIG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CONTRACT_IDS;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CURRENCY;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.LOCALE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.NO_WS_MARKER;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.PREVIEW_DATE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.REPLACEMENTS;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_NAME;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.USER_SEGMENTS;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.WORKSPACE_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.newStoreContext;

/**
 * Helper class to build an "IBM WCS conform" store context.
 * You do not have to know the exact keys if you use the helper method.
 * Use this class as static import.
 */
public class StoreContextHelper {

  private static final String CREDENTIALS = "credentials";
  private static final String PREVIEW_TOKEN = "previewToken";
  private static final String MISSING_TPL = "missing %s (%s)";

  private StoreContextHelper() {
  }

  /**
   * Set the given store context in the current request (thread).
   * Read the current context with #getCurrentContext().
   *
   * @param context the current context
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException
   */
  public static void setCurrentContext(@Nonnull StoreContext context) throws InvalidContextException {
    validateContext(context);
    Commerce.getCurrentConnection().setStoreContext(context);
  }

  /**
   * Gets the current store context within the current request (thread).
   * Set the current context with #setCurrentContext();
   *
   * @return the StoreContext
   */
  @Nullable
  public static StoreContext getCurrentContext() {
    CommerceConnection currentConnection = Commerce.getCurrentConnection();
    return currentConnection != null ? currentConnection.getStoreContext() : null;
  }

  @Nullable
  public static StoreContext getCurrentContextFor(@Nullable Locale locale) {
    StoreContext currentStoreContext = getCurrentContext();

    // locale can be null if the default locale is not set for commerce beans
    // in such a case we return the current context (a warning should be logged from caller)
    if (locale != null) {
      StoreContext result = StoreContextHelper.createContext(
              currentStoreContext.getConfigId(),
              currentStoreContext.getStoreId(),
              currentStoreContext.getStoreName(),
              currentStoreContext.getCatalogId(),
              locale.toString(),
              currentStoreContext.getCurrency().toString()
      );
      setWorkspaceId(result, currentStoreContext.getWorkspaceId());
      result.put(AbstractStoreContextProvider.CONFIG_KEY_WCS_VERSION, getWcsVersion(currentStoreContext));
      return result;
    }

    return currentStoreContext;
  }

  /**
   * Adds the given values to a store context.
   * All potential values are possible. You can use a "null" value to omit single values.
   *
   * @param storeId   the store id or null
   * @param storeName the store name or null
   * @param catalogId the catalog id or null
   * @param localeStr the locale id or null
   * @param currency  the currency id or null
   * @return the new built store context
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if locale or currency has wrong format
   */
  @Nonnull
  public static StoreContext createContext(@Nullable String configId, @Nullable String storeId,
                                           @Nullable String storeName, @Nullable String catalogId,
                                           @Nullable String localeStr, @Nullable String currency)
          throws InvalidContextException {
    StoreContext context = newStoreContext();

    addContextParameterIfNotBlank(context, CONFIG_ID, configId);
    addContextParameterIfNotBlank(context, STORE_ID, storeId);
    addContextParameterIfNotBlank(context, STORE_NAME, storeName);
    addContextParameterIfNotBlank(context, CATALOG_ID, catalogId);

    setLocale(context, localeStr);

    if (currency != null) {
      setCurrency(context, currency);
    }

    return context;
  }

  private static void addContextParameterIfNotBlank(@Nonnull StoreContext context, @Nonnull String key,
                                                    @Nullable String value) {
    if (value != null) {
      if (StringUtils.isBlank(value)) {
        throw new InvalidContextException("Value for key '" + key + "' must not be blank.");
      }

      context.put(key, value);
    }
  }

  private static void setCurrency(@Nonnull StoreContext context, @Nonnull String currencyCode) {
    Currency currency;

    try {
      currency = Currency.getInstance(currencyCode);
    } catch (IllegalArgumentException e) {
      throw new InvalidContextException(e);
    }

    context.put(CURRENCY, currency);
  }

  /**
   * Gets the store id from the given store context.
   *
   * @param context the store context
   * @return the store id
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the store id is invalid (missing or wrong type)
   */
  @Nonnull
  public static String getStoreId(@Nonnull StoreContext context) throws InvalidContextException {
    Object value = context.get(STORE_ID);

    if (!(value instanceof String)) {
      throw new InvalidContextException(String.format(MISSING_TPL, STORE_ID, formatContext(context)));
    }

    return (String) value;
  }

  /**
   * Gets the store name from the given store context.
   *
   * @param context the store context
   * @return the store name
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the store id is invalid (missing or wrong type)
   */
  @Nonnull
  public static String getStoreName(@Nonnull StoreContext context) throws InvalidContextException {
    Object value = context.get(STORE_NAME);

    if (!(value instanceof String)) {
      throw new InvalidContextException(String.format(MISSING_TPL, STORE_NAME, formatContext(context)));
    }

    return (String) value;
  }

  /**
   * Gets the store name from the given store context - in the form used for seo related handler, i.e. in low-case.
   *
   * @param context the store context
   * @return the store name in low-case
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the store id is invalid (missing or wrong type)
   */
  @Nonnull
  public static String getStoreNameInLowerCase(@Nonnull StoreContext context) throws InvalidContextException {
    return StringUtils.lowerCase(getStoreName(context));
  }

  /**
   * Gets the optional spring config id name from the given store context.
   * The config id can be used to read other store config credentials from a spring configuration.
   *
   * @param context the store context
   * @return the config id or null if not present
   */
  @Nullable
  public static String getConfigId(@Nonnull StoreContext context) throws InvalidContextException {
    return (String) context.get(CONFIG_ID);
  }

  /**
   * Gets the locale from the given store context.
   *
   * @param context the store context
   * @return the locale
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the store id is invalid (missing or wrong type)
   */
  @Nonnull
  public static Locale getLocale(@Nonnull StoreContext context) throws InvalidContextException {
    Object value = context.get(LOCALE);

    if (!(value instanceof Locale)) {
      throw new InvalidContextException(String.format(MISSING_TPL, LOCALE, formatContext(context)));
    }

    return (Locale) value;
  }

  /**
   * Sets locale to storeContext
   */
  public static void setLocale(@Nullable StoreContext context, @Nullable String localeStr) {
    if (context != null && localeStr != null) {
      Locale locale = LocaleHelper.getLocaleFromString(localeStr);

      if (locale == null) {
        throw new InvalidContextException("Locale " + localeStr + " is not valid.");
      }

      context.put(LOCALE, locale);
    }
  }

  /**
   * Gets the currency from the given store context.
   *
   * @param context the store context
   * @return the currency
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the store id is invalid (missing or wrong type)
   */
  @Nonnull
  public static Currency getCurrency(@Nonnull StoreContext context) throws InvalidContextException {
    Object value = context.get(CURRENCY);

    if (!(value instanceof Currency)) {
      throw new InvalidContextException(String.format(MISSING_TPL, CURRENCY, formatContext(context)));
    }

    return (Currency) value;
  }

  /**
   * Gets the workspace id from the given store context.
   *
   * @param context the store context
   * @return the workspace id or null if no workspace was set
   */
  @Nullable
  public static String getWorkspaceId(@Nonnull StoreContext context) {
    return (String) context.get(WORKSPACE_ID);
  }

  /**
   * Sets the workspaceId of the given context
   *
   * @param context     the given context
   * @param workspaceId the given workspaceId
   */
  public static void setWorkspaceId(@Nullable StoreContext context, @Nullable String workspaceId) {
    if (context != null) {
      if (workspaceId != null) {
        if (StringUtils.isBlank(workspaceId)) {
          throw new InvalidContextException("workspaceId has wrong format: \"" + workspaceId + "\"");
        }

        context.put(WORKSPACE_ID, workspaceId);
      } else {
        context.put(WORKSPACE_ID, NO_WS_MARKER);
      }
    }
  }

  /**
   * Gets the contract ids from the given store context.
   *
   * @param context the store context
   * @return the contract ids or null if no contract was set
   */
  @Nullable
  public static String[] getContractIds(@Nonnull StoreContext context) {
    return (String[]) context.get(CONTRACT_IDS);
  }

  /**
   * Sets the contract ids of the given context
   *
   * @param context     the given context
   * @param contractIds the given contract ids
   */
  public static void setContractIds(@Nullable StoreContext context, @Nullable String[] contractIds) {
    if (context != null && contractIds != null && contractIds.length > 0) {
      context.put(CONTRACT_IDS, contractIds);
    }
  }

  /**
   * Gets the wcs version from the given store context or WCS_VERSION_UNKNOWN if no version has been set.
   *
   * @param context the store context
   * @return the version
   */
  @Nonnull
  public static WcsVersion getWcsVersion(@Nonnull StoreContext context) {
    WcsVersion version = (WcsVersion) context.get(AbstractStoreContextProvider.CONFIG_KEY_WCS_VERSION);

    if (version == null) {
      throw new InvalidContextException(String.format(MISSING_TPL, AbstractStoreContextProvider.CONFIG_KEY_WCS_VERSION,
              formatContext(context)));
    }

    return version;
  }

  /**
   * Set the version to the given store context.
   *
   * @param context    the store context
   * @param wcsVersion the version as String
   */
  public static void setWcsVersion(@Nonnull StoreContext context, @Nullable String wcsVersion) {
    if (wcsVersion != null) {
      WcsVersion version = WcsVersion.fromVersionString(wcsVersion).orElse(null);
      if (version != null) {
        context.put(AbstractStoreContextProvider.CONFIG_KEY_WCS_VERSION, version);
      }
    }
  }

  /**
   * Gets the catalog id from the given store context.
   *
   * @param context the store context
   * @return the catalog id or null if no workspace was set
   */
  @Nullable
  public static String getCatalogId(@Nonnull StoreContext context) {
    return (String) context.get(CATALOG_ID);
  }

  /**
   * Gets the preview date in "YYYY/MM/dd HH:MM:SS" format from the given store context.
   *
   * @param context the store context
   * @return the preview date or null if no preview date was set
   */
  @Nullable
  public static String getPreviewDate(@Nonnull StoreContext context) {
    return (String) context.get(PREVIEW_DATE);
  }

  /**
   * Gets the list of comma separated user segments from the given store context.
   *
   * @param context the store context
   * @return the workspace id or null if no workspace was set
   */
  @Nullable
  public static String getUserSegments(StoreContext context) {
    return (String) context.get(USER_SEGMENTS);
  }

  public static boolean isCommerceSystemUnavailable(@Nullable StoreContext context) {
    if (context == null) {
      return false;
    }

    Object value = context.get(COMMERCE_SYSTEM_IS_UNAVAILABLE);
    return value instanceof Boolean && (Boolean) value;
  }

  public static void setCommerceSystemIsUnavailable(@Nullable StoreContext context, boolean isUnavailable) {
    if (context != null) {
      context.put(COMMERCE_SYSTEM_IS_UNAVAILABLE, isUnavailable);
    }
  }

  public static void setCredentials(@Nullable StoreContext context, WcCredentials credentials) {
    if (context != null) {
      context.put(CREDENTIALS, credentials);
    }
  }

  public static void setPreviewToken(@Nullable StoreContext context, WcPreviewToken previewToken) {
    if (context != null) {
      context.put(PREVIEW_TOKEN, previewToken);
    }
  }

  /**
   * Set the replacement map into the given store context.
   *
   * @param context      the store context
   * @param replacements the replacement map
   */
  public static void setReplacements(@Nullable StoreContext context, Map<String, String> replacements) {
    if (context != null) {
      context.put(REPLACEMENTS, replacements);
    }
  }

  /**
   * Set the configId into the given store context.
   *
   * @param context  the store context
   * @param configId the store configuration identifier
   */
  public static void setConfigId(@Nullable StoreContext context, String configId) {
    if (context != null) {
      context.put(CONFIG_ID, configId);
    }
  }

  public static void setSiteId(@Nullable StoreContext context, String siteId) {
    if (context != null) {
      context.put(StoreContextImpl.SITE, siteId);
    }
  }

  public static void setWcsTimeZone(@Nullable StoreContext context, Map<String, String> timeZone) {
    if (context != null) {
      context.put("wcsTimeZone", timeZone);
    }
  }

  /**
   * Gets true if the dynamic pricing is enabled that leads to separate personalized price calls.
   * Default: false (if not configured, it will be assumed it is not enabled)
   *
   * @param context the store context
   * @return true if enabled
   */
  public static boolean isDynamicPricingEnabled(@Nonnull StoreContext context) {
    Boolean value = (Boolean) context.get(AbstractStoreContextProvider.CONFIG_KEY_DYNAMIC_PRICING_ENABLED);
    return value != null ? value : false;
  }

  /**
   * Set the the value that dynamic pricing is enabled.
   *
   * @param context the store context
   * @param enabled the boolean value
   */
  public static void setDynamicPricingEnabled(@Nullable StoreContext context, boolean enabled) {
    if (context != null) {
      context.put(AbstractStoreContextProvider.CONFIG_KEY_DYNAMIC_PRICING_ENABLED, enabled);
    }
  }

  /**
   * Convenience method to validate the whole context.
   * Checks if all known context values exist.
   *
   * @param context the store context
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the context is invalid (missing or wrong typed values)
   */
  public static void validateContext(@Nullable StoreContext context) throws InvalidContextException {
    if (context == null) {
      throw new InvalidContextException("context is null");
    }

    getStoreId(context);
    getStoreName(context);
    getLocale(context);
    getCurrency(context);
  }

  @Nonnull
  private static String formatContext(@Nonnull final StoreContext context) {
    List<String> keys = ImmutableList.of(CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY, WORKSPACE_ID);

    return keys.stream().map(key -> key + ": " + context.get(key)).collect(Collectors.joining(", "));
  }
}
