package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.blueprint.internal.lcstudio.util.JsCommerceBean;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ui.store.BeanRecord;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * Represents a CommerceBean record in store.
 */
@ExtJSObject
@Scope(SCOPE_PROTOTYPE)
@DefaultAnnotation(NonNull.class)
public class CommerceBeanModel extends BeanRecord {
  @NonNull
  @Override
  public JsCommerceBean getBean() {
    return getBean(JsCommerceBean.class);
  }
}
