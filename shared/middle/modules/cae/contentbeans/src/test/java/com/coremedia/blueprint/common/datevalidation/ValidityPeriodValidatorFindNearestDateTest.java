package com.coremedia.blueprint.common.datevalidation;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
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
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidityPeriodValidatorFindNearestDateTest {

  @Mock
  private ObjectProvider<ValidUntilConsumer> validUntilConsumers;

  @InjectMocks
  private ValidityPeriodValidator testling;

  @ParameterizedTest
  @MethodSource("provideFindNearestDateData")
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  void testFindNearestDate(@NonNull List<CMLinkable> linkables, @NonNull Calendar validTime,
                           @NonNull Optional<Calendar> expected) {
    Optional<Calendar> actual = testling.findNearestDate(linkables, validTime);
    assertThat(actual).isEqualTo(expected);
  }

  private static Stream<Arguments> provideFindNearestDateData() {
    return Stream.of(
            Arguments.of(
                    List.of(),
                    createCalendar(2018, 3, 24),
                    Optional.empty()
            ),
            Arguments.of(
                    List.of(new Object()),
                    createCalendar(2018, 3, 24),
                    Optional.empty()
            ),
            Arguments.of(
                    List.of(
                            createLinkable(null, null),
                            createLinkable(null, null)
                    ),
                    createCalendar(2018, 3, 24),
                    Optional.empty()
            ),
            Arguments.of(
                    List.of(
                            createLinkable(null, null),
                            createLinkable(null, createCalendar(2018, 3, 25)),
                            createLinkable(null, null)
                    ),
                    createCalendar(2018, 3, 24),
                    Optional.of(createCalendar(2018, 3, 25))
            ),
            Arguments.of(
                    List.of(
                            createLinkable(null, null),
                            createLinkable(createCalendar(2018, 3, 25), null),
                            createLinkable(null, null)
                    ),
                    createCalendar(2018, 3, 24),
                    Optional.of(createCalendar(2018, 3, 25))
            ),
            Arguments.of(
                    List.of(
                            createLinkable(null, null),
                            createLinkable(createCalendar(2018, 3, 27), null),
                            createLinkable(createCalendar(2018, 3, 26), null),
                            createLinkable(null, null)
                    ),
                    createCalendar(2018, 3, 24),
                    Optional.of(createCalendar(2018, 3, 26))
            ),
            Arguments.of(
                    List.of(
                            createLinkable(null, null),
                            createLinkable(null, createCalendar(2018, 3, 27)),
                            createLinkable(null, createCalendar(2018, 3, 26)),
                            createLinkable(null, null)
                    ),
                    createCalendar(2018, 3, 24),
                    Optional.of(createCalendar(2018, 3, 26))
            ),
            Arguments.of(
                    List.of(
                            createLinkable(null, null),
                            createLinkable(createCalendar(2018, 3, 27), null),
                            createLinkable(null, createCalendar(2018, 3, 26)),
                            createLinkable(null, null)
                    ),
                    createCalendar(2018, 3, 24),
                    Optional.of(createCalendar(2018, 3, 26))
            ),
            Arguments.of(
                    List.of(
                            createLinkable(null, null),
                            createLinkable(null, createCalendar(2018, 3, 27)),
                            createLinkable(createCalendar(2018, 3, 26), null),
                            createLinkable(null, null)
                    ),
                    createCalendar(2018, 3, 24),
                    Optional.of(createCalendar(2018, 3, 26))
            )
    );
  }

  @SuppressWarnings("SameParameterValue")
  @NonNull
  private static Calendar createCalendar(int year, int month, int dayOfMonth) {
    return new Calendar.Builder()
            .setDate(year, month, dayOfMonth)
            .build();
  }

  @NonNull
  private static CMLinkable createLinkable(@Nullable Calendar validFrom, @Nullable Calendar validTo) {
    CMLinkable linkable = mock(CMLinkable.class);

    if (validFrom != null) {
      when(linkable.getValidFrom()).thenReturn(validFrom);
    }

    if (validTo != null) {
      when(linkable.getValidTo()).thenReturn(validTo);
    }

    return linkable;
  }
}
