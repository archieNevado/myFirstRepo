package com.coremedia.ecommerce.studio.rest;

import com.coremedia.livecontext.ecommerce.contract.Contract;

import java.util.Collection;
import java.util.Collections;

/**
 * Contracts representation for JSON.
 *
 * @deprecated This class is part of the commerce integration "b2b support" that is not
 * supported by the Commerce Hub architecture. It will be removed or changed in the future.
 */
@Deprecated
public class ContractsRepresentation extends AbstractCatalogRepresentation {

  private Collection<Contract> contracts = Collections.emptyList();

  public Collection<Contract> getContracts() {
    return contracts;
  }

  public void setContracts(Collection<Contract> contracts) {
    this.contracts = contracts;
  }
}
