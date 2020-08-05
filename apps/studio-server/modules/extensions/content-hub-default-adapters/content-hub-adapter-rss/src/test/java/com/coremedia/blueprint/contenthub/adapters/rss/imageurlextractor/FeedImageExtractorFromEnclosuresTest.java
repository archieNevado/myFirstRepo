package com.coremedia.blueprint.contenthub.adapters.rss.imageurlextractor;

import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class FeedImageExtractorFromEnclosuresTest {

  private FeedImageExtractorImpl testling;

  @BeforeEach
  void beforeEach() {
    testling = new FeedImageExtractorImpl();
  }

  @Test
  void extractFromEnclosuresEnclosuresNull() {
    SyndEntry mock = Mockito.mock(SyndEntry.class);
    Set<String> urls = testling.extractFromEnclosures(mock);
    assertThat(urls).isEmpty();
  }

  @Test
  void extractFromEnclosuresNoEnclosures() {
    SyndEntry mock = Mockito.mock(SyndEntry.class);
    when(mock.getEnclosures()).thenReturn(List.of());

    Set<String> urls = testling.extractFromEnclosures(mock);

    assertThat(urls).isEmpty();
  }

  @Test
  void extractFromEnclosuresEnclosureTypeNull() {
    SyndEnclosure enclosure = Mockito.mock(SyndEnclosure.class);
    SyndEntry mock = Mockito.mock(SyndEntry.class);
    when(mock.getEnclosures()).thenReturn(List.of(enclosure));

    Set<String> urls = testling.extractFromEnclosures(mock);

    assertThat(urls).isEmpty();
  }

  @Test
  void extractFromEnclosuresEnclosureTypeNotImage() {
    SyndEnclosure enclosure = Mockito.mock(SyndEnclosure.class);
    when(enclosure.getType()).thenReturn("anyOtherThanImage");
    SyndEntry mock = Mockito.mock(SyndEntry.class);
    when(mock.getEnclosures()).thenReturn(List.of(enclosure));

    Set<String> urls = testling.extractFromEnclosures(mock);

    assertThat(urls).isEmpty();
  }

  @Test
  void extractFromEnclosuresEnclosureUrlNull() {
    SyndEnclosure enclosure = Mockito.mock(SyndEnclosure.class);
    when(enclosure.getType()).thenReturn("image");
    SyndEntry mock = Mockito.mock(SyndEntry.class);
    when(mock.getEnclosures()).thenReturn(List.of(enclosure));

    Set<String> urls = testling.extractFromEnclosures(mock);

    assertThat(urls).isEmpty();
  }

  @Test
  void extractFromEnclosures() {
    SyndEnclosure enclosure = Mockito.mock(SyndEnclosure.class);
    when(enclosure.getType()).thenReturn("image");
    when(enclosure.getUrl()).thenReturn("anyURL");
    SyndEntry mock = Mockito.mock(SyndEntry.class);
    when(mock.getEnclosures()).thenReturn(List.of(enclosure));

    Set<String> urls = testling.extractFromEnclosures(mock);

    assertThat(urls).hasSize(1).contains("anyURL");
  }
}
