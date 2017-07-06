package com.coremedia.livecontext.ecommerce.hybris.p13n;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.hybris.AbstractHybrisServiceTest;
import com.coremedia.livecontext.ecommerce.hybris.HybrisTestConfig;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XmlRepoConfiguration.class, HybrisTestConfig.class})
public class SegmentServiceImplIT extends AbstractHybrisServiceTest {

  @Inject
  SegmentServiceImpl testling;

  @Betamax(tape = "hy_testFindAllSegments", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindAllSegments() throws Exception {
    List<Segment> allSegments = testling.findAllSegments();
    assertThat(allSegments).isNotEmpty();

    Segment segment = allSegments.get(0);
    assertThat(segment.getId()).isNotNull();
    assertThat(segment.getName()).isNotNull();
  }

  @Betamax(tape = "hy_testFindSegmentById", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindSegmentById() throws Exception {
    Segment segment = testling.findSegmentById("customergroup");
    assertThat(segment).isNotNull();
    assertThat(segment.getId()).isNotNull();
    assertThat(segment.getName()).isNotNull();
  }

  @Betamax(tape = "hy_testFindSegmentsForCurrentUser", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindSegmentsForCurrentUser() throws Exception {
    List<Segment> segments = testling.findSegmentsForCurrentUser();
    assertThat(segments).isEmpty();
  }
}
