package com.coremedia.blueprint.caas.preview.urlservice;

import com.coremedia.blueprint.caas.preview.client.JsonPreviewConfigurationProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class PreviewUrlControllerTest {

  private JsonPreviewConfigurationProperties config;
  private JsonPreviewUrlController previewUrlController;
  private MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

  @Before
  public void init() {
    config = new JsonPreviewConfigurationProperties();
    config.setUrl("http://localhost:8080");
    previewUrlController = new JsonPreviewUrlController(config);
  }

  @Test
  public void previewUrl_article() {
    ResponseEntity<String> responseEntity = previewUrlController.previewUrl("coremedia://cap/content/1234", "CMArticle", mockHttpServletRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(config.getUrl() + "/preview/1234/CMArticle", responseEntity.getBody());
  }

  @Test
  public void previewUrl_page() {
    ResponseEntity<String> responseEntity = previewUrlController.previewUrl("coremedia://cap/content/1234", "CMChannel", mockHttpServletRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(config.getUrl() + "/preview/1234/CMChannel", responseEntity.getBody());
  }

  @Test
  public void previewUrl_invalidId() {
    ResponseEntity<String> responseEntity = previewUrlController.previewUrl("", "CMArticle", mockHttpServletRequest);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  @Test
  public void previewUrl_invalidType() {
    ResponseEntity<String> responseEntity = previewUrlController.previewUrl("1234", "", mockHttpServletRequest);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }
}
