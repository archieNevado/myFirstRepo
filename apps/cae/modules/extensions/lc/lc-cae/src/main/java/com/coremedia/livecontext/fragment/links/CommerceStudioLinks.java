package com.coremedia.livecontext.fragment.links;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.link.CommerceLinkTemplateTypes;
import com.coremedia.livecontext.ecommerce.link.StorefrontRef;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.seo.ExternalSeoSegmentBuilder;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.util.CommerceLinkTemplatePlaceholders.ALTERNATIVE_URI_PATH;
import static com.coremedia.blueprint.base.livecontext.util.CommerceLinkTemplatePlaceholders.CATEGORY_ID;
import static com.coremedia.blueprint.base.livecontext.util.CommerceLinkTemplatePlaceholders.CATEGORY_TECH_ID;
import static com.coremedia.blueprint.base.livecontext.util.CommerceLinkTemplatePlaceholders.PAGE_ID;
import static com.coremedia.blueprint.base.livecontext.util.CommerceLinkTemplatePlaceholders.PRODUCT_ID;
import static com.coremedia.blueprint.base.livecontext.util.CommerceLinkTemplatePlaceholders.PRODUCT_TECH_ID;
import static com.coremedia.blueprint.base.livecontext.util.CommerceLinkTemplatePlaceholders.SEO_SEGMENT;
import static com.coremedia.livecontext.ecommerce.link.CommerceLinkTemplateTypes.CATEGORY_PREVIEW_URL;
import static com.coremedia.livecontext.ecommerce.link.CommerceLinkTemplateTypes.CM_CONTENT_PREVIEW_URL;
import static com.coremedia.livecontext.ecommerce.link.CommerceLinkTemplateTypes.EXTERNAL_PAGE_NON_SEO_PREVIEW_URL;
import static com.coremedia.livecontext.ecommerce.link.CommerceLinkTemplateTypes.EXTERNAL_PAGE_PREVIEW_URL;
import static com.coremedia.livecontext.ecommerce.link.CommerceLinkTemplateTypes.HOME_PAGE_PREVIEW_URL;
import static com.coremedia.livecontext.ecommerce.link.CommerceLinkTemplateTypes.PRODUCT_PREVIEW_URL;
import static com.google.common.base.Strings.isNullOrEmpty;

@DefaultAnnotation(NonNull.class)
class CommerceStudioLinks {

  private final ExternalSeoSegmentBuilder seoSegmentBuilder;
  private final CommerceLinkHelper commerceLinkHelper;

  CommerceStudioLinks(ExternalSeoSegmentBuilder seoSegmentBuilder, CommerceLinkHelper commerceLinkHelper) {
    this.seoSegmentBuilder = seoSegmentBuilder;
    this.commerceLinkHelper = commerceLinkHelper;
  }

  Optional<UriComponents> buildLinkForCategory(Category category, Map<String, Object> linkParameters,
                                               HttpServletRequest request) {
    StoreContext storeContext = CurrentStoreContext.find(request).orElse(category.getContext())
            .withDynamicReplacements();
    Map<String, Object> replacements = new HashMap<>(linkParameters);
    replacements.put(CATEGORY_ID, category.getExternalId());
    replacements.put(CATEGORY_TECH_ID, category.getExternalTechId());

    return getUriComponents(CATEGORY_PREVIEW_URL, storeContext, replacements);
  }

  Optional<UriComponents> buildLinkForAugmentedRootCategory(CMExternalChannel externalChannel,
                                                            Map<String, Object> linkParameters,
                                                            HttpServletRequest request) {
    Category category = externalChannel.getCategory();
    StoreContext storeContext = CurrentStoreContext.find(request).orElse(category.getContext())
            .withDynamicReplacements();
    String seoSegment = externalChannel.getSegment();
    Map<String, Object> replacements = new HashMap<>(linkParameters);
    replacements.put(SEO_SEGMENT, seoSegment);

    return getUriComponents(CM_CONTENT_PREVIEW_URL, storeContext, replacements);
  }

  Optional<UriComponents> buildLinkForProduct(Product product, Map<String, Object> linkParameters,
                                              HttpServletRequest request) {
    Category category = product.getCategory();
    StoreContext storeContext = CurrentStoreContext.find(request).orElse(category.getContext())
            .withDynamicReplacements();
    Map<String, Object> replacements = new HashMap<>(linkParameters);
    replacements.put(PRODUCT_ID, product.getExternalId());
    replacements.put(PRODUCT_TECH_ID, product.getExternalTechId());

    return getUriComponents(PRODUCT_PREVIEW_URL, storeContext, replacements);
  }

  Optional<UriComponents> buildLinkForExternalPage(CMExternalPage externalPage,
                                                   Map<String, Object> linkParameters,
                                                   HttpServletRequest request) {
    return commerceLinkHelper.findCommerceConnection(externalPage)
            .flatMap(connection -> buildLinkForExternalPage(externalPage, linkParameters, request, connection));
  }

  private Optional<UriComponents> buildLinkForExternalPage(CMExternalPage bean,
                                                           Map<String, Object> linkParameters,
                                                           HttpServletRequest request,
                                                           CommerceConnection connection) {
    StoreContext storeContext = CurrentStoreContext.find(request).orElse(connection.getInitialStoreContext())
            .withDynamicReplacements();
    String externalId = bean.getExternalId();
    String externalUriPath = bean.getExternalUriPath();
    Map<String, Object> replacements = new HashMap<>(linkParameters);
    replacements.put(PAGE_ID, externalId);
    replacements.put(ALTERNATIVE_URI_PATH, externalUriPath);

    CommerceLinkTemplateTypes linkTemplate = findTemplateForExternalPage(bean);

    return getUriComponents(linkTemplate, storeContext, replacements)
            .or(() -> getUriComponents(EXTERNAL_PAGE_PREVIEW_URL, storeContext, replacements));
  }

  private CommerceLinkTemplateTypes findTemplateForExternalPage(CMExternalPage bean) {
    if (!isNullOrEmpty(bean.getExternalUriPath())) {
      return EXTERNAL_PAGE_NON_SEO_PREVIEW_URL;
    } else if (isNullOrEmpty(bean.getExternalId())) {
      return HOME_PAGE_PREVIEW_URL;
    }
    return EXTERNAL_PAGE_PREVIEW_URL;
  }

  Optional<UriComponents> buildLinkForCMChannel(CMChannel channel,
                                                Map<String, Object> linkParameters,
                                                HttpServletRequest request) {
    return commerceLinkHelper.findCommerceConnection(channel)
            .flatMap(commerceConnection -> buildLinkForCMChannel(channel, linkParameters, request, commerceConnection));
  }

  private Optional<UriComponents> buildLinkForCMChannel(CMChannel channel,
                                                        Map<String, Object> linkParameters,
                                                        HttpServletRequest request,
                                                        CommerceConnection commerceConnection) {
    StoreContext storeContext = CurrentStoreContext.find(request).orElse(commerceConnection.getInitialStoreContext())
            .withDynamicReplacements();
    String seoPath = seoSegmentBuilder.asSeoSegment(channel, channel);
    Map<String, Object> replacements = new HashMap<>(linkParameters);
    replacements.put(SEO_SEGMENT, seoPath);

    return getUriComponents(CM_CONTENT_PREVIEW_URL, storeContext, replacements);
  }

  private Optional<UriComponents> getUriComponents(CommerceLinkTemplateTypes linkTemplate, StoreContext storeContext, Map<String, Object> replacements) {
    return storeContext.getConnection().getLinkService()
            .flatMap(linkService -> linkService.getStorefrontRef(linkTemplate, storeContext))
            .map(storefrontRef -> storefrontRef.replace(replacements))
            .map(StorefrontRef::toLink)
            .map(UriComponentsBuilder::fromUriString)
            .map(UriComponentsBuilder::build);
  }
}
