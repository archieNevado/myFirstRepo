package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class CaeAssetValidationService implements AssetValidationService {

  private ValidationService<ContentBean> validationService;
  private ContentBeanFactory contentBeanFactory;

  @Override
  public List<Content> filterAssets(List<Content> source) {
    List<? extends ContentBean> filteredAssetsAsContentBeans = validationService.filterList(contentBeanFactory.createBeansFor(source, ContentBean.class));
    return toContent(filteredAssetsAsContentBeans);
  }

  public void setValidationService(ValidationService<ContentBean> validationService) {
    this.validationService = validationService;
  }

  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @PostConstruct
  void initialize() {
    if (contentBeanFactory == null) {
      throw new IllegalStateException("Required property not set: contentBeanFactory");
    }
    if (validationService == null) {
      throw new IllegalStateException("Required property not set: validationService");
    }
  }

  public static List<Content> toContent(List<? extends ContentBean> contentBeans) {
    ArrayList<Content> contents = new ArrayList<>(contentBeans.size());
    for (ContentBean contentBean : contentBeans) {
      contents.add(contentBean.getContent());
    }
    return contents;
  }

}
