package com.coremedia.blueprint.common.navigation.context.finder;

import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.base.navigation.context.finder.ContextFinder;
import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

public class DelegateStrategyBeanContextFinder implements ContextFinder<ContentBean, ContentBean> {
  private ContextStrategy<Content, Content> delegate;
  private ContentBeanFactory contentBeanFactory;
  private DataViewFactory dataViewFactory;


  // --- configure --------------------------------------------------

  public void setDelegate(ContextStrategy<Content, Content> delegate) {
    this.delegate = delegate;
  }

  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  public void setDataViewFactory(DataViewFactory dataViewFactory) {
    this.dataViewFactory = dataViewFactory;
  }

  @PostConstruct
  void initialize() {
    if (contentBeanFactory == null) {
      throw new IllegalStateException("Required property not set: contentBeanFactory");
    }
    if (dataViewFactory == null) {
      throw new IllegalStateException("Required property not set: dataViewFactory");
    }
    if (delegate == null) {
      throw new IllegalStateException("Required property not set: delegate");
    }
  }

  // --- ContextFinder ----------------------------------------------

  @Override
  public final List<ContentBean> findContextsFor(ContentBean linkable) {
    return findContextsFor(linkable, null);
  }

  @Override
  public List<ContentBean> findContextsFor(ContentBean linkable, ContentBean currentContext) {
    if (linkable==null) {
      return Collections.emptyList();
    }
    Content currentContextContent = currentContext==null ? null : currentContext.getContent();
    List<Content> contents = delegate.findContextsFor(linkable.getContent(), currentContextContent);
    List<ContentBean> contentBeans = contentBeanFactory.createBeansFor(contents, ContentBean.class);
    return dataViewFactory.loadAllCached(contentBeans, null);
  }
}
