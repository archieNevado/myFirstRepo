package com.coremedia.blueprint.cae.richtext.filter;

import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.id.IdProvider;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.objectserver.web.links.UriFormatter;
import com.coremedia.xml.Filter;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LinkValidationFilterTest {
  private static final String DIV_NS = "<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">";
  private static final String CONTENT_LINK = "<a xlink:href=\"coremedia:///cap/content/242\">linktext</a>";
  private static final String EXTERNAL_LINK = "<a xlink:href=\"http://www.coremedia.com/web-content-management/-/6164/6164/-/_axt0z/-/index.html\">linktext</a>";

  @Mock
  private IdProvider idProvider;

  @Mock
  private ValidationService<Object> validationService;

  @Mock
  private ContentBean contentBean;

  private LinkValidationFilter testling;
  private UriFormatter uriFormatter;

  @Before
  public void setup() throws URISyntaxException {
    testling = new LinkValidationFilter();
    testling.setIdProvider(idProvider);
    testling.setValidationService(validationService);
    var linkFormatter = new LinkFormatter();
    var request = new MockHttpServletRequest();
    var response = new MockHttpServletResponse();
    uriFormatter = new UriFormatter(linkFormatter, idProvider, request, response);
  }

  @Test
  public void testNothing() {
    String tail = "<p>hello</p></div>";
    String result = doFilter(DIV_NS + tail);
    // Cannot rely on the order of the namespace declarations,
    // exclude the opening <div> from the check.
    assertThat(result).endsWith(tail);
  }

  @Test
  public void testValidLink() {
    when(idProvider.parseId("coremedia:///cap/content/242")).thenReturn(contentBean);
    when(validationService.validate(contentBean)).thenReturn(true);

    String tail = "<p>" + CONTENT_LINK + "</p></div>";
    String result = doFilter(DIV_NS + tail);
    assertThat(result).endsWith(tail);
  }

  @Test
  public void testInvalidLink() {
    when(idProvider.parseId("coremedia:///cap/content/242")).thenReturn(contentBean);
    when(validationService.validate(contentBean)).thenReturn(false);
    String tail = "<p>" + CONTENT_LINK + "</p></div>";
    String result = doFilter(DIV_NS + tail);
    assertThat(result).endsWith("<p>linktext</p></div>");
    testling.setRenderLinkText(false);
    tail = "<p>foo" + CONTENT_LINK + "</p>bar</div>";
    result = doFilter(DIV_NS + tail);
    assertThat(result).endsWith("<p>foo</p>bar</div>");
  }

  @Test
  public void testBrokenLink() {
    String tail = "<p>" + CONTENT_LINK + "</p></div>";
    String result = doFilter(DIV_NS + tail, List.of(testling, uriFormatter));
    assertThat(result).endsWith("<p>linktext</p></div>");
  }

  @Test
  public void testBrokenLinkPreview() {
    testling.setPreviewMode(true);
    assertThatThrownBy(() ->
            doFilter(DIV_NS + ("<p>" + CONTENT_LINK + "</p></div>"), List.of(testling, uriFormatter)))
            .isInstanceOf(RuntimeException.class);
  }

  @Test
  public void testExternalLink() {
    String tail = "<p>" + EXTERNAL_LINK + "</p></div>";
    String result = doFilter(DIV_NS + tail);
    assertThat(result).endsWith(tail);
  }

  // --- internal ---------------------------------------------------

  private String doFilter(String text) {
    return doFilter(text, List.of(testling));
  }

  private String doFilter(String text, List<Filter> filters) {
    Markup markup = MarkupFactory.fromString(text);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    markup.writeOn(filters, bos);
    return bosToString(bos);
  }

  private static String bosToString(ByteArrayOutputStream bos) {
    try {
      return bos.toString("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new Error("UTF-8 must be supported!");
    }
  }
}

