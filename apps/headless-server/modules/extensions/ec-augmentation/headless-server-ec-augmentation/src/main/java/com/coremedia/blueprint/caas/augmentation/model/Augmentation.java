package com.coremedia.blueprint.caas.augmentation.model;

import com.coremedia.cap.content.Content;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

@DefaultAnnotation(NonNull.class)
public abstract class Augmentation {

  private final CommerceRef commerceRef;
  @Nullable
  private final Content content;

  Augmentation(CommerceRef commerceRef, @Nullable Content content) {
    this.content = content;
    this.commerceRef = commerceRef;
  }

  @Nullable
  public Content getContent() {
    return content;
  }

  public String getId() {
    return commerceRef.getId() + (content != null ? ":" + content.getId() : "");
  }

  public CommerceRef getCommerceRef() {
    return commerceRef;
  }
}
