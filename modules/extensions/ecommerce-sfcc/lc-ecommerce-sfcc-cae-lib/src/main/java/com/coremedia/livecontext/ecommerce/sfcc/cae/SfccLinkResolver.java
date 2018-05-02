package com.coremedia.livecontext.ecommerce.sfcc.cae;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.cae.handlers.PreviewHandler;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccCommerceConnection;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.LiveContextLinkResolver;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.Urls;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.seo.ExternalSeoSegmentBuilder;
import com.coremedia.objectserver.beans.ContentBean;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.coremedia.livecontext.fragment.links.transformers.resolvers.Urls.getQueryParams;
import static com.coremedia.livecontext.handler.LiveContextPageHandlerBase.P13N_URI_PARAMETER;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.joining;

public class SfccLinkResolver implements LiveContextLinkResolver {

  private static final Logger LOG = LoggerFactory.getLogger(SfccLinkResolver.class);

  private final ExternalSeoSegmentBuilder seoSegmentBuilder;

  SfccLinkResolver(@Nonnull ExternalSeoSegmentBuilder seoSegmentBuilder) {
    this.seoSegmentBuilder = seoSegmentBuilder;
  }

  @Nullable
  @Override
  public String resolveUrl(String source, Object bean, String variant, CMNavigation navigation,
                           HttpServletRequest request) {
    try {
      if (bean instanceof CMProductTeaser) {
        CMProductTeaser productTeaser = (CMProductTeaser) bean;
        Product product = productTeaser.getProduct();
        if (product != null) {
          return buildLink(source, request, "Product-Show", "pid", product.getExternalId());
        } else {
          LOG.debug("Cannot generate link for product teaser '{}', product not set", productTeaser.getContentId());
        }
      } else if (bean instanceof ProductInSite) {
        ProductInSite productInSite = (ProductInSite) bean;
        return buildLink(source, request, "Product-Show", "pid", productInSite.getProduct().getExternalId());
      } else if (bean instanceof Product) {
        Product product = (Product) bean;
        return buildLink(source, request, "Product-Show", "pid", product.getExternalId());
      } else if (bean instanceof CMExternalPage) {
        CMExternalPage externalPage = (CMExternalPage) bean;
        if (externalPage.isRoot()) {
          return buildLink(source, request, "Home-Show");
        }
        String externalUriPath = externalPage.getExternalUriPath();
        if (StringUtils.isNotBlank(externalUriPath)) {
          return buildLink(source, request, "Page-Show", "cid", externalPage.getExternalId(), "cpath", externalUriPath);
        }
        return buildLink(source, request, "Page-Show", "cid", externalPage.getExternalId());
      } else if (bean instanceof LiveContextNavigation) {
        LiveContextNavigation liveContextNavigation = (LiveContextNavigation) bean;
        return buildLink(source, request, "Search-Show", "cgid", liveContextNavigation.getCategory().getExternalId());
      } else if (bean instanceof Category) {
        Category category = (Category) bean;
        return buildLink(source, request, "Search-Show", "cgid", category.getExternalId());
      } else if (bean instanceof CMNavigation) {
        CMNavigation cmNavigation = (CMNavigation) bean;
        return buildLink(source, request, "CM-Content", "pageid", seoSegmentBuilder.asSeoSegment(navigation, cmNavigation));
      } else if (bean instanceof CMLinkable) {
        CMLinkable cmLinkable = (CMLinkable) bean;
        return buildLink(source, request, "CM-Content", "pageid", seoSegmentBuilder.asSeoSegment(navigation, cmLinkable));
      }
    } catch (Exception e) {
      LOG.error("Error creating salesforce intermediate link representation for '" + debug(bean) + "'", e);
    }

    return null;
  }

  @Nonnull
  private static String buildLink(@Nullable String source, @Nonnull HttpServletRequest request, String... params) {
    List<String> paramsList = newArrayList(params);

    if (isStudioPreviewRequest(request)) {
      paramsList.add("preview");
      paramsList.add("true");
    }

    StringBuilder paramsBuilder = new StringBuilder(paramsList.stream()
            .map(param -> param != null ? param : "")  // Replace `null` with empty string.
            .map(param -> "'" + param + "'")  // Wrap each value in single quotes.
            .collect(joining(",")));

    List<Urls.Pair> queryParams = getQueryParams(source);
    for (Urls.Pair pair : queryParams) {
      // unfortunately the original (source) link already contains request parameters
      // they shouldn't be doubled
      if (!paramsBuilder.toString().contains(pair.first)) {
        paramsBuilder.append(",'").append(pair.first).append("','").append(pair.second).append("'");
      }
    }

    return "<!--VTL $include.url(" + paramsBuilder.toString() + ") VTL-->";
  }

  @VisibleForTesting
  static boolean isStudioPreviewRequest(@Nonnull HttpServletRequest request) {
    return PreviewHandler.isStudioPreviewRequest(request) ||
            "true".equals(request.getParameter(P13N_URI_PARAMETER)) ||
            "true".equals(request.getParameter("preview"));
  }

  @Nullable
  private static String debug(Object bean) {
    return (bean instanceof ContentBean)
            ? ((ContentBean) bean).getContent().getPath()
            : bean + "";
  }

  private static boolean isSfcc() {
    return CurrentCommerceConnection.find()
            .filter(SfccCommerceConnection.class::isInstance)
            .isPresent();
  }

  @Override
  public boolean isApplicable(Object bean) {
    return isSfcc();
  }
}
