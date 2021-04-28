package com.coremedia.blueprint.elastic.social.rest;

import com.coremedia.cap.content.Content;
import com.coremedia.elastic.core.cms.ContentWithSite;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.rest.cap.content.ContentRepositoryResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import static com.coremedia.elastic.social.rest.api.JsonProperties.PREVIEW_URL;
import static com.coremedia.elastic.social.rest.api.JsonProperties.SUBJECT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommentJsonCustomizerTest {

  @Mock
  private ContentRepositoryResource contentRepositoryResource;

  @InjectMocks
  private CommentJsonCustomizer customizer;

  @Test
  public void testCustomizeForList() {
    Comment comment = mock(Comment.class);
    when(comment.getTarget()).thenReturn("target");
    HashMap<String, Object> serializedObject = new HashMap<>();
    serializedObject.put(PREVIEW_URL, "/preview?id=4711");

    customizer.customize(comment, serializedObject);

    assertEquals(1, serializedObject.size());
    assertEquals("/preview?id=4711", serializedObject.get(PREVIEW_URL));
  }

  @Test
  public void testCustomizeWithoutParameter() {
    Comment comment = mock(Comment.class);
    when(comment.getTarget()).thenReturn("target");
    HashMap<String, Object> serializedObject = new HashMap<>();
    serializedObject.put(PREVIEW_URL, "/preview");

    customizer.customize(comment, serializedObject);

    assertEquals(1, serializedObject.size());
    assertEquals("/preview", serializedObject.get(PREVIEW_URL));
  }

  @Test
  public void testCustomizeForListSiteContentTarget() {
    when(contentRepositoryResource.getPreviewControllerUrlPattern()).thenReturn("/preview?id={0}");
    ContentWithSite target = mock(ContentWithSite.class);
    Content content = mock(Content.class);
    when(target.getContent()).thenReturn(content);
    when(content.getString("title")).thenReturn("foo");
    when(content.getId()).thenReturn("4711");
    Comment comment = mock(Comment.class);
    when(comment.getTarget()).thenReturn(target);
    HashMap<String, Object> serializedObject = new HashMap<>();

    customizer.customize(comment, serializedObject);

    assertEquals(2, serializedObject.size());
    assertEquals("foo", serializedObject.get(SUBJECT));
    assertEquals("/preview?id=4711", serializedObject.get(PREVIEW_URL));
  }
}
