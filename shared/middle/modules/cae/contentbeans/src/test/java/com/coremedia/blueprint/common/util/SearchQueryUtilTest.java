package com.coremedia.blueprint.common.util;

import com.coremedia.blueprint.cae.search.Condition;
import com.coremedia.blueprint.cae.search.Value;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SearchQueryUtilTest {

  @Mock
  private ContentRepository repository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void createEmptyDocumentTypeFilter() {
    assertThat(SearchQueryUtil.createDocumentTypeFilter(null, repository)).isEmpty();
    assertThat(SearchQueryUtil.createDocumentTypeFilter("", repository)).isEmpty();
    assertThat(SearchQueryUtil.createDocumentTypeFilter(", ", repository)).isEmpty();
  }

  @Test
  void createUnknownDocumentTypeFilter() {
    assertThat(SearchQueryUtil.createDocumentTypeFilter("foo", repository))
            .contains(Condition.is("documenttype", Value.anyOf(Collections.singleton("foo"))));
  }

  @Test
  void createKnownDocumentTypeFilter() {
    mockType("foo", true);
    assertThat(SearchQueryUtil.createDocumentTypeFilter("foo", repository))
            .contains(Condition.is("documenttype", Value.anyOf(Collections.singleton("foo"))));
  }

  @Test
  void multipleTypesAndSubTypes() {
    ContentType t1a = mockType("t1a", true);
    ContentType t1b = mockType("t1b", true);
    ContentType middle1 = mockType("middle1", true, t1a, t1b);
    ContentType t2a = mockType("t2a", true);
    ContentType t2b = mockType("t2b", false);
    ContentType middle2 = mockType("middle2", false, t2a, t2b);
    mockType("top1", false, middle1, middle2);
    mockType("top2", false);
    mockType("top3", true);

    Optional<Condition> result = SearchQueryUtil.createDocumentTypeFilter("top1,top3,top2", repository);
    assertTrue(result.isPresent());
    Condition condition = result.get();
    assertEquals("documenttype", condition.getField());
    assertSame(Condition.Operators.IS, condition.getOp());
    assertSame(Value.Operators.OR, condition.getValue().getOp());

    Set<String> expected = Set.of("t1a", "t1b", "t2a", "top3", "middle1");
    assertEquals(expected, Set.copyOf(condition.getValue().getValue()));
  }

  private ContentType mockType(String name, boolean concrete, ContentType... children) {
    ContentType type = mock(ContentType.class, name);
    when(type.getName()).thenReturn(name);
    when(type.isConcrete()).thenReturn(concrete);
    when(repository.getContentType(name)).thenReturn(type);

    Set<ContentType> subtypes = new HashSet<>();
    subtypes.add(type);
    subtypes.addAll(Arrays.stream(children)
                    .flatMap(child -> child.getSubtypes().stream())
                    .collect(Collectors.toUnmodifiableSet()));

    when(type.getSubtypes()).thenReturn(subtypes);
    return type;
  }

}
