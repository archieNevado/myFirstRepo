package com.coremedia.livecontext.ecommerce.hybris.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Locale;
import java.util.Optional;

public class StoreContextHelper {

  private StoreContextHelper() {
  }

  /**
   * Gets the current store context within the current request (thread),
   * or nothing if not set.
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
   *
   * @return the current store context
   */
  @NonNull
  public static StoreContext getCurrentContextOrThrow() {
    return findCurrentContext().orElseThrow(() -> new InvalidContextException("Current store context not available"));
  }

  @NonNull
  public static String getStoreId(@NonNull StoreContext context) {
    String storeId = context.getStoreId();

    if (storeId == null) {
      throw new InvalidContextException("Missing store ID in store context (" + context + ")");
    }

    return storeId;
  }

  @NonNull
  public static Locale getLocale(@NonNull StoreContext context) {
    Locale locale = context.getLocale();

    if (locale == null) {
      throw new InvalidContextException("Missing locale in store context (" + context + ")");
    }

    return locale;
  }
}
