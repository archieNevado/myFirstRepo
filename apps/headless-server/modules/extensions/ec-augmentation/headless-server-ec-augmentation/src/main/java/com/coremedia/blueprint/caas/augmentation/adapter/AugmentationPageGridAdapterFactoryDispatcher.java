package com.coremedia.blueprint.caas.augmentation.adapter;

import com.coremedia.blueprint.base.caas.model.adapter.PageGridAdapter;
import com.coremedia.blueprint.caas.augmentation.model.Augmentation;
import com.coremedia.blueprint.caas.augmentation.model.CmsOnlyType;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRef;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import graphql.schema.DataFetchingEnvironment;

@DefaultAnnotation(NonNull.class)
public class AugmentationPageGridAdapterFactoryDispatcher {

  private final AugmentationPageGridAdapterFactory factoryWithCommerce;
  private final AugmentationPageGridAdapterFactoryCmsOnly factoryCmsOnly;

  public AugmentationPageGridAdapterFactoryDispatcher(AugmentationPageGridAdapterFactory augmentationPageGridAdapterFactory, AugmentationPageGridAdapterFactoryCmsOnly factoryCmsOnly) {
    this.factoryWithCommerce = augmentationPageGridAdapterFactory;
    this.factoryCmsOnly = factoryCmsOnly;
  }

  public PageGridAdapter to(Augmentation augmentation, DataFetchingEnvironment dataFetchingEnvironment) {
    CommerceRef commerceRef = augmentation.getCommerceRef();
    if (augmentation instanceof CmsOnlyType){
      return factoryCmsOnly.to(commerceRef, dataFetchingEnvironment);
    }
    return factoryWithCommerce.to(commerceRef, dataFetchingEnvironment);
  }
}
