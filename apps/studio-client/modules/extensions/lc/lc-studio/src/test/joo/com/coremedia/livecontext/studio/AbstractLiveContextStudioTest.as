package com.coremedia.livecontext.studio {
import com.coremedia.cap.content.impl.ContentImpl;
import com.coremedia.cap.content.impl.ContentStructRemoteBeanImpl;
import com.coremedia.cap.content.impl.ContentTypeImpl;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ecommerce.studio.AbstractCatalogTest;
import com.coremedia.ecommerce.studio.model.CategoryImpl;
import com.coremedia.ecommerce.studio.model.MarketingImpl;
import com.coremedia.ecommerce.studio.model.MarketingSpotImpl;
import com.coremedia.ecommerce.studio.model.ProductImpl;
import com.coremedia.ecommerce.studio.model.ProductVariantImpl;
import com.coremedia.ecommerce.studio.model.StoreImpl;
import com.coremedia.livecontext.studio.library.LivecontextCollectionViewActionsPlugin;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.impl.BeanFactoryImpl;

public class AbstractLiveContextStudioTest extends AbstractCatalogTest {
  protected var preferences:Bean;

  override public function setUp():void {
    super.setUp();

    preferences = beanFactory.createLocalBean();
    editorContext['setPreferences'](preferences);

    resetCatalogHelper();

    BeanFactoryImpl(beanFactory).registerRemoteBeanClasses(ContentTypeImpl, ContentImpl, ContentStructRemoteBeanImpl,
            StoreImpl, CategoryImpl, MarketingImpl, ProductImpl, MarketingSpotImpl, ProductVariantImpl);

    new LivecontextCollectionViewActionsPlugin();
  }
}
}