package com.coremedia.livecontext.ecommerce.ibm.event;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnectionIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceConnectionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

/**
 * Service bean to provide a collection of all IBM commerce connection beans.
 */
@Service
class IbmCommerceConnections {

  private static final Logger LOG = LoggerFactory.getLogger(IbmCommerceConnections.class);

  private final Cache cache;
  private final SitesService sitesService;
  private final Commerce commerce;
  private final CommerceConnectionIdProvider commerceConnectionIdProvider;

  private final CacheKey<Collection<CommerceConnection>> categoryCacheKey = new IbmStoreContextsCacheKey();

  @Autowired
  IbmCommerceConnections(Cache cache, SitesService sitesService, Commerce commerce, CommerceConnectionIdProvider commerceConnectionIdProvider) {
    this.cache = cache;
    this.sitesService = sitesService;
    this.commerce = commerce;
    this.commerceConnectionIdProvider = commerceConnectionIdProvider;
  }

  @Nonnull
  Collection<CommerceConnection> getConnections() {
    return cache.get(categoryCacheKey);
  }

  class IbmStoreContextsCacheKey extends CacheKey<Collection<CommerceConnection>> {

    @Override
    public boolean equals(Object obj) {
      return obj == this;
    }

    @Override
    public int hashCode() {
      return Objects.hash(cache, sitesService, commerce, commerceConnectionIdProvider);
    }

    @Override
    public Collection<CommerceConnection> evaluate(Cache cache) throws Exception {
      Collection<CommerceConnection> connections = new LinkedList<>();
      Set<Site> sites = sitesService.getSites();
      for (Site site : sites) {
        addIbmConnection(connections, site);
      }
      LOG.debug("detected IBM commerce connections: {}", connections);
      return connections;
    }

    private void addIbmConnection(Collection<CommerceConnection> connections, Site site) {
      String connectionId = commerceConnectionIdProvider.findConnectionIdBySite(site);
      if (connectionId != null) {
        CommerceConnection connection = commerce.getConnection(connectionId);
        if (connection instanceof CommerceConnectionImpl) {
          StoreContext contextBySite = connection.getStoreContextProvider().findContextBySite(site);
          if (null != contextBySite) {
            connection.setStoreContext(contextBySite);
            connections.add(connection);
          }
        }
      }
    }

  }

}
