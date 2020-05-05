package com.coremedia.blueprint.studio.uitest.wrappers.moderation.archive.details;

import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSBy;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@ExtJSObject
@Scope("prototype")
public class MultiCommentDetailView extends Panel {
  @SuppressWarnings("UnusedDeclaration")
  @FindByExtJS(itemId = "read-only-container", global = false)
  private Container readOnlyContainer;
  private static final String COMMENT_READ_ONLY_ITEM_ID_PREFIX = "read-only-item-container_";

  public List<MultiCommentReadOnlyItemPanel> getMultiCommentReadOnlyItemContainerList() {
    final List<MultiCommentReadOnlyItemPanel> readOnlyItemPanelList = new ArrayList<>();
    readOnlyContainer.visible().assertTrue();
    final long itemCount = readOnlyContainer.getItems().count().await();
    int index = 0;
    while (index < itemCount) {
      readOnlyItemPanelList.add(readOnlyContainer.find(MultiCommentReadOnlyItemPanel.class, ExtJSBy.itemId(COMMENT_READ_ONLY_ITEM_ID_PREFIX +""+index)));
      index++;
    }

    return readOnlyItemPanelList;
  }

  public Container getCommentDetailsContainer() {
    return readOnlyContainer;
  }

}
