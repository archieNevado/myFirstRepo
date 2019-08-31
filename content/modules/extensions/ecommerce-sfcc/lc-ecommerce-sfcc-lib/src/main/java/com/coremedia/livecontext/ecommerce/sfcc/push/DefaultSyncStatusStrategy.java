package com.coremedia.livecontext.ecommerce.sfcc.push;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.push.SyncStatusStrategy;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The pushed content is defined to be out of sync by default. The editors need to keep track themselves.
 */
@DefaultAnnotation(NonNull.class)
public class DefaultSyncStatusStrategy implements SyncStatusStrategy {

  @Override
  public boolean isInSync(@NonNull String id, @NonNull StoreContext storeContext) {
    return false;
  }
}
