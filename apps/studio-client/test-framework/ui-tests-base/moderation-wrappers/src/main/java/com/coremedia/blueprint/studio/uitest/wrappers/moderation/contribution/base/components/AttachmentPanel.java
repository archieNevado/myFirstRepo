package com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base.components;

import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.Toolbar;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.field.StringDisplayField;
import net.joala.condition.BooleanCondition;
import net.joala.condition.Condition;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.Collection;

@ExtJSObject
@Scope("prototype")
public class AttachmentPanel extends Container {

  @SuppressWarnings("UnusedDeclaration") // NOSONAR - Suppress Avoid Duplicate Literals
  @FindByExtJS(itemId = "detailAttachmentToolbar", global = false)
  private Toolbar detailAttachmentToolbar;

  public Condition<Long> attachmentCount() {
    return detailAttachmentToolbar.getItems().count();
  }

  public Condition<String> fileNameFromItem(final CharSequence fileName) {
    return ((AttachmentItemContainer) getItemContainerFromFileName(fileName)).fileName();
  }

  public BooleanCondition itemMarkedAsActive(final CharSequence fileName) {
    return ((AttachmentItemContainer) getItemContainerFromFileName(fileName)).markedAsActive();
  }

  public Container getItemContainerFromFileName(final CharSequence fileName) {
    for (final AttachmentItemContainer itemContainer : getAttachmentItemContainerList()) {
      final String containerFileName = itemContainer.fileName().await();
      assert containerFileName != null : "Filename of item container must not be null.";
      if (containerFileName.equalsIgnoreCase(fileName.toString())) {
        return itemContainer;
      }
    }
    throw new IllegalStateException(String.format("Could not find any attachment with name %s in the attachment list", fileName));
  }

  private Iterable<AttachmentItemContainer> getAttachmentItemContainerList() {
    final Collection<AttachmentItemContainer> itemContainerList = new ArrayList<>();
    final Long itemCount = detailAttachmentToolbar.getItems().count().await();
    assert itemCount != null : "Item count in item container must not be null.";
    for (int i = 0; i < itemCount; i++) {
      itemContainerList.add(detailAttachmentToolbar.getComponent(i, AttachmentItemContainer.class));
    }
    return itemContainerList;
  }


  @ExtJSObject
  @Scope("prototype")
  private static final class AttachmentItemContainer extends Container {

    @SuppressWarnings("UnusedDeclaration") // NOSONAR - Suppress Avoid Duplicate Literals
    @FindByExtJS(itemId = "fileNameField", global = false)
    private StringDisplayField fileNameField;

    @SuppressWarnings("UnusedDeclaration") // NOSONAR - Suppress Avoid Duplicate Literals
    @FindByExtJS(itemId = "fileTypeField", global = false)
    private StringDisplayField fileTypeField;

    private static final String ACTIVATED_CLASS = "x-container-selected-100";

    private Condition<String> fileName() {
      return fileNameField.value();
    }

    private Condition<String> fileType() {
      return fileTypeField.value();
    }

    private BooleanCondition markedAsActive() {
      return this.hasCls(ACTIVATED_CLASS);
    }
  }
}
