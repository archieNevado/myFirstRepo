package com.coremedia.livecontext.fragment.links.transformers.resolvers;

import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.link.CommerceLinkTemplateTypes;
import com.coremedia.livecontext.ecommerce.link.LinkService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.fragment.links.transformers.resolvers.CommerceLedLinkUtils.ALTERNATIVE_PATH_KEY;
import static com.coremedia.livecontext.fragment.links.transformers.resolvers.CommerceLedLinkUtils.CATEGORY_ID_KEY;
import static com.coremedia.livecontext.fragment.links.transformers.resolvers.CommerceLedLinkUtils.LEVEL_KEY;
import static com.coremedia.livecontext.fragment.links.transformers.resolvers.CommerceLedLinkUtils.PARENT_CATEGORY_ID_KEY;
import static com.coremedia.livecontext.fragment.links.transformers.resolvers.CommerceLedLinkUtils.PRODUCT_ID_KEY;
import static com.coremedia.livecontext.fragment.links.transformers.resolvers.CommerceLedLinkUtils.SEO_PATH_KEY;
import static com.coremedia.livecontext.fragment.links.transformers.resolvers.CommerceLedLinkUtils.SEO_SEGMENT_KEY;
import static com.coremedia.livecontext.fragment.links.transformers.resolvers.CommerceLedLinkUtils.TOP_CATEGORY_ID_KEY;
import static com.coremedia.livecontext.fragment.links.transformers.resolvers.CommerceLedLinkUtils.URL_KEY;
import static com.coremedia.livecontext.fragment.links.transformers.resolvers.CommerceLedLinkUtils.getAjaxLink;
import static com.coremedia.livecontext.fragment.links.transformers.resolvers.CommerceLedLinkUtils.getCategoryLink;
import static com.coremedia.livecontext.fragment.links.transformers.resolvers.CommerceLedLinkUtils.getContentLink;
import static com.coremedia.livecontext.fragment.links.transformers.resolvers.CommerceLedLinkUtils.getExternalPageLink;
import static com.coremedia.livecontext.fragment.links.transformers.resolvers.CommerceLedLinkUtils.getProductLink;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommerceLedLinkUtilsTest {

  @Mock
  private Category category3;

  @Mock
  private Category category2;

  @Mock
  private Category category1;

  @Mock
  private Category category0;

  @Mock
  private Product product;

  @Mock
  private LinkService linkService;

  @Mock
  private StoreContext storeContext;

  @Test
  void testCategoryLink() {
    when(category3.getExternalTechId()).thenReturn("3333");
    when(category2.getExternalTechId()).thenReturn("222");
    when(category0.getExternalTechId()).thenReturn("0");
    List<Category> breadcrumbPath = new ArrayList<>();
    breadcrumbPath.add(category0);
    breadcrumbPath.add(category1);
    breadcrumbPath.add(category2);
    when(category3.getBreadcrumb()).thenReturn(breadcrumbPath);
    when(category3.getParent()).thenReturn(category2);
    when(category3.getContext()).thenReturn(storeContext);

    getCategoryLink(category3, List.of(), linkService);
    verify(linkService).getStorefrontRef(eq(CommerceLinkTemplateTypes.CATEGORY_LINK_FRAGMENT), eq(storeContext),
            eq(Map.of(CATEGORY_ID_KEY, "3333", PARENT_CATEGORY_ID_KEY, "222", TOP_CATEGORY_ID_KEY, "0", LEVEL_KEY, "3")));
  }

  @Test
  void testProductLink() {
    when(product.getExternalTechId()).thenReturn("0815");
    when(product.getCategory()).thenReturn(category0);
    when(category0.getExternalTechId()).thenReturn("ROOT");
    when(product.getContext()).thenReturn(storeContext);
    getProductLink(product, null, List.of(), linkService);

    verify(linkService).getStorefrontRef(CommerceLinkTemplateTypes.PRODUCT_LINK_FRAGMENT, storeContext,
            Map.of(PRODUCT_ID_KEY, "0815", CATEGORY_ID_KEY, "ROOT"));
  }

  @Test
  void testExternalPageLink() {
    String seoPath = "bla/blub";
    getExternalPageLink(seoPath, null, storeContext, List.of(), linkService);

    verify(linkService).getStorefrontRef(CommerceLinkTemplateTypes.SHOP_PAGE_LINK_FRAGMENT, storeContext,
            Map.of(SEO_SEGMENT_KEY, seoPath, SEO_PATH_KEY, seoPath));
  }

  @Test
  void testAltExternalPageLink() {
    String seoPath = "bla/blub";
    String alternativePath = "alternative/path";
    getExternalPageLink(seoPath, alternativePath, storeContext, List.of(), linkService);

    verify(linkService).getStorefrontRef(CommerceLinkTemplateTypes.SHOP_PAGE_LINK_FRAGMENT, storeContext,
            Map.of(ALTERNATIVE_PATH_KEY, alternativePath));
  }

  @Test
  void testContentPageLink() {
    String seoPath = "bla/blub";
    getContentLink(seoPath, storeContext, List.of(), linkService);

    verify(linkService).getStorefrontRef(CommerceLinkTemplateTypes.CM_CONTENT_LINK_FRAGMENT, storeContext,
            Map.of(SEO_SEGMENT_KEY, seoPath, SEO_PATH_KEY, seoPath));
  }

  @Test
  void testAjaxLink() {
    String url = "/bla/blub.html";
    getAjaxLink(url, storeContext, linkService);
    verify(linkService).getStorefrontRef(CommerceLinkTemplateTypes.CM_AJAX_LINK_FRAGMENT, storeContext,
            Map.of(URL_KEY, url));
  }
}
