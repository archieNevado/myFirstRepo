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

import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.newStoreContext;

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
    return StoreContextBuilderImpl.from(newStoreContext())
            .withStoreId("10001")
            .withStoreName("aurora")
            .withCurrency(Currency.getInstance("USD"))
            .withLocale(Locale.US)
            .build();
  }

  @NonNull
  @Override
  public StoreContextBuilder buildContext(@NonNull StoreContext source) {
    return StoreContextBuilderImpl.from((StoreContextImpl) source);
  }
}
