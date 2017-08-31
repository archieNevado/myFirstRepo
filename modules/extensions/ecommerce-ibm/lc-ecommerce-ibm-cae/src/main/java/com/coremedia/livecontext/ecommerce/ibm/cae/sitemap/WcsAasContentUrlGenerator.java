package com.coremedia.livecontext.ecommerce.ibm.cae.sitemap;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.cae.sitemap.ContentUrlGenerator;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.seo.ExternalSeoSegmentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.coremedia.blueprint.base.links.UriConstants.Links.ABSOLUTE_URI_KEY;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Boolean.parseBoolean;
import static java.util.Objects.requireNonNull;

public class WcsAasContentUrlGenerator extends ContentUrlGenerator {
  private static final Logger LOG = LoggerFactory.getLogger(WcsAasContentUrlGenerator.class);

  private SitesService sitesService;
  private ContextStrategy<Content, Content> contextStrategy;
  private String wcsStorefrontUrl;
  private String urlKeyword;
  private ExternalSeoSegmentBuilder externalSeoSegmentBuilder;

  // --- Spring config ----------------------------------------------

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  public void setExternalSeoSegmentBuilder(ExternalSeoSegmentBuilder externalSeoSegmentBuilder) {
    this.externalSeoSegmentBuilder = externalSeoSegmentBuilder;
  }

  @Required
  public void setContextStrategy(ContextStrategy<Content, Content> contextStrategy) {
    this.contextStrategy = contextStrategy;
  }

  @Required
  public void setWcsStorefrontUrl(String wcsStorefrontUrl) {
    this.wcsStorefrontUrl = wcsStorefrontUrl;
  }

  @Required
  public void setUrlKeyword(String urlKeyword) {
    this.urlKeyword = urlKeyword;
  }

  public String getUrlKeyword() {
    CommerceConnection connection = requireNonNull(DefaultConnection.get(), "no commerce connection available");
    return CommercePropertyHelper.replaceTokens(urlKeyword, connection.getStoreContext());
  }

  public String getWcsStorefrontUrl() {
    CommerceConnection connection = requireNonNull(DefaultConnection.get(), "no commerce connection available");
    return CommercePropertyHelper.replaceTokens(wcsStorefrontUrl, connection.getStoreContext());
  }

  // --- features ---------------------------------------------------

  // #TODO: extract @Link method

  @Override
  protected String createLink(Content content, HttpServletRequest request, HttpServletResponse response, boolean absoluteUrls) {
    String secureString = request.getParameter(SECURE_PARAM_NAME);
    String linkForCrawler = createLinkForCrawler(content, request, response, absoluteUrls);
    String linkForIndexer = createLinkForIndexer(content, parseBoolean(secureString));

    return linkForCrawler + "###" + linkForIndexer;
  }

  private String createLinkForCrawler(Content content, HttpServletRequest request, HttpServletResponse response, boolean absoluteUrls) {
    Object rememberMe = request.getAttribute(ABSOLUTE_URI_KEY);
    try {
      request.setAttribute(ABSOLUTE_URI_KEY, absoluteUrls);
      return UriComponentsBuilder.fromUriString(getLinkFormatter().formatLink(getContentBeanFactory().createBeanFor(content), null, request, response, false))
              .scheme("http")
              .build().toUriString();
    } finally {
      request.setAttribute(ABSOLUTE_URI_KEY, rememberMe);
    }
  }

  private String createLinkForIndexer(@Nonnull Content content, boolean secure) {
    //noinspection ConstantConditions
    checkArgument(content != null, "A content to built the link for must be given");
    checkState(externalSeoSegmentBuilder != null, "An external seo segment builder must be given.");

    CMLinkable linkable = getContentBeanFactory().createBeanFor(content, CMLinkable.class);
    if (linkable == null) {
      LOG.warn("Cannot create index url for non linkables. Will return null.");
      return null;
    }

    CMChannel context = getContextFor(content);
    if (context == null) {
      LOG.warn("Could not find a context for {}. Will not create an index url but return null.", content.getPath());
      return null;
    }

    Site site = sitesService.getContentSiteAspect(content).getSite();
    if (site == null) {
      LOG.warn("Could not find a site for content {}. Will not create an index url. Return null.", content.getPath());
      return null;
    }

    String language = site.getLocale().getLanguage();
    CommerceConnection connection = requireNonNull(DefaultConnection.get(), "no commerce connection available");
    StoreContext storeContext = connection.getStoreContextProvider().findContextBySite(site);
    if (storeContext == null) {
      LOG.warn("No store context found for site {}. Will not create an index url. Return null.", site.getName());
      return null;
    }

    String shopName = connection.getStoreContext().getStoreName();
    if (shopName != null) {
      shopName = shopName.toLowerCase();
    }
    return UriComponentsBuilder.fromUriString(getWcsStorefrontUrl())
            .pathSegment(language)
            .pathSegment(shopName)
            .pathSegment(getUrlKeyword())
            .pathSegment(externalSeoSegmentBuilder.asSeoSegment(context,linkable))
            .scheme(secure ? "https" : "http")
            .build().toUriString();
  }

  private CMChannel getContextFor(Content content) {
    List<Content> contexts = contextStrategy.findContextsFor(content);
    Content channel = contexts.isEmpty() ? null : contexts.get(0);
    if (channel==null) {
      return null;
    }

    return getContentBeanFactory().createBeanFor(channel, CMChannel.class);
  }
}
