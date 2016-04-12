package com.coremedia.livecontext.ecommerce.ibm.search;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractServiceTest;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.search.SuggestionResult;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertTrue;

@ContextConfiguration(classes = AbstractServiceTest.LocalConfig.class)
@ActiveProfiles(AbstractServiceTest.LocalConfig.PROFILE)
public class SearchServiceImplIT extends AbstractServiceTest {
  @Inject
  SearchServiceImpl testling;

  @Before
  public void setup() {
    super.setup();
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
  }

  @Test
  @Betamax(tape = "ssi_testGetAutocompleteSuggestions", match = {MatchRule.path, MatchRule.query})
  public void testGetAutocompleteSuggestions() {
    if (StoreContextHelper.getWcsVersion(StoreContextHelper.getCurrentContext()) < StoreContextHelper.WCS_VERSION_7_7) {
      return;
    }
    List<SuggestionResult> suggestions = testling.getAutocompleteSuggestions("dres");
    assertTrue(!suggestions.isEmpty());
  }
}
