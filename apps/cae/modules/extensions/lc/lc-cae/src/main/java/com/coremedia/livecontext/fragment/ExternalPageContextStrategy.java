package com.coremedia.livecontext.fragment;

import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * A {@link ContextStrategy} that finds a context for an augmented page identified by its external id.
 * The augmented page must be part of the navigation. All findings will be cached with full dependency
 * tracking.
 */
public class ExternalPageContextStrategy implements ContextStrategy<String, Navigation> {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  private final ContentBeanFactory contentBeanFactory;
  private final ContextStrategy<String, Content> externalPageContentContextStrategy;

  public ExternalPageContextStrategy(ContentBeanFactory contentBeanFactory, ContextStrategy<String, Content> externalPageContentContextStrategy) {
    this.contentBeanFactory = contentBeanFactory;
    this.externalPageContentContextStrategy = externalPageContentContextStrategy;
  }

  @Override
  public Navigation findAndSelectContextFor(String pageId, Navigation rootChannel) {
    List<Navigation> candidates = findContextsFor(pageId, rootChannel);
    return !candidates.isEmpty() ? candidates.get(0) : null;
  }

  @Override
  public List<Navigation> findContextsFor(@NonNull String pageId) {
    LOG.warn("method findContextsFor(pageId) is not supported, use findContextFor(pageId, navigation) instead");
    return Collections.emptyList();
  }

  @Override
  public List<Navigation> findContextsFor(@NonNull final String pageId, @Nullable final Navigation rootChannel) {
    if (rootChannel instanceof CMObject) {
      return externalPageContentContextStrategy.findContextsFor(pageId, ((CMObject) rootChannel).getContent()).stream()
              .map(content -> contentBeanFactory.createBeanFor(content, ContentBean.class))
              .filter(Navigation.class::isInstance)
              .map(Navigation.class::cast)
              .collect(Collectors.toList());
    }
    return List.of();
  }

  @Override
  public Navigation selectContext(Navigation rootChannel, List<? extends Navigation> candidates) {
    return candidates != null && !candidates.isEmpty() ? candidates.get(0) : null;
  }

}
