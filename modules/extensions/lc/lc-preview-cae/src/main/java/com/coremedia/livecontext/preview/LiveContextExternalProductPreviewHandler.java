package com.coremedia.livecontext.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.contentbeans.LiveContextExternalProduct;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.handler.LiveContextPageHandlerBase;
import com.coremedia.objectserver.web.links.Link;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

import static com.coremedia.livecontext.product.ProductPageHandler.LIVECONTEXT_POLICY_COMMERCE_PRODUCT_LINKS;

@Link
@RequestMapping
public class LiveContextExternalProductPreviewHandler extends LiveContextPageHandlerBase {

  @SuppressWarnings("unused")
  @Link(type = LiveContextExternalProduct.class)
  public Object buildLinkForExternalProduct(
          final LiveContextExternalProduct externalProduct,
          final String viewName,
          final Map<String, Object> linkParameters) {
    CommerceConnection currentConnection = DefaultConnection.get();
    if (currentConnection != null && currentConnection.getStoreContext() != null) {
      Product product;
      try {
        product = externalProduct.getProduct();
      } catch (NotFoundException | InvalidIdException e) {
        LOG.info("could not find product in catalog for id {}", externalProduct.getExternalId(), e);
        return null;
      }

      if (useCommerceProductLinks(externalProduct.getSite()) && product != null) {
        return buildCommerceLinkFor(product, linkParameters);
      }
    }
    // not responsible
    return null;
  }

  private boolean useCommerceProductLinks(Site site) {
    return getSettingsService().settingWithDefault(LIVECONTEXT_POLICY_COMMERCE_PRODUCT_LINKS, Boolean.class, true, site);
  }
}
