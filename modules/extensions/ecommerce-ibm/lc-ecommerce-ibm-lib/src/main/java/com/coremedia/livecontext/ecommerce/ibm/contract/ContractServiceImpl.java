package com.coremedia.livecontext.ecommerce.ibm.contract;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.contract.Contract;
import com.coremedia.livecontext.ecommerce.contract.ContractService;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CONTRACT;
import static com.coremedia.livecontext.ecommerce.ibm.common.IbmCommerceIdProvider.commerceId;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class ContractServiceImpl implements ContractService {

  private static final Logger LOG = LoggerFactory.getLogger(ContractServiceImpl.class);

  private WcContractWrapperService contractWrapperService;
  private CommerceCache commerceCache;
  private CommerceBeanFactory commerceBeanFactory;
  private String contractPreviewServiceUserName;

  @Nullable
  @Override
  @SuppressWarnings("unchecked")
  public Contract findContractById(@Nonnull CommerceId id, @Nonnull StoreContext storeContext) {
    UserContext userContext = UserContextHelper.getCurrentContext();

    ContractCacheKey contractCacheKey = new ContractCacheKey(id, storeContext, userContext,
            contractWrapperService, commerceCache);

    Map<String, Object> contract = commerceCache.get(contractCacheKey);
    if (contract == null) {
      return null;
    }

    return createContractBeanFor(contract);
  }

  @Nonnull
  @Override
  public Collection<Contract> findContractIdsForUser(@Nonnull UserContext userContext, @Nonnull StoreContext storeContext) {
    return findContractIdsForUser(userContext, storeContext, null);
  }

  @Nonnull
  @Override
  public Collection<Contract> findContractIdsForUser(@Nonnull UserContext userContext, @Nonnull StoreContext storeContext,
                                                     @Nullable String organizationId) {

    ContractsByUserCacheKey contractsByUserCacheKey = new ContractsByUserCacheKey(userContext, storeContext,
            organizationId, contractWrapperService, commerceCache);
    Map<String, Object> contractMap = commerceCache.get(contractsByUserCacheKey);

    Map contracts = DataMapHelper.getValueForKey(contractMap, "contracts", Map.class);
    if (contracts == null) {
      return emptyList();
    }

    return createContractBeansFor(contracts);
  }

  @Nonnull
  @Override
  public Collection<Contract> findContractIdsForServiceUser(StoreContext storeContext) {
    if (contractPreviewServiceUserName == null) {
      LOG.warn("No service user for contract preview configured for ContractService");
      return emptyList();
    }

    UserContext userContext = UserContext.builder().withUserName(contractPreviewServiceUserName).build();
    return findContractIdsForUser(userContext, storeContext);
  }

  @Nullable
  private Contract createContractBeanFor(@Nonnull Map<String, Object> contractMap) {
    if (contractMap.isEmpty()) {
      return null;
    }

    String externalId = String.valueOf(contractMap.get("referenceNumber"));
    CommerceId commerceId = toContractId(externalId);
    Contract contract = (Contract) commerceBeanFactory.createBeanFor(commerceId, StoreContextHelper.getCurrentContext());
    ((AbstractIbmCommerceBean) contract).setDelegate(contractMap);
    return contract;
  }

  static CommerceId toContractId(String externalId) {
    return commerceId(CONTRACT).withExternalId(externalId).build();
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  private List<Contract> createContractBeansFor(@Nonnull Map<String, Object> contractsMap) {
    if (contractsMap.isEmpty()) {
      return emptyList();
    }

    StoreContext currentContext = StoreContextHelper.getCurrentContext();

    return contractsMap.keySet().stream()
            .map(ContractServiceImpl::toContractId)
            .map(id -> commerceBeanFactory.createBeanFor(id, currentContext))
            .map(Contract.class::cast)
            .collect(collectingAndThen(toList(), Collections::unmodifiableList));
  }

  @Required
  public void setContractWrapperService(WcContractWrapperService contractWrapperService) {
    this.contractWrapperService = contractWrapperService;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  @Required
  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }

  public void setContractPreviewServiceUserName(String contractPreviewServiceUserName) {
    this.contractPreviewServiceUserName = contractPreviewServiceUserName;
  }

  @VisibleForTesting
  String getContractPreviewServiceUserName() {
    return contractPreviewServiceUserName;
  }
}
