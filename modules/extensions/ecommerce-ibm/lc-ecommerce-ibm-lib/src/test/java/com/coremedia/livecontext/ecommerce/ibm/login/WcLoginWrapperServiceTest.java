package com.coremedia.livecontext.ecommerce.ibm.login;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
public class WcLoginWrapperServiceTest {
  private WcLoginWrapperService testling;

  @Test
  public void testEqualsWithTypeConversion() throws Exception {
    assertTrue(testling.equalsWithTypeConversion("3", 3.5));
    assertTrue(testling.equalsWithTypeConversion("3", 3));
  }

  @Test (expected = NumberFormatException.class)
  public void testEqualsWithTypeConversionNFException() throws Exception {
    assertTrue(testling.equalsWithTypeConversion("3.5", 3.5));
  }

  @Before
  public void setup(){
    testling = new WcLoginWrapperService();
  }
}