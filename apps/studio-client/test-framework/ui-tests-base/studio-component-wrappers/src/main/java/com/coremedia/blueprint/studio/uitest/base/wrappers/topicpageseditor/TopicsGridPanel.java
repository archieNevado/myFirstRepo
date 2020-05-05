package com.coremedia.blueprint.studio.uitest.base.wrappers.topicpageseditor;

import com.coremedia.cap.common.IdHelper;
import com.coremedia.uitesting.cms.editor.ContentStore;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.grid.GridPanel;

@ExtJSObject(id="topicPagesGrid")
public class TopicsGridPanel extends GridPanel {

  public String getCapIdOfFirstTopicNode() {
    Long id = evalLong("self.getStore().getAt(0).data.topic.getNumericId()");
    return IdHelper.formatContentId(String.valueOf(id));
  }

  @Override
  public ContentStore getStore() {
    return super.getStore().evalJsProxyProxy(ContentStore.class);
  }
}
