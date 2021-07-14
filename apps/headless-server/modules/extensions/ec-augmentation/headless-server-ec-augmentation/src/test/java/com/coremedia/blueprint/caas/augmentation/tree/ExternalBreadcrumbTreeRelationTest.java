package com.coremedia.blueprint.caas.augmentation.tree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ExternalBreadcrumbTreeRelationTest {


  ExternalBreadcrumbTreeRelation testling;

  @BeforeEach
  void setup(){
    testling = new ExternalBreadcrumbTreeRelation(List.of("a", "b", "c", "d"));
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
    Collection<String> childrenOf = testling.getChildrenOf(parent);
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
    String parentOf = testling.getParentOf(child);
    assertThat(parentOf).isEqualTo(expectedParent);
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
    List<String> pathToRoot = testling.pathToRoot(child);
    assertThat(pathToRoot).isEqualTo(expectedPathToRoot);
  }

  @Test
  void isRoot() {
    assertThat(testling.isRoot("a")).isTrue();
    assertThat(testling.isRoot("b")).isFalse();
    assertThat(testling.isRoot("y")).isFalse();
  }

  @Test
  void isApplicable() {
    assertThat(testling.isApplicable("a")).isTrue();
    assertThat(testling.isApplicable("y")).isFalse();
  }

  @Test
  void unorderedTest() {
    ExternalBreadcrumbTreeRelation treeRelation = new ExternalBreadcrumbTreeRelation(List.of("z", "y", "x"));

    assertThat(new ArrayList(treeRelation.getBreadcrumb())).isEqualTo(List.of("z", "y", "x"));
  }

}
