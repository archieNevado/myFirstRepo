package com.coremedia.blueprint.uitesting.lc;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.NoCommerceConnectionAvailable;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.cap.common.CapObjectDestroyedException;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.query.QueryService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SiteDestroyedException;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.uitesting.doctypes.CMExternalChannel;
import com.coremedia.uitesting.doctypes.CMExternalProduct;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import net.joala.condition.Condition;
import net.joala.condition.ConditionFactory;
import net.joala.expression.AbstractExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

@Named
public class CatalogUtils {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogUtils.class);

  @Inject
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Inject
  private SitesService sitesService;

  @Inject
  private ContentRepository contentRepository;

  @Inject
  private ConditionFactory conditionFactory;

  @NonNull
  public Site getSite(@NonNull TestSiteConfiguration testSiteConfiguration) {
    String siteName = testSiteConfiguration.getName();
    Locale siteLocale = testSiteConfiguration.getLocale();

    Set<Site> sites = sitesService.getSites();

    return sites.stream()
            .filter(matchesNameAndLocale(siteName, siteLocale))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                    format("Unable to find site named '%s' for locale '%s'.", siteName, siteLocale)));
  }

  @NonNull
  private static Predicate<Site> matchesNameAndLocale(String siteName, Locale locale) {
    return site -> {
      try {
        return Objects.equals(siteName, site.getName()) && Objects.equals(locale, site.getLocale());
      } catch (CapObjectDestroyedException | SiteDestroyedException e) {
        LOG.debug("ignoring destroyed site '{}'", site.getId(), e);
        return false;
      }
    };
  }

  @NonNull
  public CommerceBean getCommerceBean(@NonNull String id, @NonNull TestSiteConfiguration testSiteConfiguration) {
    Site site = getSite(testSiteConfiguration);
    return getCommerceBean(id, site);
  }

  @NonNull
  public CommerceBean getCommerceBean(@NonNull String id, String siteId) {
    Site site = getSite(siteId);
    return getCommerceBean(id, site);
  }

  @NonNull
  public CommerceBean getCommerceBean(@NonNull String id, @NonNull Site site) {
    CommerceConnection commerceConnection = commerceConnectionInitializer.findConnectionForSite(site)
            .orElseThrow(() -> new NoCommerceConnectionAvailable(
                    String.format("No commerce connection available for site '%s'.", site.getName())));

    StoreContext storeContext = commerceConnection.getStoreContext();

    CurrentStoreContext.set(storeContext);

    CommerceId commerceId = CommerceIdParserHelper.parseCommerceIdOrThrow(id);
    return commerceConnection.getCommerceBeanFactory().createBeanFor(commerceId, storeContext);
  }

  @Nullable
  public Site getSite(String siteId) {
    return sitesService.getSite(siteId);
  }

  @NonNull
  public Condition<Content> getAugmentingContentCondition(@NonNull CommerceBean commerceBean) {
    String siteId = commerceBean.getContext().getSiteId();

    final Site site = sitesService.getSite(siteId);
    assert site != null;

    return conditionFactory.condition(new AbstractExpression<Content>() {
      @Override
      public Content get() {
        Collection<Content> contents = getContentsUncached(site, commerceBean.getReference());
        return contents.stream().findFirst().orElse(null);
      }
    });
  }

  @NonNull
  private Collection<Content> getContentsUncached(@NonNull Site site, @NonNull CommerceId commerceId) {
    String docTypeName = null;
    String externalPropertyName = null;

    CommerceBeanType commerceBeanType = commerceId.getCommerceBeanType();
    if (CATEGORY.equals(commerceBeanType)) {
      docTypeName = CMExternalChannel.NAME;
      externalPropertyName = CMExternalChannel.P_EXTERNAL_ID;
    } else if (PRODUCT.equals(commerceBeanType)) {
      docTypeName = CMExternalProduct.NAME;
      externalPropertyName = CMExternalProduct.P_EXTERNAL_ID;
    }

    if (docTypeName == null) {
      return emptyList();
    }

    String query = "TYPE = " + docTypeName + " : " + externalPropertyName
            + " = ?0 AND isInProduction AND BELOW ?1 ORDER BY id";
    Content folder = site.getSiteRootFolder();

    QueryService queryService = contentRepository.getQueryService();
    return queryService.poseContentQuery(query, CommerceIdFormatterHelper.format(commerceId), folder);
  }

  @NonNull
  public String getCommerceBeanName(@Nullable CommerceBean commerceBean) {
    if (commerceBean instanceof Product) {
      return ((Product) commerceBean).getName();
    } else if (commerceBean instanceof Category) {
      return ((Category) commerceBean).getName();
    } else {
      throw new UnsupportedOperationException("cannot handle commerce bean " + commerceBean);
    }
  }
}
