package com.coremedia.livecontext.ecommerce.ibm.common;

import com.google.common.base.Splitter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is meant to simplify access to values deep inside nested lists
 * and maps without having to worry about {@code NullPointerException}s while
 * providing automatic type conversion.
 */
public class DataMapHelper {

  private static final Splitter KEY_PATH_SPLITTER = Splitter.on('.');
  private static final Pattern KEY_INDEX_PATTERN = Pattern.compile("(.+)\\[(\\d+)\\]");

  private static final ConversionService DEFAULT_CONVERSION_SERVICE = new DefaultConversionService();

  private DataMapHelper() {
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public static Object getValueForPath(@NonNull Map<String, Object> map, @NonNull String path) {
    Object value = null;

    Map<String, Object> myMap = map;

    List<String> keys = KEY_PATH_SPLITTER.splitToList(path);

    for (int i = 0; i < keys.size(); i++) {
      String key = keys.get(i);
      if (i < keys.size() - 1 && !matchIndexPattern(key).matches()) {
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

          List tmpList = getListValue(myMap, keyWithoutIndex);
          if (tmpList.size() > index) {
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

  @NonNull
  private static Matcher matchIndexPattern(@NonNull CharSequence key) {
    return KEY_INDEX_PATTERN.matcher(key);
  }

  /**
   * @deprecated Use {@link #findValue(Map, String, Class)} instead.
   */
  @Deprecated
  @Nullable
  public static <T> T getValueForPath(@NonNull Map<String, Object> map, @NonNull String path, @NonNull Class<T> type) {
    return findValue(map, path, type).orElse(null);
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public static Object getValueForKey(@NonNull Map<String, Object> map, @NonNull String key) {
    Object value = map.get(key);

    if (value instanceof List) {
      List<Map<String, Object>> list = (List<Map<String, Object>>) value;
      if (!list.isEmpty()) {
        return list.get(0);
      }
    }

    return value;
  }

  @NonNull
  public static <T> Optional<T> findValue(@NonNull Map<String, Object> map, @NonNull String key,
                                          @NonNull Class<T> type) {
    Object value = getValueForPath(map, key);
    T convertedValue = convertWithFallback(value, type);
    return Optional.ofNullable(convertedValue);
  }

  /**
   * @deprecated Use {@link #findValue(Map, String, Class)} instead.
   */
  @Deprecated
  @Nullable
  public static <T> T getValueForKey(@NonNull Map<String, Object> map, @NonNull String key, @NonNull Class<T> type) {
    return findValue(map, key, type).orElse(null);
  }

  @NonNull
  public static Optional<String> findStringValue(@NonNull Map<String, Object> map, @NonNull String key) {
    return findValue(map, key, String.class);
  }

  @NonNull
  public static List getListValue(@NonNull Map<String, Object> map, @NonNull String key) {
    return findValue(map, key, List.class)
            .orElseGet(Collections::emptyList);
  }

  @Nullable
  private static <T> T convertWithFallback(@Nullable Object source, @NonNull Class<T> targetType) {
    if (source != null) {
      return convert(source, targetType);
    }

    // Avoid NPE when trying to lookup map.
    if (Map.class.isAssignableFrom(targetType)) {
      return (T) Collections.emptyMap();
    }

    // Avoid NPE when trying to lookup list.
    if (List.class.isAssignableFrom(targetType)) {
      return (T) Collections.emptyList();
    }

    return null;
  }

  @Nullable
  private static <T> T convert(@NonNull Object source, @NonNull Class<T> targetType) {
    return DEFAULT_CONVERSION_SERVICE.convert(source, targetType);
  }
}
