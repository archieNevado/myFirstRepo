package com.coremedia.blueprint.cae.web;

import com.coremedia.blueprint.cae.ContentBeanTestBase;
import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.cae.exception.InvalidContentException;
import com.coremedia.blueprint.cae.services.validation.ValidationServiceImpl;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.objectserver.web.HandlerHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Collections;

import static org.mockito.Mockito.when;

@SuppressWarnings("SameParameterValue")
public class ContentValidityInterceptorTest extends ContentBeanTestBase {
  private ContentValidityInterceptor interceptor;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private ValidationServiceImpl validationService;

  @Inject
  private ValidityPeriodValidator validityPeriodValidator;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    validationService.setValidators(Collections.singletonList(validityPeriodValidator));
    interceptor = new ContentValidityInterceptor();
    interceptor.setValidationService(validationService);
  }

  /**
   * now                          |
   * navigation        |-----|    |
   * content                      |    |--------|
   */

  @Test(expected = InvalidContentException.class)
  public void testValidity1() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2010, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    setupValidFrom(navigation, 2010, 1, 1);
    setupValidTo(navigation, 2010, 2, 1);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    setupValidFrom(content, 2011, 1, 1);
    setupValidTo(content, 2011, 2, 1);
    ModelAndView modelAndView = HandlerHelper.createModel(new PageImpl(navigation, content, true, getSitesService(), null, null, null, null));
    interceptor.postHandle(request, response, null, modelAndView);
  }

  /**
   * now                          |
   * navigation        |----------|------|
   * content                      |      |--------|
   */

  @Test(expected = InvalidContentException.class)
  public void testValidity2() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2010, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    setupValidFrom(navigation, 2010, 1, 1);
    setupValidTo(navigation, 2011, 1, 1);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    setupValidFrom(content, 2011, 1, 1);
    setupValidTo(content, 2011, 2, 1);
    PageImpl page = new PageImpl(navigation, content, true, getSitesService(), null, null, null, null);
    page.setValidFrom(content.getValidFrom());
    page.setValidTo(content.getValidTo());
    ModelAndView modelAndView = HandlerHelper.createModel(page);
    interceptor.postHandle(request, response, null, modelAndView);
  }

  /**
   * now                          |
   * navigation        |----|     |
   * content              |----|  |
   */

  @Test(expected = InvalidContentException.class)
  public void testValidity3() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2010, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    setupValidFrom(navigation, 2010, 1, 1);
    setupValidTo(navigation, 2010, 13, 1);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    setupValidFrom(content, 2010, 2, 1);
    setupValidTo(content, 2010, 4, 1);
    PageImpl page = new PageImpl(navigation, content, true, getSitesService(), null, null, null, null);
    page.setValidFrom(content.getValidFrom());
    page.setValidTo(content.getValidTo());
    ModelAndView modelAndView = HandlerHelper.createModel(page);
    interceptor.postHandle(request, response, null, modelAndView);
  }

  /**
   * now              |
   * navigation       | |----|
   * content          |    |----|
   */

  @Test(expected = InvalidContentException.class)
  public void testValidity4() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2009, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    setupValidFrom(navigation, 2010, 1, 1);
    setupValidTo(navigation, 2010, 13, 1);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    setupValidFrom(content, 2010, 2, 1);
    setupValidTo(content, 2010, 4, 1);
    ModelAndView modelAndView = HandlerHelper.createModel(new PageImpl(navigation, content, true, getSitesService(), null, null, null, null));
    interceptor.postHandle(request, response, null, modelAndView);
  }


  /**
   * now              |
   * navigation       | |----|
   * content          |    |--------------
   */

  @Test(expected = InvalidContentException.class)
  public void testValidity7() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2009, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    setupValidFrom(navigation, 2010, 1, 1);
    setupValidTo(navigation, 2010, 13, 1);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    setupValidFrom(content, 2010, 2, 1);
    ModelAndView modelAndView = HandlerHelper.createModel(new PageImpl(navigation, content, true, getSitesService(), null, null, null, null));
    interceptor.postHandle(request, response, null, modelAndView);
  }

  /**
   * now              |
   * navigation       | |----|
   * content       ---|-----------------------
   */

  @Test(expected = InvalidContentException.class)
  public void testValidity8() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2009, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    setupValidFrom(navigation, 2010, 1, 1);
    setupValidTo(navigation, 2010, 13, 1);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    ModelAndView modelAndView = HandlerHelper.createModel(new PageImpl(navigation, content, true, getSitesService(), null, null, null, null));
    interceptor.postHandle(request, response, null, modelAndView);
  }


  /**
   * now              |
   * navigation    ---|--------|
   * content          |    |----|
   */

  @Test(expected = InvalidContentException.class)
  public void testValidity5() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2009, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    setupValidTo(navigation, 2010, 13, 1);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    setupValidFrom(content, 2010, 2, 1);
    setupValidTo(content, 2010, 4, 1);
    PageImpl page = new PageImpl(navigation, content, true, getSitesService(), null, null, null, null);
    page.setValidFrom(content.getValidFrom());
    page.setValidTo(content.getValidTo());
    ModelAndView modelAndView = HandlerHelper.createModel(page);
    interceptor.postHandle(request, response, null, modelAndView);
  }


  /**
   * now              |
   * navigation    ---|-------------
   * content          |    |----|
   */

  @Test(expected = InvalidContentException.class)
  public void testValidity6() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2009, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    setupValidFrom(content, 2010, 2, 1);
    setupValidTo(content, 2010, 4, 1);
    PageImpl page = new PageImpl(navigation, content, true, getSitesService(), null, null, null, null);
    page.setValidFrom(content.getValidFrom());
    page.setValidTo(content.getValidTo());
    ModelAndView modelAndView = HandlerHelper.createModel(page);
    interceptor.postHandle(request, response, null, modelAndView);
  }


  /**
   * now              |
   * navigation    ---|-------------
   * content          |    |--------
   */

  @Test(expected = InvalidContentException.class)
  public void testValidity9() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2009, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    setupValidFrom(content, 2010, 2, 1);
    PageImpl page = new PageImpl(navigation, content, true, getSitesService(), null, null, null, null);
    page.setValidFrom(content.getValidFrom());
    page.setValidTo(content.getValidTo());
    ModelAndView modelAndView = HandlerHelper.createModel(page);
    interceptor.postHandle(request, response, null, modelAndView);
  }


  /**
   * now              |
   * navigation    ---|-------------
   * content       ---|-------------
   */

  @Test
  public void testValidity10() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2009, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    ModelAndView modelAndView = HandlerHelper.createModel(new PageImpl(navigation, content, true, getSitesService(), null, null, null, null));
    interceptor.postHandle(request, response, null, modelAndView);
  }


  /**
   * now                |
   * navigation    |----|--|
   * content          |-|----|
   */

  @Test
  public void testValidity11() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2010, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    setupValidFrom(navigation, 2010, 1, 1);
    setupValidTo(navigation, 2011, 1, 1);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    setupValidFrom(content, 2010, 3, 1);
    setupValidTo(content, 2011, 3, 1);
    ModelAndView modelAndView = HandlerHelper.createModel(new PageImpl(navigation, content, true, getSitesService(), null, null, null, null));
    interceptor.postHandle(request, response, null, modelAndView);
  }

  /**
   * now                |
   * navigation    |----|----
   * content          |-|----|
   */

  @Test
  public void testValidity12() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2010, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    setupValidFrom(navigation, 2010, 1, 1);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    setupValidFrom(content, 2010, 3, 1);
    setupValidTo(content, 2011, 3, 1);
    ModelAndView modelAndView = HandlerHelper.createModel(new PageImpl(navigation, content, true, getSitesService(), null, null, null, null));
    interceptor.postHandle(request, response, null, modelAndView);
  }

  /**
   * now                |
   * navigation    |----|--|
   * content          |-|----
   */

  @Test
  public void testValidity13() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2010, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    setupValidFrom(navigation, 2010, 1, 1);
    setupValidTo(navigation, 2011, 1, 1);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    setupValidFrom(content, 2010, 3, 1);
    ModelAndView modelAndView = HandlerHelper.createModel(new PageImpl(navigation, content, true, getSitesService(), null, null, null, null));
    interceptor.postHandle(request, response, null, modelAndView);
  }


  /**
   * now                |
   * navigation    |----|----
   * content          |-|----
   */

  @Test
  public void testValidity14() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2010, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    setupValidFrom(navigation, 2010, 1, 1);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    setupValidFrom(content, 2010, 3, 1);
    ModelAndView modelAndView = HandlerHelper.createModel(new PageImpl(navigation, content, true, getSitesService(), null, null, null, null));
    interceptor.postHandle(request, response, null, modelAndView);
  }


  /**
   * now                |
   * navigation     ----|--|
   * content          |-|----|
   */

  @Test
  public void testValidity15() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2010, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    setupValidTo(navigation, 2011, 1, 1);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    setupValidFrom(content, 2010, 3, 1);
    setupValidTo(content, 2011, 3, 1);
    ModelAndView modelAndView = HandlerHelper.createModel(new PageImpl(navigation, content, true, getSitesService(), null, null, null, null));
    interceptor.postHandle(request, response, null, modelAndView);
  }


  /**
   * now                |
   * navigation    |----|--|
   * content       -----|----|
   */

  @Test
  public void testValidity16() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2010, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    setupValidFrom(navigation, 2010, 1, 1);
    setupValidTo(navigation, 2011, 1, 1);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    setupValidTo(content, 2011, 3, 1);
    ModelAndView modelAndView = HandlerHelper.createModel(new PageImpl(navigation, content, true, getSitesService(), null, null, null, null));
    interceptor.postHandle(request, response, null, modelAndView);
  }

  /**
   * now                |
   * navigation    -----|--|
   * content       -----|----|
   */

  @Test
  public void testValidity17() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    setupNow(request, 2010, 10, 10);
    MockHttpServletResponse response = new MockHttpServletResponse();
    CMNavigation navigation = Mockito.mock(CMNavigation.class);
    setupValidTo(navigation, 2011, 1, 1);
    CMLinkable content = Mockito.mock(CMLinkable.class);
    setupValidTo(content, 2011, 3, 1);
    ModelAndView modelAndView = HandlerHelper.createModel(new PageImpl(navigation, content, true, getSitesService(), null, null, null, null));
    interceptor.postHandle(request, response, null, modelAndView);
  }


  private static void setupValidFrom(CMLinkable linkable, int fromYear, int fromMonth, int fromDay) {
    Calendar validFrom = Calendar.getInstance();
    validFrom.set(Calendar.YEAR, fromYear);
    validFrom.set(Calendar.MONTH, fromMonth);
    validFrom.set(Calendar.DAY_OF_MONTH, fromDay);
    initTime(validFrom);
    when(linkable.getValidFrom()).thenReturn(validFrom);
  }

  private static void setupValidTo(CMLinkable linkable, int toYear, int toMonth, int toDay) {
    Calendar validTo = Calendar.getInstance();
    validTo.set(Calendar.YEAR, toYear);
    validTo.set(Calendar.MONTH, toMonth);
    validTo.set(Calendar.DAY_OF_MONTH, toDay);
    initTime(validTo);
    when(linkable.getValidTo()).thenReturn(validTo);
  }

  private static void setupNow(MockHttpServletRequest request, int year, int month, int day) {
    Calendar now = Calendar.getInstance();
    now.set(Calendar.YEAR, year);
    now.set(Calendar.MONTH, month);
    now.set(Calendar.DAY_OF_MONTH, day);
    initTime(now);
    RequestAttributes requestAttributes = new ServletRequestAttributes(request);
    requestAttributes.setAttribute(ValidityPeriodValidator.REQUEST_ATTRIBUTE_PREVIEW_DATE, now, ServletRequestAttributes.SCOPE_REQUEST);
    RequestContextHolder.setRequestAttributes(requestAttributes);
  }

  private static void initTime(Calendar calendar) {
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
  }

}
