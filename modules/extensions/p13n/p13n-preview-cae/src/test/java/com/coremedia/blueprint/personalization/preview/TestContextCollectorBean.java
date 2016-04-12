package com.coremedia.blueprint.personalization.preview;

import com.coremedia.personalization.context.collector.SegmentSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class TestContextCollectorBean implements BeanPostProcessor {

  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    if(beanName.equals("segmentSource")) {
      ((SegmentSource)bean).setPathToSegments("/");
    }
    return bean;
  }

  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

}
