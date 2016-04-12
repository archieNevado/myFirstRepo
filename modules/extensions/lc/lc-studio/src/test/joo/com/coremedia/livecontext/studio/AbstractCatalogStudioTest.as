package com.coremedia.livecontext.studio {
import com.coremedia.livecontext.studio.library.LivecontextCollectionViewActionsPlugin;

public class AbstractCatalogStudioTest extends AbstractAbstractCatalogStudioTest {
  override protected function createPlugin():void {
    new LivecontextCollectionViewActionsPlugin();
  }
}
}