package com.coremedia.livecontext.ecommerce.sfcc.cae.preview;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

@DefaultAnnotation(NonNull.class)
public class UserSegmentFormatter {

  private UserSegmentFormatter() {
  }

  public static String format(String segment) {
    return '"' + segment + '"';
  }

}
