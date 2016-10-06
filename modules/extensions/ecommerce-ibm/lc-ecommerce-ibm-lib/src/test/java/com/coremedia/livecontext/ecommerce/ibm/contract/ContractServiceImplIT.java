package com.coremedia.livecontext.ecommerce.ibm.contract;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.contract.Contract;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractServiceTest;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.Collection;

import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_7;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(classes = AbstractServiceTest.LocalConfig.class)
@ActiveProfiles(AbstractServiceTest.LocalConfig.PROFILE)
public class ContractServiceImplIT extends AbstractServiceTest {

  @Inject
  ContractServiceImpl testling;

  @Betamax(tape = "contract_testFindContractIdsForUser", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindContractIdsForUser() throws Exception {
    StoreContextHelper.setCurrentContext(testConfig.getB2BStoreContext());
    UserContext userContext = userContextProvider.createContext(testConfig.getUser2Name());
    UserContextHelper.setCurrentContext(userContext);

    Collection<Contract> contractIdsForUser = testling.findContractIdsForUser(UserContextHelper.getCurrentContext(), StoreContextHelper.getCurrentContext());
    assertNotNull(contractIdsForUser);

    if (WCS_VERSION_7_7.lessThan(StoreContextHelper.getWcsVersion(testConfig.getB2BStoreContext()))) {
      assertFalse("number of eligible contracts should be more than zero", contractIdsForUser.isEmpty());
    } else {
      assertTrue("number of eligible contracts should be zero", contractIdsForUser.isEmpty());
    }
  }

  @Betamax(tape = "contract_testFindContractIdsForUser", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindContractIdsForPreviewUser() throws Exception {
    StoreContextHelper.setCurrentContext(testConfig.getB2BStoreContext());
    UserContext userContext = userContextProvider.createContext(testConfig.getPreviewUserName());
    UserContextHelper.setCurrentContext(userContext);

    Collection<Contract> contracts = testling.findContractIdsForUser(UserContextHelper.getCurrentContext(), StoreContextHelper.getCurrentContext());
    assertNotNull(contracts);

    if (WCS_VERSION_7_7.lessThan(StoreContextHelper.getWcsVersion(testConfig.getB2BStoreContext()))) {
      assertEquals(3, contracts.size());

      for (Contract contract : contracts) {
        assertTrue("contrat id has wrong format: " + contract.getId(), contract.getId().startsWith("ibm:///catalog/contract/4000"));
      }
    }
  }

  @Betamax(tape = "contract_testFindContractIdsForServiceUserWithNoServiceUser", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindContractIdsForServiceUserWithNoServiceUser() throws Exception {
    StoreContextHelper.setCurrentContext(testConfig.getB2BStoreContext());
    Collection<Contract> contracts = testling.findContractIdsForServiceUser(StoreContextHelper.getCurrentContext());
    assertNotNull(contracts);
    assertTrue(contracts.isEmpty());
  }

  @Betamax(tape = "contract_testFindContractById", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindContractById() throws Exception {
    StoreContextHelper.setCurrentContext(testConfig.getB2BStoreContext());
    UserContext userContext = userContextProvider.createContext(testConfig.getPreviewUserName());
    UserContextHelper.setCurrentContext(userContext);

    Collection<Contract> contracts = testling.findContractIdsForUser(UserContextHelper.getCurrentContext(), StoreContextHelper.getCurrentContext());
    assertNotNull(contracts);

    if (WCS_VERSION_7_7.lessThan(StoreContextHelper.getWcsVersion(testConfig.getB2BStoreContext()))) {
      for (Contract contract : contracts) {
        Contract contractById = testling.findContractById(contract.getExternalId());
        assertNotNull(contractById);
        assertEquals(contract.getExternalId(), contractById.getExternalId());
      }
    }
  }

  @Betamax(tape = "contract_testInvalidContract", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testInvalidContract() throws Exception {
    StoreContextHelper.setCurrentContext(testConfig.getB2BStoreContext());
    Contract testcontract = testling.findContractById("xxxx");
    assertNull(testcontract);
  }
}
