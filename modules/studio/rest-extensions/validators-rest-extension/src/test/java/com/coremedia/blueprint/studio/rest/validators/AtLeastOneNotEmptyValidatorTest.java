package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import com.coremedia.xml.Markup;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collection;
import java.util.List;

import static com.coremedia.blueprint.studio.rest.validators.AtLeastOneNotEmptyValidator.AT_LEAST_ONE_NOT_EMPTY_CODE;
import static com.coremedia.blueprint.studio.rest.validators.AtLeastOneNotEmptyValidator.EXACTLY_ONE_SET_CODE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AtLeastOneNotEmptyValidatorTest {
  private static final String PROPERTY = "aPropertyName";
  private static final String ANOTHER_PROPERTY = "presetProperty";

  @Mock
  private Content content;
  @Mock
  private Issues issues;

  @Before
  public void setup() {
    initMocks(this);
  }

  @Test
  public void testValidateNotEmptyCollection() throws Exception {
    AtLeastOneNotEmptyValidator validator = getValidator();
    Collection collection = mock(Collection.class);

    when(content.getProperties()).thenReturn(ImmutableMap.<String, Object>of(PROPERTY, collection));
    when(collection.isEmpty()).thenReturn(false);
    validator.validate(content, issues);
    withoutIssue();
  }

  @Test
  public void testValidateEmptyCollection() throws Exception {
    AtLeastOneNotEmptyValidator validator = getValidator();
    Collection collection = mock(Collection.class);

    when(content.getProperties()).thenReturn(ImmutableMap.<String, Object>of(PROPERTY,collection));
    when(collection.isEmpty()).thenReturn(true);
    validator.validate(content, issues);
    withError(PROPERTY);
  }

  @Test
  public void testValidateNotEmptyString() throws Exception {
    AtLeastOneNotEmptyValidator validator = spy(new AtLeastOneNotEmptyValidator());
    String string = "testString";
    validator.setProperties(ImmutableList.of(PROPERTY));

    when(content.getProperties()).thenReturn(ImmutableMap.<String, Object>of(PROPERTY, string));

    validator.validate(content, issues);
    withoutIssue();
  }

  @Test
  public void testValidateEmptyString() throws Exception {
    AtLeastOneNotEmptyValidator validator = spy(new AtLeastOneNotEmptyValidator());
    String string = "";
    validator.setProperties(ImmutableList.of(PROPERTY));

    when(content.getProperties()).thenReturn(ImmutableMap.<String, Object>of(PROPERTY, string));

    validator.validate(content, issues);
    withError(PROPERTY);
  }

  @Test
  public void testValidateNotEmptyMarkup() throws Exception {
    AtLeastOneNotEmptyValidator validator = spy(new AtLeastOneNotEmptyValidator());
    Markup markup = mock(Markup.class);
    validator.setProperties(ImmutableList.of(PROPERTY));

    when(content.getProperties()).thenReturn(ImmutableMap.<String, Object>of(PROPERTY, markup));
    doReturn(false).when(validator).isEmptyRichtext(markup);

    validator.validate(content, issues);
    withoutIssue();
  }

  @Test
  public void testValidateEmptyMarkup() throws Exception {
    AtLeastOneNotEmptyValidator validator = spy(new AtLeastOneNotEmptyValidator());
    Markup markup = mock(Markup.class);
    validator.setProperties(ImmutableList.of(PROPERTY));

    when(content.getProperties()).thenReturn(ImmutableMap.<String, Object>of(PROPERTY,markup));
    doReturn(true).when(validator).isEmptyRichtext(markup);

    validator.validate(content, issues);
    withError(PROPERTY);
  }

  @Test
  public void testValidateNotEmptyBlob() throws Exception {
    AtLeastOneNotEmptyValidator validator = getValidator();
    Blob blob = mock(Blob.class);

    when(content.getProperties()).thenReturn(ImmutableMap.<String, Object>of(PROPERTY,blob));
    when(blob.getSize()).thenReturn(1);
    validator.validate(content, issues);
    withoutIssue();
  }

  @Test
  public void testValidateEmptyBlob() throws Exception {
    AtLeastOneNotEmptyValidator validator = getValidator();
    Blob blob = mock(Blob.class);

    when(content.getProperties()).thenReturn(ImmutableMap.<String, Object>of(PROPERTY,blob));
    when(blob.getSize()).thenReturn(0);
    validator.validate(content, issues);
    withError(PROPERTY);
  }

  @Test
  public void testPresetShowIssuesProperty() throws Exception {
    AtLeastOneNotEmptyValidator validator = getValidator(ImmutableList.of(PROPERTY, ANOTHER_PROPERTY));
    validator.setShowIssueForProperty(ANOTHER_PROPERTY);

    Blob blob = mock(Blob.class);
    when(blob.getSize()).thenReturn(0);
    when(content.getProperties()).thenReturn(ImmutableMap.<String, Object>of(ANOTHER_PROPERTY, blob));

    validator.validate(content, issues);
    withError(ANOTHER_PROPERTY);
  }

  @Test
  public void testExactlyOneMustBetSet() {
    AtLeastOneNotEmptyValidator validator = getValidator(ImmutableList.of(PROPERTY, ANOTHER_PROPERTY));
    validator.setExactlyOneMustBeSet(true);

    String aTestString = "anyString1";
    String anotherTestString = "anyString2";

    when(content.getProperties()).thenReturn(ImmutableMap.<String, Object>of(PROPERTY, aTestString, ANOTHER_PROPERTY, anotherTestString));

    validator.validate(content, issues);
    withWarn(PROPERTY);
  }

  private void withWarn(String property) {
    verify(issues, times(1)).addIssue(Severity.WARN, property, EXACTLY_ONE_SET_CODE);
  }

  private void withoutIssue() {
    verify(issues, times(0)).addIssue(any(Severity.class), anyString(), anyString());
  }

  private void withError(String property) {
    verify(issues, times(1)).addIssue(Severity.ERROR, property, AT_LEAST_ONE_NOT_EMPTY_CODE);
  }

  private AtLeastOneNotEmptyValidator getValidator(){
    return getValidator(ImmutableList.of(PROPERTY));
  }

  private AtLeastOneNotEmptyValidator getValidator(List<String> properties){
    AtLeastOneNotEmptyValidator validator = new AtLeastOneNotEmptyValidator();
    validator.setProperties(properties);
    return validator;
  }
}