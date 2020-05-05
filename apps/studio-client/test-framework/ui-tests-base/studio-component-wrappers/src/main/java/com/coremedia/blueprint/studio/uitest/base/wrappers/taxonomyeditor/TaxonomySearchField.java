package com.coremedia.blueprint.studio.uitest.base.wrappers.taxonomyeditor;

import com.coremedia.cap.content.Content;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.data.Store;
import com.coremedia.uitesting.ext3.wrappers.form.field.ComboBoxField;
import com.coremedia.uitesting.ext3.wrappers.view.DataView;
import net.joala.condition.Condition;
import net.joala.condition.ConditionFactory;
import net.joala.expression.AbstractExpression;
import org.springframework.context.annotation.Scope;

import edu.umd.cs.findbugs.annotations.Nullable;
import javax.inject.Inject;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@ExtJSObject
@Scope("prototype")
public class TaxonomySearchField extends ComboBoxField {
  public static final String XTYPE = "com.coremedia.blueprint.studio.config.taxonomy.taxonomySearchField";
  private static final long SLEEP = 1000L;

  @FindByExtJS(expression = "self.getStore()", global = false)
  private Store store;

  @FindByExtJS(expression = "self.view", global = false)
  private DataView dataview;

  @Inject
  private ConditionFactory conditionFactory;

  @Override
  public Store getStore() {
    return store;
  }

  public DataView getDataview() {
    return dataview;
  }

  public void searchAndSelect(Content taxonomy) {
    visible().waitUntilTrue();
    enabled().waitUntilTrue();

    String input = taxonomy.getString("value");
    Condition<Long> dataLengthCondition = getStore().dataLength();
    dataLengthCondition.withMessage("Store should be empty before typing.").waitUntilEquals(0L);

    /*
     * Don't ask why... as it seems, TaxonomyFilterPanel is very fragile when
     * writing Strings or takes very long to update its index. The problem
     * got more severe when we introduced restricted access for test users
     * to the test folder. So, problems may relate to Rights and
     * indexing.
     *
     * See also: [CMS-16405] Taxonomy-Library-Filter: Very Unreliable in Restricted Permission Scenarios
    */
    conditionFactory.condition(new AbstractExpression<Long>() {
      @SuppressWarnings("SSBasedInspection")
      @Nullable
      @Override
      public Long get() {
        setValue("");
        try {
          Thread.sleep(SLEEP);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        writeString(input);
        return dataLengthCondition.get();
      }
    }).withMessage("Searching for '" + input + "' store should now contain an element for:" + taxonomy.getPath())
            .withTimeoutFactor(2D)
            .waitUntil(greaterThanOrEqualTo(1L));

    valid().waitUntilTrue();
    select(input);
  }
}
