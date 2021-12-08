package com.coremedia.livecontext.web.taglib;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PbeStoreSerializerTest {

  private PbeStoreSerializer testling;

  @BeforeEach
  void setUp() {
    testling = new PbeStoreSerializer();
  }

  @ParameterizedTest
  @MethodSource("provideLinkData")
  void link(String siteId, String expected) {
    StoreContext storeContext = buildStoreContext(siteId);

    String actual = testling.link(storeContext);

    assertThat(actual).isEqualTo(expected);
  }

  private static Stream<Arguments> provideLinkData() {
    return Stream.of(
            Arguments.of(
                    "someSite",
                    "livecontext/store/someSite"
            ),
            Arguments.of(
                    "anotherSite",
                    "livecontext/store/anotherSite"
            )
    );
  }

  @NonNull
  private static StoreContext buildStoreContext(@NonNull String siteId) {
    CommerceConnection connection = mock(CommerceConnection.class);

    return StoreContextBuilderImpl.from(connection, siteId)
            .build();
  }
}
