package com.coremedia.livecontext.ecommerce.sfcc.push;

import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccStoreContextProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.stubbing.defaultanswers.ReturnsDeepStubs;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.coremedia.livecontext.ecommerce.sfcc.push.SfccContentHelper.AUGEMENTED_CATEGEORY_TYPE;
import static com.coremedia.livecontext.ecommerce.sfcc.push.SfccContentHelper.AUGEMENTED_PRODUCT_TYPE;
import static com.coremedia.livecontext.ecommerce.sfcc.push.SfccContentHelper.EXTERNAL_PAGE_TYPE;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SfccContentHelperTest.LocalConfig.class)
@TestPropertySource(properties = {"livecontext.cache.invalidation.enabled=false"})
class SfccContentHelperTest {

  private static final String CATEGORY_ID = "sfcc:///catalog/category/womens-accessories-shoes";
  private static final String PRODUCT_ID = "sfcc:///catalog/product/TG250";

  @Inject
  SfccContentHelper sfccContentHelper;

  @SpyBean
  FetchContentUrlHelper fetchContentUrlHelper;

  @Test
  void testComputePageKey() {
    //category
    Category category = mock(Category.class, new ReturnsDeepStubs());
    given(category.getId().getExternalId()).willReturn(of("wurst"));
    String categoryPageKey = sfccContentHelper.computePageKey(category);
    assertThat(categoryPageKey).isEqualTo("externalRef=;categoryId=wurst;productId=;pageId=");

    //product
    Product product = mock(Product.class, new ReturnsDeepStubs());
    given(product.getId().getExternalId()).willReturn(of("bratwurst"));
    String productPageKey = sfccContentHelper.computePageKey(product);
    assertThat(productPageKey).isEqualTo("externalRef=;categoryId=;productId=bratwurst;pageId=");

    //external page
    Content externalPage = mock(Content.class, new ReturnsDeepStubs());
    given(externalPage.getType().isSubtypeOf(EXTERNAL_PAGE_TYPE)).willReturn(true);
    when(externalPage.getString("externalId")).thenReturn("about-us");
    String externalPagePageKey = sfccContentHelper.computePageKey(externalPage);
    assertThat(externalPagePageKey).isEqualTo("externalRef=;categoryId=;productId=;pageId=about-us");

    //augmented categpry
    Content augmentedCategory = mock(Content.class, new ReturnsDeepStubs());
    given(augmentedCategory.getType().isSubtypeOf(AUGEMENTED_CATEGEORY_TYPE)).willReturn(true);
    when(augmentedCategory.getString("externalId")).thenReturn(CATEGORY_ID);
    String augmentedCategoryPageKey = sfccContentHelper.computePageKey(augmentedCategory);
    assertThat(augmentedCategoryPageKey).isEqualTo("externalRef=;categoryId=womens-accessories-shoes;productId=;pageId=");

    //augmented product
    Content augmentedProduct = mock(Content.class, new ReturnsDeepStubs());
    given(augmentedProduct.getType().isSubtypeOf(AUGEMENTED_PRODUCT_TYPE)).willReturn(true);
    when(augmentedProduct.getString("externalId")).thenReturn(PRODUCT_ID);
    String augmentedProductPageKey = sfccContentHelper.computePageKey(augmentedProduct);
    assertThat(augmentedProductPageKey).isEqualTo("externalRef=;categoryId=;productId=TG250;pageId=");

    //other content
    Content article = mock(Content.class, new ReturnsDeepStubs());
    given(augmentedProduct.getType().isSubtypeOf("CMLinkable")).willReturn(true);
    doReturn(of("articleSeo")).when(fetchContentUrlHelper).getSeoSegment(article);
    String articlePageKey = sfccContentHelper.computePageKey(article);
    assertThat(articlePageKey).isEqualTo("externalRef=cm-seosegment:articleSeo;categoryId=;productId=;pageId=");
  }

  @Configuration
  @ComponentScan(basePackageClasses = SfccStoreContextProperties.class)
  public static class LocalConfig {

  }
}
