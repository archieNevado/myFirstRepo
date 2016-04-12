package com.coremedia.lc.studio.lib.augmentation;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.coremedia.rest.cap.intercept.InterceptService;
import com.coremedia.rest.linking.LinkResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.lc.studio.lib.augmentation.CategoryAugmentationResource.CATEGORY_URI_PARAM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XmlRepoConfiguration.class, CategoryAugmentationResourceTest.LocalConfig.class})
public class CategoryAugmentationResourceTest {

  private static final String CATEGORY_ID = "test:///catalog/category/leafCategory";

  @Inject
  private ContentRepository contentRepository;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private CategoryAugmentationResource testling;

  @Before
  public void setup() {
    MockCommerceEnvBuilder.create().setupEnv();
    Commerce.getCurrentConnection().getStoreContext().put(StoreContextBuilder.SITE, "theSiteId");
  }

  @Test
  public void testHandlePost() {
    Map<String, Object> input = new HashMap<>();
    input.put(CATEGORY_URI_PARAM, "livecontext/category/theSiteId/NO_WS/Dairy");

    testling.handlePost(input);

    Content externalChannel = contentRepository.getChild("/Sites/Content Test/Augmentation/root/top/leaf/leaf");
    assertNotNull(externalChannel);
    assertEquals(CATEGORY_ID, externalChannel.getString("externalId"));
  }

  @Configuration
  @ComponentScan(basePackages = {
          "com.coremedia.rest.cap.content",
  })
  @ImportResource(value = {
          "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml"
  },
          reader = com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader.class)
  public static class LocalConfig {

    @Bean
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/lc/studio/lib/validators/lc-studio-lib-test-content.xml");
    }

    @Bean
    CategoryAugmentationResource categoryAugmentationResource() {
      return new CategoryAugmentationResource();
    }

    @Bean
    public InterceptService interceptService() {
      InterceptService interceptService = mock(InterceptService.class);
      ContentWriteRequest contentWriteRequest = mock(ContentWriteRequest.class);
      when(interceptService.interceptCreate(any(Content.class), anyString(), any(ContentType.class), anyMap()))
              .thenReturn(contentWriteRequest);
      when(contentWriteRequest.getProperties()).thenReturn(Collections.<String, Object>singletonMap("externalId", CATEGORY_ID));
      return interceptService;
    }

    @Bean
    public LinkResolver linkResolver() {
      LinkResolver linkResolver = mock(LinkResolver.class);

      //mock category tree
      Category rootCategory = mock(Category.class);
      when(rootCategory.getDisplayName()).thenReturn("root");
      Category topCategory = mock(Category.class);
      when(topCategory.getDisplayName()).thenReturn("top");
      Category leafCategory = mock(Category.class, RETURNS_DEEP_STUBS);
      when(leafCategory.getDisplayName()).thenReturn("leaf");
      when(leafCategory.getId()).thenReturn(CATEGORY_ID);
      List<Category> breadcrumb = new ArrayList<>();
      breadcrumb.add(rootCategory);
      breadcrumb.add(topCategory);
      breadcrumb.add(leafCategory);
      when(leafCategory.getBreadcrumb()).thenReturn(breadcrumb);
      when(leafCategory.getContext().getSiteId()).thenReturn("theSiteId");

      when(linkResolver.resolveLink(anyString())).thenReturn(leafCategory);
      return linkResolver;
    }
  }
}