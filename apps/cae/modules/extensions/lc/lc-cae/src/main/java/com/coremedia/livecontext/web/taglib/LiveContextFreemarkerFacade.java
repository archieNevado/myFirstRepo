package com.coremedia.livecontext.web.taglib;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.blueprint.cae.web.FreemarkerEnvironment;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.fragment.FragmentContext;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.objectserver.web.taglib.MetadataTagSupport;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Collections.emptyMap;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * A Facade for LiveContext utility functions used by FreeMarker templates.
 */
public class LiveContextFreemarkerFacade extends MetadataTagSupport {
  private static final long serialVersionUID = 577878275971542409L;

  private static final String CATALOG_ID = "catalogId";
  private static final String LANG_ID = "langId";
  private static final String SITE_ID = "siteId";
  private static final String PAGE_ID = "pageId";
  private static final String STORE_ID = "storeId";
  private static final String STORE_REF = "storeRef";

  private transient LiveContextNavigationFactory liveContextNavigationFactory;

  private AugmentationService categoryAugmentationService;
  private AugmentationService productAugmentationService;

  private SitesService sitesService;
  private ContentBeanFactory contentBeanFactory;
  private DataViewFactory dataViewFactory;

  public AugmentationService getCategoryAugmentationService() {
    return categoryAugmentationService;
  }

  public void setCategoryAugmentationService(AugmentationService augmentationService) {
    this.categoryAugmentationService = augmentationService;
  }

  public AugmentationService getProductAugmentationService() {
    return productAugmentationService;
  }

  public void setProductAugmentationService(AugmentationService productAugmentationService) {
    this.productAugmentationService = productAugmentationService;
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
    var storeContext = CurrentStoreContext.get(FreemarkerEnvironment.getCurrentRequest());
    return liveContextNavigationFactory.createProductInSite(product, storeContext.getSiteId());
  }

  public boolean isFragmentRequest() {
    return fragmentContext().isFragmentRequest();
  }

  /**
   * This method returns a {@link Map} which contains information for the preview.<br>
   * The map contains the following keys: {@link #CATALOG_ID}, {@link #LANG_ID}, {@link #SITE_ID} and {@link #STORE_ID}.<br>
   *
   * @return a map containing informations for preview of fragments
   */
  @NonNull
  public Map<String, Object> getPreviewMetadata() {
    if (!isMetadataEnabled()) {
      return emptyMap();
    }

    if (!isFragmentRequest()) {
      return emptyMap();
    }

    FragmentParameters parameters = fragmentContext().getParameters();
    var storeContext = CurrentStoreContext.get(FreemarkerEnvironment.getCurrentRequest());

    Map<String, Object> map = new HashMap<>();
    map.put(CATALOG_ID, parameters.getCatalogId()
            .orElseGet(() -> storeContext.getCatalogId().get())
            .value());
    map.put(LANG_ID, "" + storeContext.getLocale());
    map.put(SITE_ID, storeContext.getSiteId());
    map.put(STORE_ID, parameters.getStoreId());

    boolean isAugmentedPage = isAugmentedPage(parameters);

    if (isAugmentedPage) {
      map.put(PAGE_ID, nullToEmpty(parameters.getPageId()));
      map.put(STORE_REF, storeContext);
    }

    return Collections.unmodifiableMap(map);
  }

  /**
   * Checks if the current fragment request targets an Augmented Page (NO Category Page, NO Product Page)
   *
   * @param parameters fragment parameters
   * @return true if request targets an Augmented Page
   */
  private boolean isAugmentedPage(FragmentParameters parameters) {
    return isEmpty(parameters.getCategoryId()) && isEmpty(parameters.getProductId());
  }

  public boolean isAugmentedContent() {
    var storeContext = CurrentStoreContext.get(FreemarkerEnvironment.getCurrentRequest());
    CommerceConnection connection = storeContext.getConnection();

    CommerceIdProvider idProvider = connection.getIdProvider();
    FragmentParameters parameters = fragmentContext().getParameters();
    boolean isAugmentedPage = isAugmentedPage(parameters);

    if (isAugmentedPage) {
      return true;
    }

    CatalogService catalogService = connection.getCatalogService();
    CatalogAlias catalogAlias = storeContext.getCatalogAlias();

    String productId = parameters.getProductId();
    String categoryId = parameters.getCategoryId();

    CommerceBean commerceBean;
    Content content = null;

    if (!isEmpty(productId)) {
      CommerceId productTechId = idProvider.formatProductTechId(catalogAlias, productId);
      commerceBean = catalogService.findProductById(productTechId, storeContext);
      if (commerceBean instanceof ProductVariant) {
        // variants are not augmented, we need to check its parent
        Product parent = ((ProductVariant) commerceBean).getParent();
        commerceBean = parent != null ? parent : commerceBean;
      }
      content = productAugmentationService.getContent(commerceBean);
    } else if (!isEmpty(categoryId)) {
      CommerceId categoryTechId = idProvider.formatCategoryTechId(catalogAlias, categoryId);
      commerceBean = catalogService.findCategoryById(categoryTechId, storeContext);
      content = categoryAugmentationService.getContent(commerceBean);
    }

    return content != null;
  }

  @NonNull
  private FragmentContext fragmentContext() {
    return FragmentContextProvider.getFragmentContext(FreemarkerEnvironment.getCurrentRequest());
  }

  @Override //Overridden for mocking in test.
  protected boolean isMetadataEnabled() {
    return super.isMetadataEnabled();
  }

  @Required
  public void setLiveContextNavigationFactory(@NonNull LiveContextNavigationFactory liveContextNavigationFactory) {
    this.liveContextNavigationFactory = liveContextNavigationFactory;
  }

  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  public void setDataViewFactory(DataViewFactory dataViewFactory) {
    this.dataViewFactory = dataViewFactory;
  }

  public ContentBean createBeanFor(Content content) {
    return dataViewFactory.loadCached(contentBeanFactory.createBeanFor(content, ContentBean.class), null);
  }

  public List createBeansFor(Iterable<? extends Content> contents) {
    return dataViewFactory.loadAllCached(contentBeanFactory.createBeansFor(contents, ContentBean.class), null);
  }

  public String getVendorName() {
    return CurrentStoreContext.find(FreemarkerEnvironment.getCurrentRequest())
            .map(StoreContext::getConnection)
            .map(CommerceConnection::getVendorName)
            .orElse(null);
  }

  public CMChannel getHomePage() {
    var storeContext = CurrentStoreContext.get(FreemarkerEnvironment.getCurrentRequest());
    String siteId = storeContext.getSiteId();
    return Optional.ofNullable(sitesService.getSite(siteId))
            .map(Site::getSiteRootDocument)
            .map(content -> contentBeanFactory.createBeanFor(content, CMChannel.class))
            .orElseThrow(() -> new IllegalStateException("Unable to find Homepage for site '" + siteId + "'."));
  }

  public boolean isProductAvailable(@NonNull Product product) {
    // return always true because this function isn't available in Commerce Hub
    return true;
  }

}
