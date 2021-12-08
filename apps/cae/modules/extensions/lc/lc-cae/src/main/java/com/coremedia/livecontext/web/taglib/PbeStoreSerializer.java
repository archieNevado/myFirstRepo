package com.coremedia.livecontext.web.taglib;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.serialization.cap.ParameterBasedLinkableSerializer;

import javax.inject.Named;
import java.util.Map;

/**
 * The StoreSerializer uses {@link com.fasterxml.jackson.databind.ObjectMapper} (and
 * other chained {@link com.fasterxml.jackson.databind.JsonSerializer}s too) to serialize objects of
 * type StoreContext into an URI link based on specific parameters. The resulting JSON takes the form:
 * <code>
 *   { "$Ref": $URI_LINK }
 * </code>
 */
@Named
public class PbeStoreSerializer extends ParameterBasedLinkableSerializer<StoreContext> {

  public static final String STORE_URI_TEMPLATE = "livecontext/store/{siteId}";

  public PbeStoreSerializer() {
    setUriTemplate(STORE_URI_TEMPLATE);
  }

  @Override
  public void fillLinkParameters(StoreContext storeContext, Map<String, String> params) {
    params.put("siteId", storeContext.getSiteId());
  }

  @Override
  public Class<StoreContext> handledType() {
    return StoreContext.class;
  }
}
