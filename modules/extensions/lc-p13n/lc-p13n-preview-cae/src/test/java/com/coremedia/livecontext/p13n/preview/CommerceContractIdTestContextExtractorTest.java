package com.coremedia.livecontext.p13n.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.personalization.contentbeans.CMUserProfile;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.newStoreContext;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommerceContractIdTestContextExtractorTest {

  private CommerceContractIdTestContextExtractor testling;

  @Mock
  private Content content;

  @Mock
  private CMUserProfile cmUserProfile;

  @Mock
  private Map<String, Object> profileExtensions, properties, commerce;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Spy
  private BaseCommerceConnection commerceConnection;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Before
  public void setUp() throws Exception {
    testling = new CommerceContractIdTestContextExtractor();
    testling.setContentBeanFactory(contentBeanFactory);

    StoreContextImpl storeContext = newStoreContext();

    when(storeContextProvider.buildContext(storeContext)).thenReturn(StoreContextBuilderImpl.from(storeContext));

    commerceConnection.setStoreContext(storeContext);
    commerceConnection.setStoreContextProvider(storeContextProvider);
    CurrentCommerceConnection.set(commerceConnection);
  }

  @After
  public void teardown() {
    CurrentCommerceConnection.remove();
  }

  @Test
  public void testExtractTestContextsFromContent() {
    List<String> userContractIds = ImmutableList.of("contract1", "contract2");
    List<String> contracts = ImmutableList.of(
            "vendor:///catalog/contract/contract1",
            "vendor:///catalog/contract/contract2");

    when(contentBeanFactory.createBeanFor(content)).thenReturn(cmUserProfile);
    when(cmUserProfile.getProfileExtensions()).thenReturn(profileExtensions);
    when(profileExtensions.get(CommerceContractIdTestContextExtractor.PROPERTIES_PREFIX)).thenReturn(properties);
    when(properties.get(CommerceContractIdTestContextExtractor.COMMERCE_CONTEXT)).thenReturn(commerce);
    when(commerce.get(CommerceContractIdTestContextExtractor.USER_CONTRACT_PROPERTY)).thenReturn(contracts);

    testling.extractTestContextsFromContent(content, null);

    StoreContext storeContext = commerceConnection.getStoreContext();
    assertEquals(userContractIds, storeContext.getContractIdsForPreview());
  }
}
