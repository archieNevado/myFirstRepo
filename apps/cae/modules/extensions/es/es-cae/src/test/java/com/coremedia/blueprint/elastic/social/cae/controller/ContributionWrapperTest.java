package com.coremedia.blueprint.elastic.social.cae.controller;

import com.coremedia.elastic.social.api.comments.Comment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ContributionWrapperTest {

  @Test
  void testWrapper() {
    ContributionWrapper<Comment, CommentWrapper> subContributionWrapper1 = mock(ContributionWrapper.class);
    ContributionWrapper<Comment, CommentWrapper> subContributionWrapper2 = mock(ContributionWrapper.class);
    List<ContributionWrapper<Comment, CommentWrapper>> subContributionWrappers = List.of(subContributionWrapper1, subContributionWrapper2);

    Comment comment = mock(Comment.class);

    ContributionWrapper<Comment, ContributionWrapper<Comment, CommentWrapper>> wrapper = new ContributionWrapper<>(comment, subContributionWrappers);

    assertThat(wrapper.getContribution()).isEqualTo(comment);
    assertThat(wrapper.getSubContributions()).hasSize(2);

    Comment comment2 = mock(Comment.class);
    wrapper.setContribution(comment2);
    assertThat(wrapper.getContribution()).isEqualTo(comment2);
  }
}
