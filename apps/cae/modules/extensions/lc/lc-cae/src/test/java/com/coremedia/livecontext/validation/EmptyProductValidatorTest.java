package com.coremedia.livecontext.validation;

import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.function.Predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class EmptyProductValidatorTest {

  private EmptyProductValidator testling;
  private Predicate predicate;

  private DeliveryConfigurationProperties deliveryConfigurationProperties;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private CMProductTeaser productTeaser;

  @Mock
  private Product product;

  @Before
  public void defaultSetup() {
    deliveryConfigurationProperties = new DeliveryConfigurationProperties();
    deliveryConfigurationProperties.setPreviewMode(false);
    testling = new EmptyProductValidator();
    testling.setDeliveryConfigurationProperties(deliveryConfigurationProperties);
    predicate = testling.createPredicate();
    when(productTeaser.getProduct()).thenReturn(product);
    when(productTeaser.getContent().getPath()).thenReturn("irrelevant");
  }

  @Test
  public void supports() {
    assertTrue(testling.supports(CMProductTeaser.class));
  }

  @Test
  public void predicateIsLiveNoProductTeaser() {
    assertFalse(predicate.test(null));
  }

  @Test
  public void predicateIsPreviewNoProductTeaser() {
    deliveryConfigurationProperties.setPreviewMode(true);
    assertTrue(predicate.test(null));
  }

  @Test
  public void predicateIsLiveHasProduct() {
    assertTrue(predicate.test(productTeaser));
    verify(productTeaser).getProduct();
  }

  @Test
  public void predicateIsPreviewHasProduct() {
    assertTrue(predicate.test(productTeaser));
    verify(productTeaser).getProduct();
  }

  @Test
  public void predicateIsLiveNoProduct() {
    when(productTeaser.getProduct()).thenReturn(null);
    assertFalse(predicate.test(productTeaser));
    verify(productTeaser).getProduct();
  }

  @Test
  public void predicateIsPreviewNoProduct() {
    deliveryConfigurationProperties.setPreviewMode(true);
    assertTrue(predicate.test(productTeaser));
    verify(productTeaser, never()).getProduct();
  }

  @Test
  public void predicateIsLiveNotFoundException() {
    when(productTeaser.getProduct()).thenThrow(NotFoundException.class);
    assertFalse(predicate.test(productTeaser));
    verify(productTeaser).getProduct();
  }

  @Test
  public void predicateIsPreviewNotFoundException() {
    deliveryConfigurationProperties.setPreviewMode(true);
    assertTrue(predicate.test(productTeaser));
    verify(productTeaser, never()).getProduct();
  }
}
