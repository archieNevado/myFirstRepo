package com.coremedia.livecontext.fragment.links.transformers.resolvers;

import com.coremedia.livecontext.ecommerce.link.QueryParam;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletRequest;
import java.util.List;
import java.util.Map;

import static com.coremedia.objectserver.request.RequestUtils.PARAMETERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommerceLinkResolverTest {

  @Test
  void getQueryParams() {
    ServletRequest request = mock(ServletRequest.class);
    when(request.getAttribute(PARAMETERS)).thenReturn(Map.of("a", "1", "brot", "kaese"));
    List<QueryParam> queryParams = CommerceLinkResolver.getLinkParameters("http://acme.com/?bla=blub&brot=wurst", request, "nice");

    assertThat(queryParams).containsOnly(
                    QueryParam.of("a", "1"),
                    QueryParam.of("brot", "kaese"),
                    QueryParam.of("bla", "blub"),
                    QueryParam.of("brot", "wurst"),
                    QueryParam.of("view", "nice")
            );
  }
}
