package com.acme.coremedia.studio.request.predicate {
import com.coremedia.ui.data.coremediaRemoteServiceUri;
import com.coremedia.ui.data.impl.RemoteService;

/**
 * Predicates for <code>url</code> elements in <code>requestOptions</code>.
 */
public class UrlPredicate {
  function UrlPredicate() {
  }

  public static function contentApiRequest(actual:String):Boolean {
    return actual && actual.indexOf(RemoteService.REMOTE_SERVICE_URI.path + 'content/') === 0;
  }

  public static function feedbackGroupsRequest(actual:String):Boolean {
    return actual && actual.indexOf(RemoteService.REMOTE_SERVICE_URI.path + "feedback/groups") === 0;
  }

  public static function jobServiceRequest(jobType:String):Function {
    return function (actual:String):Boolean {
      return actual && actual.indexOf(RemoteService.REMOTE_SERVICE_URI.path + "jobService/" + jobType) === 0;
    }
  }

  public static function jobRequest(jobId:String):Function {
    return function (actual:String):Boolean {
      return actual && actual.indexOf(RemoteService.REMOTE_SERVICE_URI.path + "job/" + jobId) === 0;
    }
  }
}
}
