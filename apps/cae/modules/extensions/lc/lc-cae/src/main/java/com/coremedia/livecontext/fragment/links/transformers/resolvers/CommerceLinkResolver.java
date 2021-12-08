package com.coremedia.livecontext.fragment.links.transformers.resolvers;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.blueprint.cae.handlers.PreviewHandler;
import com.coremedia.blueprint.cae.layout.ContentBeanBackedPageGridPlacement;
import com.coremedia.blueprint.common.contentbeans.CMDynamicList;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.layout.DynamizableContainer;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.commercebeans.CategoryInSite;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.livecontext.contentbeans.LiveContextExternalProduct;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.link.LinkService;
import com.coremedia.livecontext.ecommerce.link.QueryParam;
import com.coremedia.livecontext.ecommerce.link.StorefrontRef;
import com.coremedia.livecontext.ecommerce.order.Cart;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.seo.ExternalSeoSegmentBuilder;
import com.coremedia.objectserver.beans.ContentBean;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static com.coremedia.blueprint.base.links.UriConstants.Segments.PREFIX_DYNAMIC;
import static com.coremedia.blueprint.base.livecontext.ecommerce.link.UrlUtil.getQueryParamList;

@Component
@DefaultAnnotation(NonNull.class)
public class CommerceLinkResolver implements LiveContextLinkResolver {

  private static final Logger LOG = LoggerFactory.getLogger(CommerceLinkResolver.class);

  private static final String PATTERN_DYNAMIC = "/" + PREFIX_DYNAMIC + "/";

  private final ExternalSeoSegmentBuilder seoSegmentBuilder;

  public CommerceLinkResolver(ExternalSeoSegmentBuilder seoSegmentBuilder) {
    this.seoSegmentBuilder = seoSegmentBuilder;
  }

  @Override
  public Optional<String> resolveUrl(String source, Object bean, String variant, CMNavigation navigation,
                                     HttpServletRequest request) {
    try {
      String link = buildLink(source, bean, navigation, request);
      return Optional.ofNullable(link);
    } catch (Exception e) {
      LOG.warn("Unable to create intermediate commerce link representation for '{}'.", debug(bean), e);
      return Optional.empty();
    }
  }

  @Nullable
  private String buildLink(String source, Object bean, CMNavigation navigation, HttpServletRequest request) {
    var storeContext = CurrentStoreContext.find(request).orElse(null);
    if (storeContext == null) {
      return null;
    }

    LinkService linkService = storeContext.getConnection().getLinkService().orElse(null);
    if (linkService == null) {
      return null;
    }

    return buildLink(source, bean, navigation, linkService, storeContext, request)
            .map(StorefrontRef::toLink)
            .orElse(null);
  }

  @SuppressWarnings({"IfStatementWithTooManyBranches", "OverlyComplexMethod"})
  private Optional<StorefrontRef> buildLink(String source, Object bean, CMNavigation navigation,
                                            LinkService linkService, StoreContext storeContext,
                                            HttpServletRequest request) {
    List<QueryParam> linkParameters = getQueryParamList(source);

    if (bean instanceof CMProductTeaser) {
      CMProductTeaser productTeaser = (CMProductTeaser) bean;
      Product product = productTeaser.getProduct();

      if (product == null) {
        LOG.debug("Cannot generate link for product teaser '{}'; product is not set.", productTeaser.getContentId());
        return Optional.empty();
      }

      return CommerceLedLinkUtils.getProductLink(product, null, linkParameters, linkService);
    } else if (bean instanceof ProductInSite) {
      ProductInSite productInSite = (ProductInSite) bean;
      Product product = productInSite.getProduct();
      return CommerceLedLinkUtils.getProductLink(product, null, linkParameters, linkService);
    } else if (bean instanceof LiveContextExternalProduct) {
      LiveContextExternalProduct externalProduct = (LiveContextExternalProduct) bean;
      Product product = externalProduct.getProduct();

      if (product == null) {
        LOG.debug("Cannot generate link for augmented product '{}'; product is not set.",
                externalProduct.getContentId());
        return Optional.empty();
      }

      return CommerceLedLinkUtils.getProductLink(product, null, linkParameters, linkService);
    } else if (bean instanceof Product) {
      Product product = (Product) bean;
      return CommerceLedLinkUtils.getProductLink(product, null, linkParameters, linkService);
    } else if (bean instanceof CMExternalPage) {
      CMExternalPage externalPage = (CMExternalPage) bean;
      if (externalPage.isRoot()) {
        return CommerceLedLinkUtils.getExternalPageLink(null, null, storeContext, linkParameters, linkService);
      }
      String seoPath = externalPage.getExternalId();
      String externalUriPath = externalPage.getExternalUriPath();
      return CommerceLedLinkUtils.getExternalPageLink(seoPath, externalUriPath, storeContext, linkParameters, linkService);
    } else if (bean instanceof LiveContextNavigation) {
      LiveContextNavigation liveContextNavigation = (LiveContextNavigation) bean;
      Category category = liveContextNavigation.getCategory();
      return CommerceLedLinkUtils.getCategoryLink(category, linkParameters, linkService);
    } else if (bean instanceof CategoryInSite) {
      CategoryInSite categoryInSite = (CategoryInSite) bean;
      return CommerceLedLinkUtils.getCategoryLink(categoryInSite.getCategory(), linkParameters, linkService);
    } else if (bean instanceof Category) {
      Category category = (Category) bean;
      return CommerceLedLinkUtils.getCategoryLink(category, linkParameters, linkService);
    } else if (bean instanceof CMNavigation) {
      CMNavigation cmNavigation = (CMNavigation) bean;
      String seoPath = seoSegmentBuilder.asSeoSegment(navigation, cmNavigation);
      return CommerceLedLinkUtils.getContentLink(seoPath, storeContext, linkParameters, linkService);
    } else if (bean instanceof CMDynamicList) {
      String relativeLink = deabsolutizeLink(source);
      return CommerceLedLinkUtils.getAjaxLink(relativeLink, storeContext, linkService);
    } else if (bean instanceof CMLinkable) {
      CMLinkable cmLinkable = (CMLinkable) bean;
      String seoPath = seoSegmentBuilder.asSeoSegment(navigation, cmLinkable);
      return CommerceLedLinkUtils.getContentLink(seoPath, storeContext, linkParameters, linkService);
    } else if (bean instanceof ContentBeanBackedPageGridPlacement || bean instanceof DynamizableContainer || bean instanceof Cart) {
      String relativeLink = deabsolutizeLink(source);
      //we only want to wrap CAE Ajax calls into commerce Ajax calls
      //make sure we only translate dynamic CAE links
      if(isDynamicCaeLink(relativeLink)){
        return CommerceLedLinkUtils.getAjaxLink(relativeLink, storeContext, linkService);
      }
    }
    return Optional.empty();
  }

  private static boolean isDynamicCaeLink(String relativeLink) {
    return relativeLink.contains(PATTERN_DYNAMIC);
  }

  @Nullable
  private static String debug(Object bean) {
    if (bean instanceof ContentBean) {
      Content content = ((ContentBean) bean).getContent();
      if (content != null) {
        return content.getPath();
      }
    }

    return bean + "";
  }

  @Override
  public boolean isApplicable(Object bean, HttpServletRequest request) {
    // Only execute when link service is available and current request
    // is not the studio preview URL request (`/preview?id=xxx`).
    return isLinkServiceAvailable(request) && !isStudioPreviewRequest(request);
  }

  private static boolean isLinkServiceAvailable(HttpServletRequest request) {
    return CurrentStoreContext.find(request)
            .map(StoreContext::getConnection)
            .map(CommerceConnection::getLinkService)
            .isPresent();
  }

  private static boolean isStudioPreviewRequest(HttpServletRequest request) {
    return PreviewHandler.isStudioPreviewRequest(request);
  }

  static String deabsolutizeLink(String cmsLink) {
    // example: https://preview.host.name/bla/servlet/dynamic/placement/p13n/sitegenesis-en-gb/130/main?targetView=%5Bcarousel%5D
    if (cmsLink.startsWith("http") || cmsLink.startsWith("//")) {
      int index = StringUtils.ordinalIndexOf(cmsLink, "/", 3);
      if (index != -1) {
        return cmsLink.substring(index);
      }
    }
    return cmsLink;
  }
}
