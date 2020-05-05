package com.coremedia.blueprint.studio.uitest.base.wrappers.qcreate;

import com.coremedia.uitesting.cms.editor.components.premular.fields.LinkListPropertyFieldToolbar;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import org.springframework.context.annotation.Scope;

/**
 * A Linklist-Property-Field toolbar enriched by the Quick-Create-Linklist-Menu.
 *
 * @since 2013-08-09
 */
@ExtJSObject
@Scope("prototype")
public class LinkListPropertyFieldQuickCreateToolbar extends LinkListPropertyFieldToolbar {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "createFromLinkListMenuButton")
  private QuickCreateLinklistMenu createFromLinkListMenuButton;

  public QuickCreateLinklistMenu getCreateFromLinkListMenuButton() {
    return createFromLinkListMenuButton;
  }
}
