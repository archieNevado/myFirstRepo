package com.coremedia.blueprint.caas.augmentation.tree;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ExternalBreadcrumbTreeRelationTest {

  private static final String CATEGORY_ID_PREFIX = "test:///catalog/category/";
  ExternalBreadcrumbTreeRelation testling;

  @BeforeEach
  void setup(){
    var breadcrumb = Stream.of("a", "b", "c", "d")
            .map(ExternalBreadcrumbTreeRelationTest::toCategoryId)
            .collect(Collectors.toList());
    testling = new ExternalBreadcrumbTreeRelation(breadcrumb);
  }

  static String toFormattedCategoryId(String s) {
    return CATEGORY_ID_PREFIX + s;
  }

  static CommerceId toCategoryId(String s) {
    return CommerceIdParserHelper.parseCommerceIdOrThrow(toFormattedCategoryId(s));
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> getChildrenOf() {
    return Stream.of(
            Arguments.of("a", List.of("b")),
            Arguments.of("c", List.of("d")),
            Arguments.of("d", List.of())
    );
  }

  @ParameterizedTest
  @MethodSource
  void getChildrenOf(String parent, List<String> expectedChildList) {
    Collection<String> childrenOf = testling.getChildrenOf(toCategoryId(parent)).stream()
            .map(CommerceId::getExternalId)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
    assertThat(childrenOf).asList().isEqualTo(expectedChildList);
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> getParentOf() {
    return Stream.of(
            Arguments.of("b", "a"),
            Arguments.of("d", "c"),
            Arguments.of("a", null)
    );
  }

  @ParameterizedTest
  @MethodSource
  void getParentOf(String child, String expectedParent) {
    var commerceId = testling.getParentOf(toCategoryId(child));
    if (expectedParent == null) {
      assertThat(commerceId).isNull();
    } else {
      var parentOf = commerceId.getExternalId();
      assertThat(parentOf).contains(expectedParent);
    }
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> pathToRoot() {
    return Stream.of(
            Arguments.of("d", List.of("a", "b", "c", "d")),
            Arguments.of("b", List.of("a", "b")),
            Arguments.of("a", List.of("a")),
            Arguments.of("y", List.of())
    );
  }

  @ParameterizedTest
  @MethodSource
  void pathToRoot(String child, List<String> expectedPathToRoot) {
    List<String> pathToRoot = testling.pathToRoot(toCategoryId(child)).stream()
            .map(CommerceId::getExternalId)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
    assertThat(pathToRoot).isEqualTo(expectedPathToRoot);
  }

  @Test
  void isRoot() {
    assertThat(testling.isRoot(toCategoryId("a"))).isTrue();
    assertThat(testling.isRoot(toCategoryId("b"))).isFalse();
    assertThat(testling.isRoot(toCategoryId("y"))).isFalse();
  }

  @Test
  void isApplicable() {
    assertThat(testling.isApplicable(toCategoryId("a"))).isTrue();
    assertThat(testling.isApplicable(toCategoryId("y"))).isFalse();
  }

}
