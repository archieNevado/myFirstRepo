package com.acme.coremedia.studio.request.support {
/**
 * Mock response (actually request) generator. First it will be checked, if this
 * mock response is applicable and if it is, it will generate a mocked XMLHttpRequest
 * created by a given provider. This XMLHttpRequest simulates an already answered
 * request, thus it will provide response data.
 */
public class MockRequestResponse implements RequestSentObserver {
  private var _requestOptionsMatcher:RequestOptionsMatcher;
  private var _mockStatus:uint;
  private var _mockResponseProvider:Function;
  private var _countDown:uint;
  private var _created:uint;
  private var _sent:uint;

  /**
   * Constructor.
   *
   * @param requestOptionsMatcher matcher to see, if the request shall be mocked.
   * @param mockStatus status code to use on mocking
   * @param mockResponseProvider response to provide on mocking
   * @param countDown how many times to create such a mock response; any value <= 0 will not stop mocking
   */
  public function MockRequestResponse(requestOptionsMatcher:RequestOptionsMatcher,
                                      mockStatus:uint,
                                      mockResponseProvider:Function,
                                      countDown:uint = 0) {
    _requestOptionsMatcher = requestOptionsMatcher;
    _mockStatus = mockStatus;
    _mockResponseProvider = mockResponseProvider;
    _countDown = countDown;
    _created = 0;
    _sent = 0;
  }

  /**
   * The number of times the mocked request has been sent.
   */
  public function get sent():uint {
    return _sent;
  }

  public function requestSent():void {
    _sent++;
  }

  /**
   * Validates if the request shall be mocked or not.
   * @param requestOptions options to decide if to mock or not
   * @return true if {@link #createResponse} shall be called to mock the request, false otherwise
   */
  public function isApplicable(requestOptions:Object):Boolean {
    if (_countDown <= 0 || _countDown - _created > 0) {
      return _requestOptionsMatcher.matches(requestOptions);
    }
    return false;
  }

  /**
   * Will create a mocked XMLHttpRequest which already includes the (mocked) response, which is later
   * interpreted by Ext's connection.
   * <p>
   * Note, that the request does not inherit XMLHttpRequest but just mocks those methods/properties
   * which are required during Ext JS processing. The mock may be incomplete, so it may need to be
   * extended for not yet tested use-cases.
   *
   * @param requestOptions options to create mock response, expected to contain properties url, method and data
   * @return mocked request
   */
  public function createResponse(requestOptions:Object):MockXMLHttpRequest {
    _created++;
    return new MockXMLHttpRequest(this, _mockStatus, _mockResponseProvider.call(this, requestOptions));
  }

}
}
