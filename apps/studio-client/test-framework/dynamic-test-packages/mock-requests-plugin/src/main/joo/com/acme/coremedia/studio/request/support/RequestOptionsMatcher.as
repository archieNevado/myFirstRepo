package com.acme.coremedia.studio.request.support {
import com.acme.coremedia.studio.request.predicate.CommonRequestOptionsPredicate;

/**
 * Matcher for <code>requestOptions</code>.
 */
public class RequestOptionsMatcher {
  private var _urlMatcher:Function = CommonRequestOptionsPredicate.ALWAYS_TRUE;
  private var _methodMatcher:Function = CommonRequestOptionsPredicate.ALWAYS_TRUE;
  private var _dataMatcher:Function = CommonRequestOptionsPredicate.ALWAYS_TRUE;


  public function RequestOptionsMatcher() {
  }

  /**
   * Set matcher for the URL. Will receive for example <code>api/content/12</code> as input.
   * @param value predicate function
   */
  public function set urlMatcher(value:Function):void {
    _urlMatcher = value;
  }

  /**
   * Set matcher for the method. Will receive for example <code>PUT</code> as input.
   * @param value predicate function
   */
  public function set methodMatcher(value:Function):void {
    _methodMatcher = value;
  }

  /**
   * Set matcher for the data object. Will receive for example stringified JSON as input.
   * @param value predicate function
   */
  public function set dataMatcher(value:Function):void {
    _dataMatcher = value;
  }

  /**
   * Determines if this matcher matches.
   * @param requestOptions
   * @return
   */
  public function matches(requestOptions:Object):Boolean {
    if (!_urlMatcher.call(this, requestOptions['url'])) {
      return false;
    }
    if (!_methodMatcher.call(this, requestOptions['method'])) {
      return false;
    }
    return _dataMatcher.call(this, requestOptions['data']);
  }
}
}
