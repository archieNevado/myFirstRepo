package com.coremedia.blueprint.studio.uitest.base.wrappers.qcreate;

import com.coremedia.blueprint.studio.uitest.base.wrappers.newcontent.NewContentDialog;
import com.coremedia.blueprint.studio.uitest.base.wrappers.newcontent.NewContentDialogAction;
import com.coremedia.cap.content.Content;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSBy;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ui.IconButton;
import net.joala.condition.Condition;
import org.springframework.context.annotation.Scope;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wrapper for the Toolbar button for Linklists to quickly create contents on the fly.
 *
 * @since 2013-08-09
 */
@ExtJSObject
@Scope("prototype")
public class QuickCreateLinklistMenu extends IconButton {
  public Condition<List<String>> contentTypes() {
    return getMenu().stringsCondition("self.items.keys");
  }

  /**
   * Initiates content creation of the given content type. Does not handle
   * any dialogs which pop up.
   *
   * @param contentType content type to create
   */
  public void triggerCreateContent(final String contentType) {
    clickAndSelectFromMenu(ExtJSBy.itemId(contentType));
  }

  public NewContentDialog getNewContentDialog(final String contentType) {
    final NewContentDialogAction action =
            getMenu().evalJsProxy(NewContentDialogAction.class,
                    "self.items.get(contentType).baseAction",
                    "contentType", contentType);
    return action.getDialog();
  }

  /**
   * Creates content of the given type and name in the given folder.
   *
   * @param contentType content type to create
   * @param name        name of the document; if {@code null} don't enter a name
   * @param folder      folder to create the content in; if {@code null} use folder suggested by default
   */
  public void createContent(final String contentType, final String name, final Content folder) {
    actionFor(contentType)
            .withName(name)
            .inFolder(folder)
            .execute();
  }

  @SuppressWarnings("AnonymousInnerClass")
  public NewContentDialog.NewContentDialogActionPerformer actionFor(final String contentType) {
    checkNotNull(contentType, "Content Type must not be null.");
    final NewContentDialog dialog = getNewContentDialog(contentType);
    return dialog.action()
                 .before(new Runnable() {
                   @Override
                   public void run() {
                     triggerCreateContent(contentType);
                   }
                 });
  }
}
