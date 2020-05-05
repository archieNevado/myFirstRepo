package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.uitesting.cms.editor.components.collectionview.list.LibraryListView;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.data.Model;
import com.coremedia.uitesting.ext3.wrappers.view.TableView;
import net.joala.condition.Condition;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import static org.hamcrest.Matchers.notNullValue;

@ExtJSObject
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CatalogSearchList extends LibraryListView {

  public static final String XTYPE = "com.coremedia.ecommerce.studio.config.catalogSearchList";

  @SuppressWarnings("UnusedDeclaration")
  @FindByExtJS(xtype = CatalogSearchContextMenu.XTYPE, global = true)
  private CatalogSearchContextMenu contextMenu;

  public CatalogSearchContextMenu getContextMenu() {
    return contextMenu;
  }
}
