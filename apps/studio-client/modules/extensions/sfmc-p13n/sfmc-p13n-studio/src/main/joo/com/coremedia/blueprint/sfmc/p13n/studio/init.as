package com.coremedia.blueprint.sfmc.p13n.studio {
import com.coremedia.blueprint.base.sfmc.p13n.studio.model.JourneyImpl;
import com.coremedia.blueprint.base.sfmc.p13n.studio.model.JourneysImpl;
import com.coremedia.ui.data.impl.BeanFactoryImpl;

public function init():void {
  BeanFactoryImpl.initBeanFactory().registerRemoteBeanClasses(JourneyImpl, JourneysImpl);
}
}
