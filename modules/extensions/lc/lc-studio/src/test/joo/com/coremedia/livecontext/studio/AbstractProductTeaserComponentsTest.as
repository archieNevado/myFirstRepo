package com.coremedia.livecontext.studio {
import com.coremedia.livecontext.studio.library.LivecontextCollectionViewActionsPlugin;

public class AbstractProductTeaserComponentsTest extends AbstractAbstractProductTeaserComponentsTest {
  override protected function createPlugin():void {
    new LivecontextCollectionViewActionsPlugin();
  }
}
}