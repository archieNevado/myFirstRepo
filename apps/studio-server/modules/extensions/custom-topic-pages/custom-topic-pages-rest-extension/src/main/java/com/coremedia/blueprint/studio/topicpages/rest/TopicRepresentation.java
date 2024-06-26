package com.coremedia.blueprint.studio.topicpages.rest;

import com.coremedia.cap.content.Content;
import com.google.common.base.Strings;

/**
 * Representation of a topic page.
 */
public class TopicRepresentation {
  public static final int STATUS_OK = 0;
  public static final int STATUS_ERROR_COULD_NOT_RESOLVE_ROOT_CHANNEL = 1;
  public static final int STATUS_ERROR_CUSTOM_PAGE_EXISTS = 2;
  public static final int STATUS_ERROR_DELETION_FAILED = 3;

  public static final String GLOBAL_SITE_NAME = "global";

  private Content topic;
  private String name;
  private Content page;
  private int status = STATUS_OK;
  private Content rootChannel;
  private Content topicPagesFolder;

  public TopicRepresentation(Content content, String name, Content page, Content rootChannel, Content topicPagesFolder) {
    this(content, name);
    this.page = page;
    this.rootChannel = rootChannel;
    this.topicPagesFolder = topicPagesFolder;
  }

  public TopicRepresentation(Content content, String name) {
    this.topic = content;
    this.name = name;
  }

  public Content getTopicPagesFolder() {
    return topicPagesFolder;
  }

  public Content getRootChannel() {
    return rootChannel;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public Content getPage() {
    return page;
  }

  public void setPage(Content page) {
    this.page = page;
  }

  public String getName() {
    return name;
  }

  public Content getTopic() {
    return topic;
  }
}
