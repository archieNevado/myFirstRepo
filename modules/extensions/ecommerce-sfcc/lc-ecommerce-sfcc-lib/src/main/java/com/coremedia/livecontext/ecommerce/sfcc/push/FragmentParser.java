package com.coremedia.livecontext.ecommerce.sfcc.push;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DefaultAnnotation(NonNull.class)
class FragmentParser {

  static final String FRAGMENT_START_DELIMITER = "<!-- START COREMEDIA FRAGMENT";
  static final String FRAGMENT_END_DELIMITER = "<!-- END COREMEDIA FRAGMENT -->";
  private static final String FRAGMENT_START_TAG_END_DELIMITER = "-->";

  Map<String, Map<String, String>> parseFragments(String shopPayload) {

    Map<String, Map<String, String>> fragmentsPerPage = new HashMap<>();

    List<String> chunksDividedStartDelimiter = Lists.newArrayList(Splitter.on(FRAGMENT_START_DELIMITER).split(shopPayload));
    for (int i = 0; i < chunksDividedStartDelimiter.size(); i++) {
      String chunk = chunksDividedStartDelimiter.get(i);
      // first element is always part of the shop html
      if (i > 0) {
        List<String> subChunksDividedByEndDelimiter = Lists.newArrayList(Splitter.on(FRAGMENT_END_DELIMITER).split(chunk));
        if (subChunksDividedByEndDelimiter.size() > 0) {
          String fragmentSubChunk = subChunksDividedByEndDelimiter.get(0);
          String fragmentPayload = fragmentSubChunk.substring(
                  fragmentSubChunk.indexOf(FRAGMENT_START_TAG_END_DELIMITER)+FRAGMENT_START_TAG_END_DELIMITER.length());
          int fragmentKeyStart = fragmentSubChunk.indexOf("fragmentKey=");
          if (fragmentKeyStart != -1) fragmentKeyStart += 12;
          int fragmentKeyEnd = fragmentSubChunk.indexOf(")");
          if (fragmentKeyStart != -1 && fragmentKeyEnd != -1 && fragmentKeyStart < fragmentKeyEnd) {
            String fragmentKey = fragmentSubChunk.substring(fragmentKeyStart, fragmentKeyEnd);
            updateFragments(fragmentKey, fragmentPayload, fragmentsPerPage);
          }
        }
        // ignore all other sub chunks apart from the first one because they are part of the outer html
      }
    }

    return fragmentsPerPage;
  }

  static  Map<String, Map<String, String>> updateFragments(String fragmentKey, String payload, Map<String, Map<String, String>> pagesToFragments){
    String pageKey = parsePageKeyFromFragmentKey(fragmentKey);
    if (pagesToFragments.containsKey(pageKey)){
      Map<String, String> fragments = pagesToFragments.get(pageKey);
      fragments.put(fragmentKey.trim(), payload.trim());
    } else {
      Map<String, String> fragments = new HashMap<>();
      fragments.put(fragmentKey.trim(), payload.trim());
      pagesToFragments.put(pageKey, fragments);
    }
    return pagesToFragments;
  }

  @NonNull
  static String parsePageKeyFromFragmentKey(@NonNull String fragmentKey){
    int pageKeyEnd = fragmentKey.indexOf(";view=");
    if (pageKeyEnd == -1){
      throw new IllegalArgumentException("FragmentKey " + fragmentKey +" does not contain pageKey");
    }
    return fragmentKey.substring(0, pageKeyEnd);
  }
}
