package com.coremedia.ecommerce.studio {
import com.coremedia.ecommerce.studio.model.CatalogImpl;
import com.coremedia.ecommerce.studio.model.CategoryImpl;
import com.coremedia.ecommerce.studio.model.CommerceBeanPreviewsImpl;
import com.coremedia.ecommerce.studio.model.ContractImpl;
import com.coremedia.ecommerce.studio.model.ContractsImpl;
import com.coremedia.ecommerce.studio.model.FacetsImpl;
import com.coremedia.ecommerce.studio.model.MarketingImpl;
import com.coremedia.ecommerce.studio.model.MarketingSpotImpl;
import com.coremedia.ecommerce.studio.model.ProductImpl;
import com.coremedia.ecommerce.studio.model.ProductVariantImpl;
import com.coremedia.ecommerce.studio.model.SearchFacetsImpl;
import com.coremedia.ecommerce.studio.model.SegmentImpl;
import com.coremedia.ecommerce.studio.model.SegmentsImpl;
import com.coremedia.ecommerce.studio.model.StoreImpl;
import com.coremedia.ecommerce.studio.model.WorkspaceImpl;
import com.coremedia.ecommerce.studio.model.WorkspacesImpl;
import com.coremedia.ui.data.impl.BeanFactoryImpl;

public function init():void {
  BeanFactoryImpl.initBeanFactory().registerRemoteBeanClasses(
          CategoryImpl,
          StoreImpl,
          CatalogImpl,
          ProductImpl,
          ProductVariantImpl,
          SegmentImpl,
          SegmentsImpl,
          ContractImpl,
          ContractsImpl,
          WorkspaceImpl,
          WorkspacesImpl,
          MarketingSpotImpl,
          MarketingImpl,
          FacetsImpl,
          SearchFacetsImpl,
          CommerceBeanPreviewsImpl
  );
}
}
