package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.objectserver.web.HandlerHelper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_EXTENSION;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_EXTENSION;
import static java.util.Objects.requireNonNull;

@RequestMapping
public class ProductCatalogPictureHandler extends CatalogPictureHandlerBase {

  //TODO: mbi siehe unten
  private static final String SAP_HYBRIS_VENDOR_ID = "SAP Hybris";

  /**
   * URI Pattern for transformed blobs for products
   * e.g. /catalogimage/product/10202/en_US/full/PC_SHIRT.jpg
   */
  public static final String IMAGE_URI_PATTERN =
          "/" + AssetService.PRODUCT_URI_PREFIX +
                  "/{" + STORE_ID + "}" +
                  "/{" + LOCALE + "}" +
                  "/{" + FORMAT_NAME + "}" +
                  "/{" + PART_NUMBER + "}" +
                  ".{" + SEGMENT_EXTENSION + ":" + PATTERN_EXTENSION + "}";


  @RequestMapping(value = IMAGE_URI_PATTERN)
  public ModelAndView handleRequestWidthHeightForProduct(@PathVariable(STORE_ID) String storeId,
                                                         @PathVariable(LOCALE) String locale,
                                                         @PathVariable(FORMAT_NAME) String formatName,
                                                         @PathVariable(PART_NUMBER) String partNumber,
                                                         @PathVariable(SEGMENT_EXTENSION) String extension,
                                                         WebRequest request,
                                                         HttpServletResponse response) throws IOException {
    //the given partnumber can be of a product or of a sku but we need the correct reference id
    //so ask catalog service we will give us a sku instance if the partnumber belongs to a sku
    CommerceConnection connection = requireNonNull(DefaultConnection.get(), "no commerce connection available");
    CatalogService catalogService = requireNonNull(connection.getCatalogService(), "no catalog service available");
    CommerceIdProvider idProvider = connection.getIdProvider();
    Product productOrSku = catalogService.findProductById(idProvider.formatProductId(partNumber));
    // Todo mbi: if we only use a product id (instead of a sku id) the fallback does not work in the asset service
    if (productOrSku != null) {
      if (productOrSku.isVariant()) {
        productOrSku = catalogService.findProductVariantById(idProvider.formatProductVariantId(partNumber));
      }
    }
    if (productOrSku != null) {
      ModelAndView modelAndView = handleRequestWidthHeight(storeId, locale, formatName, productOrSku.getReference(), extension, request);
      //TODO: mbi fallback to hybris picture when no asset defined
      if (HandlerHelper.isNotFound(modelAndView)
              && SAP_HYBRIS_VENDOR_ID.equals(connection.getVendorName())) {
        response.sendRedirect(productOrSku.getCatalogPicture().getUrl());
      }
      return modelAndView;
    }
    return HandlerHelper.notFound();
  }
}
