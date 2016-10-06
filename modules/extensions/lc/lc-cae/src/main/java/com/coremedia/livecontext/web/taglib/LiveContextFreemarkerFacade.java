package com.coremedia.livecontext.web.taglib;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.fragment.FragmentContext;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import com.coremedia.objectserver.util.undoc.CMMetadataRenderer;
import com.coremedia.objectserver.util.undoc.MetadataInfo;
import com.coremedia.objectserver.view.freemarker.FreemarkerUtils;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * A Facade for LiveContext utility functions used by FreeMarker templates.
 */
public class LiveContextFreemarkerFacade {
  private static final String CATALOG_ID = "catalogId";
  private static final String LANG_ID = "langId";
  private static final String SITE_ID = "siteId";
  private static final String PAGE_ID = "pageId";
  private static final String STORE_ID = "storeId";
  static final String HAS_ITEMS = "hasItems";
  static final String PLACEMENT_NAME = "placementName";
  private static final String STORE_REF = "storeRef";
  static final String IS_IN_LAYOUT = "isInLayout";

  private LiveContextNavigationFactory liveContextNavigationFactory;
  private String secureScheme;
  private MetadataInfo metadataInfo;

  public String formatPrice(Object amount, Currency currency, Locale locale) {
    return FormatFunctions.formatPrice(amount, currency, locale);
  }

  public ProductInSite createProductInSite(Product product) {
    return liveContextNavigationFactory.createProductInSite(product, getStoreContextProvider().getCurrentContext().getSiteId());
  }

  public FragmentContext fragmentContext() {
    return FragmentContextProvider.getFragmentContext(FreemarkerUtils.getCurrentRequest());
  }

  public String getSecureScheme() {
    return secureScheme;
  }

  /**
   * Values generated here are copied by coremedia-pbe.js (WCS workspace) into property node
   * {@link com.coremedia.objectserver.util.undoc.CMMetadataRenderer#DEFAULT_METADATA_PROPERTY}
   * with property "properties.shopUrl"
   * @throws IOException
   */
  @Nonnull
  public Map<String, Object> getPreviewMetadata() throws IOException {
    if (!metadataInfo.isMetadataEnabled()) {
      return Collections.emptyMap();
    }

    if (fragmentContext() == null || !fragmentContext().isFragmentRequest()) {
      return Collections.emptyMap();
    }

    FragmentParameters parameters = fragmentContext().getParameters();
    StoreContext currentContext = getStoreContextProvider().getCurrentContext();

    ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
    builder.put(CATALOG_ID, currentContext.getCatalogId())
            .put(LANG_ID, "" + currentContext.getLocale())
            .put(SITE_ID, currentContext.getSiteId())
            .put(STORE_ID, parameters.getStoreId());

    boolean isAugmentedPage = isEmpty(parameters.getCategoryId()) && isEmpty(parameters.getProductId());

    if (isAugmentedPage) {
      builder.put(PAGE_ID, parameters.getPageId())
              .put(STORE_REF, currentContext);
    }

    return builder.build();
  }

  /**
   * This method returns a JSON String which contains information about the state of the fragment.<br>
   * The JSON is rendered by a {@link CMMetadataRenderer}. <br>
   * The {@link CMMetadataRenderer} gets invoked with the following datastructure: List<Map>.<br>
   * The map contains the following keys: {@link #PLACEMENT_NAME}, {@link #IS_IN_LAYOUT} and {@link #HAS_ITEMS}.<br>
   * The values of those keys are of type boolean.
   *
   * @param placementObject a PageGridPlacement
   * @return json representation of necessary flags for highlighting fragments in preview mode
   * @throws IOException
   */
  @Nonnull
  public Map<String, Object> getFragmentHighlightingMetaData(@Nonnull Object placementObject) throws IOException {
    PageGridPlacement placement = asPageGridPlacement(placementObject);
    String placementName = placement != null ? placement.getName() : asPageGridPlacementName(placementObject);

    if (!metadataInfo.isMetadataEnabled()) {
      return Collections.emptyMap();
    }

    if (fragmentContext() == null || !fragmentContext().isFragmentRequest()) {
      return Collections.emptyMap();
    }

    return getFragmentHighlightingMetaDataInternal(placement, placementName);
  }

  private Map<String, Object> getFragmentHighlightingMetaDataInternal(PageGridPlacement placement, String placementName) throws IOException {
    boolean isInLayout = placement != null;
    boolean hasItems = hasItems(placement, isInLayout);

    List<Object> metaDataList = new LinkedList<>();
    ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
    builder.put(IS_IN_LAYOUT, isInLayout);
    builder.put(HAS_ITEMS, hasItems);
    builder.put(PLACEMENT_NAME, placementName);
    metaDataList.add(builder.build());

    return Collections.<String, Object>singletonMap("fragmentRequest", metaDataList);
  }

  private static PageGridPlacement asPageGridPlacement(Object object) {
    if (object instanceof  PageGridPlacement) {
      return (PageGridPlacement) object;
    }
    return null;
  }

  private static String asPageGridPlacementName(Object object) {
    if (object instanceof String) {
      return (String) object;
    }
    return null;
  }

  private static boolean hasItems(PageGridPlacement placement, boolean isInLayout) {
    return isInLayout && !placement.getItems().isEmpty();
  }

  private static StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }

  @Required
  public void setLiveContextNavigationFactory(@Nonnull LiveContextNavigationFactory liveContextNavigationFactory) {
    this.liveContextNavigationFactory = liveContextNavigationFactory;
  }

  @Required
  public void setMetadataInfo(MetadataInfo metadataInfo) {
    this.metadataInfo = metadataInfo;
  }

  public void setSecureScheme(String secureScheme) {
    this.secureScheme = secureScheme;
  }
}
