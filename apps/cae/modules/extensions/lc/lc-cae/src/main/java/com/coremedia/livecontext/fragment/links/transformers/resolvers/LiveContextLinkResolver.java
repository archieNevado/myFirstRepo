package com.coremedia.livecontext.fragment.links.transformers.resolvers;

import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@DefaultAnnotation(NonNull.class)
public interface LiveContextLinkResolver {

  /**
   * Is resolver applicable for current bean?
   *
   * @param bean    content bean
   * @param request the current request
   * @return <code>true</code> if resolver should try to resolve URL for current bean, <code>false</code> if not.
   */
  boolean isApplicable(@Nullable Object bean, HttpServletRequest request);

  /**
   * Resolves static part of the commerce URL for the current bean.
   *
   * @param source     source link
   * @param bean       current content
   * @param navigation current navigation context
   * @param view       view parameter passed via link tag.
   * @param request    current request
   * @return the static URL part of the LiveContext URL
   */
  Optional<String> resolveUrl(String source, @Nullable Object bean, @Nullable CMNavigation navigation, @Nullable String view, HttpServletRequest request);
}
