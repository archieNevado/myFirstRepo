package com.coremedia.livecontext.ecommerce.ibm.contract;

import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.contract.Contract;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;

import java.util.Map;
import java.util.Objects;

public class ContractImpl extends AbstractIbmCommerceBean implements Contract {

  private Map<String, Object> delegate;
  private WcContractWrapperService contractWrapperService;
  private static int DEFAULT_CATALOG_IDENTIFIER = 0;

  @SuppressWarnings("unchecked")
  public Map<String, Object> getDelegate() {
    if (delegate == null) {
      delegate = (Map<String, Object>) getCommerceCache().get(new ContractCacheKey(getId(),
              getContext(), UserContextHelper.getCurrentContext(),getContractWrapperService(), getCommerceCache()));
      if (delegate == null) {
        throw new NotFoundException(getId() + " (contract not found in catalog)");
      }
    }
    return delegate;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setDelegate(Object delegate) {
    this.delegate = (Map<String, Object>) delegate;
  }

  @Override
  public String getName() {
    return DataMapHelper.getValueForKey(getDelegate(), "name", String.class);
  }

  @Override
  public String getDescription() {
    return DataMapHelper.getValueForKey(getDelegate(), "name", String.class);
  }

  @Override
  public boolean isDefaultContract() {
    Integer usageType = DataMapHelper.getValueForKey(getDelegate(), "usage", Integer.class);
    return Objects.equals(DEFAULT_CATALOG_IDENTIFIER, usageType);
  }

  @Override
  public String getReference() {
    return null;
  }

  @Override
  public String getExternalId() {
    return DataMapHelper.getValueForKey(getDelegate(), "referenceNumber", String.class);
  }

  @Override
  public String getExternalTechId() {
    return DataMapHelper.getValueForKey(getDelegate(), "referenceNumber", String.class);
  }

  public void setContractWrapperService(WcContractWrapperService contractWrapperService) {
    this.contractWrapperService = contractWrapperService;
  }

  public WcContractWrapperService getContractWrapperService() {
    return contractWrapperService;
  }
}
