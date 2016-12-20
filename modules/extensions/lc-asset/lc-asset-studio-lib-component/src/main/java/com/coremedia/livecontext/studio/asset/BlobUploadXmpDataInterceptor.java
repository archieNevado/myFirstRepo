package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.struct.Struct;
import com.coremedia.ecommerce.common.ProductIdExtractor;
import com.coremedia.livecontext.asset.util.AssetHelper;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.rest.cap.intercept.ContentWriteInterceptorBase;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.asset.util.AssetReadSettingsHelper.NAME_LOCAL_SETTINGS;

/**
 * Extracts product codes from XMP/IPTC Data and stores product references to struct property.
 */
public class BlobUploadXmpDataInterceptor extends ContentWriteInterceptorBase {

  private static final Logger LOG = LoggerFactory.getLogger(BlobUploadXmpDataInterceptor.class);
  private static final String ASSET_PRODUCT_IDS_ATTRIBUTE_NAME = "defaultProductIds";

  private String blobProperty;
  private AssetHelper assetHelper;

  @Override
  public void intercept(@Nonnull ContentWriteRequest request) {
    Map<String, Object> properties = request.getProperties();

    if (!properties.containsKey(blobProperty)) {
      //not my turf
      return;
    }

    CommerceConnection commerceConnection = Commerce.getCurrentConnection();
    if (commerceConnection == null) {
      return;
    }

    CommerceIdProvider idProvider = commerceConnection.getIdProvider();
    CatalogService catalogService = commerceConnection.getCatalogService();
    if (idProvider == null || catalogService == null) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("No id provider/catalog service for the commerce connection " + commerceConnection +
                " The product metadata will not be extracted");
      }
      return;
    }

    Object value = properties.get(blobProperty);
    if (value instanceof Blob) {
      Blob blob = (Blob) value;
      List<String> productIds = getProductIds(request, blob);

      properties.put(NAME_LOCAL_SETTINGS, assetHelper.updateCMPictureForExternalIds(request.getEntity(), productIds));
    } else if (value == null) {
      // delete blob action
      Struct result = assetHelper.updateCMPictureOnBlobDelete(request.getEntity());

      if (result != null) {
        properties.put(NAME_LOCAL_SETTINGS, result);
      }
    }
  }

  private List<String> getProductIds(@Nonnull ContentWriteRequest request, @Nonnull Blob blob) {
    List<String> productIds = new ArrayList<>();

    Iterable<String> xmpIds = getXmpIds(request, blob);
    for (String externalId : xmpIds) {
      Product product = retrieveProductOrVariant(externalId);
      if (product != null) {
        productIds.add(product.getId());
      } else if (LOG.isDebugEnabled()) {
        LOG.debug("Product id " + externalId + " could not be found in catalog. XMP data not persisted.");
      }
    }

    return productIds;
  }

  @Nonnull
  private Iterable<String> getXmpIds(@Nonnull ContentWriteRequest request, @Nonnull Blob blob) {
    Object assetProductIds = request.getAttribute(ASSET_PRODUCT_IDS_ATTRIBUTE_NAME);

    if (assetProductIds != null) {
      return (Iterable) assetProductIds;
    } else {
      return ProductIdExtractor.extractProductIds(blob);
    }
  }

  @VisibleForTesting
  Product retrieveProductOrVariant(String externalId) {
    CommerceIdProvider idProvider = Commerce.getCurrentConnection().getIdProvider();
    CatalogService catalogService = Commerce.getCurrentConnection().getCatalogService();

    String id = idProvider.formatProductId(externalId);
    //the catalogservice allows to retrieve Categories, Products and/or ProductVariants by a single call of #findProductById
    Product result = catalogService.findProductById(id);
    if (result != null) {
      return result;
    } else {
      //for other catalog implementations an explicit request for ProductVariants
      id = idProvider.formatProductVariantId(externalId);
      result = catalogService.findProductVariantById(id);
    }
    return result;
  }

  @Required
  public void setBlobProperty(String blobProperty) {
    this.blobProperty = blobProperty;
  }

  @Required
  public void setAssetHelper(AssetHelper assetHelper) {
    this.assetHelper = assetHelper;
  }
}
