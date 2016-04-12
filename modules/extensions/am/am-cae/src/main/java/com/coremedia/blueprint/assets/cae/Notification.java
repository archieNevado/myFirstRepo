package com.coremedia.blueprint.assets.cae;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Notification {

  private NotificationType type;
  private String key;
  private List<?> params;

  public Notification(@Nonnull NotificationType type, @Nonnull String key, @Nullable List<?> params) {
    this.type = type;
    this.key = key;
    this.params = params == null ? Collections.emptyList() : new ArrayList<>(params);
  }

  @Nonnull
  public NotificationType getType() {
    return type;
  }

  @Nonnull
  public String getKey() {
    return key;
  }

  public List<?> getParams() {
    return params;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Notification)) {
      return false;
    }

    Notification notification = (Notification) o;

    if (!key.equals(notification.key)) {
      return false;
    }
    // Probably incorrect - comparing Object[] arrays with Arrays.equals
    return Objects.equals(params, notification.params) && type == notification.type;

  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + key.hashCode();
    result = 31 * result + (params != null ? params.hashCode() : 0);
    return result;
  }

  public enum NotificationType {
    SUCCESS,
    INFO,
    WARNING,
    ERROR
  }
}
