package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.blueprint.cae.handlers.PageHandlerBase;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.cap.common.CapObjectDestroyedException;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SiteDestroyedException;
import com.coremedia.livecontext.ecommerce.catalog.AxisFilter;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.catalog.VariantFilter;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.UserVariantHelper;
import com.coremedia.objectserver.web.links.Link;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.coremedia.blueprint.base.links.UriConstants.ContentTypes.CONTENT_TYPE_HTML;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.PREFIX_DYNAMIC;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENTS_FRAGMENT;
import static java.lang.String.format;

/**
 * Handle dynamic product asset requests.
 */
@Link
@RequestMapping
public class ProductAssetsHandler extends PageHandlerBase {

  private static final Logger LOG = LoggerFactory.getLogger(ProductAssetsHandler.class);

  private static final String URI_PREFIX = "productassets";

  private static final String SEGMENT_SITE = "site";
  private static final String SEGMENT_VIEW = "view";
  private static final String SEGMENT_CATEGORY_ID = "categoryId";
  private static final String SEGMENT_PRODUCT_ID = "productId";
  private static final String SEGMENT_ORIENTATION = "orientation";
  private static final String SEGMENT_TYPES = "types";
  private static final String REQUEST_PARAM_SKU_ID = "catEntryId";
  private static final String REQUEST_PARAM_ATTRIBUTES = "attributes";
  private static final String VIEW_NAME = "asDynaAssets";

  // Todo mbi: make this configurable
  private boolean useStableIds = false;

  /**
   * URI pattern, for URIs like "/dynamic/fragment/productassets/87a126f3812f638/asAssets/square/10567/8004"
   */
  public static final String DYNAMIC_URI_PATTERN = '/' + PREFIX_DYNAMIC +
          '/' + SEGMENTS_FRAGMENT +
          '/' + URI_PREFIX +
          "/{" + SEGMENT_SITE + '}' +
          "/{" + SEGMENT_VIEW + '}' +
          "/{" + SEGMENT_CATEGORY_ID + '}' +
          "/{" + SEGMENT_PRODUCT_ID + '}' +
          "/{" + SEGMENT_ORIENTATION + '}' +
          "/{" + SEGMENT_TYPES + '}';
  private static final String DEFAULT_ORIENTATION = "portrait";
  private static final String DEFAULT_SEGMENT_TYPES = "all";

  @RequestMapping(value = DYNAMIC_URI_PATTERN, produces = CONTENT_TYPE_HTML, method = {RequestMethod.GET, RequestMethod.POST})
  public ModelAndView handleFragment(
          @PathVariable(SEGMENT_SITE) String siteName,
          @PathVariable(SEGMENT_VIEW) String view,
          @PathVariable(SEGMENT_CATEGORY_ID) String categoryId,
          @PathVariable(SEGMENT_PRODUCT_ID) String productId,
          @PathVariable(SEGMENT_ORIENTATION) String orientation,
          @PathVariable(SEGMENT_TYPES) String types,
          @RequestParam(value = REQUEST_PARAM_SKU_ID, required = false) String skuId,
          @RequestParam(value = REQUEST_PARAM_ATTRIBUTES, required = false) String attributes,
          HttpServletRequest request,
          HttpServletResponse response) {

    CommerceConnection connection = DefaultConnection.get();
    StoreContext storeContext = connection != null ? connection.getStoreContext() : null;
    if (storeContext == null) {
      LOG.warn("Commerce connection not properly initialized.");
      return HandlerHelper.notFound();
    }

    if (StringUtils.isBlank(productId)) {
      LOG.warn("Cannot handle request because productId is null.");
      return HandlerHelper.notFound();
    }

    Product self = findProduct(productId, skuId, attributes, storeContext);
    if (self == null) {
      return HandlerHelper.notFound();
    }

    Site site = getSiteByName(siteName);

    if (site != null) {

      //strange, the "produces" annotation value does not work, so we set the response mime type manually
      response.setContentType(CONTENT_TYPE_HTML);

      ModelAndView modelWithView = HandlerHelper.createModelWithView(self, view);
      if (StringUtils.isNotBlank(orientation)) {
        modelWithView.addObject(SEGMENT_ORIENTATION, orientation);
      }
      if (StringUtils.isNotBlank(types)) {
        modelWithView.addObject(SEGMENT_TYPES, types);
      }

      Content rootChannel = site.getSiteRootDocument();
      CMNavigation navigation = getContentBeanFactory().createBeanFor(rootChannel, CMChannel.class);
      Page page = asPage(navigation, navigation, UserVariantHelper.getUser(request));
      addPageModel(modelWithView, page);

      return modelWithView;
    }
    return HandlerHelper.notFound();
  }

  // called (1) to create fragment link and (2) to create link as needed by Product.asDynaAssets.ftl
  @Link(type = Product.class, uri = DYNAMIC_URI_PATTERN, view = {"fragment", "asAssets"})
  public UriComponents buildFragmentLink(Product product, UriTemplate uriPattern, Map<String, Object> linkParameters, HttpServletRequest request) {
    CommerceConnection connection = DefaultConnection.get();
    StoreContext storeContext = connection != null ? connection.getStoreContext() : null;
    if (storeContext == null) {
      LOG.warn("Commerce connection not properly initialized.");
      return null;
    }
    if (product != null) {
      UriComponentsBuilder result = UriComponentsBuilder.fromPath(uriPattern.toString());
      result = addLinkParametersAsQueryParameters(result, linkParameters);
      String siteId = storeContext.getSiteId();
      Site site = getSitesService().getSite(siteId);
      if (site != null) {
        Content rootChannel = site.getSiteRootDocument();
        String vanityName = urlPathFormattingHelper.getVanityName(rootChannel);
        String categoryId = getCategoryExternalTechId(product);
        String orientation = getAttribute(SEGMENT_ORIENTATION, request, DEFAULT_ORIENTATION);
        String types = getAttribute(SEGMENT_TYPES, request, DEFAULT_SEGMENT_TYPES);
        Map<String, String> paramMap = new HashMap<>(6);
        paramMap.put(SEGMENT_CATEGORY_ID, categoryId);
        paramMap.put(SEGMENT_PRODUCT_ID, product.getExternalTechId());
        paramMap.put(SEGMENT_ORIENTATION, orientation);
        paramMap.put(SEGMENT_TYPES, types);
        paramMap.put(SEGMENT_VIEW, VIEW_NAME);
        paramMap.put(SEGMENT_SITE, vanityName);
        return result.buildAndExpand(paramMap);
      }
    }
    return null;
  }

  @Nonnull
  private String getAttribute(@Nonnull  String name, @Nonnull HttpServletRequest request, @Nonnull String defaultValue) {
    Object orientation = request.getAttribute(name);
    return orientation instanceof String ? (String) orientation : defaultValue;
  }

  private String getCategoryExternalTechId(Product product) {
    Category category = product.getCategory();
    if (category != null) {
      return category.getExternalTechId();
    }
    throw new CommerceException(format("product '%s' has no category", product));
  }

  private Site getSiteByName(String siteName) {
    Set<Site> sites = getSitesService().getSites();
    for (Site site : sites) {
      try {
        Content rootChannel = site.getSiteRootDocument();
        if (rootChannel == null) {
          continue;
        }
        String vanityName = urlPathFormattingHelper.getVanityName(rootChannel);
        if (siteName.equalsIgnoreCase(vanityName)) {
          return site;
        }
      } catch (CapObjectDestroyedException | SiteDestroyedException e) {
        LOG.debug("ignoring destroyed site '{}'", site.getId(), e);
      }
    }
    return null;
  }

  public static List<VariantFilter> parseAttributesToFilters(String attributes) {
    // we support two different formats: semicolon separated list of alternating keys and values (eg. a;2;b;3;c;4)
    List<VariantFilter> result = parseAttributesFromSSL(attributes);
    // ... and the good old comma separated key value pair list (eg. a=2,b=3,c=4)
    return result.size() > 0 ? result : parseAttributesFromCSL(attributes);
  }

  public static List<VariantFilter> parseAttributesFromCSL(String attributes) {
    List<VariantFilter> result = new ArrayList<>();
    String[] kvPairs = attributes.split(",");
    for(String kvPair: kvPairs) {
      int index = kvPair.indexOf("=");
      if (index > 0) {
        String key = kvPair.substring(0, index);
        String value = kvPair.substring(index + 1);
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
          result.add(new AxisFilter(key, value));
        }
      }
    }
    return result;
  }

  @Nonnull
  public static List<VariantFilter> parseAttributesFromSSL(String attributes) {
    List<VariantFilter> result = new ArrayList<>();
    String[] tokens = attributes.split(";");
    if (tokens.length > 0 && attributes.contains(";")) {
      for (int i = 0; i < tokens.length; i+=2) {
        String key = tokens[i];
        String value = i+1 <= tokens.length-1 ? tokens[i+1] : "";
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
          result.add(new AxisFilter(key, value));
        }
      }
    }
    return result;
  }

  // Todo mbi: the following method is changed
  private Product findProduct(String productId, String skuId, String attributes, StoreContext storeContext) {

    Product result = null;

    if (StringUtils.isBlank(attributes)) {
      // in general an existing skuId parameter is more current than a productId param
      // in case a skuId was passed we hope it is a real sku and we can close case
      if (StringUtils.isNotBlank(skuId)) {
        result = (ProductVariant) getCommerceBeanFactory().loadBeanFor(formatProductVariantId(skuId), storeContext);
        if (!result.isVariant()) {
          result = null;
        }
      }
      // we give it a chance that the productId is actually a skuId
      if (result == null) {
        result = (ProductVariant) getCommerceBeanFactory().loadBeanFor(formatProductVariantId(productId), storeContext);
        // if not we convert it to a product and take it
        if (!result.isVariant()) {
          result = (Product) getCommerceBeanFactory().loadBeanFor(formatProductId(productId), storeContext);
        }
      }
      if (result == null) {
        LOG.warn("Cannot handle request because neither the product '{}' nor the sku '{}' cannot be determined.", productId, skuId);
        return null;
      }
    }
    else {
      // attention: in case the attributes are set we do not trust the skuId or productId parameter
      // we always try to determine the base product and retrieve the sku from given attributes
      Product product = (Product) getCommerceBeanFactory().loadBeanFor(formatProductId(productId), storeContext);
      if (product.isVariant()) {
        ProductVariant productVariant = (ProductVariant) getCommerceBeanFactory().loadBeanFor(formatProductVariantId(productId), storeContext);
        product = productVariant.getParent();
        if (product == null) {
          LOG.warn("Cannot handle request because the base product of the sku '{}' cannot be determined.", productId);
          return null;
        }
      }
      List<VariantFilter> filters = parseAttributesToFilters(attributes);
      List<ProductVariant> productVariants = product.getVariants(filters);
      if (productVariants.size() > 0) {
        result = productVariants.get(0);
      }
      if (result == null) {
        result = product;
      }
    }
    return result;
  }

  // Todo mbi: added
  private String formatProductId(String externalId) {
    return useStableIds ? getCommerceIdProvider().formatProductId(externalId) :
            getCommerceIdProvider().formatProductTechId(externalId);
  }

  // Todo mbi: added
  private String formatProductVariantId(String externalId) {
    return useStableIds ? getCommerceIdProvider().formatProductVariantId(externalId) :
            getCommerceIdProvider().formatProductVariantTechId(externalId);
  }

  private CommerceBeanFactory getCommerceBeanFactory() {
    return DefaultConnection.get().getCommerceBeanFactory();
  }

  private CommerceIdProvider getCommerceIdProvider() {
    return DefaultConnection.get().getIdProvider();
  }

  // Todo mbi: added
  public void setUseStableIds(boolean useStableIds) {
    this.useStableIds = useStableIds;
  }
}
