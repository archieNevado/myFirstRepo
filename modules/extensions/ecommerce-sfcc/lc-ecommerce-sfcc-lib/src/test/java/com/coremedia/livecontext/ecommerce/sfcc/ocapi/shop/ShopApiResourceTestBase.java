package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.OCAPITestBase;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Base class for all data API resource tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ShopApiTestConfiguration.class)
@TestPropertySource(properties = "livecontext.sfcc.ocapi.shopBasePath=/s/{storeId}/dw/shop/")
public abstract class ShopApiResourceTestBase extends OCAPITestBase {

}
