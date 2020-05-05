package com.acme.coremedia.studio.request.support {
public interface RequestSentObserver {
  /**
   * Track the number of times that this request has been sent.
   */
  function requestSent():void;
}
}
