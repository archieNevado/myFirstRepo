package com.coremedia.blueprint.contenthub.adapters.rss.imageurlextractor;


import com.rometools.modules.mediarss.MediaEntryModule;
import com.rometools.modules.mediarss.types.MediaContent;
import com.rometools.modules.mediarss.types.MediaGroup;
import com.rometools.modules.mediarss.types.Metadata;
import com.rometools.modules.mediarss.types.Thumbnail;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class FeedImageExtractorImpl implements FeedImageExtractor {

  @NonNull
  @Override
  public List<String> extractImageUrls(@NonNull SyndEntry entry) {
    Set<String> result = new LinkedHashSet<>(extractFromEnclosures(entry));
    result.addAll(extractFromMediaModules(entry));
    result.addAll(extractFromContents(entry));
    return new ArrayList<>(result);
  }

  Set<String> extractFromMediaModules(@NonNull SyndEntry entry) {
    List<Module> modules = entry.getModules();
    if (modules == null) {
      return new LinkedHashSet<>();
    }

    List<MediaEntryModule> collect = modules.stream()
            .filter(MediaEntryModule.class::isInstance)
            .map(MediaEntryModule.class::cast)
            .collect(Collectors.toList());

    Set<String> urls = new LinkedHashSet<>();
    collect.stream()
            .map(this::extractFromMediaModule)
            .forEach(urls::addAll);
    return urls;
  }

  /**
   * Searches the HTML contents of the feed for image references
   * and extracts them via regular expression.
   * @param entry the feed entry
   * @return Set<String> result the list of extracted images
   */
  @NonNull
  Set<String> extractFromContents(@NonNull SyndEntry entry) {
    StringBuilder text = new StringBuilder();
    Optional<String> descriptionText = extractDescriptionText(entry);
    descriptionText.ifPresent(text::append);

    List<SyndContent> contents = entry.getContents();
    if (contents != null && !contents.isEmpty()) {
      for (SyndContent content : contents) {
        String value = content.getValue();
        if (value != null) {
          text.append(value);
        }
      }
    }

    return findUrlsInText(text.toString());
  }

  @NonNull
  private Set<String> findUrlsInText(@NonNull String text) {
    String imgRegex = "src\\s*=\\s*([\\\"'])?([^ \\\"']*)";
    Pattern p = Pattern.compile(imgRegex, Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(text);
    Set<String> result = new LinkedHashSet<>();
    while (m.find()) {
      String imageUrl = m.group(2);
      if (imageUrl.contains(".png") || imageUrl.contains(".jpg") || imageUrl.contains(".jpeg")) {
        result.add(imageUrl);
      }
    }
    return result;
  }

  @NonNull
  private static Optional<String> extractDescriptionText(@NonNull SyndEntry entry) {
    SyndContent description = entry.getDescription();
    if (description == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(description.getValue());
  }

  /**
   * Searches the media entries of the feed for thumbnail references
   * @param module the media module that may contain image references
   * @return Set<String> result the extracted images
   */
  @NonNull
  Set<String> extractFromMediaModule(@NonNull MediaEntryModule module) {
    Set<String> result = new LinkedHashSet<>();
    result.addAll(extractMetadataThumbnailUrls(module));
    result.addAll(extractFromMediaContents(module));
    result.addAll(extractFromMediaGroups(module));
    return result;
  }

  @NonNull
  Set<String> extractFromMediaGroups(@NonNull MediaEntryModule module) {
    MediaGroup[] mediaGroups = module.getMediaGroups();
    if (mediaGroups == null) {
      return new LinkedHashSet<>();
    }

    Set<String> fromMediaGroups = new LinkedHashSet<>();
    for (MediaGroup group : mediaGroups) {
      MediaContent[] contents = group.getContents();
      if (contents == null) {
        continue;
      }

      for (MediaContent content : contents) {
        String type = content.getType();
        if (type == null) {
          continue;
        }

        if (type.startsWith("image") && content.getReference() != null) {
          String url = content.getReference().toString();
          fromMediaGroups.add(url);
        }
      }
    }
    return fromMediaGroups;
  }

  @NonNull
  Set<String> extractMetadataThumbnailUrls(@NonNull MediaEntryModule module) {
    Metadata metadata = module.getMetadata();
    if (metadata == null) {
      return new LinkedHashSet<>();
    }
    Thumbnail[] thumbnails = metadata.getThumbnail();
    if (thumbnails == null) {
      return new LinkedHashSet<>();
    }

    Set<String> metadataThumbnailUrls = new LinkedHashSet<>();
    for (Thumbnail thumb : thumbnails) {
      URI uri = thumb.getUrl();
      if (uri != null) {
        metadataThumbnailUrls.add(uri.toString());
      }
    }
    return metadataThumbnailUrls;
  }

  @NonNull
  Set<String> extractFromMediaContents(@NonNull MediaEntryModule module) {
    MediaContent[] mediaContents = module.getMediaContents();
    if (mediaContents == null) {
      return new LinkedHashSet<>();
    }

    Set<String> fromMediaContents = new LinkedHashSet<>();
    for (MediaContent mediaContent : mediaContents) {
      getThumbnailUrl(mediaContent).ifPresent(fromMediaContents::add);
    }
    return fromMediaContents;
  }

  @NonNull
  private static Optional<String> getThumbnailUrl(@NonNull MediaContent mediaContent) {
    Metadata metadata = mediaContent.getMetadata();
    if (metadata == null || metadata.getThumbnail() == null || metadata.getThumbnail().length == 0) {
      return Optional.empty();
    }

    Thumbnail thumbnail = metadata.getThumbnail()[0];
    return Optional.of(thumbnail.getUrl().toString());
  }

  /**
   * Extracts images of the enclosure element from the feed entry
   * @param entry the entry to extract the enclosures from
   * @return Set<String> the extracted images
   */
  @NonNull
  Set<String> extractFromEnclosures(@NonNull SyndEntry entry) {
    List<SyndEnclosure> enclosures = entry.getEnclosures();
    if (enclosures == null) {
      return new LinkedHashSet<>();
    }
    Set<String> result = new LinkedHashSet<>();
    for (SyndEnclosure enclosure : enclosures) {
      Optional<String> imageUrl = extractImageUrl(enclosure);
      imageUrl.ifPresent(result::add);
    }
    return result;
  }

  @NonNull
  private static Optional<String> extractImageUrl(@NonNull SyndEnclosure enclosure) {
    String type = enclosure.getType();
    if (type == null) {
      return Optional.empty();
    }

    if (!type.contains("image")) {
      return Optional.empty();
    }

    return Optional.ofNullable(enclosure.getUrl());
  }
}
