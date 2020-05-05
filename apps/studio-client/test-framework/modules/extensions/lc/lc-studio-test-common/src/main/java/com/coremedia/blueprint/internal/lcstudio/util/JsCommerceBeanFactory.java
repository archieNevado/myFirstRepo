package com.coremedia.blueprint.internal.lcstudio.util;

import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.uitesting.ui.data.RemoteBeanFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper.format;

/**
 * A factory for creating JavaScript representations of CommerceBeans.
 */
@Named
@Singleton
public class JsCommerceBeanFactory {
  private Provider<RemoteBeanFactory> remoteBeanFactory;
  private Class<JsCommerceBean> jsWrapperClass;

  @Inject
  protected JsCommerceBeanFactory(final Provider<RemoteBeanFactory> remoteBeanFactoryProvider) {
    this.remoteBeanFactory = remoteBeanFactoryProvider;
    this.jsWrapperClass = JsCommerceBean.class;
  }

  public final JsCommerceBean get(CommerceBean commerceBean) {
    return remoteBeanFactory.get().getRemoteBean(getRemoteBeanPath(commerceBean), jsWrapperClass);
  }


  /**
   * Return the remote-bean-path for the given CommerceBean.
   * @param commerceBean commerceBean to get remote bean path for
   * @return remote bean path
   */
  protected String getRemoteBeanPath(CommerceBean commerceBean) {
    CommerceId commerceId = commerceBean.getId();
    String partNumber = commerceId.getExternalId().orElseThrow(() -> new InvalidIdException(format(commerceId)));
    String catalogAlias = commerceId.getCatalogAlias().value();
    if (commerceBean instanceof Product) {
      return String.format("livecontext/product/%s/%s/NO_WS/%s", commerceBean.getContext().getSiteId(), catalogAlias, partNumber);
    } else if (commerceBean instanceof Category) {
      return String.format("livecontext/category/%s/%s/NO_WS/%s", commerceBean.getContext().getSiteId(), catalogAlias, partNumber);

    }
    return null;
  }
}