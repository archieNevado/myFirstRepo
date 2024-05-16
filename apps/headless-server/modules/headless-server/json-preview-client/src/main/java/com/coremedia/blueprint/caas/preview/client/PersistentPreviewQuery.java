package com.coremedia.blueprint.caas.preview.client;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@DefaultAnnotation(NonNull.class)
class PersistentPreviewQuery {

  private final String name;
  private final String query;

  PersistentPreviewQuery(String name, String query) {
    this.name = name;
    this.query = query;
  }

  String getName() {
    return name;
  }

  String getQuery() {
    return query;
  }

  static Optional<PersistentPreviewQuery> of(String lookupPath, String name) {
    try {
      String query = IOUtils.resourceToString(lookupPath + name + ".graphql", StandardCharsets.UTF_8);
      return Optional.of(new PersistentPreviewQuery(name, query));
    } catch (IOException ignored) {
      return Optional.empty();
    }
  }
}
