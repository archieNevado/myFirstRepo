package com.coremedia.blueprint.studio.uitest.base.wrappers.topicpageseditor;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;

@ExtJSObject(id="topicPagesEditor")
public class TopicPagesEditor extends Panel {
  public static final String XTYPE = "com.coremedia.blueprint.studio.topicpages.config.topicPagesEditor";

  @FindByExtJS(id="topicsPanel")
  private TopicPanel topicPanel;

  public TopicPanel getTopicPanel() {
    return topicPanel;
  }
}
