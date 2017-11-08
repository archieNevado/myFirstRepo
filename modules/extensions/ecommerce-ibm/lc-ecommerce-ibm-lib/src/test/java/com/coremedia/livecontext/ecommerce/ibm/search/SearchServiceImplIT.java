package com.coremedia.livecontext.ecommerce.ibm.search;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.search.SuggestionResult;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.List;

import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_7;
import static org.junit.Assert.assertFalse;

@ContextConfiguration(classes = IbmServiceTestBase.LocalConfig.class)
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
public class SearchServiceImplIT extends IbmServiceTestBase {
  @Inject
  SearchServiceImpl testling;

  @Before
  @Override
  public void setup() {
    super.setup();
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
  }

  @Test
  @Betamax(tape = "ssi_testGetAutocompleteSuggestions", match = {MatchRule.path, MatchRule.query})
  public void testGetAutocompleteSuggestions() {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    if (StoreContextHelper.getWcsVersion(currentContext).lessThan(WCS_VERSION_7_7)) {
      return;
    }
    List<SuggestionResult> suggestions = testling.getAutocompleteSuggestions("dres", currentContext);
    assertFalse(suggestions.isEmpty());
  }
}
