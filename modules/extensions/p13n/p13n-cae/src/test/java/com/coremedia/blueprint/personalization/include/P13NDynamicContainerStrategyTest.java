package com.coremedia.blueprint.personalization.include;

import com.coremedia.blueprint.common.layout.Container;
import com.coremedia.blueprint.personalization.contentbeans.CMP13NSearch;
import com.coremedia.blueprint.personalization.contentbeans.CMSelectionRules;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class P13NDynamicContainerStrategyTest {

  @Mock
  private Container outerContainer;

  @Mock
  private Container innerContainer;

  @Mock
  private Container secondInnerContainer;

  @Mock
  private CMP13NSearch persoSearch;

  @Mock
  private CMSelectionRules persoContent;
  
  private final P13NDynamicContainerStrategy testling = new P13NDynamicContainerStrategy();
  
  @Test
  public void testEmptyContainer() {
    assertFalse(testling.isDynamic(Collections.emptyList()));
  }

  @Test
  public void testEmptyNestedContainers() {
    when(outerContainer.getItems()).thenReturn(Collections.singletonList(innerContainer));
    when(innerContainer.getItems()).thenReturn(Collections.emptyList());
    assertFalse(testling.isDynamic(outerContainer.getItems()));
  }

  @Test
  public void testEmptyCyclicContainers() {
    when(outerContainer.getItems()).thenReturn(ImmutableList.of(innerContainer, innerContainer));
    when(innerContainer.getItems()).thenReturn(Collections.singletonList(outerContainer));
    assertFalse(testling.isDynamic(outerContainer.getItems()));
  }

  @Test
  public void testNegativeSimpleContainer() {
    when(outerContainer.getItems()).thenReturn(ImmutableList.of("foo", "bar"));
    assertFalse(testling.isDynamic(outerContainer.getItems()));
  }

  @Test
  public void testNegativeSimpleNestedContainer() {
    when(outerContainer.getItems()).thenReturn(ImmutableList.of("foo", innerContainer, innerContainer, "bar"));
    when(innerContainer.getItems()).thenReturn(ImmutableList.of("inner1", "inner2"));
    assertFalse(testling.isDynamic(outerContainer.getItems()));
  }

  @Test
  public void testFirstNegativeSecondPositiveNestedContainer() {
    when(outerContainer.getItems()).thenReturn(ImmutableList.of("foo", innerContainer, secondInnerContainer, "bar"));
    when(innerContainer.getItems()).thenReturn(ImmutableList.of("inner1", "inner2"));
    when(secondInnerContainer.getItems()).thenReturn(ImmutableList.of(persoContent));
    assertTrue(testling.isDynamic(outerContainer.getItems()));
  }

  @Test
  public void testPositiveSimpleContainer1() {
    when(outerContainer.getItems()).thenReturn(ImmutableList.of("foo", "bar", persoSearch));
    assertTrue(testling.isDynamic(outerContainer.getItems()));
  }

  @Test
  public void testPositiveSimpleContainer2() {
    when(outerContainer.getItems()).thenReturn(ImmutableList.of("foo", persoContent, "bar"));
    assertTrue(testling.isDynamic(outerContainer.getItems()));
  }

  @Test
  public void testPositiveSimpleNestedContainer1() {
    when(outerContainer.getItems()).thenReturn(ImmutableList.of("foo", innerContainer, "bar"));
    when(innerContainer.getItems()).thenReturn(ImmutableList.of(persoSearch, persoSearch, "inner1", "inner2"));
    assertTrue(testling.isDynamic(outerContainer.getItems()));
  }

  @Test
  public void testPositiveSimpleNestedContainer2() {
    when(outerContainer.getItems()).thenReturn(ImmutableList.of("foo", innerContainer, "bar"));
    when(innerContainer.getItems()).thenReturn(ImmutableList.of("inner1", persoContent, "inner2"));
    assertTrue(testling.isDynamic(outerContainer.getItems()));
  }

  @Test
  public void testPositiveSimpleNestedContainer3() {
    when(outerContainer.getItems()).thenReturn(ImmutableList.of("foo", innerContainer, persoSearch, "bar"));
    when(innerContainer.getItems()).thenReturn(ImmutableList.of("inner1", "inner2", persoContent));
    assertTrue(testling.isDynamic(outerContainer.getItems()));
  }
}
