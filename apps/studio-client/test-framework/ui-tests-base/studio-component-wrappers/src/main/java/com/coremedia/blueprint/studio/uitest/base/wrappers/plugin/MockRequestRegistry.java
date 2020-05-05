package com.coremedia.blueprint.studio.uitest.base.wrappers.plugin;

import com.coremedia.uitesting.webdriver.JsProxy;
import com.coremedia.uitesting.webdriver.access.JsProxyBean;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import net.joala.condition.Condition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

/**
 * Provokes remote errors. Requires MockRequestsPlugin to be installed.
 */
// squid:S1192: We want to repeat String literals and not extract it to constants
@SuppressWarnings({"UnusedReturnValue", "unused", "squid:S1192", "JSUnresolvedFunction", "JSUnresolvedVariable"})
@JsProxyBean(expression = "com.acme.coremedia.studio.request.MOCK_REQUEST_REGISTRY")
@Scope(BeanDefinition.SCOPE_SINGLETON)
@DefaultAnnotation(NonNull.class)
public class MockRequestRegistry extends JsProxy {
  /**
   * Queries the number of times the specific mock request got sent.
   *
   * @param id id of the mock request as returned by the register methods
   * @return condition
   */
  public Condition<Long> sentCount(String id) {
    return longCondition("self.getSent(id)", "id", id);
  }

  /**
   * Delete this very mock request/response.
   *
   * @param id id of the mock request as returned by the register methods
   */
  public void deleteMock(String id) {
    evalVoid("self.deleteMock(id)", "id", id);
  }

  /**
   * Clears all registered mock requests. May be used as alternative to count-down feature for explicit requests
   * to simulate normal operation of a system after previous failure.
   */
  public void clearMockRequests() {
    requireMockRequestsPlugin();
    evalVoid("self.clearMockRequests()");
  }

  /**
   * Provokes a failure similar as it would have been raised by {@link com.coremedia.rest.validators.RegExpValidator}.
   *
   * @param propertyName property to validate
   * @param validPattern pattern which must match the value to pass; otherwise the error will be provoked
   * @return ID of the added mock request; may be used to query state or to delete this very mock
   */
  public String registerMockRegExpValidator(String propertyName, String validPattern) {
    requireMockRequestsPlugin();
    return evalString("self.registerMockRegExpValidator(propertyName, validPattern)",
            "propertyName", propertyName,
            "validPattern", validPattern
    );
  }

  /**
   * @return ID of the added mock request; may be used to query state or to delete this very mock
   */
  public String registerFeedbackGroupsMockRequest(String groupsString) {
    requireMockRequestsPlugin();
    return evalString("self.registerFeedbackGroupsMockRequest(groupsString)",
            "groupsString", groupsString);
  }

  /**
   * @return ID of the added mock request; may be used to query state or to delete this very mock
   */
  public String registerJobServiceMockRequest(String jobType, String jobId) {
    requireMockRequestsPlugin();
    return evalString("self.registerJobServiceMockRequest(jobType, jobId)",
            "jobType", jobType, "jobId", jobId);
  }

  /**
   * @return ID of the added mock request; may be used to query state or to delete this very mock
   */
  public String registerJobMockRequest(String jobId, String httpCode, String resultString) {
    requireMockRequestsPlugin();
    return evalString("self.registerJobMockRequest(jobId, httpCode, resultString)",
            "jobId", jobId, "httpCode", httpCode, "resultString", resultString);
  }

  /**
   * Provokes a validation issue as if the validator has thrown an exception.
   *
   * @param propertyName     property to validate
   * @param provokeValue     value the property shall reach in order to provoke the exception
   * @param exceptionMessage exception message
   * @return ID of the added mock request; may be used to query state or to delete this very mock
   */
  public String registerMockIssuesDetectedInternalError(String propertyName, Object provokeValue, String exceptionMessage) {
    return registerMockIssuesDetectedInternalError(propertyName, provokeValue, exceptionMessage, 0);
  }

  /**
   * Provokes a validation issue as if the validator has thrown an exception.
   *
   * @param propertyName     property to validate
   * @param provokeValue     value the property shall reach in order to provoke the exception
   * @param exceptionMessage exception message
   * @param countDown        how many times to create such a mock response; any value <= 0 will not stop mocking
   * @return ID of the added mock request; may be used to query state or to delete this very mock
   */
  public String registerMockIssuesDetectedInternalError(String propertyName, Object provokeValue, String exceptionMessage, int countDown) {
    requireMockRequestsPlugin();
    return evalString("self.registerMockIssuesDetectedInternalError(propertyName, provokeValue, exceptionMessage, countDown)",
            "propertyName", propertyName,
            "provokeValue", provokeValue,
            "exceptionMessage", exceptionMessage,
            "countDown", countDown
    );
  }

  /**
   * Registers a mocked temporary error for access to Structs. If it is a write or read request depends
   * on the method parameter. Note, that Structs are written as an extra request just as other blob
   * properties but in contrast to for example String properties.
   *
   * @param propertyName   the Struct property name to mock status codes for; use empty String for <em>any</em>
   * @param expectedMethod a method to match, for example {@code GET}, or {@code PUT} (case does not matter)
   * @param mockStatus     the status code to answer in response; body will be empty
   * @param countDown      how many times to create such a mock response; any value <= 0 will not stop mocking
   * @return ID of the added mock request; may be used to query state or to delete this very mock
   */
  public String registerErrorStatusOnStructAccess(String propertyName, String expectedMethod, int mockStatus, int countDown) {
    requireMockRequestsPlugin();
    return evalString("self.registerErrorStatusOnStructAccess(propertyName, expectedMethod, mockStatus, countDown)",
            "propertyName", propertyName,
            "expectedMethod", expectedMethod,
            "mockStatus", mockStatus,
            "countDown", countDown
    );
  }

  private void requireMockRequestsPlugin() {
    exists().withMessage("MockRequestsPlugin plugin does not seem to be installed. Please use dynamic packages service, to install it.").waitUntilTrue();
  }
}
