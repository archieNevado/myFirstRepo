package com.coremedia.livecontext.ecommerce.workspace;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkspaceIdTest {

  @ParameterizedTest
  @ValueSource(strings = {
          "1234",
          "one-two-three",
  })
  void validValues(String value) {
    WorkspaceId.of(value);
  }

  @ParameterizedTest
  @ValueSource(strings = {
          "",
          " ",
          "\n",
          "\t",
  })
  void invalidValues(String value) {
    assertThrows(IllegalArgumentException.class, () -> WorkspaceId.of(value));
  }

  @Test
  void equality() {
    WorkspaceId anniversary1 = WorkspaceId.of("anniversary");
    WorkspaceId anniversary2 = WorkspaceId.of("anniversary");
    WorkspaceId winterCollection = WorkspaceId.of("winter_collection");

    assertThat(anniversary1).isEqualTo(anniversary1);
    assertThat(anniversary1).isEqualTo(anniversary2);
    assertThat(anniversary1).isNotEqualTo(winterCollection);
  }
}
