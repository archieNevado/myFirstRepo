package com.coremedia.blueprint.studio.uitest.base.wrappers.taxonomyeditor;

import com.coremedia.blueprint.studio.uitest.base.wrappers.taxonomyeditor.store.TaxonomyNodeStore;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.grid.GridPanel;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class TaxonomyExplorerColumn extends GridPanel {

  @Override
  public TaxonomyNodeStore getStore() {
    return super.getStore().evalJsProxyProxy(TaxonomyNodeStore.class);
  }

/*
  public BooleanCondition taxonomyAvailable(Content content) {
    return getStore().booleanCondition("self.findBy(function(r,id){return r.getBean()===content;}) != -1",
            "content", jsContentFactory.get(content))
            .withMessage(String.format("Check if taxonomy '%s' (%s) is available.", content.getPath(), content.getId()));
  }

  public Condition<Long> taxonomyPosition(Content content) {
    return getStore().longCondition("self.findBy(function(r,id){return r.getBean()===content;})",
            "content", jsContentFactory.get(content))
            .withMessage(String.format("Position of Content '%s' (%s).", content.getPath(), content.getId()));
  }

  private String getTaxonomyId(Content content) {
    if (content.getType().getName().equals("Folder_")) {
      return content.getName();
    } else {
      return content.getString("value");
    }
  }
*/
}
