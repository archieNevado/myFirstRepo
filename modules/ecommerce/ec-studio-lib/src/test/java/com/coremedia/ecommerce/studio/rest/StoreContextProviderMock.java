package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.base.livecontext.util.LocaleHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;

import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CURRENCY;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.LOCALE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_NAME;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.newStoreContext;

public class StoreContextProviderMock implements StoreContextProvider {

  @NonNull
  @Override
  public Optional<StoreContext> findContextBySiteId(@NonNull String siteId) {
    if (siteId.equals("Helios")) {
      return Optional.of(createContext());
    } else {
      throw new InvalidContextException("Could not find context for " + siteId);
    }
  }

  @NonNull
  @Override
  public Optional<StoreContext> findContextBySite(@NonNull Site site) {
    if ("Helios".equals(site.getName())) {
      return Optional.of(createContext());
    } else {
      throw new InvalidContextException("Could not find context for " + site.getName());
    }
  }

  @NonNull
  @Override
  public Optional<StoreContext> findContextByContent(@NonNull Content content) {
    return Optional.of(createContext());
  }

  @NonNull
  @Override
  public Optional<StoreContext> createContext(@NonNull Site site) {
    return Optional.of(createContext());
  }

  @NonNull
  private StoreContext createContext() {
    return createContext("10001", "aurora", "10001", "en_US", "USD");
  }

  @NonNull
  private StoreContext createContext(@Nullable String storeId, @Nullable String storeName, String catalogId,
                                     @Nullable String localeStr, @Nullable String currency) {
    StoreContext context = newStoreContext();

    if (storeId != null) {
      context.put(STORE_ID, parseString(storeId, STORE_ID));
    }

    if (storeName != null) {
      context.put(STORE_NAME, parseString(storeName, STORE_NAME));
    }

    if (localeStr != null) {
      context.put(LOCALE, parseLocale(localeStr));
    }

    if (currency != null) {
      context.put(CURRENCY, parseCurrency(currency));
    }

    return context;
  }

  @NonNull
  private static String parseString(@NonNull String str, @NonNull String description) {
    if (StringUtils.isBlank(str)) {
      throw new InvalidContextException("'" + description + "' has wrong format: \"" + str + "\"");
    }

    return str;
  }

  @NonNull
  private static Locale parseLocale(@NonNull String localeStr) {
    return LocaleHelper.parseLocaleFromString(localeStr)
            .orElseThrow(() -> new InvalidContextException("Locale '" + localeStr + "' is not valid."));
  }

  @NonNull
  private static Currency parseCurrency(@NonNull String currencyStr) {
    try {
      return Currency.getInstance(currencyStr);
    } catch (IllegalArgumentException e) {
      throw new InvalidContextException(e);
    }
  }

  @NonNull
  @Override
  public StoreContextBuilder buildContext(@NonNull StoreContext source) {
    return StoreContextBuilderImpl.from((StoreContextImpl) source);
  }
}
