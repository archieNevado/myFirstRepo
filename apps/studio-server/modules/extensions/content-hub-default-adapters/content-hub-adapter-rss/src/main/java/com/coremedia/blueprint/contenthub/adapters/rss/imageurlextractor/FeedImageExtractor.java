package com.coremedia.blueprint.contenthub.adapters.rss.imageurlextractor;

import com.rometools.rome.feed.synd.SyndEntry;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;

/**
 * Utility class to extract all image URLs from a feed entry.
 */
public interface FeedImageExtractor {

  /**
   * Evaluates the HTML and the metadata for the RSS entry to find images
   *
   * @param entry the RSS entry to evaluate
   * @return the list of image URLs
   */
  List<String> extractImageUrls(@NonNull SyndEntry entry);
}
