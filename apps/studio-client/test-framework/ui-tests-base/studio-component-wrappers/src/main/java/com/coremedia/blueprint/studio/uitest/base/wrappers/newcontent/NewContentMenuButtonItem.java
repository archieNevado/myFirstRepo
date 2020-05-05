package com.coremedia.blueprint.studio.uitest.base.wrappers.newcontent;

import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.menu.Item;
import org.springframework.context.annotation.Scope;

/**
 * @since 6/8/12
 */
@ExtJSObject
@Scope("prototype")
public class NewContentMenuButtonItem extends Item {

  @Override
  public NewContentDialogAction getBaseAction() {
    return super.getBaseAction().evalJsProxyProxy(NewContentDialogAction.class);
  }
}
