package com.coremedia.livecontext.web.taglib;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PbeStoreSerializerTest {

  private PbeStoreSerializer testling;

  @BeforeEach
  void setUp() {
    testling = new PbeStoreSerializer();
  }

  @ParameterizedTest
  @MethodSource("provideLinkData")
  void link(String siteId, WorkspaceId workspaceId, String expected) {
    StoreContext storeContext = buildStoreContext(siteId, workspaceId);

    String actual = testling.link(storeContext);

    assertThat(actual).isEqualTo(expected);
  }

  private static Stream<Arguments> provideLinkData() {
    return Stream.of(
            Arguments.of(
                    "someSite",
                    WorkspaceId.of("someWorkspaceId"),
                    "livecontext/store/someSite/someWorkspaceId"
            ),
            Arguments.of(
                    // `null` should be replaced with `NO_WS` in the resulting URL.
                    "anotherSite",
                    null,
                    "livecontext/store/anotherSite/NO_WS"
            )
    );
  }

  @NonNull
  private static StoreContext buildStoreContext(@NonNull String siteId, @Nullable WorkspaceId workspaceId) {
    return StoreContextImpl
            .builder(siteId)
            .withWorkspaceId(workspaceId)
            .build();
  }
}
