package com.coremedia.blueprint.contenthub.adapters.rss.imageurlextractor;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FeedImageExtractorExtractFromContentsTest {

  private static final String A_URL_WITHOUT_IMAGE_ENDING = "http://asdfasdfsaf.de";
  private static final String A_URL_PNG = "http://asdfasdfsaf.de/anything.png";
  private static final String A_URL_JPG = "http://asdfasdfsaf.de/anything.jpg";
  private static final String A_URL_JPEG = "http://asdfasdfsaf.de/anything.jpeg";
  private static final String A_TEXT_CONTAINING_URL_NO_IMAGE_ENDING = "I have the url src=\"" + A_URL_WITHOUT_IMAGE_ENDING + "\"! and some more stuff";
  private static final String A_TEXT_CONTAINING_URL_PNG = "I have the url src=\"" + A_URL_PNG + "\"! and some more stuff";
  private static final String A_TEXT_CONTAINING_URL_JPG = "I have the url src=\"" + A_URL_JPG + "\"! and some more stuff";
  private static final String A_TEXT_CONTAINING_URL_JPEG = "I have the url src=\"" + A_URL_JPEG + "\"! and some more stuff";
  private FeedImageExtractorImpl testling;

  @BeforeEach
  void beforeEach() {
    testling = new FeedImageExtractorImpl();
  }

  @Test
  void extractFromContentsNothing() {
    SyndEntry syndEntry = mock(SyndEntry.class);
    Set<String> urls = testling.extractFromContents(syndEntry);
    assertThat(urls).isEmpty();
  }

  @Test
  void extractFromDescriptionValueNull() {
    SyndEntry syndEntry = mock(SyndEntry.class);
    SyndContent description = mock(SyndContent.class);
    when(syndEntry.getDescription()).thenReturn(description);
    Set<String> urls = testling.extractFromContents(syndEntry);
    assertThat(urls).isEmpty();
  }

  @Test
  void extractFromDescriptionNoImageEnding() {
    SyndEntry syndEntry = mockDescriptionWithUrl(A_TEXT_CONTAINING_URL_NO_IMAGE_ENDING);

    Set<String> urls = testling.extractFromContents(syndEntry);

    assertThat(urls).isEmpty();
  }

  @Test
  void extractFromDescriptionAllImages() {
    SyndEntry syndEntry = mockDescriptionWithUrl(A_TEXT_CONTAINING_URL_NO_IMAGE_ENDING + A_TEXT_CONTAINING_URL_JPEG + A_TEXT_CONTAINING_URL_JPG + A_TEXT_CONTAINING_URL_PNG);

    Set<String> urls = testling.extractFromContents(syndEntry);

    assertThat(urls).hasSize(3);
  }

  @Test
  void extractFromContentsNullValueAndUrl() {
    SyndEntry syndEntry = mock(SyndEntry.class);
    SyndContent contentNullValue = mock(SyndContent.class);
    SyndContent contentWithValue = mock(SyndContent.class);
    when(contentWithValue.getValue()).thenReturn(A_TEXT_CONTAINING_URL_JPEG);
    when(syndEntry.getContents()).thenReturn(List.of(contentNullValue, contentWithValue));

    Set<String> urls = testling.extractFromContents(syndEntry);

    assertThat(urls).hasSize(1).containsSequence(A_URL_JPEG);
  }

  @NonNull
  private SyndEntry mockDescriptionWithUrl(@NonNull String text) {
    SyndEntry syndEntry = mock(SyndEntry.class);
    SyndContent description = mock(SyndContent.class);
    when(syndEntry.getDescription()).thenReturn(description);
    when(description.getValue()).thenReturn(text);
    return syndEntry;
  }

}
