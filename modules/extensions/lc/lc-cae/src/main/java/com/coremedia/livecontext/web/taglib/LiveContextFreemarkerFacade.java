package com.coremedia.livecontext.web.taglib;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.context.ProductInSite;
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
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * A Facade for LiveContext utility functions used by FreeMarker templates.
 */
public class LiveContextFreemarkerFacade {
  private static final String CATALOG_ID = "catalogId";
  private static final String LANG_ID = "langId";
  private static final String SITE_ID = "siteId";
  private static final String PAGE_ID = "pageId";
  private static final String STORE_ID = "storeId";
  private static final String STORE_REF = "storeRef";

  private LiveContextNavigationFactory liveContextNavigationFactory;
  private String secureScheme;
  private CMMetadataRenderer metadataRenderer;
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

  public void setSecureScheme(String secureScheme) {
    this.secureScheme = secureScheme;
  }

  @Required
  public void setLiveContextNavigationFactory(@Nonnull LiveContextNavigationFactory liveContextNavigationFactory) {
    this.liveContextNavigationFactory = liveContextNavigationFactory;
  }

  public void setMetadataInfo(MetadataInfo metadataInfo) {
    this.metadataInfo = metadataInfo;
  }

  @Required
  public void setMetadataRenderer(CMMetadataRenderer metadataRenderer) {
    this.metadataRenderer = metadataRenderer;
  }

  public StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }

  @Nonnull
  public String getPreviewMetadata() throws IOException {
    if (metadataInfo.isMetadataEnabled()) {
      List<Object> metaDataList = new LinkedList<>();
      ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
      if (fragmentContext() != null && fragmentContext().isFragmentRequest()) {
        FragmentParameters parameters = fragmentContext().getParameters();
        StoreContext currentContext = getStoreContextProvider().getCurrentContext();

        builder.put(CATALOG_ID, currentContext.getCatalogId())
                .put(LANG_ID, "" + currentContext.getLocale())
                .put(SITE_ID, currentContext.getSiteId())
                .put(STORE_ID, parameters.getStoreId());

        if (StringUtils.isEmpty(parameters.getCategoryId()) && StringUtils.isEmpty(parameters.getProductId())) {
          builder.put(PAGE_ID, parameters.getPageId())
                  .put(STORE_REF, currentContext);
        }
        metaDataList.add(builder.build());
      }
      return metadataRenderer.generateMetadataValue(metaDataList);
    }
    return "";
  }
}
