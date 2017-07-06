package com.coremedia.livecontext.ecommerce.hybris.rest.resources;


import com.coremedia.livecontext.ecommerce.hybris.rest.HybrisRestConnector;

import javax.inject.Inject;
import javax.inject.Named;

abstract class AbstractHybrisResource {

  private HybrisRestConnector connector;
  private HybrisRestConnector occConnector;

  public HybrisRestConnector getConnector() {
    return connector;
  }

  @Inject
  @Named("hybrisRestConnector")
  public void setConnector(HybrisRestConnector connector) {
    this.connector = connector;
  }

  public HybrisRestConnector getOccConnector() {
    return occConnector;
  }

  @Inject
  @Named("hybrisOccRestConnector")
  public void setOccConnector(HybrisRestConnector occConnector) {
    this.occConnector = occConnector;
  }
}
