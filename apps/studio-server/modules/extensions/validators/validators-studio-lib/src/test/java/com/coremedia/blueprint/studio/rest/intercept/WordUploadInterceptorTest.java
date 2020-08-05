package com.coremedia.blueprint.studio.rest.intercept;

import com.coremedia.blueprint.taxonomies.TaxonomyConfiguration;
import com.coremedia.blueprint.taxonomies.TaxonomyResolver;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.undoc.common.spring.CapRepositoriesConfiguration;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.coremedia.rest.cap.intercept.impl.ContentWriteRequestImpl;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import com.coremedia.xml.Markup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {CapRepositoriesConfiguration.class})
@TestPropertySource(properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:/com/coremedia/blueprint/studio/rest/intercept/WordUploadInterceptorTest-content.xml",
        "repository.params.userxml=classpath:/com/coremedia/testing/usertest.xml",
})
@ImportResource(value = {"classpath:/com/coremedia/cap/multisite/multisite-services.xml"},
        reader = ResourceAwareXmlBeanDefinitionReader.class)
@Import({TaxonomyConfiguration.class})
public class WordUploadInterceptorTest {
  private Blob wordDocument;

  @Inject
  private ContentRepository repository;

  @Inject
  private SitesService sitesService;

  @Inject
  private TaxonomyResolver taxonomyResolver;

  @Before
  public void setUp() throws Exception {
    wordDocument = repository.getConnection().getBlobService().fromInputStream(WordUploadInterceptorTest.class.getResourceAsStream("upload-test.docx"), WordUploadInterceptor.DOCX_MIMETYPE);
  }

  @Test
  public void test() throws Exception {
    WordUploadInterceptor testling = new WordUploadInterceptor(repository, sitesService, taxonomyResolver, Collections.emptyList());

    Map<String, Object> properties = new HashMap<>();
    properties.put(WordUploadInterceptor.DATA_PROPERTY, wordDocument);

    Content picture = repository.getChild("/Sites/Site/Article");
    ContentWriteRequest contentWriteRequest = new ContentWriteRequestImpl(picture, picture.getParent(), picture.getName(),
            picture.getType(), properties, null);
    testling.intercept(contentWriteRequest);

    assertTrue(properties.containsKey("detailText"));
    assertEquals(((List) properties.get("pictures")).size(), 2);

    Markup detailText = (Markup) properties.get("detailText");
    String markup = detailText.asXml();

    assertTrue(markup.contains("xlink:href=\"http://www.google.de/\""));
    assertTrue(markup.contains("<table>"));
    assertTrue(markup.contains("<img"));
  }

}
