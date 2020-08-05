package com.coremedia.blueprint.contenthub.adapters.rss.imageurlextractor;

import com.rometools.modules.mediarss.MediaEntryModule;
import com.rometools.rome.feed.synd.SyndEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class FeedImageExtractorFromMediaModulesTest {

  private FeedImageExtractorImpl testling;

  @BeforeEach
  void beforeEach() {
    testling = spy(new FeedImageExtractorImpl());
  }

  @Test
  void extractModulesAreNull() {
    SyndEntry mock = mock(SyndEntry.class);
    when(mock.getModules()).thenReturn(null);
    Set<String> urls = testling.extractFromMediaModules(mock);
    assertThat(urls).isEmpty();
  }

  @Test
  void extractModulesAreEmpty() {
    SyndEntry syndEntry = mock(SyndEntry.class);
    when(syndEntry.getModules()).thenReturn(List.of());

    Set<String> urls = testling.extractFromMediaModules(syndEntry);

    assertThat(urls).isEmpty();
  }

  @Test
  void extractModules() {
    SyndEntry syndEntry = mock(SyndEntry.class);
    MediaEntryModule module = mock(MediaEntryModule.class);
    when(syndEntry.getModules()).thenReturn(List.of(module));
    Set<String> extractFromMediaModule = new LinkedHashSet<>();
    extractFromMediaModule.add("firstUrl");
    extractFromMediaModule.add("secondUrl");
    doReturn(extractFromMediaModule).when(testling).extractFromMediaModule(module);

    Set<String> urls = testling.extractFromMediaModules(syndEntry);

    assertThat(urls).hasSize(2).containsSequence("firstUrl", "secondUrl");
  }
}
