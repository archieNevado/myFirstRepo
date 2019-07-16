package com.coremedia.livecontext.ecommerce.sfcc.push;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.sfcc.push.SfccPushJsonFactory.FRAGMENTS_PROPERTY;
import static com.coremedia.livecontext.ecommerce.sfcc.push.SfccPushJsonFactory.PAGE_KEY_PROPERTY;
import static org.assertj.core.api.Assertions.assertThat;

class SfccPushJsonFactoryTest {

  @Test
  public void testCreateFragmentsJson() throws IOException {
    Map<String, String> pageFragments = new HashMap<>();
    pageFragments.put("key1", "payload1");
    pageFragments.put("key2", "payload2");
    pageFragments.put("key3", "payload3");

    JSONObject json = SfccPushJsonFactory.createJsonObjectForPageFragments("externalRef=;categoryId=;productId=;pageId=", pageFragments);
    assertThat(json.get(PAGE_KEY_PROPERTY)).isNotNull();
    assertThat(json).isNotNull();
    JSONArray fragmentsList = (JSONArray) json.get(FRAGMENTS_PROPERTY);
    assertThat(fragmentsList.length()).isEqualTo(3);
  }

}
