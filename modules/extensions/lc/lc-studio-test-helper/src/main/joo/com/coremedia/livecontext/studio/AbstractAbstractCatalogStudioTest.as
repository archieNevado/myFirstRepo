package com.coremedia.livecontext.studio {
import com.coremedia.cap.content.impl.ContentImpl;
import com.coremedia.cap.content.impl.ContentStructRemoteBeanImpl;
import com.coremedia.cap.content.impl.ContentTypeImpl;
import com.coremedia.ecommerce.studio.*;
import com.coremedia.ecommerce.studio.model.*;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.impl.BeanFactoryImpl;

public class AbstractAbstractCatalogStudioTest extends AbstractCatalogTest {

  override public function setUp():void {
    super.setUp();

    resetCatalogHelper();

    BeanFactoryImpl(beanFactory).registerRemoteBeanClasses(ContentTypeImpl, ContentImpl, ContentStructRemoteBeanImpl,
            StoreImpl, CategoryImpl, MarketingImpl, ProductImpl, MarketingSpotImpl, ProductVariantImpl);

    createPlugin();
  }

  protected function createPlugin():void {
    // Effectively abstract, must be implemented
  }
}
}