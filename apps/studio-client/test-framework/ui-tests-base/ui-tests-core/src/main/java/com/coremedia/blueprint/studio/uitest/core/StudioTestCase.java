package com.coremedia.blueprint.studio.uitest.core;

import com.coremedia.cms.integration.test.util.CleanupRegistries;
import com.coremedia.studio.test.dynamicpkg.DynamicModulesTestService;
import com.coremedia.testing.junit.LabelCondition;
import com.coremedia.testing.junit.RerunFailingTestsCountWorkaroundRule;
import com.coremedia.testing.logging.LogbackMdcTestName;
import com.coremedia.uitesting.junit.webdriver.AbstractWebDriverTestCase;
import com.coremedia.uitesting.uapi.helper.UapiTestSetupRule;
import com.coremedia.uitesting.webdriver.CoreMediaWebDriverProvider;
import edu.umd.cs.findbugs.annotations.Nullable;
import net.joala.bdd.watcher.JUnitScenarioWatcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static com.coremedia.testing.junit.LabelCondition.denyLabel;
import static com.coremedia.uitesting.uapi.helper.ConsoleLogAssert.assertThat;
import static com.coremedia.uitesting.uapi.helper.XssAssert.assertThat;

@SuppressWarnings({"SpringAutowiredFieldsWarningInspection", "squid:S3306"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:**/release-testing-context.xml", initializers = StudioTestCaseInitializer.class)
public abstract class StudioTestCase extends AbstractWebDriverTestCase {
  @Value("${studio.main.url}")
  private String jooProxyTargetUri;

  @Value("${studio.proxy.port:0}")
  private Integer studioProxyPort;

  @Nullable
  private DynamicModulesTestService dynamicModulesTestService;

  protected static final String MEDIA_BLUEPRINT_75_IDENTIFIER = "mbp75";

  @Rule
  @Inject
  public JUnitScenarioWatcher jUnitScenarioWatcher; // NOSONAR rule fields must be public

  private final TestRule logbackMdcTestName = new LogbackMdcTestName();

  @Inject
  private CoreMediaWebDriverProvider driverProvider;

  @Inject
  private UapiTestSetupRule uapiTestSetupRule;

  @Inject
  private CleanupRegistries cleanupRegistries;

  private final LabelCondition labelCondition = denyLabel(StudioTestLabels.KNOWN_ISSUE_PATTERN, "Test fails because of known product issue.");

  private final TestRule rerunFailingTestsCountWorkaroundRule = new RerunFailingTestsCountWorkaroundRule();

  @Override
  @PostConstruct
  public void initRuleChain() {
    super.initRuleChain();
    /*
     * The order of execution in RuleChain depend on test lifecycle state:
     *
     * On set up test execution will start with the outer rule and end with
     * the last rule.
     *
     * On tear down execution will start to tear down with the last rule and
     * end tear down with the outer rule.
     *
     * As the original ruleChain is last, this means, that for example the
     * browser is started after the UAPI setup passed and it will shutdown
     * prior to any UAPI cleanup.
     */
    ruleChain = RuleChain
            // Start Only: Skip tests with known issues. (Introduced for Ext3 to Ext6 migration)
            .outerRule(labelCondition)
            // Start/Stop: Switch diagnostic context for logging based on currently running test. Required to split log-files per test.
            .around(logbackMdcTestName)
            // Stop Only: Rerun Test on Failure.
            .around(rerunFailingTestsCountWorkaroundRule)
            // Stop Only: Remove artifacts
            .around(cleanupRegistries)
            // Start/Stop: Prepare test-user et al. and clean-up afterwards
            .around(uapiTestSetupRule)
            // Contains any WebDriver specific setup and teardown.
            .around(ruleChain);
  }

  /**
   * Starts {@link DynamicModulesTestService} for the given dynamic packages.
   * Any previously service is stopped and it is ensured, that the service is
   * stopped automatically after each test execution.
   *
   * @param dynamicPackagesIncludes packages to include
   * @return service
   */
  protected DynamicModulesTestService startDynamicModulesTestService(String... dynamicPackagesIncludes) {
    if (dynamicModulesTestService != null) {
      dynamicModulesTestService.stop();
    }
    dynamicModulesTestService = DynamicModulesTestService.builder(jooProxyTargetUri)
            .setProxyPort(studioProxyPort)
            .setDynamicPackagesIncludes(dynamicPackagesIncludes)
            .start();
    return dynamicModulesTestService;
  }

  @After
  public void tearDownBase() {
    assertThat(driverProvider.get()).hasNoRecordedXssViolations();
    assertThat(getJsErrorCaptor()).hasNoConsoleErrors();
    if (dynamicModulesTestService != null) {
      dynamicModulesTestService.stop();
      dynamicModulesTestService = null;
    }
  }

}
