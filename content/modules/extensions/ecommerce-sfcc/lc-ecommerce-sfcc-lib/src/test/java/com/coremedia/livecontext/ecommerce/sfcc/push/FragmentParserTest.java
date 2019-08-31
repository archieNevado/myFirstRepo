package com.coremedia.livecontext.ecommerce.sfcc.push;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "logging.level.root=INFO"
})
public class FragmentParserTest {

  private static final String BEGIN_TAG_PATTERN = FragmentParser.FRAGMENT_START_DELIMITER + " (url=http://domain.com/bla/blub, cacheHit=true, fragmentKey=externalRef=%s;categoryId=%s;productId=%s;pageId=%s;view=%s;placement=%s) -->";
  private static final String BEGIN_TAG_1 = String.format(BEGIN_TAG_PATTERN, "", "", "", "", "", "");
  private static final String BEGIN_TAG_2 = String.format(BEGIN_TAG_PATTERN, "", "", "fred", "", "", "");
  private static final String BEGIN_TAG_3 = String.format(BEGIN_TAG_PATTERN, "", "", "", "", "vom", "");
  private static final String BEGIN_TAG_4 = String.format(BEGIN_TAG_PATTERN, "", "", "", "", "", "jupiter");
  private static final String END_TAG = FragmentParser.FRAGMENT_END_DELIMITER;

  private FragmentParser testling;

  @Before
  public void setup() {
    testling = new FragmentParser();
  }

  @Test
  public void parsePlayload() {
    String payload = "outer html1 " +
            BEGIN_TAG_1 +
            "fragment1 payload" +
            END_TAG +
            "outer html2 " +
            BEGIN_TAG_2 +
            "fragment2 payload" +
            END_TAG +
            "outer html2 " +
            BEGIN_TAG_1 +
            "fragment3 payload" +
            END_TAG +
            "outer html3";
    Map<String, Map<String, String>> fragments = testling.parseFragments(payload);
    assertThat(fragments).isNotNull();
    assertThat(fragments).isNotEmpty();
    assertThat(fragments).hasSize(2);
  }

  @Test
  public void parseEmptyPlayload() {
    Map<String, Map<String, String>> fragments = testling.parseFragments("");
    assertThat(fragments).isEmpty();
  }

  @Test
  public void parseHtml() throws IOException {
    URL url = Resources.getResource("fragmentParserInput.html");
    String inputHtml = Resources.toString(url, Charsets.UTF_8);
    Map<String, Map<String, String>> fragments = testling.parseFragments(inputHtml);
    assertThat(fragments).isNotNull();
    assertThat(fragments).isNotEmpty();
    assertThat(fragments).hasSize(2);
    assertThat(fragments.get("externalRef=;categoryId=;productId=;pageId=about-us")).hasSize(6);
  }

  @Test
  public void testParsePageKeyFromFragmentKey(){
    String pageKey = "externalRef=fred;categoryId=vom;productId=jupiter;pageId=";
    String fragmentKey = pageKey + ";view=stern;placement=gern";

    String parsePageKey = FragmentParser.parsePageKeyFromFragmentKey(fragmentKey);
    assertThat(parsePageKey).isEqualTo(pageKey);
  }

  @Test
  public void testUpdateFragments() {
    String pageKey = "externalRef=fred;categoryId=vom;productId=jupiter;pageId=";
    String fragmentKey1 = pageKey + ";view=stern;placement=gern";
    String fragmentKey2 = pageKey + ";view=haar;placement=wunderbar";

    String payload = "Er kam vom and'ren Stern er landete nicht gern";
    Map<String, Map<String, String>> pagesToFragmentsMap = FragmentParser.updateFragments(fragmentKey1, payload, new HashMap<>());
    pagesToFragmentsMap = FragmentParser.updateFragments(fragmentKey2, payload, pagesToFragmentsMap);

    assertThat(pagesToFragmentsMap).containsKey(pageKey);
    Map<String, String> fragmentsOfPage = pagesToFragmentsMap.get(pageKey);
    assertThat(fragmentsOfPage).containsKeys(fragmentKey1, fragmentKey2);
    assertThat(fragmentsOfPage.get(fragmentKey2)).isEqualTo(payload);
  }
}
