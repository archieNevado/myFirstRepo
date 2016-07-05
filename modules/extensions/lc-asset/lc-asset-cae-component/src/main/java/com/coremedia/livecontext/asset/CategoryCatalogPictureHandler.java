package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_EXTENSION;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_EXTENSION;

@RequestMapping
public class CategoryCatalogPictureHandler extends CatalogPictureHandlerBase {

  /**
   * URI Pattern for transformed blobs for categories
   * e.g. /catalogimage/category/10202/en_US/full/PC_Deli.jpg
   */
  public static final String IMAGE_URI_PATTERN =
          "/" + AssetService.CATEGORY_URI_PREFIX +
                  "/{" + STORE_ID + "}" +
                  "/{" + LOCALE + "}" +
                  "/{" + FORMAT_NAME + "}" +
                  "/{" + PART_NUMBER + "}" +
                  ".{" + SEGMENT_EXTENSION + ":" + PATTERN_EXTENSION + "}";

  @RequestMapping(value = IMAGE_URI_PATTERN)
  public ModelAndView handleRequestWidthHeightForCategory(@PathVariable(STORE_ID) String storeId,
                                               @PathVariable(LOCALE) String locale,
                                               @PathVariable(FORMAT_NAME) String formatName,
                                               @PathVariable(PART_NUMBER) String partNumber,
                                               @PathVariable(SEGMENT_EXTENSION) String extension,
                                               WebRequest request) throws IOException {

    String id = BaseCommerceIdHelper.getCurrentCommerceIdProvider().formatCategoryId(partNumber);

    return handleRequestWidthHeight(storeId, locale, formatName, id, extension, request);
  }
}
