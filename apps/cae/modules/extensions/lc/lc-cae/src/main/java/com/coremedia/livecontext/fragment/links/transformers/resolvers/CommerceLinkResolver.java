package com.coremedia.livecontext.fragment.links.transformers.resolvers;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.cae.handlers.PreviewHandler;
import com.coremedia.blueprint.cae.layout.ContentBeanBackedPageGridPlacement;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.layout.DynamizableContainer;
import com.coremedia.livecontext.commercebeans.CategoryInSite;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.livecontext.contentbeans.LiveContextExternalProduct;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.link.LinkService;
import com.coremedia.livecontext.ecommerce.link.QueryParam;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.seo.ExternalSeoSegmentBuilder;
import com.coremedia.objectserver.beans.ContentBean;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.link.UrlUtil.getQueryParamList;
import static com.coremedia.livecontext.fragment.links.transformers.resolvers.AbstractLiveContextLinkResolver.deabsolutizeLink;

@Component
public class CommerceLinkResolver implements LiveContextLinkResolver {

  private static final Logger LOG = LoggerFactory.getLogger(CommerceLinkResolver.class);

  private final ExternalSeoSegmentBuilder seoSegmentBuilder;

  public CommerceLinkResolver(@NonNull ExternalSeoSegmentBuilder seoSegmentBuilder) {
    this.seoSegmentBuilder = seoSegmentBuilder;
  }

  @NonNull
  @Override
  public Optional<String> resolveUrl(@NonNull String source, Object bean, String variant, CMNavigation navigation,
                                     HttpServletRequest request) {
    try {
      String link = buildLink(source, bean, navigation, request);
      return Optional.ofNullable(link);
    } catch (Exception e) {
      LOG.error("Error creating salesforce intermediate link representation for '{}'", debug(bean), e);
      return Optional.empty();
    }
  }

  @Nullable
  @SuppressWarnings({"IfStatementWithTooManyBranches", "OverlyComplexMethod"})
  private String buildLink(@NonNull String source, Object bean, CMNavigation navigation, HttpServletRequest request) {
    CommerceConnection connection = CurrentCommerceConnection.find().orElse(null);
    if (connection == null){
      return null;
    }

    LinkService linkService = connection.getLinkService().orElse(null);
    if (linkService == null){
      return null;
    }

    List<QueryParam> linkParameters = getQueryParamList(source);
    if (bean instanceof CMProductTeaser) {
      CMProductTeaser productTeaser = (CMProductTeaser) bean;
      Product product = productTeaser.getProduct();

      if (product == null) {
        LOG.debug("Cannot generate link for product teaser '{}', product not set", productTeaser.getContentId());
        return null;
      }

      return linkService.getProductLink(product, null, linkParameters, request).toLink();

    } else if (bean instanceof ProductInSite) {
      ProductInSite productInSite = (ProductInSite) bean;
      Product product = productInSite.getProduct();
      return linkService.getProductLink(product, null, linkParameters, request).toLink();

    } else if (bean instanceof LiveContextExternalProduct) {
      LiveContextExternalProduct externalProduct = (LiveContextExternalProduct) bean;
      Product product = externalProduct.getProduct();

      if (product == null) {
        LOG.debug("Cannot generate link for augmented product '{}', product not set", externalProduct.getContentId());
        return null;
      }

      return linkService.getProductLink(product, null, linkParameters, request).toLink();

    } else if (bean instanceof Product) {
      Product product = (Product) bean;
      return linkService.getProductLink(product, null, linkParameters, request).toLink();

    } else if (bean instanceof CMExternalPage) {
      CMExternalPage externalPage = (CMExternalPage) bean;
      if (externalPage.isRoot()) {
        return linkService.getExternalPageLink(null, null, connection.getStoreContext(), linkParameters, request).toLink();
      }
      String seoPath = externalPage.getExternalId();
      return linkService.getExternalPageLink(seoPath, null, connection.getStoreContext(), linkParameters, request).toLink();

    } else if (bean instanceof LiveContextNavigation) {
      LiveContextNavigation liveContextNavigation = (LiveContextNavigation) bean;
      Category category = liveContextNavigation.getCategory();
      return linkService.getCategoryLink(category, linkParameters, request).toLink();

    } else if (bean instanceof CategoryInSite) {
      CategoryInSite categoryInSite = (CategoryInSite) bean;
      return linkService.getCategoryLink(categoryInSite.getCategory(), linkParameters, request).toLink();

    } else if (bean instanceof Category) {
      Category category = (Category) bean;
      return linkService.getCategoryLink(category, linkParameters, request).toLink();

    } else if (bean instanceof CMNavigation) {
      CMNavigation cmNavigation = (CMNavigation) bean;
      String seoPath = seoSegmentBuilder.asSeoSegment(navigation, cmNavigation);
      return linkService.getContentLink(seoPath, connection.getStoreContext(), linkParameters, request).toLink();

    } else if (bean instanceof CMLinkable) {
      CMLinkable cmLinkable = (CMLinkable) bean;
      String seoPath = seoSegmentBuilder.asSeoSegment(navigation, cmLinkable);
      return linkService.getContentLink(seoPath, connection.getStoreContext(), linkParameters, request).toLink();

    } else if (bean instanceof ContentBeanBackedPageGridPlacement || bean instanceof DynamizableContainer) {
      String relativeLink = deabsolutizeLink(source);
      return linkService.getAjaxLink(relativeLink, connection.getStoreContext(), request).toLink();

    } else {
      return null;
    }
  }

  private static boolean isStudioPreviewRequest(@NonNull HttpServletRequest request) {
    return PreviewHandler.isStudioPreviewRequest(request);
  }

  @Nullable
  private static String debug(Object bean) {
    return (bean instanceof ContentBean && ((ContentBean) bean).getContent() != null)
            ? ((ContentBean) bean).getContent().getPath()
            : bean + "";
  }

  private static boolean isLinkServiceAvailable() {
    return CurrentCommerceConnection.find()
            .map(CommerceConnection::getLinkService)
            .isPresent();
  }

  @Override
  public boolean isApplicable(Object bean, HttpServletRequest request) {
    //only execute when link service available and current request is not the studio preview url request (/preview?id=xxx)
    return isLinkServiceAvailable() && !isStudioPreviewRequest(request);
  }
}
