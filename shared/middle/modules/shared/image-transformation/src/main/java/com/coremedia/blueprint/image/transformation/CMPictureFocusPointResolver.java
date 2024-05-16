package com.coremedia.blueprint.image.transformation;

import com.coremedia.cap.common.InvalidPropertyValueException;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.transform.ContentFocusPointResolver;
import com.coremedia.cap.transform.FocusPoint;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.invoke.MethodHandles.lookup;

public class CMPictureFocusPointResolver implements ContentFocusPointResolver {
  public static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());
  private static final String FOCUS_POINT_X = "x";
  private static final String FOCUS_POINT_Y = "y";
  private static final String FOCUS_POINT_STRUCT_NAME = "focusPoint";

  @NonNull
  @Override
  public Optional<FocusPoint> getFocusPoint(@NonNull Content content, @Nullable String property) {
    Optional<Struct> localSettings = getLocalSettings(content);
    return localSettings.flatMap(settings -> fetchFocusPoint(settings, content));
  }

  @NonNull
  public Optional<Struct> getLocalSettings(@NonNull Content content) {
    Map<String, Object> properties = content.getProperties();
    if (!properties.containsKey("localSettings")) {
      return Optional.empty();
    }
    try {
      return Optional.ofNullable(content.getStruct("localSettings"));
    } catch (InvalidPropertyValueException e) {
      return Optional.empty();
    }
  }

  @NonNull
  private static Optional<FocusPoint> fetchFocusPoint(Struct localSettings, Content content) {
    if (!localSettings.getProperties().containsKey(FOCUS_POINT_STRUCT_NAME)) {
      return Optional.empty();
    }
    try {
      Struct focusPointStruct = localSettings.getStruct(FOCUS_POINT_STRUCT_NAME);
      Optional<Double> optionalX = focusPointStruct.getOptionalDouble(List.of(FOCUS_POINT_X));
      Optional<Double> optionalY = focusPointStruct.getOptionalDouble(List.of(FOCUS_POINT_Y));
      if (optionalX.isEmpty() || optionalY.isEmpty()) {
        return Optional.empty();
      }
      float x = optionalX.get().floatValue();
      float y = optionalY.get().floatValue();
      if (x < 0.0 || x > 1.0 || y < 0.0 || y > 1.0) {
        LOG.warn("Parameters for focus point of content '{}' must be between 0 and 1, but are {} and {}.", content, x, y);
        return Optional.empty();
      } else {
        return Optional.of(new FocusPoint(x, y));
      }
    } catch (InvalidPropertyValueException | NoSuchPropertyDescriptorException | IllegalArgumentException e) {
      // That's ok, focusPoint is an optional setting.
      return Optional.empty();
    }
  }

  @Override
  public boolean isApplicable(@NonNull Content content, @Nullable String property) {
    return content.getType()
            .isSubtypeOf("CMPicture");
  }
}
