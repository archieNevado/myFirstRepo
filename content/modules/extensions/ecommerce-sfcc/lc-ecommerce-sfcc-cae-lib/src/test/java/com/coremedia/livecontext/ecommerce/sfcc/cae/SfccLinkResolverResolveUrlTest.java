package com.coremedia.livecontext.ecommerce.sfcc.cae;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.LiveContextLinkResolver;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.seo.ExternalSeoSegmentBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class SfccLinkResolverResolveUrlTest {

  @Mock
  private ExternalSeoSegmentBuilder seoSegmentBuilder;

  private String variant = null;

  @Mock
  private CMNavigation navigation;

  private LiveContextLinkResolver sfccLinkResolver;

  @BeforeEach
  void setUp() {
    initMocks(this);

    sfccLinkResolver = new SfccLinkResolver(seoSegmentBuilder);
  }

  @ParameterizedTest
  @MethodSource("provideResolveUrlArgs")
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  void resolveUrl(String source, Object bean, Optional<String> expected) {
    HttpServletRequest request = buildRequest(false, false, false);

    Optional<String> actual = sfccLinkResolver.resolveUrl(source, bean, variant, navigation, request);

    assertThat(actual).isEqualTo(expected);
  }

  private static Stream<Arguments> provideResolveUrlArgs() {
    Product product = buildProduct("external-product-id-1");

    ProductInSite productInSite = mock(ProductInSite.class);
    Product productInSiteProduct = buildProduct("external-product-id-2");
    when(productInSite.getProduct()).thenReturn(productInSiteProduct);

    CMProductTeaser productTeaserWithoutProduct = mock(CMProductTeaser.class);

    CMProductTeaser productTeaserWithProduct = mock(CMProductTeaser.class);
    Product productTeaserProduct = buildProduct("external-product-id-3");
    when(productTeaserWithProduct.getProduct()).thenReturn(productTeaserProduct);

    Category category = buildCategory("external-category-id-1");

    LiveContextNavigation lcNavigation = mock(LiveContextNavigation.class);
    Category lcNavigationCategory = buildCategory("external-category-id-2");
    when(lcNavigation.getCategory()).thenReturn(lcNavigationCategory);

    CMExternalPage externalRootPage = buildExternalPage(true, "ignored", "ignored");

    CMExternalPage externalPageWithoutExternalUriPath = buildExternalPage(false, "external-page-id-1", "");

    CMExternalPage externalPageWithExternalUriPath = buildExternalPage(false, "external-page-id-2", "some-uri-path");

    CMNavigation cmNavigation = mock(CMNavigation.class);

    CMLinkable cmLinkable = mock(CMLinkable.class);

    return Stream.of(
            Arguments.of(
                    "/a/b/c/4711?foo=bar&bla=blub",
                    product,
                    Optional.of("<!--VTL $include.url('Product-Show','pid','external-product-id-1','foo','bar','bla','blub') VTL-->")
            ),
            Arguments.of(
                    "/a/b/c/4711",
                    productInSite,
                    Optional.of("<!--VTL $include.url('Product-Show','pid','external-product-id-2') VTL-->")
            ),
            Arguments.of(
                    "",
                    productTeaserWithoutProduct,
                    Optional.empty()
            ),
            Arguments.of(
                    "/a/b/c/4711?",
                    productTeaserWithProduct,
                    Optional.of("<!--VTL $include.url('Product-Show','pid','external-product-id-3') VTL-->")
            ),
            Arguments.of(
                    "/a/b/c/4711?f",
                    externalRootPage,
                    Optional.of("<!--VTL $include.url('Home-Show') VTL-->")
            ),
            Arguments.of(
                    "/?foo=bar&bla=blub",
                    externalPageWithoutExternalUriPath,
                    Optional.of("<!--VTL $include.url('Page-Show','cid','external-page-id-1','foo','bar','bla','blub') VTL-->")
            ),
            Arguments.of(
                    "?foo=bar&bla=blub",
                    externalPageWithExternalUriPath,
                    Optional.of("<!--VTL $include.url('Page-Show','cid','external-page-id-2','cpath','some-uri-path','foo','bar','bla','blub') VTL-->")
            ),
            Arguments.of(
                    "/a/b/c/4711?foo=bar?bla=blub",
                    category,
                    Optional.of("<!--VTL $include.url('Search-Show','cgid','external-category-id-1','foo','bar?bla=blub') VTL-->")
            ),
            Arguments.of(
                    "a?foo=bar&bla=blub",
                    lcNavigation,
                    Optional.of("<!--VTL $include.url('Search-Show','cgid','external-category-id-2','foo','bar','bla','blub') VTL-->")
            ),
            Arguments.of(
                    "",
                    // Navigation is `null` for now, but test case should
                    // be extended by using an actual navigation object.
                    cmNavigation,
                    Optional.of("<!--VTL $include.url('CM-Content','pageid','') VTL-->")
            ),
            Arguments.of(
                    "",
                    // Navigation is `null` for now, but test case should
                    // be extended by using an actual navigation object.
                    cmLinkable,
                    Optional.of("<!--VTL $include.url('CM-Content','pageid','') VTL-->")
            ),
            Arguments.of(
                    "",
                    new Object(), // unrecognized object
                    Optional.empty()
            )
    );
  }

  @Test
  void resolveUrlForPreview() {
    Product product = buildProduct("external-product-id");
    HttpServletRequest request = buildRequest(true, false, false);

    Optional<String> actual = sfccLinkResolver.resolveUrl("", product, variant, navigation, request);

    assertThat(actual)
            .contains("<!--VTL $include.url('Product-Show','pid','external-product-id','preview','true') VTL-->");
  }

  private static CMExternalPage buildExternalPage(boolean isRoot, String externalId, String externalUriPath) {
    CMExternalPage externalPage = mock(CMExternalPage.class);

    when(externalPage.isRoot()).thenReturn(isRoot);
    when(externalPage.getExternalId()).thenReturn(externalId);
    when(externalPage.getExternalUriPath()).thenReturn(externalUriPath);

    return externalPage;
  }

  private static Product buildProduct(String externalId) {
    Product product = mock(Product.class);

    when(product.getExternalId()).thenReturn(externalId);

    return product;
  }

  private static Category buildCategory(String externalId) {
    Category category = mock(Category.class);

    when(category.getExternalId()).thenReturn(externalId);

    return category;
  }

  private static HttpServletRequest buildRequest(boolean isStudioPreview, boolean isP13nTest, boolean isPreview) {
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getAttribute("isStudioPreview")).thenReturn(String.valueOf(isStudioPreview));
    when(request.getParameter("p13n_test")).thenReturn(String.valueOf(isP13nTest));
    when(request.getParameter("preview")).thenReturn(String.valueOf(isPreview));

    return request;
  }
}
