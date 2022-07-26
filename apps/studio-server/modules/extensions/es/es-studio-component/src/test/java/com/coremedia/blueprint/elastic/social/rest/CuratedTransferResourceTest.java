package com.coremedia.blueprint.elastic.social.rest;

import com.coremedia.blueprint.base.elastic.common.BlobConverter;
import com.coremedia.blueprint.elastic.social.util.BbCodeToCoreMediaRichtextTransformer;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.core.api.models.ModelException;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.comments.CommentService;
import com.coremedia.elastic.social.api.reviews.Review;
import com.coremedia.elastic.social.api.reviews.ReviewService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.xml.Markup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CuratedTransferResourceTest.TestConfiguration.class})
public class CuratedTransferResourceTest {
  @Configuration(proxyBeanMethods = false)
  @Import(XmlRepoConfiguration.class)
  public static class TestConfiguration {
    @Bean
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig(CONTENT_REPOSITORY_URL);
    }

    @Bean
    CommentService commentService() {
      return mock(CommentService.class);
    }

    @Bean
    ReviewService reviewService() {
      return mock(ReviewService.class);
    }

    @Bean
    BlobConverter blobConverter() {
      return mock(BlobConverter.class);
    }
  }

  private static final String ARTICLE_CONTENT_ID = "coremedia:///cap/content/42";
  private static final String ARTICLE_NAME = "test";
  private static final String CLASS_PATH = "/com/coremedia/blueprint/elastic/social/rest/";
  private static final String CM_GALLERY_DOC_TYPE = "CMGallery";
  private static final String CM_GALLERY_PROPERTY_TO_COPY_TO = "items";
  private static final String COMMENT_AUTHOR_NAME = "Dilbert";
  private static final String COMMENT_DATE = "21.09.2012-16:23";
  private static final String COMMENT_ID = "42";
  private static final String COMMENT_TEXT = "It is a dummy text!";
  private static final String CONTENT_PROPERTY_TO_COPY_TO = "detailText";
  private static final String CONTENT_REPOSITORY_URL = "classpath:" + CLASS_PATH + "ct-test-content.xml";
  private static final String COREMEDIA_RICHTEXT_GRAMMAR = "coremedia-richtext-1.0";
  private static final String DEFAULT_DATE_STRING = "10.10.2010-12:42";
  private static final String DUMMY_TEXT = "dummy";
  private static final String EXPECTED_CONTENT_AS_BB_CODE = "[i]" + COMMENT_AUTHOR_NAME + "[/i], " + "21.09.2012 | 16:23:" + "[cmQuote]" + COMMENT_TEXT + "[/cmQuote]";
  private static final String EXPECTED_DUMMY_CONTENT_AS_BB_CODE = "[i]" + DUMMY_TEXT + "[/i], " + "10.10.2010 | 12:42:" + "[cmQuote]" + DUMMY_TEXT + "[/cmQuote]";
  private static final String IMAGE_ATTACHMENT_FILE_NAME = "attachment-42.jpg";
  private static final String IMAGE_ATTACHMENT_FILE_NAME_WITHOUT_TYPE = "attachment-42";
  private static final String IMAGE_GALLERY_CONTENT_ID = "coremedia:///cap/content/58";
  private static final String IMAGE_GALLERY_NAME = "my-gallery";
  private static final String IMAGE_MIME_TYPE = "image/jpeg";
  private static final String INVALID_CONTENT_ID = "coremedia:///cap/content/68";
  private static final String PROPERTY_TITLE = "title";
  private static final String VALID_COMMENT_IDS = "42,23";

  @Autowired
  private ContentRepository contentRepository;

  @Autowired
  private CommentService commentService;

  @Autowired
  private ReviewService reviewService;

  @Autowired
  private BlobConverter blobConverter;

  private CuratedTransferResource curatedTransferResource;

  @Before
  public void setUp() {
    curatedTransferResource = new CuratedTransferResource(contentRepository, commentService, reviewService, blobConverter);
  }

  // --- CuratedTransfer: Comments -------------------------------------------------------------------------------------

  @Test(expected = IllegalArgumentException.class)
  public void postProcess_createArticleFromComments_capIdMustNotBeNull() {
    curatedTransferResource.postProcess(null, VALID_COMMENT_IDS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void postProcess_invalidContendId() {
    curatedTransferResource.postProcess(COMMENT_TEXT, VALID_COMMENT_IDS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void postProcess_commentIdsMustNotBeNull() {
    curatedTransferResource.postProcess(ARTICLE_CONTENT_ID, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void postProcess_articleToCopyToDoesNotExist() throws Exception {
    Comment comment = mockComment(COMMENT_ID, COMMENT_TEXT, COMMENT_AUTHOR_NAME, dateFromString(COMMENT_DATE));
    when(commentService.getComment(comment.getId())).thenReturn(comment);
    curatedTransferResource.postProcess(INVALID_CONTENT_ID, COMMENT_ID);
  }

  @Test
  public void postProcess_copyFromOneComment() throws ParseException {
    Content articleToCopyTo = getContent(ARTICLE_CONTENT_ID);

    Comment comment = mockComment(COMMENT_ID, COMMENT_TEXT, COMMENT_AUTHOR_NAME, dateFromString(COMMENT_DATE));
    when(commentService.getComment(comment.getId())).thenReturn(comment);

    // Actual computation
    curatedTransferResource.postProcess(ARTICLE_CONTENT_ID, COMMENT_ID);

    Markup expectedContentAsRichtext = BbCodeToCoreMediaRichtextTransformer.newInstance().transform(EXPECTED_CONTENT_AS_BB_CODE)
            .withGrammar(COREMEDIA_RICHTEXT_GRAMMAR);
    assertThat(articleToCopyTo.get(CONTENT_PROPERTY_TO_COPY_TO)).isEqualTo(expectedContentAsRichtext);
    assertThat(articleToCopyTo.get(PROPERTY_TITLE)).isEqualTo(ARTICLE_NAME);
  }

  @Test
  public void postProcess_copyFromOneReview() throws ParseException {
    Content articleToCopyTo = getContent(ARTICLE_CONTENT_ID);

    Review review = mockReview(COMMENT_ID, COMMENT_TEXT, COMMENT_AUTHOR_NAME, dateFromString(COMMENT_DATE));
    when(commentService.getComment(review.getId())).thenReturn(review);

    // Actual computation
    curatedTransferResource.postProcess(ARTICLE_CONTENT_ID, COMMENT_ID);

    Markup expectedContentAsRichtext = BbCodeToCoreMediaRichtextTransformer.newInstance().transform(EXPECTED_CONTENT_AS_BB_CODE)
            .withGrammar(COREMEDIA_RICHTEXT_GRAMMAR);
    assertThat(articleToCopyTo.get(CONTENT_PROPERTY_TO_COPY_TO)).isEqualTo(expectedContentAsRichtext);
    assertThat(articleToCopyTo.get(PROPERTY_TITLE)).isEqualTo(ARTICLE_NAME);
  }

  @Test
  public void postProcess_copyFromOneAnonymousComment() throws ParseException {
    String commentAuthorName = null;

    Content articleToCopyTo = getContent(ARTICLE_CONTENT_ID);

    Comment comment = mockComment(COMMENT_ID, COMMENT_TEXT, commentAuthorName, dateFromString(COMMENT_DATE));
    when(commentService.getComment(comment.getId())).thenReturn(comment);

    // Actual computation
    curatedTransferResource.postProcess(ARTICLE_CONTENT_ID, COMMENT_ID);

    String expectedContentAsBbCode = "[i]anonymous[/i], " + "21.09.2012 | 16:23:" + "[cmQuote]" + COMMENT_TEXT + "[/cmQuote]";
    Markup expectedContentAsRichtext = BbCodeToCoreMediaRichtextTransformer.newInstance().transform(expectedContentAsBbCode)
            .withGrammar(COREMEDIA_RICHTEXT_GRAMMAR);
    assertThat(articleToCopyTo.get(CONTENT_PROPERTY_TO_COPY_TO)).isEqualTo(expectedContentAsRichtext);
    assertThat(articleToCopyTo.get(PROPERTY_TITLE)).isEqualTo(ARTICLE_NAME);
  }

  @Test
  public void postProcess_copyFromOneUserComment() throws ParseException {
    CommunityUser author = mock(CommunityUser.class);
    when(author.isAnonymous()).thenReturn(false);
    when(author.getName()).thenReturn(COMMENT_AUTHOR_NAME);

    Content articleToCopyTo = getContent(ARTICLE_CONTENT_ID);

    Comment comment = mockComment(COMMENT_ID, COMMENT_TEXT, "", dateFromString(COMMENT_DATE));
    when(comment.getAuthor()).thenReturn(author);
    when(commentService.getComment(comment.getId())).thenReturn(comment);

    // Actual computation
    curatedTransferResource.postProcess(ARTICLE_CONTENT_ID, COMMENT_ID);

    Markup expectedContentAsRichtext = BbCodeToCoreMediaRichtextTransformer.newInstance().transform(EXPECTED_CONTENT_AS_BB_CODE)
            .withGrammar(COREMEDIA_RICHTEXT_GRAMMAR);
    assertThat(articleToCopyTo.get(CONTENT_PROPERTY_TO_COPY_TO)).isEqualTo(expectedContentAsRichtext);
    assertThat(articleToCopyTo.get(PROPERTY_TITLE)).isEqualTo(ARTICLE_NAME);
  }

  @Test
  public void postProcess_copyFromTwoComments() throws ParseException {
    String secondCommentId = "555";
    String secondCommentAuthorName = "Hobbes";
    String secondCommentText = "3000";

    Content articleToCopyTo = getContent(ARTICLE_CONTENT_ID);

    Comment comment01 = mockComment(COMMENT_ID, COMMENT_TEXT, COMMENT_AUTHOR_NAME, dateFromString(COMMENT_DATE));
    when(commentService.getComment(comment01.getId())).thenReturn(comment01);

    Comment comment02 = mockComment(secondCommentId, secondCommentText, secondCommentAuthorName, dateFromString("23.09.2012-09:04"));
    when(commentService.getComment(comment02.getId())).thenReturn(comment02);

    // Actual computation
    String commentIds = COMMENT_ID + ";" + secondCommentId;
    curatedTransferResource.postProcess(ARTICLE_CONTENT_ID, commentIds);

    String lineBreak = "\r\n";
    String secondCommentBbCode = "[i]" + secondCommentAuthorName + "[/i], " + "23.09.2012 | 09:04:" + "[cmQuote]" + secondCommentText + "[/cmQuote]";

    String mergedCommentsAsBbCode = EXPECTED_CONTENT_AS_BB_CODE + lineBreak + secondCommentBbCode;
    Markup expectedRichtextContent = BbCodeToCoreMediaRichtextTransformer.newInstance().transform(mergedCommentsAsBbCode)
            .withGrammar(COREMEDIA_RICHTEXT_GRAMMAR);
    assertThat(articleToCopyTo.get(CONTENT_PROPERTY_TO_COPY_TO)).isEqualTo(expectedRichtextContent);
    assertThat(articleToCopyTo.get(PROPERTY_TITLE)).isEqualTo(ARTICLE_NAME);
  }

  @Test
  public void postProcess_commentNotFound() {
    Content articleToCopyTo = getContent(ARTICLE_CONTENT_ID);

    // Actual computation
    curatedTransferResource.postProcess(ARTICLE_CONTENT_ID, COMMENT_ID);

    Markup expectedRichtextContent = BbCodeToCoreMediaRichtextTransformer.newInstance().transform(EXPECTED_DUMMY_CONTENT_AS_BB_CODE)
            .withGrammar(COREMEDIA_RICHTEXT_GRAMMAR);
    assertThat(articleToCopyTo.get(CONTENT_PROPERTY_TO_COPY_TO)).isEqualTo(expectedRichtextContent);
    assertThat(articleToCopyTo.get(PROPERTY_TITLE)).isEqualTo(ARTICLE_NAME);
  }

  // --- CuratedTransfer: Image attachments ----------------------------------------------------------------------------

  @Test(expected = IllegalArgumentException.class)
  public void postProcessImages_capIdMustNotBeNull() {
    curatedTransferResource.postProcessImages(null, VALID_COMMENT_IDS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void postProcessImages_commentIdsMustNotBeNull() {
    curatedTransferResource.postProcess(ARTICLE_CONTENT_ID, null);
  }

  @Test
  public void postProcessImages_copyFromOneCommentWithoutImageAttachment() throws ParseException {
    // Mock comment
    Comment commentWithoutImageAttachment = mockComment(COMMENT_ID, DUMMY_TEXT, DUMMY_TEXT, createDefaultDate());
    when(commentService.getComment(commentWithoutImageAttachment.getId())).thenReturn(commentWithoutImageAttachment);

    // Mock image gallery (including the containing folder)
    Content imageGallery = getContent(IMAGE_GALLERY_CONTENT_ID);

    curatedTransferResource.postProcessImages(IMAGE_GALLERY_CONTENT_ID, COMMENT_ID);

    assertThat(imageGallery).satisfies(
            gallery -> {
              assertThat(gallery.get(PROPERTY_TITLE)).isEqualTo(IMAGE_GALLERY_NAME);
              assertThat(gallery.get(CONTENT_PROPERTY_TO_COPY_TO)).isInstanceOf(Markup.class);
            }
    );
  }

  @Test
  public void postProcessImages_copyOneImageAttachmentFromOneComment() throws Exception {
    // Mock image gallery (including the containing folder)
    Content imageGallery = getContent(IMAGE_GALLERY_CONTENT_ID);
    if (!imageGallery.isCheckedOut())
      imageGallery.checkOut();

    // image attachment
    com.coremedia.cap.common.Blob imageAttachmentAsCapBlob =
            contentRepository.getConnection().getBlobService().fromInputStream(
                    getClass().getResourceAsStream(CLASS_PATH + IMAGE_ATTACHMENT_FILE_NAME), IMAGE_MIME_TYPE);

    // Mock comment
    List<Blob> imageAttachments = List.of(mockImageBlob(IMAGE_ATTACHMENT_FILE_NAME));
    Comment commentWithOneImageAttachment = mockCommentWithImageAttachment(COMMENT_ID, imageAttachments);
    when(commentWithOneImageAttachment.getText()).thenReturn(COMMENT_TEXT);
    when(commentService.getComment(commentWithOneImageAttachment.getId())).thenReturn(commentWithOneImageAttachment);

    // Fake image attachment
    Blob imageAttachment = commentWithOneImageAttachment.getAttachments().get(0);
    when(blobConverter.capBlobFrom(imageAttachment)).thenReturn(imageAttachmentAsCapBlob);

    // Actual computation
    curatedTransferResource.postProcessImages(IMAGE_GALLERY_CONTENT_ID, COMMENT_ID);

    assertThat(imageGallery).satisfies(
            gallery -> {
              assertThat(gallery.getType()).isEqualTo(contentRepository.getContentType(CM_GALLERY_DOC_TYPE));
              assertThat(gallery.get(PROPERTY_TITLE)).isEqualTo(IMAGE_GALLERY_NAME);
              assertThat(gallery.get(CONTENT_PROPERTY_TO_COPY_TO)).isInstanceOf(Markup.class);
              assertThat(gallery.getList(CM_GALLERY_PROPERTY_TO_COPY_TO))
                      .map(item -> ((Content) item).getName())
                      .containsExactly(IMAGE_ATTACHMENT_FILE_NAME_WITHOUT_TYPE);
            }
    );
  }

  @Test
  public void postProcessImages_copyTwoImageAttachmentsWithSameFileNameFromOneComment() throws Exception {
    // Mock image gallery (including the containing folder)
    Content imageGallery = getContent(IMAGE_GALLERY_CONTENT_ID);

    if (!imageGallery.isCheckedOut())
      imageGallery.checkOut();

    // Fake image attachments
    Blob firstAttachment = mockImageBlob(IMAGE_ATTACHMENT_FILE_NAME);
    Blob secondAttachment = mockImageBlob(IMAGE_ATTACHMENT_FILE_NAME);
    List<Blob> imageAttachments = List.of(firstAttachment, secondAttachment);
    com.coremedia.cap.common.Blob dummyCapBlob =
            contentRepository.getConnection().getBlobService().fromInputStream(
                    getClass().getResourceAsStream(CLASS_PATH + IMAGE_ATTACHMENT_FILE_NAME), IMAGE_MIME_TYPE);
    when(blobConverter.capBlobFrom(firstAttachment)).thenReturn(dummyCapBlob);
    when(blobConverter.capBlobFrom(secondAttachment)).thenReturn(dummyCapBlob);

    // Mock comments
    Comment firstComment = mockCommentWithImageAttachment(COMMENT_ID, imageAttachments);
    when(commentService.getComment(firstComment.getId())).thenReturn(firstComment);

    // Actual computation
    curatedTransferResource.postProcessImages(IMAGE_GALLERY_CONTENT_ID, COMMENT_ID);

    assertThat(imageGallery).satisfies(
            gallery -> {
              assertThat(gallery.getType()).isEqualTo(contentRepository.getContentType(CM_GALLERY_DOC_TYPE));
              assertThat(gallery.getList(CM_GALLERY_PROPERTY_TO_COPY_TO))
                      .hasSize(2)
                      .allSatisfy(item ->
                              assertThat(item).isInstanceOf(Content.class)
                                      .asInstanceOf(type(Content.class))
                                      .extracting(Content::getName).asString()
                                      .startsWith(IMAGE_ATTACHMENT_FILE_NAME_WITHOUT_TYPE));
            }
    );
  }

  @Test
  public void postProcessImages_copyOneImageAttachmentWhoseFilePathExistsInTheRepository() throws Exception {
    // Mock image gallery (including the containing folder)
    Content imageGallery = getContent(IMAGE_GALLERY_CONTENT_ID);
    if (!imageGallery.isCheckedOut())
      imageGallery.checkOut();

    // image attachments
    Blob imageAttachment = mockImageBlob(IMAGE_ATTACHMENT_FILE_NAME);
    com.coremedia.cap.common.Blob dummyCapBlob =
            contentRepository.getConnection().getBlobService().fromInputStream(
                    getClass().getResourceAsStream(CLASS_PATH + IMAGE_ATTACHMENT_FILE_NAME), IMAGE_MIME_TYPE);

    when(blobConverter.capBlobFrom(imageAttachment)).thenReturn(dummyCapBlob);

    // Mock comments
    Comment firstComment = mockCommentWithImageAttachment(COMMENT_ID, List.of(imageAttachment));
    when(commentService.getComment(firstComment.getId())).thenReturn(firstComment);

    // Actual computation
    curatedTransferResource.postProcessImages(IMAGE_GALLERY_CONTENT_ID, COMMENT_ID);

    assertThat(imageGallery).satisfies(
            gallery -> {
              assertThat(gallery.getType()).isEqualTo(contentRepository.getContentType(CM_GALLERY_DOC_TYPE));
              assertThat(gallery.getList(CM_GALLERY_PROPERTY_TO_COPY_TO))
                      .hasSize(1)
                      .allSatisfy(item ->
                              assertThat(item).isInstanceOf(Content.class)
                                      .asInstanceOf(type(Content.class))
                                      .extracting(Content::getName).asString()
                                      .startsWith(IMAGE_ATTACHMENT_FILE_NAME_WITHOUT_TYPE));
            }
    );
  }

  @Test
  public void postProcess_commentWithInvalidUser() throws ParseException {
    CommunityUser author = mock(CommunityUser.class);
    when(author.isAnonymous()).thenThrow(new ModelException("No delegate for model"));

    Content articleToCopyTo = getContent(ARTICLE_CONTENT_ID);

    Comment comment = mockComment(COMMENT_ID, COMMENT_TEXT, "", dateFromString(COMMENT_DATE));
    when(comment.getAuthor()).thenReturn(author);
    when(commentService.getComment(comment.getId())).thenReturn(comment);

    // Actual computation
    curatedTransferResource.postProcess(ARTICLE_CONTENT_ID, COMMENT_ID);

    String expectedContentAsBbCode = "[i]" + "anonymous" + "[/i], " + "21.09.2012 | 16:23:" + "[cmQuote]" + COMMENT_TEXT + "[/cmQuote]";
    Markup expectedContentAsRichtext = BbCodeToCoreMediaRichtextTransformer.newInstance().transform(expectedContentAsBbCode)
            .withGrammar(COREMEDIA_RICHTEXT_GRAMMAR);
    assertThat(articleToCopyTo.get(CONTENT_PROPERTY_TO_COPY_TO)).isEqualTo(expectedContentAsRichtext);
  }

  // --- Helper methods ------------------------------------------------------------------------------------------------

  private Content getContent(String contentId) {
    return contentRepository.getContent(contentId);
  }

  private Comment mockComment(String commentId, String commentText, String commentAuthorName, Date commentDate) throws ParseException {
    Date date = (commentDate != null) ? commentDate : createDefaultDate();

    Comment comment = mock(Comment.class);
    when(comment.getId()).thenReturn(commentId);
    when(comment.getText()).thenReturn(commentText);
    when(comment.getAuthorName()).thenReturn(commentAuthorName);
    when(comment.getCreationDate()).thenReturn(date);

    return comment;
  }

  private Review mockReview(String reviewId, String reviewText, String reviewAuthorName, Date reviewDate) throws ParseException {
    Date date = (reviewDate != null) ? reviewDate : createDefaultDate();

    Review review = mock(Review.class);
    when(review.getId()).thenReturn(reviewId);
    when(review.getText()).thenReturn(reviewText);
    when(review.getAuthorName()).thenReturn(reviewAuthorName);
    when(review.getCreationDate()).thenReturn(date);

    return review;
  }

  private Comment mockCommentWithImageAttachment(String commentId, List<Blob> imageAttachments) throws ParseException {
    Comment comment = mockComment(commentId, "COMMENT_WITH_ATTACHMENT", "JOHN_DOE", createDefaultDate());
    when(comment.getAttachments()).thenReturn(imageAttachments);
    return comment;
  }

  private Blob mockImageBlob(String attachmentFileName) {
    Blob imageBlob = mock(Blob.class);
    when(imageBlob.getContentType()).thenReturn(IMAGE_MIME_TYPE);
    when(imageBlob.getFileName()).thenReturn(attachmentFileName);
    return imageBlob;
  }

  private static Date dateFromString(String germanDateString) throws ParseException {
    return new SimpleDateFormat("dd.MM.yyyy-HH:mm").parse(germanDateString);
  }

  private static Date createDefaultDate() throws ParseException {
    return dateFromString(DEFAULT_DATE_STRING);
  }
}
