package com.coremedia.livecontext.fragment.links.transformers.resolvers;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Optional;

/**
 * URL utilities for Livecontext
 */
public class Urls {

  private Urls() {
  }

  @NonNull
  public static Optional<String> getQueryString(@NonNull String s) {
    int index = s.indexOf('?');
    return index >= 0 && index < s.length()-1 ? Optional.of(s.substring(index+1)) : Optional.empty();
  }

}
