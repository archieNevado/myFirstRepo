package com.coremedia.blueprint.themeimporter;

import com.coremedia.blueprint.themeimporter.descriptors.ThemeDefinition;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

class ThemeDefinitionHelper {
  // static class
  private ThemeDefinitionHelper() {}

  static ThemeDefinition themeDefinitionFromDom(Document document) {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(ThemeDefinition.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      return (ThemeDefinition) jaxbUnmarshaller.unmarshal(document);
    } catch (JAXBException e) {
      throw new IllegalArgumentException("Cannot extract theme definition from DOM", e);
    }
  }
}
