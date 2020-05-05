package com.coremedia.blueprint.studio.uitest.base.wrappers.taxonomyeditor.store;

import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.data.Store;
import net.joala.condition.BooleanCondition;
import net.joala.condition.Condition;
import org.springframework.context.annotation.Scope;

/**
 * <p>
 * Represents a specialized store (used in GridPanels and alike) which can hold content beans.
 * </p>
 * <p>
 * To provide a specialized ContentStore override the method {@code getStore()} of your store-owning component
 * to:
 * </p>
 * <pre>
 * &#64;Override public ContentStore getStore() {
 * return super.getStore().evalJsProxyProxy(ContentStore.class);
 * }
 * </pre>
 * @since 5/4/12
 */
@ExtJSObject
@Scope("prototype")
public class TaxonomyNodeStore extends Store {

  public BooleanCondition taxonomyContained(final Content content) {
    return booleanCondition("self.findBy(function(r,id){return r.getBean().ref===contentId;}) != -1",
            "contentId", getIdAsString(content))
            .withMessage(String.format("Check if content '%s' (%s) is available.", content.getPath(), content.getId()));
  }

  public Condition<Long> taxonomyPosition(final Content content) {
    return longCondition("self.findBy(function(r,id){return r.getBean().ref===contentId;})",
            "contentId", getIdAsString(content))
            .withMessage(String.format("Position of Content '%s' (%s).", content.getPath(), content.getId()));
  }
  
  private String getIdAsString(Content content) {
    final int numericId = IdHelper.parseContentId(content.getId());
    return "content/" + numericId;
  }
}
