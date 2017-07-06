package com.coremedia.livecontext.ecommerce.hybris.event;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.event.InvalidationEvent;
import com.coremedia.livecontext.ecommerce.event.InvalidationService;
import com.coremedia.livecontext.ecommerce.hybris.common.AbstractCommerceService;
import com.coremedia.livecontext.ecommerce.hybris.common.CommerceIdHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.coremedia.livecontext.ecommerce.event.InvalidationEvent.CATEGORY_EVENT;
import static com.coremedia.livecontext.ecommerce.event.InvalidationEvent.CLEAR_ALL_EVENT;
import static com.coremedia.livecontext.ecommerce.event.InvalidationEvent.PRODUCT_EVENT;
import static com.coremedia.livecontext.ecommerce.event.InvalidationEvent.SEGMENT_EVENT;
import static com.coremedia.livecontext.ecommerce.event.InvalidationEvent.UNKNOWN_TYPE_EVENT;

public class InvalidationServiceImpl extends AbstractCommerceService implements InvalidationService {

  private static final String CLEAR_ALL_CONTENT_TYPE = "clearall";
  private static final String PRODUCT_CONTENT_TYPE = "product";
  private static final String SKU_CONTENT_TYPE = "sku";
  private static final String CATEGORY_CONTENT_TYPE = "category";
  private static final String SEGMENT_TYPE = "segment";

  private int maxWaitInMilliseconds = 20000;
  private int chunkSize = 500;

  private InvalidationResource invalidationResource;

  @Override
  @Nonnull
  public List<InvalidationEvent> getInvalidations(long lastExecutionTimeStamp) throws CommerceException {
    InvalidationsDocument invalidationsDocument = invalidationResource.getInvalidations(lastExecutionTimeStamp, maxWaitInMilliseconds, chunkSize);
    if (invalidationsDocument != null && invalidationsDocument.getInvalidations() != null) {
      List<InvalidationEvent> commerceCacheInvalidations = new ArrayList<>();
      for (InvalidationEvent sourceEvent: invalidationsDocument.getInvalidations()) {
        InvalidationEvent convertedEvent = convertEvent(sourceEvent);
        if (convertedEvent != null) {
          commerceCacheInvalidations.add(convertedEvent);
        }
      }
      return commerceCacheInvalidations;
    }
    return Collections.emptyList();
  }

  private InvalidationEvent convertEvent(InvalidationEvent sourceEvent) {
    if (sourceEvent.getTechId() != null) {
      String techId = sourceEvent.getTechId();
      String contentType = sourceEvent.getContentType().toLowerCase();
      if (StringUtils.isNotBlank(techId) && StringUtils.isNotBlank(contentType)) {
        switch (contentType) {
          case CLEAR_ALL_CONTENT_TYPE:
            return new InvalidationEvent(
                    null,
                    techId,
                    sourceEvent.getName(),
                    CLEAR_ALL_EVENT, sourceEvent.getTimestamp()
            );
          case PRODUCT_CONTENT_TYPE:
            return new InvalidationEvent(
                    CommerceIdHelper.formatProductId(techId),
                    techId,
                    sourceEvent.getName(),
                    PRODUCT_EVENT, sourceEvent.getTimestamp()
            );
          case SKU_CONTENT_TYPE:
            return new InvalidationEvent(
                    CommerceIdHelper.formatProductVariantId(techId),
                    techId,
                    sourceEvent.getName(),
                    PRODUCT_EVENT, sourceEvent.getTimestamp()
            );
          case CATEGORY_CONTENT_TYPE:
            return new InvalidationEvent(
                    CommerceIdHelper.formatCategoryId(techId),
                    techId,
                    sourceEvent.getName(),
                    CATEGORY_EVENT, sourceEvent.getTimestamp()
            );
          case SEGMENT_TYPE:
            return new InvalidationEvent(
                    CommerceIdHelper.formatSegmentId(techId),
                    techId,
                    sourceEvent.getName(),
                    SEGMENT_EVENT, sourceEvent.getTimestamp()
            );
          default:
            return new InvalidationEvent(
                    contentType+":"+techId,
                    techId,
                    sourceEvent.getName(),
                    UNKNOWN_TYPE_EVENT, sourceEvent.getTimestamp()
            );
        }
      }
    }
    return null;
  }

  @Nonnull
  @Override
  public String getServiceEndpointId() {
    String serviceEndpointId = null;
    if (invalidationResource.getConnector() != null) {
      serviceEndpointId = invalidationResource.getConnector().getServiceEndpointId();
    }
    return serviceEndpointId != null ? serviceEndpointId : "unknown";
  }

  @Required
  public void setInvalidationResource(InvalidationResource invalidationResource) {
    this.invalidationResource = invalidationResource;
  }

  public void setMaxWaitInMilliseconds(int maxWaitInMilliseconds) {
    this.maxWaitInMilliseconds = maxWaitInMilliseconds;
  }

  public void setChunkSize(int chunkSize) {
    this.chunkSize = chunkSize;
  }
}
