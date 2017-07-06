package com.coremedia.livecontext.ecommerce.hybris.event;

import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.HybrisRestConnector;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

public class InvalidationResource {

  private final static String INVALIDATIONS_PATH = "/cacheinvalidation/{timestamp}";

  private HybrisRestConnector connector;

  public InvalidationsDocument getInvalidations(long timestamp, int maxWaitInMilliseconds, int chunkSize) {
    List<String> uriTemplateParameters = new ArrayList<>();
    uriTemplateParameters.add(timestamp+"");
    MultiValueMap<String, String>  queryParams = new LinkedMultiValueMap<>();
    queryParams.add("maxWait", maxWaitInMilliseconds+"");
    queryParams.add("chunkSize", chunkSize+"");
    queryParams.add("site", StoreContextHelper.getStoreId());
    return connector.performGet(INVALIDATIONS_PATH, StoreContextHelper.getCurrentContextOrThrow(), InvalidationsDocument.class, uriTemplateParameters, queryParams, true);
  }

  @Required
  public void setConnector(HybrisRestConnector connector) {
    this.connector = connector;
  }

  public HybrisRestConnector getConnector() {
    return connector;
  }
}
