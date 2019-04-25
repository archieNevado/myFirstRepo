package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.OCAPITestBase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * Base class for all data API resource tests.
 */
@ContextConfiguration(classes = ShopApiTestConfiguration.class)
@TestPropertySource(properties = "livecontext.sfcc.ocapi.shopBasePath=/s/{storeId}/dw/shop/")
public abstract class ShopApiResourceTestBase extends OCAPITestBase {

}
