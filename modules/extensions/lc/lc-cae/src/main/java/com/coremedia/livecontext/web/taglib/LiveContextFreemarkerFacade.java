package com.coremedia.livecontext.web.taglib;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.fragment.FragmentContext;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import com.coremedia.objectserver.view.freemarker.FreemarkerUtils;
import com.coremedia.objectserver.web.taglib.MetadataTagSupport;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * A Facade for LiveContext utility functions used by FreeMarker templates.
 */
public class LiveContextFreemarkerFacade extends MetadataTagSupport {
  private static final String CATALOG_ID = "catalogId";
  private static final String LANG_ID = "langId";
  private static final String SITE_ID = "siteId";
  private static final String PAGE_ID = "pageId";
  private static final String STORE_ID = "storeId";
  private static final String STORE_REF = "storeRef";

  private transient LiveContextNavigationFactory liveContextNavigationFactory;
  private String secureScheme;

  private AugmentationService augmentationService;
  private SitesService sitesService;

  public AugmentationService getAugmentationService() {
    return augmentationService;
  }

  public void setAugmentationService(AugmentationService augmentationService) {
    this.augmentationService = augmentationService;
  }

  public SitesService getSitesService() {
    return sitesService;
  }

  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  public String formatPrice(Object amount, Currency currency, Locale locale) {
    return FormatFunctions.formatPrice(amount, currency, locale);
  }

  public ProductInSite createProductInSite(Product product) {
    CommerceConnection connection = requireNonNull(DefaultConnection.get(), "no commerce connection available");
    return liveContextNavigationFactory.createProductInSite(product, connection.getStoreContext().getSiteId());
  }



  public String getSecureScheme() {
    return secureScheme;
  }

  /**
   * This method returns a {@link Map} which contains information for the preview.<br>
   * The map contains the following keys: {@link #CATALOG_ID}, {@link #LANG_ID}, {@link #SITE_ID} and {@link #STORE_ID}.<br>
   *
   * @return a map containing informations for preview of fragments
   * @throws IOException
   */
  @Nonnull
  public Map<String, Object> getPreviewMetadata() throws IOException {
    if (!isMetadataEnabled()) {
      return Collections.emptyMap();
    }

    if (fragmentContext() == null || !fragmentContext().isFragmentRequest()) {
      return Collections.emptyMap();
    }

    FragmentParameters parameters = fragmentContext().getParameters();
    CommerceConnection connection = requireNonNull(DefaultConnection.get(), "no commerce connection available");
    StoreContext currentContext = connection.getStoreContext();

    ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
    builder.put(CATALOG_ID, currentContext.getCatalogId())
            .put(LANG_ID, "" + currentContext.getLocale())
            .put(SITE_ID, currentContext.getSiteId())
            .put(STORE_ID, parameters.getStoreId());

    boolean isAugmentedPage = isAugmentedPage(parameters);

    if (isAugmentedPage) {
      builder.put(PAGE_ID, parameters.getPageId())
              .put(STORE_REF, currentContext);
    }

    return builder.build();
  }

  private boolean isAugmentedPage(FragmentParameters parameters) {
    return isEmpty(parameters.getCategoryId()) && isEmpty(parameters.getProductId());
  }

  public boolean isAugmentedContent() {
    CommerceConnection connection = requireNonNull(DefaultConnection.get(), "no commerce connection available");
    CommerceIdProvider idProvider = connection.getIdProvider();
    FragmentParameters parameters = fragmentContext().getParameters();
    String categoryId = parameters.getCategoryId();
    String productId = parameters.getProductId();
    Content content;
    CommerceBean commerceBean = null;
    boolean isAugmentedPage = isAugmentedPage(parameters);
    if (!isAugmentedPage) {
      if (!isEmpty(productId))  {
        commerceBean = connection.getCatalogService().findProductById(idProvider.formatProductTechId(productId));
      } else if (!isEmpty(categoryId)) {
        commerceBean = connection.getCatalogService().findCategoryById(idProvider.formatCategoryTechId(categoryId));
      }
      content = augmentationService.getContent(commerceBean);
      return (content != null);
    }
    return true;
  }

  public FragmentContext fragmentContext() {
    return FragmentContextProvider.getFragmentContext(FreemarkerUtils.getCurrentRequest());
  }

  @Override //Overriden for mocking in test.
  protected boolean isMetadataEnabled() {
    return super.isMetadataEnabled();
  }

  @Required
  public void setLiveContextNavigationFactory(@Nonnull LiveContextNavigationFactory liveContextNavigationFactory) {
    this.liveContextNavigationFactory = liveContextNavigationFactory;
  }

  public void setSecureScheme(String secureScheme) {
    this.secureScheme = secureScheme;
  }

  public String getVendorName() {
    CommerceConnection currentConnection = DefaultConnection.get();
    if (currentConnection != null) {
      return currentConnection.getVendorName();
    }
    return null;
  }
}
