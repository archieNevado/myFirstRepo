package com.coremedia.livecontext.studio {
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ecommerce.studio.AbstractCatalogTest;
import com.coremedia.livecontext.studio.library.LivecontextCollectionViewActionsPlugin;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.beanFactory;

public class AbstractLiveContextStudioTest extends AbstractCatalogTest {
  protected var preferences:Bean;

  override public function setUp():void {
    super.setUp();

    preferences = beanFactory.createLocalBean();
    editorContext['setPreferences'](preferences);

    resetCatalogHelper();

    new LivecontextCollectionViewActionsPlugin();
  }
}
}
