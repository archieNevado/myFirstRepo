package com.coremedia.blueprint.caas.preview.client;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@DefaultAnnotation(NonNull.class)
class PersistentPreviewQueries {

  private final Map<String, String> persistedPreviewQueries;

  PersistentPreviewQueries(Stream<String> persistedQueryNames) {
    persistedPreviewQueries = persistedQueryNames
            .map(PersistentPreviewQuery::of)
            .flatMap(Optional::stream)
            .collect(
                    toMap(
                            PersistentPreviewQuery::getName,
                            PersistentPreviewQuery::getQuery
                    )
            );
  }

  Optional<String> getPreviewQuery(String name) {
    return Optional.of(persistedPreviewQueries.get(name));
  }

}
