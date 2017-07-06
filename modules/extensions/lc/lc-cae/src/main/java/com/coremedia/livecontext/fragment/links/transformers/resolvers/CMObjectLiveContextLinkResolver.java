package com.coremedia.livecontext.fragment.links.transformers.resolvers;

import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannelImpl;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.seo.SeoSegmentBuilder;
import com.coremedia.livecontext.fragment.resolver.ExternalReferenceResolver;
import com.coremedia.livecontext.handler.ExternalNavigationHandler;
import com.coremedia.livecontext.logictypes.CommerceLedPageExtension;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Default LiveContext link resolver for all objects of type {@link com.coremedia.blueprint.common.contentbeans.CMObject}.
 * This fill the json object representing the link (which will be returned by the fragment call).
 */
public class CMObjectLiveContextLinkResolver extends AbstractLiveContextLinkResolver {
  //common constants to determine the template name
  public static final String KEY_OBJECT_TYPE = "objectType";
  //these are the supported object types that can be rendered as fragments on commerce site
  public static final String OBJECT_TYPE_PRODUCT = "product";
  public static final String OBJECT_TYPE_CONTENT = "content";
  public static final String OBJECT_TYPE_CATEGORY = "category";
  public static final String OBJECT_TYPE_LC_PAGE = "page";

  public static final String KEY_RENDER_TYPE = "renderType";
  public static final String RENDER_TYPE_URL = "url";

  //additional parameter values to be read from the corresponding JSP
  private static final String EXTERNAL_SEOSEGMENT_PARAMETER_NAME = "externalSeoSegment";
  private static final String EXTERNAL_URI_PATH_PARAMETER_NAME = "externalUriPath";

  //the id values of the objects
  public static final String PRODUCT_ID = "productId";
  public static final String CATEGORY_ID = "categoryId";
  public static final String CONTENT_ID = "contentId";
  public static final String LEVEL = "level";
  public static final String TOP_CATEGORY_ID = "topCategoryId";
  public static final String PARENT_CATEGORY_ID = "parentCategoryId";

  private SeoSegmentBuilder seoSegmentBuilder;

  private CommerceLedPageExtension commerceLedPageExtension;
  private ExternalNavigationHandler externalNavigationHandler;

  @Required
  public void setCommerceLedPageExtension(CommerceLedPageExtension commerceLedPageExtension) {
    this.commerceLedPageExtension = commerceLedPageExtension;
  }

  @Required
  public void setSeoSegmentBuilder(SeoSegmentBuilder seoSegmentBuilder) {
    this.seoSegmentBuilder = seoSegmentBuilder;
  }

  @Required
  public void setExternalNavigationHandler(ExternalNavigationHandler externalNavigationHandler) {
    this.externalNavigationHandler = externalNavigationHandler;
  }

  @Override
  public boolean isApplicable(Object bean) {
    return (bean instanceof CMObject) || (bean instanceof ProductInSite);
  }

  /**
   * Evaluates the given bean type. Depending on the type, values are put
   * into a JSON map. The JSON is passed to the CoreMedia Content Widget and is
   * used to evaluate the corresponding JSP that is used to generate the link on commerce site.
   * Additional values, like SEO segments are already evaluated here so that the JSP can process them.
   *
   * The JSP on the commerce site will be determined by the KEY_OBJECT_TYPE and KEY_RENDER_TYPE, e.g.
   * for a ProductTeaser or a ProductInSite object the KEY_OBJECT_TYPE is "product" and the KEY_RENDER_TYPE value
   * is "url", result in the template "Product.url.jsp".
   *
   * @param bean       Bean for which URL is to be rendered
   * @param variant    Link variant
   * @param navigation Current navigation of bean for which URL is to be rendered
   * @return The JSON object send to commerce.
   * @throws JSONException
   */
  @Override
  protected JSONObject resolveUrlInternal(Object bean, String variant, CMNavigation navigation) throws JSONException {
    JSONObject out = new JSONObject();
    out.put(KEY_RENDER_TYPE, RENDER_TYPE_URL);

    try {
      // Product
      if (bean instanceof CMProductTeaser || bean instanceof ProductInSite) {
        Product product;
        // Todo: mbi better logging
        if (bean instanceof CMProductTeaser) {
          product = ((CMProductTeaser) bean).getProduct();
          if (product == null) {
            throw new IllegalArgumentException("Product cannot be retrieved (product teaser: " + ((CMProductTeaser) bean).getContent().getPath() + ")");
          }
        } else {
          product = ((ProductInSite) bean).getProduct();
          if (product == null) {
            throw new IllegalArgumentException("Product cannot be retrieved (in site: " + ((ProductInSite) bean).getSite() + ")");
          }
        }

        //set type and id
        out.put(KEY_OBJECT_TYPE, OBJECT_TYPE_PRODUCT);
        out.put(PRODUCT_ID, product.getExternalTechId());

        //determine the category id too, since a product can be inside different categories
        List<Category> breadcrumb = product.getCategory().getBreadcrumb();
        Category parentCategory = (breadcrumb.size() > 0) ? Lists.reverse(breadcrumb).get(0) : null;
        if (parentCategory != null) {
          String categoryId = parentCategory.getExternalTechId();
          out.put(CATEGORY_ID, categoryId);
        }
      }
      // Category in commerce are mapped by "CMExternalChannel" objects, LiveContextExternalChannel are instances of these
      else if (bean instanceof LiveContextExternalChannelImpl) {
        LiveContextExternalChannelImpl externalChannel = (LiveContextExternalChannelImpl) bean;
        Category category = externalChannel.getCategory();
        if (category != null) {
          int level = category.getBreadcrumb().size();
          if (level > 3) {
            level = 3;
          }

          //determine type and id
          out.put(KEY_OBJECT_TYPE, OBJECT_TYPE_CATEGORY);
          out.put(CATEGORY_ID, category.getExternalTechId());
          out.put(LEVEL, level);
          if (level >= 2) {
            out.put(TOP_CATEGORY_ID, category.getBreadcrumb().get(0).getExternalTechId());
          }
          if (level >= 3) {
            Category parent = category.getParent();
            out.put(PARENT_CATEGORY_ID, parent != null ? parent.getExternalTechId() : category.getExternalTechId());
          }
        }
      }
      else if (bean instanceof CMExternalPage) {
        CMExternalPage externalChannel = (CMExternalPage) bean;
        out.put(KEY_OBJECT_TYPE, OBJECT_TYPE_LC_PAGE);
        if (isNotEmpty(externalChannel.getExternalUriPath())) {
          //pass the fully qualified url to shop system
          out.put(EXTERNAL_URI_PATH_PARAMETER_NAME,
                  externalNavigationHandler.buildLinkForExternalPage(externalChannel, Collections.<String, Object>emptyMap()));
        } else {
          out.put(EXTERNAL_SEOSEGMENT_PARAMETER_NAME, externalChannel.getExternalId());
        }
      }
      // Content
      else if (bean instanceof CMObject) {
        CMObject contentBean = (CMObject) bean;
        String contentId = ExternalReferenceResolver.CONTENT_ID_FRAGMENT_PREFIX + navigation.getContentId() + "-" + contentBean.getContentId();

        //set type and id
        out.put(KEY_OBJECT_TYPE, OBJECT_TYPE_CONTENT);
        out.put(CONTENT_ID, contentId);

        // read url prefix for coremedia content
        if (commerceLedPageExtension.isCommerceLedChannel(contentBean)) {
          String contentURLKeyword = commerceLedPageExtension.getContentURLKeyword();
          if (StringUtils.isNotBlank(contentURLKeyword)) {
            out.put("staticContentURLKeyword", contentURLKeyword);
          }
        }

        // SEO Segments
        String externalSeoSegment = seoSegmentBuilder.asSeoSegment(navigation, contentBean);
        if (StringUtils.isNotBlank(externalSeoSegment)) {
          out.put(EXTERNAL_SEOSEGMENT_PARAMETER_NAME, externalSeoSegment);
        }
      }
    } catch (Exception e) {
      LOG.error("Error creating placeholder JSON representation", e);
    }

    return out;
  }
}
