package com.coremedia.livecontext.ecommerce.ibm.common;

import com.google.common.base.Splitter;
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
  public static Object getValueForPath(@Nonnull Map<String, Object> map, @Nonnull String path) {
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

  @Nonnull
  private static Matcher matchIndexPattern(@Nonnull CharSequence key) {
    return KEY_INDEX_PATTERN.matcher(key);
  }

  @Nullable
  public static <T> T getValueForPath(@Nonnull Map<String, Object> map, @Nonnull String path, @Nonnull Class<T> type) {
    Object valueForPath = getValueForPath(map, path);
    return convertWithFallback(valueForPath, type);
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

  /**
   * @deprecated Use {@link #findValue(Map, String, Class)} instead.
   */
  @Deprecated
  @Nonnull
  public static <T> Optional<T> findValueForKey(@Nonnull Map<String, Object> map, @Nonnull String key,
                                                @Nonnull Class<T> type) {
    return findValue(map, key, type);
  }

  @Nonnull
  public static <T> Optional<T> findValue(@Nonnull Map<String, Object> map, @Nonnull String key,
                                          @Nonnull Class<T> type) {
    Object valueForKey = getValueForPath(map, key);
    T value = convertWithFallback(valueForKey, type);
    return Optional.ofNullable(value);
  }

  @Nullable
  public static <T> T getValueForKey(@Nonnull Map<String, Object> map, @Nonnull String key, @Nonnull Class<T> type) {
    Object valueForKey = getValueForPath(map, key);
    return convertWithFallback(valueForKey, type);
  }

  @Nonnull
  public static Optional<String> findStringValue(@Nonnull Map<String, Object> map, @Nonnull String key) {
    return findValue(map, key, String.class);
  }

  @Nonnull
  public static List getListValue(@Nonnull Map<String, Object> map, @Nonnull String key) {
    return findValue(map, key, List.class)
            .orElseGet(Collections::emptyList);
  }

  @Nullable
  private static <T> T convertWithFallback(@Nullable Object source, @Nonnull Class<T> targetType) {
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
  private static <T> T convert(@Nonnull Object source, @Nonnull Class<T> targetType) {
    return DEFAULT_CONVERSION_SERVICE.convert(source, targetType);
  }
}
