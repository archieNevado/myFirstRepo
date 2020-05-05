package com.coremedia.blueprint.studio.uitest.base.wrappers.topicpageseditor;

import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.form.field.ComboBoxField;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;


@ExtJSObject
@Scope(SCOPE_PROTOTYPE)
@Named("topicPagesEditorBase")
public class Base extends ComboBoxField {
  public static final String XTYPE = "combo";

  public void expand() {
    evalVoid("self.el.next().dom.click()");
  }

  public void selectNext() {
    evalVoid("self.selectNext()");
  }

  public void reload() {
    expand();
    evalVoid("self.selectPrev()");
    clickSelection();
    blur();
    expand();
    evalVoid("self.selectNext()");
    clickSelection();
    blur();
  }

  public String getValue() {
    return evalString("self.getValue()");
  }

  public void clickSelection() {
    evalVoid("self.list.child('.' + self.selectedClass).dom.click()");
  }
}
