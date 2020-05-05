package com.coremedia.blueprint.internal.lcstudio.util;

import com.coremedia.blueprint.uitesting.lc.CatalogUtils;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.uitesting.cms.editor.JsContent;
import com.coremedia.uitesting.ui.data.RemoteBean;
import net.joala.condition.BooleanCondition;
import net.joala.condition.Condition;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * A Java representation of a studio-rest-client CommerceBean.
 */
@SuppressWarnings({"JSUnresolvedFunction", "unused"})
@Named
@Scope(SCOPE_PROTOTYPE)
public class JsCommerceBean extends RemoteBean<CommerceBean> {
  private final CatalogUtils catalogUtils;

  public JsCommerceBean(CatalogUtils catalogUtils) {
    this.catalogUtils = catalogUtils;
  }

  /**
   * Retrieve the commerce bean representing the result of the embedded expression.
   *
   * @return the commerce bean
   */
  @Override
  public CommerceBean getBean() {
    String siteId = evalString("self.get('siteId')");
    String id = evalString("self.get('id')");

    return catalogUtils.getCommerceBean(id, siteId);
  }

  /**
   * Access augmenting content.
   *
   * @return content
   */
  public JsContent getContent() {
    return evalJsProxy(JsContent.class, "self.getContent()");
  }

  public BooleanCondition augmented() {
    return booleanCondition("!!self.getContent()");
  }

  public Condition<String> name() {
    return stringCondition("self.getName()");
  }

  public Condition<String> shortDescription() {
    return stringCondition("self.getShortDescription()");
  }

  public Condition<String> externalId() {
    return stringCondition("self.getExternalId()");
  }

  public Condition<String> id() {
    return stringCondition("self.getId()");
  }

  public Condition<String> externalTechId() {
    return stringCondition("self.getExternalTechId()");
  }

  public Condition<String> siteId() {
    return stringCondition("self.getSiteId()");
  }

  public Condition<String> customAttribute(String attributeName) {
    return stringCondition("self.getCustomAttribute(attributeName)", "attributeName", attributeName);
  }
}
