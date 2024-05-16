package com.coremedia.blueprint.elastic.social.cae.controller;

import com.coremedia.elastic.social.api.comments.Comment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CommentWrapperTest {

  @Test
  void testWrapper() {
    CommentWrapper subCommentWrapper1 = mock(CommentWrapper.class);
    CommentWrapper subCommentWrapper2 = mock(CommentWrapper.class);
    List<CommentWrapper> subCommentWrappers = List.of(subCommentWrapper1, subCommentWrapper2);

    Comment comment = mock(Comment.class);

    CommentWrapper wrapper = new CommentWrapper(comment, subCommentWrappers);

    assertThat(wrapper.getComment()).isEqualTo(comment);
    assertThat(wrapper.getSubComments()).hasSize(2);
  }
}
