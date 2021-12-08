package com.coremedia.livecontext.fragment.links.transformers.resolvers;

import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.link.CommerceLinkTemplateTypes;
import com.coremedia.livecontext.ecommerce.link.LinkService;
import com.coremedia.livecontext.ecommerce.link.QueryParam;
import com.coremedia.livecontext.ecommerce.link.StorefrontRef;
import com.google.common.base.Strings;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Strings.nullToEmpty;

class CommerceLedLinkUtils {

  static final String CATEGORY_ID_KEY = "categoryId";
  static final String PARENT_CATEGORY_ID_KEY = "parentCategoryId";
  static final String TOP_CATEGORY_ID_KEY = "topCategoryId";
  static final String LEVEL_KEY = "level";
  static final String PRODUCT_ID_KEY = "productId";
  static final String ALTERNATIVE_PATH_KEY = "externalUriPath";
  static final String SEO_SEGMENT_KEY = "externalSeoSegment";
  static final String URL_KEY = "url";
  static final String SEO_PATH_KEY = "seoPath";

  private CommerceLedLinkUtils() {
  }

  static Optional<StorefrontRef> getCategoryLink(Category category, List<QueryParam> linkParameters, LinkService linkService) {
    List<Category> breadcrumbPath = category.getBreadcrumb();

    int level = Math.min(breadcrumbPath.size(), 3);

    Map<String, String> replacements = new HashMap<>();
    replacements.put(CATEGORY_ID_KEY, getTechIdOrExternalId(category));
    replacements.put(LEVEL_KEY, String.valueOf(level));

    if (level >= 2) {
      replacements.put(TOP_CATEGORY_ID_KEY, getTechIdOrExternalId(breadcrumbPath.get(0)));
    }

    if (level == 3) {
      Category parent = category.getParent();
      if (parent != null) {
        replacements.put(PARENT_CATEGORY_ID_KEY, getTechIdOrExternalId(parent));
      }
    }

    return linkService.getStorefrontRef(CommerceLinkTemplateTypes.CATEGORY_LINK_FRAGMENT, category.getContext(), Collections.unmodifiableMap(replacements))
            .map(ref -> ref.expand(linkParameters));
  }

  static Optional<StorefrontRef> getProductLink(Product product, @Nullable Category alternativeCategory, List<QueryParam> linkParameters,
                                                LinkService linkService) {
    Category category = alternativeCategory != null ? alternativeCategory : product.getCategory();
    Map<String, String> replacements = Map.of(
            PRODUCT_ID_KEY, getTechIdOrExternalId(product),
            CATEGORY_ID_KEY, getTechIdOrExternalId(category));

    return linkService.getStorefrontRef(CommerceLinkTemplateTypes.PRODUCT_LINK_FRAGMENT, product.getContext(), replacements)
            .map(ref -> ref.expand(linkParameters));
  }

  private static String getTechIdOrExternalId(CommerceBean commerceBean) {
    String externalTechId = commerceBean.getExternalTechId();
    if (externalTechId != null) {
      return externalTechId;
    }
    return commerceBean.getExternalId();
  }

  static Optional<StorefrontRef> getExternalPageLink(@Nullable String seoPath, @Nullable String alternativePath,
                                                     StoreContext storeContext, List<QueryParam> linkParameters,
                                                     LinkService linkService) {
    Map<String, String> replacements = new HashMap<>();

    //Homepage
    if (Strings.isNullOrEmpty(seoPath) && Strings.isNullOrEmpty(alternativePath)) {
      return linkService.getStorefrontRef(CommerceLinkTemplateTypes.HOME_PAGE_LINK_FRAGMENT, storeContext)
              .map(ref -> ref.expand(linkParameters)).or(() ->
                      linkService.getStorefrontRef(CommerceLinkTemplateTypes.SHOP_PAGE_LINK_FRAGMENT, storeContext)
                              .map(ref -> ref.expand(linkParameters))
              );
    }

    if (!Strings.isNullOrEmpty(alternativePath)) {
      //Non-SEO
      replacements.put(ALTERNATIVE_PATH_KEY, alternativePath);
    } else {
      //SEO
      replacements.put(SEO_SEGMENT_KEY, nullToEmpty(seoPath));
      replacements.put(SEO_PATH_KEY, nullToEmpty(seoPath));
    }

    return linkService.getStorefrontRef(CommerceLinkTemplateTypes.SHOP_PAGE_LINK_FRAGMENT, storeContext, Collections.unmodifiableMap(replacements))
            .map(ref -> ref.expand(linkParameters));
  }

  static Optional<StorefrontRef> getContentLink(@Nullable String seoPath, StoreContext storeContext,
                                                List<QueryParam> linkParameters, LinkService linkService) {
    Map<String, String> replacements = Map.of(
            SEO_SEGMENT_KEY, nullToEmpty(seoPath),
            SEO_PATH_KEY, nullToEmpty(seoPath));

    return linkService.getStorefrontRef(CommerceLinkTemplateTypes.CM_CONTENT_LINK_FRAGMENT, storeContext, replacements)
            .map(ref -> ref.expand(linkParameters));
  }

  static Optional<StorefrontRef> getAjaxLink(String url, StoreContext storeContext, LinkService linkService) {
    return linkService.getStorefrontRef(CommerceLinkTemplateTypes.CM_AJAX_LINK_FRAGMENT, storeContext, Map.of(URL_KEY, url));
  }
}
