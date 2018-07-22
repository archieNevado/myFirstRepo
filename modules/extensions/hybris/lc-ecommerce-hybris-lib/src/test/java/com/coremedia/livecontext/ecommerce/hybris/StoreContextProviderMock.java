package com.coremedia.livecontext.ecommerce.hybris;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Currency;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CURRENCY;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.LOCALE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_NAME;

public class StoreContextProviderMock implements StoreContextProvider {

  @NonNull
  @Override
  public Optional<StoreContext> findContextBySiteId(@NonNull String siteId) {
    if (!"Helios".equals(siteId)) {
      throw new InvalidContextException("Could not find context for " + siteId);
    }

    return Optional.of(createContext());
  }

  @NonNull
  @Override
  public Optional<StoreContext> findContextBySite(@NonNull Site site) {
    if (!"Helios".equals(site.getName())) {
      throw new InvalidContextException("Could not find context for " + site.getName());
    }

    return Optional.of(createContext());
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
  private StoreContext createContext(String storeId, String storeName, String catalogId, String locale,
                                     String currency) {
    StoreContext context = StoreContextImpl.newStoreContext();

    if (storeId != null) {
      if (StringUtils.isBlank(storeId)) {
        throw new InvalidContextException("storeId has wrong format: \"" + storeId + "\"");
      }

      context.put(STORE_ID, storeId);
    }

    if (storeName != null) {
      if (StringUtils.isBlank(storeName)) {
        throw new InvalidContextException("storeName has wrong format: \"" + storeId + "\"");
      }

      context.put(STORE_NAME, storeName);
    }

    if (locale != null) {
      try {
        context.put(LOCALE, LocaleUtils.toLocale(locale));
      } catch (IllegalArgumentException e) {
        throw new InvalidContextException(e);
      }
    }

    if (currency != null) {
      try {
        context.put(CURRENCY, Currency.getInstance(currency));
      } catch (IllegalArgumentException e) {
        throw new InvalidContextException(e);
      }
    }

    return context;
  }

  @NonNull
  @Override
  public StoreContextBuilder buildContext(@NonNull StoreContext source) {
    return StoreContextBuilderImpl.from((StoreContextImpl) source);
  }
}
