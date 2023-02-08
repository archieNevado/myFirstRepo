package com.coremedia.blueprint.cae.web;

import com.coremedia.blueprint.base.tree.NavigationLinkListContentTreeRelation;
import com.coremedia.blueprint.cae.exception.InvalidContentException;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.web.HandlerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class ChannelValidityInterceptor implements HandlerInterceptor {

  private static final Logger LOG = LoggerFactory.getLogger(ChannelValidityInterceptor.class);

  private NavigationLinkListContentTreeRelation treeRelation;

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    if (null != modelAndView) {
      Object self = HandlerHelper.getRootModel(modelAndView);

      // Rendered Channels must be part of a global navigation
      if (self instanceof Page) {
        Page page = (Page) self;
        Object content = page.getContent();
        if (content instanceof CMChannel
                && CMChannel.NAME.equals(((CMChannel) content).getContent().getType().getName())
                && !isPartOfGlobalNavigation((CMChannel) content)) {
          final String msg = "Trying to render a channel that is not part of the global navigation, returning " + SC_NOT_FOUND + ".  Page=" + self;
          LOG.debug(msg);
          throw new InvalidContentException(msg, self);
        }
      }
    }
  }

  public void setTreeRelation(NavigationLinkListContentTreeRelation treeRelation) {
    this.treeRelation = treeRelation;
  }

  @PostConstruct
  protected void initialize() {
    if (treeRelation == null) {
      throw new IllegalStateException("Required property not set: treeRelation");
    }
  }

  private boolean isPartOfGlobalNavigation(CMChannel channel) {
    List<Content> pathToRoot = treeRelation.pathToRoot(channel.getContent());
    // the following check only makes sense with
    // com.coremedia.blueprint.base.tree.NavigationLinkListContentTreeRelation
    return !pathToRoot.isEmpty() && treeRelation.isRoot(pathToRoot.get(0));
  }

}
