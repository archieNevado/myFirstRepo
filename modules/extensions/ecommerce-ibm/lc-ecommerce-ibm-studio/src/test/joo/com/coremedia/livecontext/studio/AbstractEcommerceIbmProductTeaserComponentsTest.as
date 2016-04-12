package com.coremedia.livecontext.studio {
import com.coremedia.livecontext.studio.library.EcommerceIbmCollectionViewActionsPlugin;

public class AbstractEcommerceIbmProductTeaserComponentsTest extends AbstractAbstractProductTeaserComponentsTest {
  override protected function createPlugin():void {
    new EcommerceIbmCollectionViewActionsPlugin();
  }
}
}