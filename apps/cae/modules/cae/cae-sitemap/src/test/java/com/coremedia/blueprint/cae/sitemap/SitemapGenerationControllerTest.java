package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.blueprint.base.multisite.cae.SiteResolver;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.datevalidation.ValidationPeriodPredicate;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.objectserver.web.links.LinkScheme;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;

import static com.coremedia.blueprint.cae.sitemap.SitemapGenerationControllerTest.LocalConfig.PROFILE;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.CACHE;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.CONTENT_BEAN_FACTORY;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.DATA_VIEW_FACTORY;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.ID_PROVIDER;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.LINK_FORMATTER;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = SitemapGenerationControllerTest.LocalConfig.class)
@ActiveProfiles(PROFILE)
public class SitemapGenerationControllerTest {
  private static final String SITE_SEGMENT = "theSiteSegment";
  private LinkFormatter linkFormatter;

  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties({
          CaeSitemapConfigurationProperties.class,
          DeliveryConfigurationProperties.class,
  })
  @ImportResource(
          value = {
                  CONTENT_BEAN_FACTORY,
                  DATA_VIEW_FACTORY,
                  ID_PROVIDER,
                  LINK_FORMATTER,
                  CACHE,
                  "classpath:/framework/spring/blueprint-contentbeans.xml",
                  "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml",
                  "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-cae-services.xml",
          },
          reader = ResourceAwareXmlBeanDefinitionReader.class
  )
  @Import(XmlRepoConfiguration.class)
  @Profile(PROFILE)
  public static class LocalConfig {
    public static final String PROFILE = "SitemapGenerationControllerTest";
    private static final String CONTENT_REPOSITORY = "classpath:/com/coremedia/blueprint/cae/sitemap/testcontent.xml";

    @Bean
    @Scope(SCOPE_SINGLETON)
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig(CONTENT_REPOSITORY);
    }
  }

  private SitemapGenerationController testling;

  @Inject
  private MockHttpServletRequest request;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private MockHttpServletResponse response;

  @Inject
  private ContentBeanFactory contentBeanFactory;

  @Inject
  private SiteResolver siteResolver;

  @Inject
  private CaeSitemapConfigurationProperties properties;

  private ValidationService validationServiceAlwaysTrue = new ValidationService() {
    @Override
    public List filterList(List source) {
      return source;
    }

    @Override
    public boolean validate(Object source) {
      return true;
    }
  };
  @Before
  public void setUp() throws Exception {
    linkFormatter = new LinkFormatter();
    linkFormatter.setSchemes(singletonList(new GeneralPurposeLinkScheme()));
    initTestling(validationServiceAlwaysTrue, List.of());
  }

  private void initTestling(ValidationService validationService, List<String> exclusionPaths) {
    SitemapSetup sitemapSetup = new SitemapSetup(properties);
    sitemapSetup.setSitemapRendererFactory(new PlainSitemapRendererFactory());
    List<Predicate<Content>> predicates = List.of(content -> {
      var bean = contentBeanFactory.createBeanFor(content, CMLinkable.class);
      return bean != null && new ValidationPeriodPredicate(Calendar.getInstance()).test(bean);
    });
    var generator = new ContentUrlGenerator(linkFormatter, contentBeanFactory, validationService, exclusionPaths, predicates);
    sitemapSetup.setUrlGenerators(List.of(generator));

    testling = new SitemapGenerationController(siteResolver, site -> sitemapSetup);
  }

  @Test
  public void testNoParams() throws Exception {
    request.addParameter(SitemapRequestParams.PARAM_EXCLUDE_FOLDERS, (String) null);

    testling.handleRequestInternal(SITE_SEGMENT, request, response);

    List<String> urlList = convertToList();
    assertNotNull(urlList);
    assertEquals(7, urlList.size());
  }

  @Test
  public void testGzipParam() throws Exception {
    request.addParameter(SitemapRequestParams.PARAM_EXCLUDE_FOLDERS, (String) null);
    request.addParameter(SitemapRequestParams.PARAM_GZIP_COMPRESSION, "true");

    testling.handleRequestInternal(SITE_SEGMENT, request, response);

    List<String> urlList = convertGzipToList();
    assertNotNull(urlList);
    assertEquals(7, urlList.size());
  }

  @Test
  public void testNoSuchSite() throws Exception {
    request.addParameter(SitemapRequestParams.PARAM_EXCLUDE_FOLDERS, (String) null);

    testling.handleRequestInternal("noSuchSite", request, response);

    assertThat(response.getStatus(), Matchers.equalTo(HttpServletResponse.SC_NOT_FOUND));
  }

  @Test
  public void testParamExcludeFolders() throws Exception {
    request.addParameter(SitemapRequestParams.PARAM_EXCLUDE_FOLDERS, "Contact");

    testling.handleRequestInternal(SITE_SEGMENT, request, response);

    List<String> urlList = convertToList();

    assertNotNull(urlList);
    assertEquals(6, urlList.size());
  }

  @Test
  public void testParamExcludeMultipleFolders() throws Exception {
    request.addParameter(SitemapRequestParams.PARAM_EXCLUDE_FOLDERS, "Contact,Navigation");

    testling.handleRequestInternal(SITE_SEGMENT, request, response);

    List<String> urlList = convertToList();

    assertNotNull(urlList);
    assertEquals(5, urlList.size());
  }

  @Test
  public void testParamExclusionPaths() throws Exception {
    request.addParameter(SitemapRequestParams.PARAM_EXCLUDE_FOLDERS, (String) null);
    initTestling(validationServiceAlwaysTrue, List.of("About/Contact"));

    testling.handleRequestInternal(SITE_SEGMENT, request, response);

    List<String> urlList = convertToList();

    assertNotNull(urlList);
    assertEquals(6, urlList.size());
  }

  @Test
  public void testValidationService() throws Exception {
    ValidationService validationServiceRemoveCMChannel = new ValidationService() {
      @Override
      public List filterList(List list) {
        for (int i = 0; i < list.size(); i++) {
          Object o = list.get(i);
          if (o instanceof CMChannel) {
            list.remove(o);
          }
        }
        return list;
      }

      @Override
      public boolean validate(Object source) {
        return !(source instanceof CMChannel);
      }
    };
    initTestling(validationServiceRemoveCMChannel, List.of());
    request.addParameter(SitemapRequestParams.PARAM_EXCLUDE_FOLDERS, (String) null);

    testling.handleRequestInternal(SITE_SEGMENT, request, response);

    List<String> urlList = convertToList();
    assertNotNull(urlList);
    assertEquals(6, urlList.size());
  }

  /**
   * Link scheme for tests. This link scheme renders links for all content beans with the pattern
   * http://www.coremedia.com/<content type>/<content id>
   */
  class GeneralPurposeLinkScheme implements LinkScheme {

    @Nullable
    @Override
    public String formatLink(Object bean, String view, @NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response, boolean forRedirect) throws URISyntaxException {
      ContentBean contentBean = (ContentBean) bean;
      StringBuilder stringBuilder = new StringBuilder("http://www.coremedia.com/");
      stringBuilder.append(contentBean.getContent().getType().getName()).append("/").append(IdHelper.parseContentId(contentBean.getContent().getId()));

      return stringBuilder.toString();
    }
  }

  /**
   * Convert output list to a list object to verify the results.
   *
   * @return A list where each entry contains one line of the print writer.
   */
  private List<String> convertToList() throws UnsupportedEncodingException {
    return asList(response.getContentAsString());
  }

  /**
   * Converts line separated string to an array.
   *
   * @param value A list of values, separated by linefeed.
   * @return The array of each line.
   */
  private List<String> asList(String value) {
    Scanner scanner = new Scanner(value);
    List<String> result = new ArrayList<>();

    while (scanner.hasNextLine()) {
      result.add(scanner.nextLine());
    }

    return result;
  }

  /**
   * Convert output list to a list object to verify the results.
   *
   * @return A list where each entry contains one line of the print writer.
   */
  private List<String> convertGzipToList() throws IOException {
    GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(response.getContentAsByteArray()));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (int value = 0; value != -1; ) {
      value = gzipInputStream.read();
      if (value != -1) {
        baos.write(value);
      }
    }
    gzipInputStream.close();
    baos.close();
    return asList(new String(baos.toByteArray(), StandardCharsets.UTF_8));
  }
}
