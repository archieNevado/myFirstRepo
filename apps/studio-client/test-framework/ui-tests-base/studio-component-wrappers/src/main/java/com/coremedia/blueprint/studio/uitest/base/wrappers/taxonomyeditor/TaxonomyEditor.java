package com.coremedia.blueprint.studio.uitest.base.wrappers.taxonomyeditor;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;

import javax.inject.Inject;
import javax.inject.Singleton;

@ExtJSObject(id="taxonomyEditor")
@Singleton
public class TaxonomyEditor extends Panel {
  public static final String XTYPE = "com.coremedia.blueprint.studio.config.taxonomy.taxonomyEditor";

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(id = "taxonomySearchField")
  private TaxonomySearchField searchField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(id= "taxonomy-reload-button")
  private Button reloadButton;

  @Inject
  private TaxonomyExplorerPanel explorerPanel;

  public TaxonomySearchField getSearchField() {
    return searchField;
  }

  public TaxonomyExplorerPanel getExplorerPanel() {
    return explorerPanel;
  }

  public void clickReload() {
    reloadButton.click();
  }
}
