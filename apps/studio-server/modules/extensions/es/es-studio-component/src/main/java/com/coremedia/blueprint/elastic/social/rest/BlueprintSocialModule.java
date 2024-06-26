package com.coremedia.blueprint.elastic.social.rest;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class BlueprintSocialModule extends SimpleModule {
  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")

  private Collection<JsonSerializer<?>> jsonSerializers;

  @Autowired // not necessary for spring but maven likes it
  public BlueprintSocialModule(Collection<JsonSerializer<?>> jsonSerializers) {
    super("BlueprintSocial", new Version(1, 0, 0, null, null, null));
    this.jsonSerializers = jsonSerializers;
  }

  @Override
  public void setupModule(final SetupContext context) {
    for (final JsonSerializer<?> jsonSerializer : jsonSerializers) {
      if (jsonSerializer.getClass().getPackage().equals(BlueprintSocialModule.class.getPackage()) ) {
        addSerializer(jsonSerializer);
      }
    }
    super.setupModule(context);
  }
}
