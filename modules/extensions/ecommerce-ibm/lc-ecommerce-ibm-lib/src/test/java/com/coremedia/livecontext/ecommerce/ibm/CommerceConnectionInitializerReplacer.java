package com.coremedia.livecontext.ecommerce.ibm;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.cap.multisite.Site;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

/**
 * bean post processor to tweak the commerce connection initializer to simply return the thread local connection
 */
class CommerceConnectionInitializerReplacer implements BeanPostProcessor {
  @Override
  public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
    return o;
  }

  @Override
  public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
    if ("commerceConnectionHelper".equals(s) && o instanceof CommerceConnectionInitializer) {
      CommerceConnectionInitializer initializer = (CommerceConnectionInitializer) spy(o);
      doAnswer(invocationOnMock -> Optional.of(CurrentCommerceConnection.get()))
              .when(initializer).findConnectionForSite(any(Site.class));
      return initializer;
    }
    return o;
  }


}
