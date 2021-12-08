package com.coremedia.livecontext.fragment.links.transformers;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RunWith(Parameterized.class)
public class TokenResolverUtilsTest {

  private final String description;
  private final String input;
  private final Map<String, Object> parameters;
  private final boolean mustBeComplete;
  private final boolean encode;
  private final Object expectedResult;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  public TokenResolverUtilsTest(String description, String input, Map<String, Object> parameters, boolean encode, boolean mustBeComplete, Object expectedResult) {
    this.description = description;
    this.input = input;
    this.parameters = parameters;
    this.mustBeComplete = mustBeComplete;
    this.expectedResult = expectedResult;
    this.encode = encode;
  }

  @Parameterized.Parameters(name = "{index}: {0}: {1} -> {4}")
  public static Collection<Object[]> data() {
    Map<String, Object> parameters;
    parameters = new HashMap<>();
    parameters.put("token1", "value1");
    parameters.put("token2", "value2");
    parameters.put("emptyToken", "");
    parameters.put("nullToken", null);
    parameters.put("utf8Token", "caf\u00e9");

    return Arrays.asList(new Object[][]{
            {"standard replacement", "/{token1}/{token2}/{emptyToken}/{nullToken}", parameters, true, false, "/value1/value2//%7BnullToken%7D"},
            {"standard replacement, use token1 twice", "/{token1}/{token1}/{emptyToken}/{nullToken}", parameters, true, false, "/value1/value1//%7BnullToken%7D"},
            {"remove unmapped tokens", "/{token1}/{token2}/{emptyToken}/{nullToken}", parameters, true, true, "/value1/value2//"},
            {"deal with incorrect input", "/{{token1}}/{token1}}/{{token1}/{token1/token1}/token1", parameters, true, false, "/%7B%7Btoken1%7D%7D/value1%7D/%7B%7Btoken1%7D/%7Btoken1/token1%7D/token1"},
            {"deal with incorrect input", "/{{token1}}/bla", parameters, true, false, "/%7B%7Btoken1%7D%7D/bla"},
            {"deal with incorrect input", "/{token1}}/bla", parameters, true, false, "/value1%7D/bla"},
            {"deal with incorrect input", "/{{token1}/bla", parameters, true, false, "/%7B%7Btoken1%7D/bla"},
            {"deal with incorrect input", "/{token1/bla", parameters, true, false, "/%7Btoken1/bla"},
            {"deal with tokens containing UTF-8", "/s/{utf8Token}", parameters, true, false, "/s/caf%C3%A9"},
            {"deal with urls containing UTF-8", "/thé/{utf8Token}", parameters, true, false, "/th%C3%A9/caf%C3%A9"},
            {"do not encode significant parameters", "/?param1=tom&jerry&param2={token1}", parameters, false, false, "/?param1=tom&jerry&param2=value1"},
            {"deal with encoded uris", "https://uwe@%7Btoken2%7D.com:8001/%23fragment/?param1=tom%26jerry&param2=%7Btoken1%7D#anchor", parameters, true, false, "https://uwe@value2.com:8001/%23fragment/?param1=tom%26jerry&param2=value1#anchor"},
            {"no tokens","/article/?param1=value1", parameters, false, false,"/article/?param1=value1"},
            {"no replacement for ajax includes","/foo?showVoting=true&ajaxelementid=%23votingReplacement", parameters, false, false,"/foo?showVoting=true&ajaxelementid=%23votingReplacement"}
    });
  }

  @Test
  public void performTest() throws Exception {
    String replacedString = TokenResolverUtils.replaceTokens(input, parameters, mustBeComplete, encode);
    Assert.assertEquals(description, expectedResult, replacedString);
  }
}
