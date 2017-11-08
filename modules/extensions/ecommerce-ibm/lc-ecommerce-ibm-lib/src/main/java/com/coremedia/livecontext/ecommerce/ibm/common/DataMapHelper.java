package com.coremedia.livecontext.ecommerce.ibm.common;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataMapHelper {

  private static final Pattern KEY_INDEX_PATTERN = Pattern.compile("(.+)\\[(\\d+)\\]");

  private static final ConversionService DEFAULT_CONVERSION_SERVICE = new DefaultConversionService();

  private DataMapHelper() {
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public static Object getValueForPath(@Nonnull Map<String, Object> map, @Nonnull String path) {
    Map<String, Object> myMap = map;
    Object value = null;
    String[] keys = path.split("\\.");
    for (int i = 0; i < keys.length; i++) {
      String key = keys[i];
      if (i < keys.length - 1 && !matchIndexPattern(key).matches()) {
        value = getValueForKey(myMap, key);
        if (value instanceof Map) {
          myMap = (Map<String, Object>) value;
        } else {
          return null;
        }
      } else {
        Matcher matcher = matchIndexPattern(key);
        if (matcher.matches()) { // a list entry is expected
          String keyWithoutIndex = matcher.group(1);
          int index = Integer.parseInt(matcher.group(2));

          List tmpList = getValueForKey(myMap, keyWithoutIndex, List.class);
          if (tmpList != null && tmpList.size() > index) {
            value = tmpList.get(index);
            if (value instanceof Map) {
              myMap = (Map<String, Object>) value;
            }
          } else {
            return null;
          }
        } else {
          // for the last key ...
          value = myMap.get(key);
        }
      }
    }
    return value;
  }

  private static Matcher matchIndexPattern(CharSequence key) {
    return KEY_INDEX_PATTERN.matcher(key);
  }

  @Nullable
  public static <T> T getValueForPath(@Nonnull Map<String, Object> map, @Nonnull String path, @Nonnull Class<T> type) {
    Object valueForPath = getValueForPath(map, path);
    return convert(valueForPath, type, null);
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public static Object getValueForKey(@Nonnull Map<String, Object> map, @Nonnull String key) {
    Object value = map.get(key);
    if (value instanceof List) {
      List<Map<String, Object>> list = (List<Map<String, Object>>) value;
      if (!list.isEmpty()) {
        return list.get(0);
      }
    }
    return value;
  }

  @Nonnull
  public static <T> Optional<T> findValueForKey(@Nonnull Map<String, Object> map, @Nonnull String key,
                                                @Nonnull Class<T> type) {
    Object valueForKey = getValueForPath(map, key);
    T value = convert(valueForKey, type, null);
    return Optional.ofNullable(value);
  }

  @Nullable
  public static <T> T getValueForKey(@Nonnull Map<String, Object> map, @Nonnull String key, @Nonnull Class<T> type) {
    Object valueForKey = getValueForPath(map, key);
    return convert(valueForKey, type, null);
  }

  @Nullable
  private static <T> T convert(@Nullable Object source, @Nonnull Class<T> targetType, @Nullable T defaultValue) {
    if (source != null) {
      return DEFAULT_CONVERSION_SERVICE.convert(source, targetType);
    }
    // avoid NPE when trying to lookup map or list
    if (defaultValue == null) {
      if (Map.class.isAssignableFrom(targetType) ) {
        return (T) Collections.emptyMap();
      }
      if (List.class.isAssignableFrom(targetType) ) {
        return (T) Collections.emptyList();
      }
    }
    return defaultValue;
  }
}
