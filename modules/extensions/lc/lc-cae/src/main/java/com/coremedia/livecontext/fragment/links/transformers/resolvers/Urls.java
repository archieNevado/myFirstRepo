package com.coremedia.livecontext.fragment.links.transformers.resolvers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * URL utilities for Livecontext
 */
public class Urls {

  private Urls() {
  }

  @Nonnull
  static Optional<String> getQueryString(@Nullable String s) {
    if (isBlank(s)) {
      return Optional.empty();
    }
    int index = s.indexOf("?");
    return index >= 0 && index < s.length()-1 ? Optional.of(s.substring(index+1)) : Optional.empty();
  }

  @Nonnull
  public static List<Pair> getQueryParams(@Nullable String s) {
    List<Pair> pairs = getQueryString(s)
            .map(Urls::splitQueryString)
            .orElseGet(Collections::emptyList);
    if (pairs.size() == 1 && pairs.get(0).second == null) {
      return Collections.emptyList();
    }
    return pairs;
  }

  @Nonnull
  private static List<Pair> splitQueryString(@Nonnull String queryString) {
    return Arrays.stream(queryString.split("&"))
            .map(Urls::splitQueryParam)
            .collect(toList());
  }

  @Nonnull
  private static Pair splitQueryParam(@Nonnull String keyValuePairStr) {
    final int idx = keyValuePairStr.indexOf("=");
    final String key = idx > 0 ? keyValuePairStr.substring(0, idx) : keyValuePairStr;
    final String value = idx > 0 && keyValuePairStr.length() > idx + 1 ? keyValuePairStr.substring(idx + 1) : null;
    return new Pair(key, value);
  }

  public static class Pair implements Serializable {

    public String first;
    public String second;

    Pair(String first, String second) {
      this.first = first;
      this.second = second;
    }

    @Override
    public boolean equals(Object anObject) {
      if (this == anObject) {
        return true;
      }

      if (anObject instanceof Pair) {
        Pair other = (Pair) anObject;
        return areEqual(first, other.first) && areEqual(second, other.second);
      }

      return false;
    }

    private boolean areEqual(Object a, Object b) {
      return a == null ? (b == null) : a.equals(b);
    }

    @Override
    public int hashCode() {
      int hf = first == null ? 0 : first.hashCode();
      int hs = second == null ? 0 : second.hashCode();
      return hf + hs * 1997;
    }

    @Override
    public String toString() {
      String firstValue = first != null ? (first + " with class: " + first.getClass()) : "null";
      String secondValue = second != null ? (second + " with class: " + second.getClass()) : "null";

      return "Pair(" + firstValue + ", " + secondValue + ")";
    }

    // Added accessors

    public String getFirst() {
      return first;
    }

    public String getSecond() {
      return second;
    }
  }

}
