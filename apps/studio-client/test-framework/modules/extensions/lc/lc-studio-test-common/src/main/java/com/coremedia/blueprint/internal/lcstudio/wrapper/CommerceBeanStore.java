package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.blueprint.internal.lcstudio.util.JsCommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ui.data.RemoteBeanStore;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@ExtJSObject
@Scope(SCOPE_PROTOTYPE)
@DefaultAnnotation(NonNull.class)
public class CommerceBeanStore extends RemoteBeanStore<CommerceBean, JsCommerceBean> {
  /**
   * Retrieve the commerce bean model by its external ID or name. If there is no
   * match in store, {@link CommerceBeanModel#exists()} will evaluate
   * to false.
   *
   * @param externalIdOrName external ID, e. g. {@code GFR033_3301}; or name e. g. {@code Shoes}
   * @return wrapper for commerce bean model
   */
  public CommerceBeanModel queryByExternalIdOrName(String externalIdOrName) {
    return evalJsProxy(
            CommerceBeanModel.class,
            "self.queryBy(function(model){return model.getBean && [model.getBean().getExternalId(), model.getBean().getName()].includes(externalIdOrName)}).first()",
            "externalIdOrName",
            externalIdOrName);
  }

  /**
   * Retrieve the commerce bean model by its external ID. If the given external
   * ID is not available in store, {@link CommerceBeanModel#exists()} will evaluate
   * to false.
   *
   * @param externalId external ID, e. g. {@code GFR033_3301}
   * @return wrapper for commerce bean model
   */
  public CommerceBeanModel queryByExternalId(String externalId) {
    return evalJsProxy(
            CommerceBeanModel.class,
            "self.queryBy(function(model){return model.getBean && model.getBean().getExternalId() === externalId}).first()",
            "externalId",
            externalId);
  }

  /**
   * Retrieve the commerce bean model by its name. If the given name is not
   * available in store, {@link CommerceBeanModel#exists()} will evaluate to false.
   *
   * @param name name of the commerce bean, e. g. "Casual Yet Classic"
   * @return wrapper for commerce bean model
   */
  public CommerceBeanModel queryByName(String name) {
    return evalJsProxy(
            CommerceBeanModel.class,
            "self.queryBy(function(model){return model.getBean && model.getBean().getName() === name}).first()",
            "name",
            name);
  }

  @NonNull
  public CommerceBeanModel queryAt(CommerceBean commerceBean, long position) {
    return queryAt("bean", commerceBean, position);
  }

  @NonNull
  @Override
  public CommerceBeanModel queryAt(@NonNull String property, Object value, long position) {
    return super.queryAt(property, value, position, CommerceBeanModel.class);
  }

  @NonNull
  @Override
  public CommerceBeanModel getAt(long index) {
    return super.getAt(index).evalJsProxyProxy(CommerceBeanModel.class);
  }

  @NonNull
  @Override
  public CommerceBeanModel getById(String id) {
    return super.getById(id).evalJsProxyProxy(CommerceBeanModel.class);
  }

}
