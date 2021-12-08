package com.coremedia.blueprint.common.datevalidation;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidityPeriodValidatorIsValidityPeriodUsedTest {

  @Mock
  private ObjectProvider<ValidUntilConsumer> validUntilConsumers;

  @InjectMocks
  private ValidityPeriodValidator testling;

  @ParameterizedTest
  @MethodSource("provideIsValidityPeriodUsedData")
  void testIsValidityPeriodUsed(@NonNull List<CMLinkable> linkables, boolean expected) {
    assertThat(testling.isValidityPeriodUsed(linkables)).isEqualTo(expected);
  }

  private static Stream<Arguments> provideIsValidityPeriodUsedData() {
    return Stream.of(
            Arguments.of(
                    List.of(),
                    false
            ),
            Arguments.of(
                    List.of(new Object()),
                    false
            ),
            Arguments.of(
                    List.of(createLinkable(false, false)),
                    false
            ),
            Arguments.of(
                    List.of(createLinkable(true, false)),
                    true
            ),
            Arguments.of(
                    List.of(createLinkable(false, true)),
                    true
            ),
            Arguments.of(
                    List.of(createLinkable(true, true)),
                    true
            )
    );
  }

  @NonNull
  private static CMLinkable createLinkable(boolean hasValidFrom, boolean hasValidTo) {
    CMLinkable linkable = mock(CMLinkable.class);

    if (hasValidFrom) {
      when(linkable.getValidFrom()).thenReturn(Calendar.getInstance());
    }

    if (hasValidTo) {
      when(linkable.getValidTo()).thenReturn(Calendar.getInstance());
    }

    return linkable;
  }
}
