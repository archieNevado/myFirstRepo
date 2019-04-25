package com.coremedia.livecontext.ecommerce.hybris.event;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.HybrisRestConnector;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class InvalidationResource {

  private static final String INVALIDATIONS_PATH = "/cacheinvalidation/{timestamp}";

  private HybrisRestConnector connector;

  @NonNull
  Optional<InvalidationsDocument> getInvalidations(long timestamp, int maxWaitInMilliseconds, int chunkSize,
                                                   @NonNull StoreContext storeContext) {
    List<String> uriTemplateParameters = Collections.singletonList(timestamp + "");

    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("maxWait", maxWaitInMilliseconds + "");
    queryParams.add("chunkSize", chunkSize + "");
    queryParams.add("site", StoreContextHelper.getStoreId(storeContext));

    InvalidationsDocument invalidationsDocument = connector.performGet(INVALIDATIONS_PATH, storeContext,
            InvalidationsDocument.class, uriTemplateParameters, queryParams, true);

    return Optional.ofNullable(invalidationsDocument);
  }

  @Required
  public void setConnector(HybrisRestConnector connector) {
    this.connector = connector;
  }

  public HybrisRestConnector getConnector() {
    return connector;
  }
}
