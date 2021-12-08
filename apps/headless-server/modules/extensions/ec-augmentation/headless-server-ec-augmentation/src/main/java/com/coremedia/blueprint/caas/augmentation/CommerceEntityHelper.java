package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRef;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SKU;
import static java.lang.invoke.MethodHandles.lookup;

@DefaultAnnotation(NonNull.class)
public class CommerceEntityHelper {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  private final SitesService sitesService;
  private final CommerceConnectionSupplier commerceConnectionSupplier;

  public CommerceEntityHelper(SitesService sitesService, CommerceConnectionSupplier commerceConnectionSupplier) {
    this.sitesService = sitesService;
    this.commerceConnectionSupplier = commerceConnectionSupplier;
  }

  @Nullable
  public static CommerceBean createCommerceBean(CommerceId commerceId, CommerceConnection commerceConnection) {
    StoreContext storeContext = commerceConnection.getInitialStoreContext();
    return commerceConnection.getCommerceBeanFactory().createBeanFor(commerceId, storeContext);
  }

  @Nullable
  public <T extends CommerceBean> T createCommerceBean(CommerceId id, String siteId, Class<T> expectedType) {
    CommerceConnection connection = getCommerceConnection(siteId);
    if (connection == null) {
      return null;
    }
    CommerceBean bean = createCommerceBean(id, connection);
    if (bean == null || !expectedType.isAssignableFrom(bean.getClass())) {
      return null;
    }
    return (T) bean;
  }

  @Nullable
  public CommerceConnection getCommerceConnection(String siteId) {
    try {
      Site site = sitesService.getSite(siteId);
      if (site == null) {
        LOG.info("Cannot find site for siteId {}.", siteId);
        return null;
      }
      CommerceConnection connection = commerceConnectionSupplier.findConnection(site).orElse(null);

      if (connection == null) {
        LOG.warn("Cannot find commerce connection for siteId {}", siteId);
        return null;
      }
      return connection;
    } catch (CommerceException e) {
      LOG.warn("Cannot find commerce connection for siteId {}", siteId, e);
      return null;
    }
  }

  public CommerceId getCommerceId(CommerceRef commerceRef) {
    CommerceConnection commerceConnection = getCommerceConnection(commerceRef.getSiteId());
    String erroMsg = "Could not create commerce id from ";
    if (commerceConnection == null) {
      throw new IllegalArgumentException(erroMsg + commerceRef);
    }

    CatalogAlias catalogAlias = CatalogAlias.of(commerceRef.getCatalogAlias());
    CommerceIdProvider idProvider = commerceConnection.getIdProvider();
    CommerceBeanType type = commerceRef.getType();
    String externalId = commerceRef.getExternalId();

    if (CATEGORY.equals(type)) {
      return idProvider.formatCategoryId(catalogAlias, externalId);
    } else if (SKU.equals(type)) {
      return idProvider.formatProductVariantId(catalogAlias, externalId);
    } else if (PRODUCT.equals(type)) {
      return idProvider.formatProductId(catalogAlias, externalId);
    }

    throw new IllegalArgumentException(erroMsg + commerceRef);
  }

  /**
   * Ensures that the id is in the long format, which is required by subsequent calls:
   *
   * Example: <code>vendor:///summer_catalog/category/men</code> or <code>vendor:///catalog/category/men</code>
   *
   * @param categoryId the external id
   * @param catalogAlias the catalog alias
   * @param connection the commerce connection to be used
   * @return id in the long format
   */
  public static CommerceId getCategoryId(String categoryId, CatalogAlias catalogAlias, CommerceConnection connection) {
    CommerceIdProvider idProvider = connection.getIdProvider();
    Optional<CommerceId> commerceIdOptional = CommerceIdParserHelper.parseCommerceId(categoryId);
    return commerceIdOptional.orElseGet(() -> idProvider.formatCategoryId(catalogAlias, categoryId));
  }

  /**
   * Ensures that the id is in the long format, which is required by subsequent calls:
   *
   * Example: <code>vendor:///summer_catalog/product/foo-1</code> or <code>vendor:///catalog/product/foo-1</code>
   *
   * @param productId the external id
   * @param catalogAlias the catalog alias
   * @param connection the commerce connection to be used
   * @return id in the long format
   */
  public static CommerceId getProductId(String productId, CatalogAlias catalogAlias, CommerceConnection connection) {
    CommerceIdProvider idProvider = connection.getIdProvider();
    Optional<CommerceId> commerceIdOptional = CommerceIdParserHelper.parseCommerceId(productId);
    return commerceIdOptional.orElseGet(() -> idProvider.formatProductId(catalogAlias, productId));
  }

  @Nullable
  public CommerceBean getCommerceBean(CommerceId commerceId, String siteId) {
    CommerceConnection connection = getCommerceConnection(siteId);
    if (connection == null) {
      return null;
    }
    return connection.getCommerceBeanFactory().createBeanFor(commerceId, connection.getInitialStoreContext());
  }

}
