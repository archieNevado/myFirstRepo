package com.coremedia.blueprint.contenthub.adapters.rss.imageurlextractor;

import com.rometools.modules.mediarss.MediaEntryModule;
import com.rometools.modules.mediarss.types.MediaContent;
import com.rometools.modules.mediarss.types.MediaGroup;
import com.rometools.modules.mediarss.types.Metadata;
import com.rometools.modules.mediarss.types.Reference;
import com.rometools.modules.mediarss.types.Thumbnail;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class FeedImageExtractorFromMediaModuleTest {
  private static final String ONE_URI = "http://anyurl.com";
  private static final String ANOTHER_URI = "http://anotheruri.com";

  private FeedImageExtractorImpl testling;

  @BeforeEach
  void beforeEach() {
    testling = spy(new FeedImageExtractorImpl());
  }

  @Test
  void extractFromMediaModule() {
    MediaEntryModule module = mock(MediaEntryModule.class);
    doReturn(Set.of("firstResult")).when(testling).extractMetadataThumbnailUrls(module);
    doReturn(Set.of("secondResult")).when(testling).extractFromMediaContents(module);
    doReturn(Set.of("thirdResult")).when(testling).extractFromMediaGroups(module);

    Set<String> allUrls = testling.extractFromMediaModule(module);

    assertThat(allUrls).hasSize(3).containsSequence("firstResult", "secondResult", "thirdResult");
  }

  @Test
  void extractMetadataThumbnailUrlsMetadataNull() {
    MediaEntryModule module = mock(MediaEntryModule.class);

    Set<String> urls = testling.extractMetadataThumbnailUrls(module);

    assertThat(urls).isEmpty();
  }

  @Test
  void extractMetadataThumbnailUrlsThumbnailsNull() {
    MediaEntryModule module = mock(MediaEntryModule.class);
    when(module.getMetadata()).thenReturn(mock(Metadata.class));

    Set<String> urls = testling.extractMetadataThumbnailUrls(module);

    assertThat(urls).isEmpty();
  }

  @Test
  void extractMetadataThumbnailUrlsThumbnails() throws URISyntaxException {
    MediaEntryModule module = mock(MediaEntryModule.class);
    Metadata mock = mock(Metadata.class);
    when(module.getMetadata()).thenReturn(mock);

    Thumbnail[] thumbnails = getThumbnails(ONE_URI, ANOTHER_URI);
    when(mock.getThumbnail()).thenReturn(thumbnails);

    Set<String> urls = testling.extractMetadataThumbnailUrls(module);

    assertThat(urls).hasSize(2).containsSequence(ONE_URI, ANOTHER_URI);
  }

  @Test
  void extractFromMediaContentContentsNull() {
    MediaEntryModule module = mock(MediaEntryModule.class);
    Set<String> urls = testling.extractFromMediaContents(module);

    assertThat(urls).isEmpty();
  }

  @Test
  void extractFromMediaContentNoContents() {
    MediaEntryModule module = mock(MediaEntryModule.class);
    when(module.getMediaContents()).thenReturn(new MediaContent[]{});
    Set<String> urls = testling.extractFromMediaContents(module);

    assertThat(urls).isEmpty();
  }

  @Test
  void extractFromMediaContentDifferentOnes() throws URISyntaxException {
    MediaEntryModule module = mock(MediaEntryModule.class);
    MediaContent[] mediaContents = {
            mockMediaContentWithoutMetadata(),
            mockMediaContentWithThumbnailNull(),
            mockMediaContentWithThumbnailEmpty(),
            mockMediaContentWithThumbnailUrl()
    };
    when(module.getMediaContents()).thenReturn(mediaContents);
    Set<String> urls = testling.extractFromMediaContents(module);

    assertThat(urls).hasSize(1).containsSequence(ONE_URI);
  }

  @Test
  void extractFromMediaGroupsMediaGroupsNull() {
    MediaEntryModule module = mock(MediaEntryModule.class);

    Set<String> urls = testling.extractFromMediaGroups(module);

    assertThat(urls).isEmpty();
  }

  @Test
  void extractFromMediaGroupsMediaGroupsEmpty() {
    MediaEntryModule module = mock(MediaEntryModule.class);
    when(module.getMediaGroups()).thenReturn(new MediaGroup[]{});
    Set<String> urls = testling.extractFromMediaGroups(module);

    assertThat(urls).isEmpty();
  }

  @Test
  void extractFromMediaGroupsMediaContentsNull() {
    MediaEntryModule module = mock(MediaEntryModule.class);
    when(module.getMediaGroups()).thenReturn(new MediaGroup[]{
            mock(MediaGroup.class)
    });

    Set<String> urls = testling.extractFromMediaGroups(module);

    assertThat(urls).isEmpty();
  }

  @Test
  void extractFromMediaGroupsMediaContentsEmpty() {
    MediaEntryModule module = mock(MediaEntryModule.class);
    MediaGroup mediaGroup = mock(MediaGroup.class);

    when(module.getMediaGroups()).thenReturn(new MediaGroup[]{
            mediaGroup
    });
    when(mediaGroup.getContents()).thenReturn(new MediaContent[]{});

    Set<String> urls = testling.extractFromMediaGroups(module);

    assertThat(urls).isEmpty();
  }

  @Test
  void extractFromMediaGroupsMediaContentsTypeNull() {
    MediaEntryModule module = mock(MediaEntryModule.class);
    MediaGroup mediaGroup = mock(MediaGroup.class);

    when(module.getMediaGroups()).thenReturn(new MediaGroup[]{
            mediaGroup
    });
    when(mediaGroup.getContents()).thenReturn(new MediaContent[]{
            mock(MediaContent.class)
    });

    Set<String> urls = testling.extractFromMediaGroups(module);

    assertThat(urls).isEmpty();
  }


  @Test
  void extractFromMediaGroupsMediaContentsReferenceNull() {
    MediaEntryModule module = mock(MediaEntryModule.class);
    MediaGroup mediaGroup = mock(MediaGroup.class);

    when(module.getMediaGroups()).thenReturn(new MediaGroup[]{
            mediaGroup
    });
    MediaContent mediaContent = mock(MediaContent.class);
    when(mediaContent.getType()).thenReturn("image");
    when(mediaGroup.getContents()).thenReturn(new MediaContent[]{
            mediaContent
    });

    Set<String> urls = testling.extractFromMediaGroups(module);

    assertThat(urls).isEmpty();
  }

  @Test
  void extractFromMediaGroupsMediaContentsTypeNotImage() {
    MediaEntryModule module = mock(MediaEntryModule.class);
    MediaGroup mediaGroup = mock(MediaGroup.class);

    when(module.getMediaGroups()).thenReturn(new MediaGroup[]{
            mediaGroup
    });
    MediaContent mediaContent = mock(MediaContent.class);
    when(mediaContent.getType()).thenReturn("notimage");
    when(mediaGroup.getContents()).thenReturn(new MediaContent[]{
            mediaContent
    });

    Set<String> urls = testling.extractFromMediaGroups(module);

    assertThat(urls).isEmpty();
  }

  @Test
  void extractFromMediaGroups() {
    MediaEntryModule module = mock(MediaEntryModule.class);
    MediaGroup mediaGroup = mock(MediaGroup.class);

    when(module.getMediaGroups()).thenReturn(new MediaGroup[]{
            mediaGroup
    });
    MediaContent mediaContent = mock(MediaContent.class);
    when(mediaContent.getType()).thenReturn("image");
    Reference mock = mock(Reference.class);
    when(mediaContent.getReference()).thenReturn(mock);
    when(mock.toString()).thenReturn(ONE_URI);
    when(mediaGroup.getContents()).thenReturn(new MediaContent[]{
            mediaContent
    });

    Set<String> urls = testling.extractFromMediaGroups(module);

    assertThat(urls).hasSize(1)
            .containsSequence(ONE_URI);
  }

  @NonNull
  private MediaContent mockMediaContentWithThumbnailUrl() throws URISyntaxException {
    MediaContent mock = mock(MediaContent.class);
    Metadata metadata = mock(Metadata.class);
    when(mock.getMetadata()).thenReturn(metadata);
    Thumbnail[] thumbnails = {
            getThumbnail(ONE_URI)
    };
    when(metadata.getThumbnail()).thenReturn(thumbnails);
    return mock;
  }

  @NonNull
  private MediaContent mockMediaContentWithThumbnailEmpty() {
    MediaContent mock = mock(MediaContent.class);
    Metadata metadata = mock(Metadata.class);
    when(mock.getMetadata()).thenReturn(metadata);
    when(metadata.getThumbnail()).thenReturn(new Thumbnail[]{});
    return mock;
  }

  @NonNull
  private MediaContent mockMediaContentWithThumbnailNull() {
    MediaContent mock = mock(MediaContent.class);
    when(mock.getMetadata()).thenReturn(mock(Metadata.class));
    return mock;
  }

  @NonNull
  private MediaContent mockMediaContentWithoutMetadata() {
    return mock(MediaContent.class);
  }

  @NonNull
  private Thumbnail[] getThumbnails(@NonNull String uri1,
                                    @NonNull String uri2) throws URISyntaxException {
    Thumbnail thumbnail = getThumbnail(uri1);
    Thumbnail thumbnail2 = getThumbnail(uri2);
    return new Thumbnail[] {
      thumbnail, thumbnail2
    };
  }

  @NonNull
  private Thumbnail getThumbnail(@NonNull String uri1) throws URISyntaxException {
    Thumbnail thumbnail = mock(Thumbnail.class);
    when(thumbnail.getUrl()).thenReturn(new URI(uri1));
    return thumbnail;
  }
}
