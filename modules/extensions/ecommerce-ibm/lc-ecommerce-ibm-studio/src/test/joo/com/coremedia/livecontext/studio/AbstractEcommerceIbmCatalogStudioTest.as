package com.coremedia.livecontext.studio {
import com.coremedia.livecontext.studio.library.EcommerceIbmCollectionViewActionsPlugin;

public class AbstractEcommerceIbmCatalogStudioTest extends AbstractAbstractCatalogStudioTest {
  override protected function createPlugin():void {
    new EcommerceIbmCollectionViewActionsPlugin();
  }
}
}