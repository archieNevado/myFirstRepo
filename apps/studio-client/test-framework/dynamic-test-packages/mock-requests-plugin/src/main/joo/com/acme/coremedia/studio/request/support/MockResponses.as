package com.acme.coremedia.studio.request.support {
/**
 * Factory methods for mocked responses. The responses are very similar to the REST tests, so you may look
 * for examples there.
 */
public class MockResponses {
  function MockResponses() {
  }

  /**
   * Empty response body.
   * @return empty body
   */
  public static function empty():Object {
    return null;
  }

  /**
   * Used to mock validation issue failures.
   * @param requestOptions options used to generate the response
   * @param code code, typically the type of the validator like <code>RegExpValidator</code>.
   * @param propertyName the property name for which the issue got detected
   * @param arguments typical arguments inserted by the validator; in case of <code>RegExpValidator</code> for example the regular expression pattern.
   * @return mock response
   */
  public static function issuesDetectedError(requestOptions:Object,
                                             code:String,
                                             propertyName:String,
                                             arguments:Array):Object {
    var contentId:String = parseContentId(requestOptions);
    var contentPath:String = 'content/' + contentId;
    return {
      '$Type': 'com.coremedia.cap.content.IssuesDetectedError',
      errorCode: 'CAP-REST-01008',
      errorName: 'NOT_VALID',
      issues: {
        entity: {'$Ref': contentPath},
        byProperty: {
          title: [
            {
              '$Type': 'com.coremedia.ui.data.validation.Issue',
              entity: {
                '$Ref': contentPath
              },
              severity: 'ERROR',
              property: propertyName,
              code: code,
              'arguments': arguments
            }
          ]
        }
      }

    }
  }

  /**
   * Parses content ID from URL. It actually just returns the last portion of the URL path.
   *
   * @param requestOptions options to parse url property of
   * @return content ID (assuming, that this was a request like api/content/1234)
   */
  private static function parseContentId(requestOptions:Object):String {
    return requestOptions['url'].split('/').pop();
  }

  public static function feedbackGroups(groups:Object):Object {
    return groups
  }

  public static function jobReference(jobId:String):Object {
    return {
      "$Ref":"job/" + jobId
    }
  }

  public static function jobResult(result:Object):Object {
    return result;
  }
}
}
