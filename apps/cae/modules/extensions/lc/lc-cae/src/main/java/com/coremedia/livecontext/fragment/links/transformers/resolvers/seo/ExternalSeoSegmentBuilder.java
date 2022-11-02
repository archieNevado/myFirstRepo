package com.coremedia.livecontext.fragment.links.transformers.resolvers.seo;

import com.coremedia.blueprint.base.links.SettingsBasedVanityUrlMapper;
import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.base.links.VanityUrlMapperCacheKey;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.text.MessageFormat.format;

/**
 * Generates external seo segment name for a given navigation and linkable for use in commerce links.
 */
public class ExternalSeoSegmentBuilder implements SeoSegmentBuilder {

  public static final Logger LOG = LoggerFactory.getLogger(ExternalSeoSegmentBuilder.class);

  private static final String PATH_DELIMITER = "--";
  private static final String ID_DELIMITER = "-";
  private static final String ID_DELIMITER_BEGIN_REGEX = "^" +ID_DELIMITER + "+";
  private static final String ID_DELIMITER_END_REGEX = ID_DELIMITER + "+$";
  private static final String DUMMY_SEGMENT = "s";

  private NavigationSegmentsUriHelper navigationSegmentsUriHelper;
  private Cache cache;
  private SettingsService settingsService;
  private UrlPathFormattingHelper urlPathFormattingHelper;

  @Override
  @NonNull
  public String asSeoSegment(CMNavigation navigation, CMObject target) {
    if (navigation == null || target == null) {
      return "";
    }

    try {
      StringBuilder sb = new StringBuilder();

      //vanity url configured for the target?
      String vanity = getVanityUrl(navigation, target);

      if (vanity != null) {
        //we allow '/' in the vanity url which we understand as a path delimiter.
        vanity = vanity.replaceAll("/", PATH_DELIMITER);
        sb.append(vanity);
      } else {
        List<String> navigationPath = navigationSegmentsUriHelper.getPathList(navigation);
        // we omit the root segment (e.g. "aurora" because it is reproducible)
        for (int i = 1; i < navigationPath.size(); i++) {
          if (i > 1) {
            sb.append(PATH_DELIMITER);
          }
          sb.append(navigationPath.get(i));
        }

        if (!navigation.equals(target)) {
          String segment = null;
          if (target instanceof Linkable) {
            Linkable linkable = (Linkable) target;
            segment = linkable.getSegment();
          }
          if (sb.length() > 0) {
            sb.append(PATH_DELIMITER);
          }
          sb.append(asSeoTitle(StringUtils.isNotBlank(segment) ? segment : DUMMY_SEGMENT));
          sb.append(ID_DELIMITER);
          sb.append(target.getContentId());
        }
      }

      return UriComponentsBuilder.fromPath(sb.toString()).toUriString();
    }
    catch (Exception e) {
      LOG.error(format("Cannot generate SEOSegment for the navigation {0} and target {1}",
              navigation.getContent().getPath(), target.getContent().getPath()), e);
      return "";
    }
  }

  @Nullable
  private String getVanityUrl(CMNavigation navigation, CMObject target) {
    Content rootChannnel = navigation.getRootNavigation().getContent();
    final SettingsBasedVanityUrlMapper vanityUrlMapper = cache.get(new VanityUrlMapperCacheKey(rootChannnel, settingsService));
    return vanityUrlMapper.patternFor(target.getContent());
  }

  /**
   * To lowercase;
   * reduce multiple dashes to one;
   * remove dashes at the beginning.
   * remove dashes at the end.
   */
  private String asSeoTitle(String s) {
    return urlPathFormattingHelper.tidyUrlPath(s)
            // Remove dashes at the beginning
            .replaceAll(ID_DELIMITER_BEGIN_REGEX, "")
            // Remove dashes at the end
            .replaceAll(ID_DELIMITER_END_REGEX, "");
  }

  @Required
  public void setNavigationSegmentsUriHelper(NavigationSegmentsUriHelper navigationSegmentsUriHelper) {
    this.navigationSegmentsUriHelper = navigationSegmentsUriHelper;
  }

  @Required
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Required
  public void setUrlPathFormattingHelper(UrlPathFormattingHelper urlPathFormattingHelper) {
    this.urlPathFormattingHelper = urlPathFormattingHelper;
  }

}
