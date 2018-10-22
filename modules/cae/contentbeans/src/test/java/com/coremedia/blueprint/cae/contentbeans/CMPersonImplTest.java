package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMPerson;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.common.personaldata.PersonalData;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestContextHolder;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CMPersonImplTest.PageImplTestConfiguration.class)
public class CMPersonImplTest {
  private static final String CONTENT_REPOSITORY_URL = "classpath:/com/coremedia/blueprint/cae/contentbeans/personimpl/personimpltest-content.xml";

  private static final int PERSON1_ID = 2;
  private static final int PERSON2_ID = 4;
  private CMPerson person1;
  private CMPerson person2;

  @Inject
  private ContentRepository contentRepository;
  @Inject
  private ContentBeanFactory contentBeanFactory;

  @Before
  public void setup() {
    person1 = getContentBean(PERSON1_ID);
    person2 = getContentBean(PERSON2_ID);
  }

  private <T> T getContentBean(int id) {
    Content content = contentRepository.getContent(IdHelper.formatContentId(id));
    return (T) contentBeanFactory.createBeanFor(content);
  }

  @After
  public void teardown() {
    // make sure that tests do not interfere with each other via thread locals!
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  public void getFirstName() {
    assertEquals("firstName_Person", person1.getFirstName());
  }

  @Test
  public void getLastName() {
    assertEquals("lastName_Person", person1.getLastName());
  }

  @Test
  public void getDisplayName() {
    assertEquals("displayName_Person", person1.getDisplayName());
    assertEquals("firstName_Person lastName_Person", person2.getDisplayName());
  }

  @Test
  public void getEMail() {
    assertEquals("eMail_Person", person1.getEMail());
    assertEquals("", person2.getEMail());
  }

  @Test
  public void getOrganization() {
    assertEquals("organization_Person", person1.getOrganization());
    assertEquals("", person2.getOrganization());
  }

  @Test
  public void getJobTitle() {
    assertEquals("jobTitle_Person", person1.getJobTitle());
    assertEquals("", person2.getJobTitle());
  }

  @Test
  public void getMiscStruct() {
    @PersonalData Struct miscStruct = person1.getMisc();
    assertNotNull(miscStruct);
    String stringProperty = miscStruct.getString("stringProperty");
    assertNotNull(miscStruct);
    assertEquals("testString", stringProperty);
  }

  @Configuration
  @Import(XmlRepoConfiguration.class)
  @ImportResource(value = {
    "classpath:/com/coremedia/cap/common/xml/uapi-xml-services.xml",
    "classpath:/framework/spring/blueprint-contentbeans.xml",
  },
    reader = com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader.class)
  static class PageImplTestConfiguration {
    @Bean
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig(CONTENT_REPOSITORY_URL);
    }
  }
}
