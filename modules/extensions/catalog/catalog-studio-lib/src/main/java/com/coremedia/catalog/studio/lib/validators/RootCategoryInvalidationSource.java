package com.coremedia.catalog.studio.lib.validators;

import com.coremedia.blueprint.base.ecommerce.catalog.CmsCatalogService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnectionIdProvider;
import com.coremedia.rest.invalidations.SimpleInvalidationSource;
import com.coremedia.rest.linking.Linker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

class RootCategoryInvalidationSource extends SimpleInvalidationSource implements ApplicationListener<ContextRefreshedEvent> {

  private static final Logger LOG = LoggerFactory.getLogger(RootCategoryInvalidationSource.class);

  @Autowired
  private Cache cache;
  @Autowired
  Linker linker;
  @Autowired
  private SitesService sitesService;
  @Autowired
  private Commerce commerce;
  @Autowired
  private CommerceConnectionIdProvider commerceConnectionIdProvider;
  @Autowired
  private CmsCatalogService catalogService;

  private CacheKey<Collection<Category>> categoryCacheKey = new RootCategoriesCacheKey(this);

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    // pull root categories into cache
    getRootCategories();
  }

  Collection<Category> getRootCategories() {
    return cache.get(categoryCacheKey);
  }

  class RootCategoriesCacheKey extends CacheKey<Collection<Category>> {

    private final RootCategoryInvalidationSource rootCategoryInvalidationSource;

    public RootCategoriesCacheKey(RootCategoryInvalidationSource rootCategoryInvalidationSource) {
      this.rootCategoryInvalidationSource = rootCategoryInvalidationSource;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      RootCategoriesCacheKey that = (RootCategoriesCacheKey) o;
      return Objects.equals(rootCategoryInvalidationSource, that.rootCategoryInvalidationSource);
    }

    @Override
    public int hashCode() {
      return Objects.hash(rootCategoryInvalidationSource);
    }

    @Override
    public Collection<Category> evaluate(Cache cache) throws Exception {
      Collection<Category> rootCategories = new LinkedList<>();
      Set<Site> sites = sitesService.getSites();
      for (Site site : sites) {
        String connectionId = commerceConnectionIdProvider.findConnectionIdBySite(site);
        if (connectionId != null) {
          CommerceConnection connection = commerce.getConnection(connectionId);
          if (connection!=null && "coremedia".equals(connection.getVendorName())){
            connection.setStoreContext(connection.getStoreContextProvider().findContextBySite(site));
            Commerce.setCurrentConnection(connection);
            try {
              Category rootCategory = cache.get(new RootCategoryCacheKey(connection, catalogService, linker, rootCategoryInvalidationSource));
              rootCategories.add(rootCategory);
            } catch (Exception e) {
              LOG.debug("unable to determine root category for connection {}", connection, e);
            }
          }
        }

      }
      return rootCategories;
    }

    @Override
    public boolean recomputeOnInvalidation(Cache cache, Collection<Category> value, int numDependents) {
      return true;
    }

  }

}
