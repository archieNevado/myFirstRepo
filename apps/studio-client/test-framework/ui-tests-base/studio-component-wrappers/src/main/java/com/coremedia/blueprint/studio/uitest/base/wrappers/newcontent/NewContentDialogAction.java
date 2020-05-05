package com.coremedia.blueprint.studio.uitest.base.wrappers.newcontent;

import com.coremedia.blueprint.studio.uitest.base.wrappers.template.CreateFromTemplateDialog;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ui.actions.OpenDialogAction;
import org.springframework.context.annotation.Scope;

/**
 * @since 2013-03-15
 */
@ExtJSObject
@Scope("prototype")
public class NewContentDialogAction extends OpenDialogAction {
  @Override
  public NewContentDialog getDialog() {
    return super.getDialog().evalJsProxyProxy(NewContentDialog.class);
  }

  public CreateFromTemplateDialog getCreateFromTemplateDialog() {
    return super.getDialog().evalJsProxyProxy(CreateFromTemplateDialog.class);
  }
}
