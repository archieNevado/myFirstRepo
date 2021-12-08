package com.coremedia.blueprint.cae.richtext.filter;

import com.coremedia.blueprint.cae.config.BlueprintRichtextFiltersConfiguration;
import com.coremedia.blueprint.testing.ContentTestConfiguration;
import com.coremedia.blueprint.testing.ContentTestHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.objectserver.view.TextView;
import com.coremedia.objectserver.view.ViewDispatcher;
import com.coremedia.objectserver.view.ViewUtils;
import com.coremedia.xml.ExtendedContentHandler;
import com.coremedia.xml.Filter;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static com.coremedia.blueprint.cae.richtext.filter.LinkEmbedFilterTest.LocalConfig.PROFILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = LinkEmbedFilterTest.LocalConfig.class)
@ActiveProfiles(PROFILE)
public class LinkEmbedFilterTest {
  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties({
          DeliveryConfigurationProperties.class
  })
  @Import({
          BlueprintRichtextFiltersConfiguration.class,
          ContentTestConfiguration.class,
  })
  @Profile(PROFILE)
  public static class LocalConfig {
    public static final String PROFILE = "LinkEmbedFilterTest";
    private static final String CONTENT_REPOSITORY = "classpath:/com/coremedia/blueprint/cae/richtext/filter/linkembedfiltertest-content.xml";

    @Bean
    @Scope(SCOPE_SINGLETON)
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig(CONTENT_REPOSITORY);
    }
  }

  private static final String HEADER = "<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n";
  private static final String FOOTER = "\n</div>";
  private static final String EMBEDDED_BLOCK = "<div class=\"embedded\"><p>embedded block snippet</p></div>";
  private static final String EMBEDDED_FLOW = "<span class=\"richTextImage\"><p>embedded flow snippet</p></span>";

  private List<Filter> newXmlFilters = new ArrayList<>();
  private LinkEmbedFilter testling;

  @Inject
  private LinkEmbedFilter linkEmbedFilter;

  @Inject
  private MockHttpServletRequest request;

  @Inject
  private MockHttpServletResponse response;

  @Inject
  private ContentTestHelper contentTestHelper;

  // --- setup ------------------------------------------------------

  @Before
  public void setUp() {
    ViewDispatcher viewDispatcher = mock(ViewDispatcher.class);
    when(viewDispatcher.getView(any(Content.class), any(String.class))).thenReturn(new DivTextView());

    request.setAttribute(ViewUtils.VIEWDISPATCHER, viewDispatcher);

    testling = (LinkEmbedFilter) linkEmbedFilter.getInstance(request, response);
    newXmlFilters.add(testling);
  }

  // --- tests ------------------------------------------------------

  @Test
  public void testBlockIntoDiv() {
    String result = getResult(248);
    String expected = HEADER +
            EMBEDDED_BLOCK +
            FOOTER;
    assertEquals("testBlockIntoDiv", expected, result);
  }

  /**
   * Ensure that a empty p which is not affected by link embedding
   * is not omitted by the LinkEmbedFilter.
   */
  @Test
  public void testDontTouchNotAffectedP() {
    String result = getResult(260);
    // If the source xml does not contain a link at all, Markup omits the
    // xlink namespace declaration.
    String expected = "<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\">\n" +
            "<p class=\"orig\"></p>" +
            FOOTER;
    assertEquals("testDontTouchNotAffectedP", expected, result);
  }

  @Test
  public void testFlowIntoDiv() {
    String result = getResult(252);
    String expected = HEADER +
            EMBEDDED_FLOW +
            FOOTER;
    assertEquals("testFlowIntoDiv", expected, result);
  }

  /**
   * Regression test for CMS-343:
   * This shows that the question of short tags is not subject of the LinkEmbedFilter
   * but of the invoking infrastructure.
   */
  @Test
  public void regressionTestCMS343() {
    String result = getResult(262);
    String expected = "<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\">\n" +
            "  <div id=\"outer\">\n" +
            "    TEXT1\n" +
            "    <div id=\"inner\"></div>\n" +  // Subject of this test: No short tag here!
            "    TEXT2\n" +
            "  </div>\n" +
            "</div>";
    assertEquals("regressionTestCMS-343", expected, result);
  }


  // --- internal ---------------------------------------------------

  private String getResult(int id) {
    Markup markup = contentTestHelper.getContent(id).getMarkup("detailText");
    StringWriter stringWriter = new StringWriter();
    // Using an XHTML serializer is responsible for XHTML compliant
    // handling of empty divs and spans.  The LinkEmbedFilter has nothing
    // to do with it.
    ExtendedContentHandler xhtmlWriter = MarkupFactory.newXhtmlSerializer(stringWriter);
    markup.writeOn(newXmlFilters, xhtmlWriter);
    return stringWriter.toString();
  }

  /**
   * @ maintainer:
   * This must always match LinkEmbedFilter#hasBlockLevelView
   */
  private boolean embedAsFlow(Object bean) {
    return "CMPicture".equals(((Content) bean).getType().getName());
  }

  /**
   * This is a dummy view for simulating the rendering process.
   */
  private class DivTextView implements TextView {
    @SuppressWarnings("ProhibitedExceptionThrown")
    @Override
    public void render(Object bean, String view, Writer out, @NonNull HttpServletRequest request,
                       @NonNull HttpServletResponse response) {
      try {
        if (embedAsFlow(bean)) {
          out.write(EMBEDDED_FLOW);
        } else {
          out.write(EMBEDDED_BLOCK);
        }
      } catch (IOException e) {
        throw new RuntimeException("Test is broken, not a product bug", e);
      }
    }
  }
}

