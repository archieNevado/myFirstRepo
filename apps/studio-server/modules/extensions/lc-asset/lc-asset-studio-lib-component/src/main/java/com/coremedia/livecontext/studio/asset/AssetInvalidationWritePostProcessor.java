package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.util.CommerceReferenceHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.ecommerce.studio.rest.cache.CommerceCacheInvalidationSource;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.rest.cap.intercept.ContentWritePostprocessorBase;
import com.coremedia.rest.intercept.WriteReport;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * {@link com.coremedia.rest.cap.intercept.ContentWritePostprocessor}
 * which invalidates CommerceRemote Beans if image data is changed.
 * We cannot use a repository listener nor write interceptor for this.
 * The first one doesn't tell us which property is changed.
 * The second one invalidates too early.
 */
public class AssetInvalidationWritePostProcessor extends ContentWritePostprocessorBase {

  private static final Logger LOG = LoggerFactory.getLogger(AssetInvalidationWritePostProcessor.class);

  /**
   * Name of the document property CMPicture#data.
   */
  @VisibleForTesting
  static String CMPICTURE_DATA = "data";

  @VisibleForTesting
  static final String STRUCT_PROPERTY_NAME = "localSettings";

  private final CommerceConnectionSupplier commerceConnectionSupplier;
  private final CommerceCacheInvalidationSource commerceCacheInvalidationSource;

  AssetInvalidationWritePostProcessor(CommerceCacheInvalidationSource commerceCacheInvalidationSource,
                                      CommerceConnectionSupplier commerceConnectionSupplier) {
    this.commerceCacheInvalidationSource = commerceCacheInvalidationSource;
    this.commerceConnectionSupplier = commerceConnectionSupplier;
  }

  @Override
  public void postProcess(WriteReport<Content> report) {
    Content content = report.getEntity();
    Map<String, Object> properties = report.getOverwrittenProperties();

    if (content != null && properties != null && properties.containsKey(CMPICTURE_DATA)) {
      invalidate(content);
    }
  }

  private void invalidate(@NonNull Content content) {
    Struct localSettings = (Struct) content.get(STRUCT_PROPERTY_NAME);

    Set<String> productReferences = new HashSet<>(CommerceReferenceHelper.getExternalReferences(localSettings));

    Optional<CommerceConnection> commerceConnection = commerceConnectionSupplier.findConnection(content);

    if (commerceConnection.isEmpty()) {
      LOG.debug("Commerce connection not available, will not invalidate references.");
      return;
    }

    StoreContext storeContext = commerceConnection.get().getInitialStoreContext();
    commerceCacheInvalidationSource.invalidateReferences(productReferences, storeContext);
  }
}
