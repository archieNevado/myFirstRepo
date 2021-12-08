package com.coremedia.blueprint.elastic.base;

import com.coremedia.elastic.core.api.tenant.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * This bean registers the tenants configured in content repository (via root channel settings).
 */
@SuppressWarnings("UnusedDeclaration") // used by Spring - it's a managed bean
public class TenantInitializer {

  private static final Logger LOG = LoggerFactory.getLogger(TenantInitializer.class);
  private static final String DELAY_PATTERN = "${tenant.recomputation.interval:60000}";
  private static final String INTERNAL_TENANT = "internal";

  private final TenantService tenantService;
  private final TenantHelper tenantHelper;

  public TenantInitializer(TenantService tenantService, TenantHelper tenantHelper) {
    this.tenantService = tenantService;
    this.tenantHelper = tenantHelper;
  }

  private void registerTenantsFromContent() {
    final Collection<String> configuredTenants = tenantHelper.readTenantsFromContent();
    final Collection<String> registeredTenants = tenantService.getRegistered();
    deregisterTenants(configuredTenants, registeredTenants);
    registerTenants(configuredTenants, registeredTenants);
  }

  private void registerTenants(Collection<String> configuredTenants, Collection<String> registeredTenants) {
    final Collection<String> tenantsToAdd = configuredTenants.stream()
            .filter(Predicate.not(registeredTenants::contains))
            .collect(Collectors.toList());
    if(!tenantsToAdd.isEmpty()) {
      LOG.debug("registering tenants {}", tenantsToAdd);
      tenantService.registerAll(tenantsToAdd);
    }
  }

  private void deregisterTenants(Collection<String> configuredTenants, Collection<String> registeredTenants) {
    final Collection<String> tenantsToRemove = registeredTenants.stream()
            .filter(Predicate.not(configuredTenants::contains))
            .collect(Collectors.toList());
    // The internal tenant must not be deregistered
    tenantsToRemove.remove(INTERNAL_TENANT);
    if(!tenantsToRemove.isEmpty()) {
      LOG.debug("deregistering tenants {}", tenantsToRemove);
      tenantService.deregisterAll(tenantsToRemove);
    }
  }

  @Scheduled(
          fixedDelayString = DELAY_PATTERN,
          initialDelayString = "0")
  void initializeTenants() {
    try {
      registerTenantsFromContent();
    } catch (Exception e) {
      LOG.info("caught unexpected exception while computing tenants", e);
    }
  }
}
