package com.coremedia.blueprint.studio.uitest.base.wrappers.taxonomyeditor;

import com.coremedia.uitesting.ui.components.SwitchingContainer;
import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.field.TextField;
import net.joala.condition.Condition;

import javax.inject.Singleton;

@ExtJSObject(id = "taxonomyExplorerPanel")
@Singleton
public class TaxonomyExplorerPanel extends Panel {

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(id = "taxonomyRootsColumn")
  private TaxonomyExplorerColumn rootColumn;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "columnsContainer", global = false)
  private Container columns;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "documentFormDispatcher")
  private SwitchingContainer documentFormDispatcher;

  public TaxonomyExplorerColumn getRootColumn() {
    return rootColumn;
  }

  public Container getColumns() {
    return columns;
  }

  public TaxonomyExplorerColumn taxonomySubColumn(final int columnIndex) {
    columns.visible().assertTrue();
    return columns.evalJsProxy(TaxonomyExplorerColumn.class, "self.items.get(index)", "index", columnIndex);
  }

  public Condition<String> nameOfTaxomonyInDocumentEditor() {
    final TextField textField = documentFormDispatcher.evalJsProxy(TextField.class, "self.findByType('textfield')[0]");
    return textField.stringCondition("self.value");
  }
}
