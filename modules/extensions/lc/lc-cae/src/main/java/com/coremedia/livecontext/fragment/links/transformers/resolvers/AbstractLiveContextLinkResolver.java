package com.coremedia.livecontext.fragment.links.transformers.resolvers;

import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

/**
 * Base class for all LiveContext link resolvers.
 */
public abstract class AbstractLiveContextLinkResolver implements LiveContextLinkResolver {

  protected static final Logger LOG = LoggerFactory.getLogger(AbstractLiveContextLinkResolver.class);

  public static final String KEY_PLAIN_LINK = "--PLAIN_LINK--";

  public static final String LIVECONTEXT_COMMENT_PREFIX = "<!--CM ";
  public static final String LIVECONTEXT_COMMENT_SUFFIX = "CM-->";

  @Override
  public String resolveUrl(String source, Object bean, String variant, CMNavigation navigation,
                           HttpServletRequest request) {
    long start = System.currentTimeMillis();

    String result = resolveUrlInner(source, bean, variant, navigation, request);

    if (LOG.isTraceEnabled()) {
      long duration = System.currentTimeMillis() - start;
      LOG.trace("building url {} takes {} milliseconds.", result, duration);
    }

    return result;
  }

  @Nullable
  private String resolveUrlInner(String source, Object bean, String variant, CMNavigation navigation,
                                 HttpServletRequest request) {
    try {
      JSONObject json = resolveUrlInternal(source, bean, variant, navigation, request);

      // Special handling for Links that are resolved directly,
      // i.e. for com.coremedia.blueprint.common.contentbeans.CMExternalLink
      if (json.has(KEY_PLAIN_LINK)) {
        return json.getString(KEY_PLAIN_LINK);
      }

      return LIVECONTEXT_COMMENT_PREFIX.concat(json.toString()).concat(LIVECONTEXT_COMMENT_SUFFIX);
    } catch (JSONException e) {
      LOG.error("Could not build URL JSON for {}", bean.toString().concat("#").concat("variant"), e);
      return null;
    }
  }

  /**
   * @param source     source link
   * @param bean       Bean for which URL is to be rendered
   * @param variant    Link variant
   * @param navigation Current navigation of bean for which URL is to be rendered
   * @param request
   * @return JSON object containing all relevant details for URL rendering, except for "type":"URL", which
   * will be added by {@link AbstractLiveContextLinkResolver} automatically.
   */
  protected abstract JSONObject resolveUrlInternal(String source, Object bean, String variant, CMNavigation navigation,
                                                   HttpServletRequest request) throws JSONException;
}
