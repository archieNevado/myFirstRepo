package com.acme.coremedia.studio.request {
import com.acme.coremedia.studio.request.predicate.DataPredicate;
import com.acme.coremedia.studio.request.predicate.MethodPredicate;
import com.acme.coremedia.studio.request.predicate.UrlPredicate;
import com.acme.coremedia.studio.request.support.MockRequestResponse;
import com.acme.coremedia.studio.request.support.MockResponses;
import com.acme.coremedia.studio.request.support.RequestOptionsMatcher;
import com.coremedia.ui.logging.Logger;

import ext.JSON;

/**
 * Registry for MockRequests. Meant to be called from Java API in tests. That is why method parameters need to be
 * of simple types such as Strings for example.
 */
public class MockRequestRegistry {
  private var _mockRequests:Object = {};

  public function MockRequestRegistry() {
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Retrieve number of times the given request has been sent.
   *
   * @param id id of the MockRequestResponse
   * @return number of times the request has been sent; 0 if never or if the mock with the given ID does not exist
   */
  public function getSent(id:String):uint {
    if (_mockRequests.hasOwnProperty(id)) {
      return _mockRequests[id].sent;
    }
    return 0;
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Clears all mock requests.
   */
  public function clearMockRequests():void {
    _mockRequests = {};
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Delete a specific Mock.
   * @param id the mock to delete/disable.
   */
  public function deleteMock(id:String):void {
    if (_mockRequests.hasOwnProperty(id)) {
      delete _mockRequests[id];
    }
  }

  /**
   * Find an applicable Mock response. If not found, null will be returned.
   * @param requestOptions options to match
   * @return mock request generator, or null, if the request shall not be mocked
   */
  public function findApplicableMock(requestOptions:Object):MockRequestResponse {
    Logger.debug("Searching applicable mock requests.");
    for (var id:String in _mockRequests) {
      var candidate:MockRequestResponse = _mockRequests[id];
      if (candidate.isApplicable(requestOptions)) {
        Logger.debug("Found applicable mock with ID '" + id + "' for request options: " + JSON.encode(requestOptions));
        return candidate;
      }
    }
    return null;
  }

  /**
   * Creates a unique ID which may be used for more fine grained control of mocks. You may track, if
   * mocks actually got triggered, or you may explicitly remove a given mock by id.
   *
   * @return unique ID
   */
  private static function createUniqueId():String {
    // https://gist.github.com/6174/6062387
    return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
  }

  /**
   * Adds an additional mock and returns its ID.
   *
   * @param mockRequestResponse mock to add
   * @return unique ID of the mock
   */
  private function addMockRequestResponse(mockRequestResponse:MockRequestResponse):String {
    var id:String = createUniqueId();
    _mockRequests[id] = mockRequestResponse;
    return id;
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Registers a mock RegExpValidator.
   * @param propertyName property to validate
   * @param validPattern pattern, will cause raising a mocked issue if it does not match the given value
   */
  public function registerMockRegExpValidator(propertyName:String, validPattern:String):String {
    var regExp:RegExp = new RegExp(validPattern);

    var matcher:RequestOptionsMatcher = new RequestOptionsMatcher();
    matcher.urlMatcher = UrlPredicate.contentApiRequest;
    matcher.methodMatcher = MethodPredicate.putRequest;
    // The request shall be mocked when the regular expression does not match (as it is a "valid" pattern the logic is reversed).
    matcher.dataMatcher = function (actual:String):Boolean {
      return DataPredicate.matchesContentProperty(actual, propertyName, function (value:String):Boolean {
        return !regExp.test(value);
      })
    };

    Logger.info("Installing mocked RegExpValidator for property: " + propertyName + ", using pattern: " + validPattern);

    return addMockRequestResponse(new MockRequestResponse(
            matcher,
            403,
            function (requestOptions:Object):Object {
              return MockResponses.issuesDetectedError(requestOptions, 'RegExpValidator', propertyName, [validPattern]);
            }
    ));
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Registers a mock validation error where an exception is raised by the validator (the exception
   * message will be part of the arguments array in the response).
   *
   * @param propertyName property to validate
   * @param provokeValue value the property shall have in order to provoke the error
   * @param exceptionMessage message of the provoked exception
   * @param countDown how many times to create such a mock response; any value <= 0 will not stop mocking
   */
  public function registerMockIssuesDetectedInternalError(propertyName:String, provokeValue:*, exceptionMessage:String, countDown:uint = 0):String {
    var matcher:RequestOptionsMatcher = new RequestOptionsMatcher();
    matcher.urlMatcher = UrlPredicate.contentApiRequest;
    matcher.methodMatcher = MethodPredicate.putRequest;
    matcher.dataMatcher = function (actual:String):Boolean {
      return DataPredicate.matchesContentProperty(actual, propertyName, function (value:*):Boolean {
        return provokeValue === value;
      })
    };

    Logger.info("Installing mocked issue detection with exception for property: " + propertyName + ", provoke value: " + provokeValue + ", mock exception message: " + exceptionMessage);

    return addMockRequestResponse(new MockRequestResponse(
            matcher,
            403,
            function (requestOptions:Object):Object {
              return MockResponses.issuesDetectedError(requestOptions, 'internalError', propertyName, [exceptionMessage]);
            },
            countDown
    ));
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Registers a mocked temporary error for access to Structs. If it is a write or read request depends
   * on the method parameter. Note, that Structs are written as an extra request just as other blob
   * properties but in contrast to for example String properties.
   *
   * @param propertyName the Struct property name to mock status codes for; use empty String for <em>any</em>
   * @param expectedMethod a method to match, for example <code>GET</code>, or <code>PUT</code> (case does not matter)
   * @param mockStatus the status code to answer in response; body will be empty
   * @param countDown how many times to create such a mock response; any value <= 0 will not stop mocking
   */
  public function registerErrorStatusOnStructAccess(propertyName:String, expectedMethod:String, mockStatus:uint, countDown:uint = 0):String {
    var urlMatcherPattern:RegExp = new RegExp('.*api/content/[0-9]*/structs/' + propertyName + '.*');
    var matcher:RequestOptionsMatcher = new RequestOptionsMatcher();
    matcher.urlMatcher = function (actual:String):Boolean {
      return actual && urlMatcherPattern.test(actual);
    };
    matcher.methodMatcher = function (actual:String):Boolean {
      return actual && actual.toUpperCase() === expectedMethod.toUpperCase();
    };

    Logger.info("Installed mocked error status for Struct access for property: " + propertyName + ", method: " + expectedMethod + ", mock status: " + mockStatus);

    return addMockRequestResponse(new MockRequestResponse(
            matcher,
            mockStatus,
            function (requestOptions:Object):Object {
              return MockResponses.empty();
            },
            countDown
    ));
  }

  //noinspection JSUnusedGlobalSymbols
  public function registerFeedbackGroupsMockRequest(groupsString:String):String {
    var groups:Object = JSON.decode(groupsString);

    var matcher:RequestOptionsMatcher = new RequestOptionsMatcher();
    matcher.urlMatcher = UrlPredicate.feedbackGroupsRequest;
    matcher.methodMatcher = MethodPredicate.getRequest;

    return addMockRequestResponse(new MockRequestResponse(
            matcher,
            200,
            function ():Object {
              return MockResponses.feedbackGroups(groups);
            }
    ));
  }

  //noinspection JSUnusedGlobalSymbols
  public function registerJobServiceMockRequest(jobType:String, jobId:String):String {
    var matcher:RequestOptionsMatcher = new RequestOptionsMatcher();
    matcher.urlMatcher = UrlPredicate.jobServiceRequest(jobType);
    matcher.methodMatcher = MethodPredicate.postRequest;

    return addMockRequestResponse(new MockRequestResponse(
            matcher,
            200,
            function ():Object {
              return MockResponses.jobReference(jobId);
            }
    ));
  }

  //noinspection JSUnusedGlobalSymbols
  public function registerJobMockRequest(jobId:String, httpCode:String, resultString:String):String {
    var result:Object = JSON.decode(resultString);

    var matcher:RequestOptionsMatcher = new RequestOptionsMatcher();
    matcher.urlMatcher = UrlPredicate.jobRequest(jobId);
    matcher.methodMatcher = MethodPredicate.getRequest;

    return addMockRequestResponse(new MockRequestResponse(
            matcher,
            parseInt(httpCode),
            function ():Object {
              return MockResponses.jobResult(result);
            }
    ));
  }
}
}
