package com.coremedia.livecontext.fragment.links.transformers;

import com.coremedia.objectserver.web.links.LinkTransformer;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

/**
 * Checks if the {@link com.coremedia.livecontext.fragment.links.transformers.resolvers.LiveContextLinkResolver}
 * is the last element in the linkTransformers List Bean.
 */
public class LiveContextLinkTransformerOrderChecker implements ApplicationListener<ContextRefreshedEvent> {

  private List<LinkTransformer> linkTransformers;
  private LiveContextLinkTransformer liveContextLinkTransformer;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    //check if the LiveContextLinkResolver is the last in the linkTransformers list
    if (linkTransformers != null && linkTransformers.contains(liveContextLinkTransformer)) {
      if (linkTransformers.indexOf(liveContextLinkTransformer) != linkTransformers.size() - 1) {
        throw new IllegalStateException(LiveContextLinkTransformer.class + " must be last in the linkTransformers List Bean");
      }
    }
  }

  @Required
  public void setLinkTransformers(List<LinkTransformer> linkTransformers) {
    this.linkTransformers = linkTransformers;
  }

  @Required
  public void setLiveContextLinkTransformer(LiveContextLinkTransformer liveContextLinkTransformer) {
    this.liveContextLinkTransformer = liveContextLinkTransformer;
  }
}
