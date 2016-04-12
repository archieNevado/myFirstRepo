package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.objectserver.web.HandlerHelper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_EXTENSION;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_EXTENSION;

@RequestMapping
public class ProductCatalogPictureHandler extends CatalogPictureHandlerBase {

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
                                               HttpServletRequest request) throws IOException {
    //the given partnumber can be of a product or of a sku but we need the correct reference id
    //so ask catalog service we will give us a sku instance if the partnumber belongs to a sku
    Product productOrSkuByPartNumber = Commerce.getCurrentConnection().getCatalogService().findProductById(
            BaseCommerceIdHelper.getCurrentCommerceIdProvider().formatProductId(partNumber));
    if (productOrSkuByPartNumber == null) {
      return HandlerHelper.notFound();
    }

    return handleRequestWidthHeight(storeId, locale, formatName, productOrSkuByPartNumber.getReference(), request);
  }
}
