package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.base.livecontext.util.LocaleHelper;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.login.WcCredentials;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CATALOG_ALIAS;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CATALOG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.COMMERCE_SYSTEM_IS_UNAVAILABLE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CURRENCY;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.LOCALE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.REPLACEMENTS;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_NAME;

/**
 * Helper class to build an "IBM WCS conform" store context.
 * You do not have to know the exact keys if you use the helper method.
 * Use this class as static import.
 */
public class StoreContextHelper {

  private static final String CREDENTIALS = "credentials";
  private static final String MISSING_TPL = "missing %s (%s)";

  private StoreContextHelper() {
  }

  /**
   * Set the given store context in the current request (thread).
   * Read the current context with #getCurrentContext().
   *
   * @param context the current context
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the context is invalid (missing or wrong typed values)
   */
  public static void setCurrentContext(@NonNull StoreContext context) {
    validateContext(context);

    CurrentCommerceConnection.get().setStoreContext(context);
  }

  /**
   * Gets the current store context within the current request (thread),
   * or nothing if not set.
   * <p>
   * Set the current context with #setCurrentContext();
   *
   * @return the current store context, or nothing
   */
  @NonNull
  public static Optional<StoreContext> findCurrentContext() {
    return CurrentCommerceConnection.find().map(CommerceConnection::getStoreContext);
  }

  /**
   * Gets the current store context within the current request (thread),
   * or throws an exception if not set.
   * <p>
   * Set the current context with #setCurrentContext();
   *
   * @return the current store context
   */
  @NonNull
  public static StoreContext getCurrentContextOrThrow() {
    return findCurrentContext().orElseThrow(() -> new InvalidContextException("Current store context not available"));
  }

  @NonNull
  public static StoreContext getCurrentContextFor(@Nullable Locale locale) {
    StoreContext currentStoreContext = getCurrentContextOrThrow();

    // locale can be null if the default locale is not set for commerce beans
    // in such a case we return the current context (a warning should be logged from caller)
    if (locale == null) {
      return currentStoreContext;
    }

    StoreContext result = createContext(
            currentStoreContext.getSiteId(),
            currentStoreContext.getStoreId(),
            currentStoreContext.getStoreName(),
            currentStoreContext.getCatalogId() != null ? CatalogId.of(currentStoreContext.getCatalogId()) : null,
            locale.toString(),
            currentStoreContext.getCurrency()
    );

    result.put(CATALOG_ALIAS, currentStoreContext.getCatalogAlias());
    result.setWorkspaceId(currentStoreContext.getWorkspaceId().orElse(null));
    result.put(AbstractStoreContextProvider.CONFIG_KEY_WCS_VERSION, getWcsVersion(currentStoreContext));

    return result;
  }

  /**
   * Adds the given values to a store context.
   * All potential values are possible. You can use a "null" value to omit most single values.
   *
   * @param siteId    the site id or null
   * @param storeId   the store id or null
   * @param storeName the store name or null
   * @param catalogId the catalog id or null
   * @param localeStr the locale id or null
   * @param currency  the currency or null
   * @return the new built store context
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if locale or currency has wrong format
   */
  @NonNull
  public static StoreContext createContext(@NonNull String siteId, @Nullable String storeId, @Nullable String storeName,
                                           @Nullable CatalogId catalogId, @Nullable String localeStr,
                                           @Nullable Currency currency) {
    StoreContext context = StoreContextImpl.builder(siteId).build();

    addContextParameterIfNotBlank(context, STORE_ID, storeId);
    addContextParameterIfNotBlank(context, STORE_NAME, storeName);

    if (catalogId != null) {
      context.put(CATALOG_ID, catalogId.value());
    }

    if (localeStr != null) {
      Locale locale = LocaleHelper.parseLocaleFromString(localeStr)
              .orElseThrow(() -> new InvalidContextException("Locale '" + localeStr + "' is not valid."));
      setLocale(context, locale);
    }

    if (currency != null) {
      context.put(CURRENCY, currency);
    }

    return context;
  }

  private static void addContextParameterIfNotBlank(@NonNull StoreContext context, @NonNull String key,
                                                    @Nullable String value) {
    if (value == null) {
      return;
    }

    if (StringUtils.isBlank(value)) {
      throw new InvalidContextException("Value for key '" + key + "' must not be blank.");
    }

    context.put(key, value);
  }

  /**
   * Gets the store id from the given store context.
   *
   * @param context the store context
   * @return the store id
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the store id is invalid (missing or wrong type)
   */
  @NonNull
  public static String getStoreId(@NonNull StoreContext context) {
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
  @NonNull
  public static String getStoreName(@NonNull StoreContext context) {
    Object value = context.get(STORE_NAME);

    if (!(value instanceof String)) {
      throw new InvalidContextException(String.format(MISSING_TPL, STORE_NAME, formatContext(context)));
    }

    return (String) value;
  }

  /**
   * Gets the locale from the given store context.
   *
   * @param context the store context
   * @return the locale
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the store id is invalid (missing or wrong type)
   */
  @NonNull
  public static Locale getLocale(@NonNull StoreContext context) {
    Object value = context.get(LOCALE);

    if (!(value instanceof Locale)) {
      throw new InvalidContextException(String.format(MISSING_TPL, LOCALE, formatContext(context)));
    }

    return (Locale) value;
  }

  /**
   * Sets locale to storeContext
   */
  public static void setLocale(@NonNull StoreContext context, @NonNull Locale locale) {
    context.put(LOCALE, locale);
  }

  /**
   * Gets the currency from the given store context.
   *
   * @param context the store context
   * @return the currency
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the store id is invalid (missing or wrong type)
   */
  @NonNull
  public static Currency getCurrency(@NonNull StoreContext context) {
    Object value = context.get(CURRENCY);

    if (!(value instanceof Currency)) {
      throw new InvalidContextException(String.format(MISSING_TPL, CURRENCY, formatContext(context)));
    }

    return (Currency) value;
  }

  /**
   * Gets the wcs version from the given store context or WCS_VERSION_UNKNOWN if no version has been set.
   *
   * @param context the store context
   * @return the version
   */
  @NonNull
  public static WcsVersion getWcsVersion(@NonNull StoreContext context) {
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
  public static void setWcsVersion(@NonNull StoreContext context, @NonNull String wcsVersion) {
    WcsVersion.fromVersionString(wcsVersion)
            .ifPresent(version -> context.put(AbstractStoreContextProvider.CONFIG_KEY_WCS_VERSION, version));
  }

  public static boolean isCommerceSystemUnavailable(@NonNull StoreContext context) {
    Object value = context.get(COMMERCE_SYSTEM_IS_UNAVAILABLE);
    return value instanceof Boolean && (Boolean) value;
  }

  public static void setCommerceSystemIsUnavailable(@NonNull StoreContext context, boolean isUnavailable) {
    context.put(COMMERCE_SYSTEM_IS_UNAVAILABLE, isUnavailable);
  }

  public static void setCredentials(@NonNull StoreContext context, WcCredentials credentials) {
    context.put(CREDENTIALS, credentials);
  }

  /**
   * Set the replacement map into the given store context.
   *
   * @param context      the store context
   * @param replacements the replacement map
   */
  public static void setReplacements(@NonNull StoreContext context, Map<String, String> replacements) {
    context.put(REPLACEMENTS, replacements);
  }

  /**
   * Gets true if the dynamic pricing is enabled that leads to separate personalized price calls.
   * Default: false (if not configured, it will be assumed it is not enabled)
   *
   * @param context the store context
   * @return true if enabled
   */
  public static boolean isDynamicPricingEnabled(@NonNull StoreContext context) {
    Boolean value = (Boolean) context.get(AbstractStoreContextProvider.CONFIG_KEY_DYNAMIC_PRICING_ENABLED);
    return value != null ? value : false;
  }

  /**
   * Convenience method to validate the whole context.
   * Checks if all known context values exist.
   *
   * @param context the store context
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the context is invalid (missing or wrong typed values)
   */
  public static void validateContext(@NonNull StoreContext context) {
    getStoreId(context);
    getStoreName(context);
    getLocale(context);
    getCurrency(context);
  }

  public static boolean isValid(@NonNull StoreContext storeContext) {
    try {
      validateContext(storeContext);
    } catch (InvalidContextException e) {
      return false;
    }
    return true;
  }

  @NonNull
  private static String formatContext(@NonNull StoreContext context) {
    ImmutableMap.Builder<String, Object> mapBuilder = ImmutableMap.builder();

    ImmutableList.of(STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY)
            .forEach(key -> mapBuilder.put(key, String.valueOf(context.get(key))));

    mapBuilder.put("workspaceId", context.getWorkspaceId());

    Map<String, Object> keyValuePairs = mapBuilder.build();

    return Joiner.on(", ")
            .withKeyValueSeparator(": ")
            .join(keyValuePairs);
  }
}
