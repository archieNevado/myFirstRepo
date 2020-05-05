package com.coremedia.blueprint.studio.uitest.wrappers.moderation;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base.ModerationDetailViewBase;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.comment.ModerationCommentDetailView;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.profile.ModerationProfileDetailView;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.review.ModerationReviewDetailView;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.filter.ModerationSearchFiltersPanel;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.moderateditems.ModeratedItemsRowSelectionModel;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.moderateditems.ModeratedItemsView;
import com.coremedia.elastic.core.api.models.Model;
import com.coremedia.elastic.core.api.users.User;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.reviews.Review;
import com.coremedia.uitesting.cms.editor.components.collectionview.search.SearchFiltersPanel;
import com.coremedia.uitesting.ext3.wrappers.MessageBox;
import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.webdriver.IdleIndicators;
import net.joala.condition.Condition;
import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;

@ExtJSObject
@Scope("prototype")
public class ModerationTabPanel extends Panel {
  private static final Logger LOG = LoggerFactory.getLogger(ModerationTabPanel.class);

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  public static final String XTYPE = "com.coremedia.elastic.social.studio.";

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(xtype = "grid", global = false)
  private ModeratedItemsView itemsView;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "com-coremedia-elastic-social-studio-model-impl-CommentImpl", global = false)
  private ModerationCommentDetailView moderationCommentDetailView;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "com-coremedia-elastic-social-studio-model-impl-ReviewImpl", global = false)
  private ModerationReviewDetailView moderationReviewDetailView;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "com-coremedia-elastic-social-studio-model-impl-UserImpl", global = false)
  private ModerationProfileDetailView moderationProfileDetailView;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-empty-details-view", global = false)
  private Panel emptyDetailPanel;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-layout-statusbar", global = false)
  private ModerationStatusBar statusBar;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "editLastButton", global = false)
  private Button editLastButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(xtype = SearchFiltersPanel.XTYPE, global = false)
  private ModerationSearchFiltersPanel moderationSearchFiltersPanel;

  @Inject
  private IdleIndicators idleIndicators;

  /**
   * MessageBox is probably the same as
   * {@link ModerationProfileDetailView#getDeleteConfirmationMessageBox()}. We are using it in here directly
   * to provide a more flexible way to handle any dialogs appearing during moderation steps.
   */
  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private MessageBox messageBox;

  public ModeratedItemsView getItemsView() {
    return itemsView;
  }

  public ModerationCommentDetailView getCommentDetailView() {
    return moderationCommentDetailView;
  }

  public ModerationReviewDetailView getModerationReviewDetailView() {
    return moderationReviewDetailView;
  }

  public ModerationProfileDetailView getProfileDetailView() {
    return moderationProfileDetailView;
  }

  public Panel getEmptyDetailPanel() {
    return emptyDetailPanel;
  }

  public ModerationStatusBar getStatusBar() {
    return statusBar;
  }

  public ModerationSearchFiltersPanel getModerationSearchFiltersPanel() {
    return moderationSearchFiltersPanel;
  }

  public Button getLastEditedButton() {
    return editLastButton;
  }

  /**
   * <p>
   * Convenience method to approve a comment. Handles waiting for the comment in item list and
   * that it disappears afterwards.
   * </p>
   *
   * @param comment comment to moderate
   */
  @SuppressWarnings("TypeMayBeWeakened") // NOSONAR
  public void approve(final Comment comment) {
    LOG.info("Approve comment with text {}",comment.getText());
    approve(comment, moderationCommentDetailView);
  }

  /**
   * <p>
   * Convenience method to approve a review. Handles waiting for the review in item list and
   * that it disappears afterwards.
   * </p>
   *
   * @param review review to moderate
   */
  @SuppressWarnings("TypeMayBeWeakened") // NOSONAR
  public void approve(final Review review) {
    LOG.info("Approve comment with text {}",review.getText());
    approve(review, moderationReviewDetailView);
  }

  /**
   * <p>
   * Convenience method to reject a comment. Handles waiting for the comment in item list and
   * that it disappears afterwards.
   * </p>
   *
   * @param comment comment to moderate
   */
  @SuppressWarnings("TypeMayBeWeakened") // NOSONAR
  public void reject(final Comment comment) {
    LOG.info("Reject comment with text {}",comment.getText());
    reject(comment, moderationCommentDetailView);
  }

  /**
   * <p>
   * Convenience method to approve a user. Handles waiting for the user in item list and
   * that it disappears afterwards.
   * </p>
   *
   * @param user user to moderate
   */
  @SuppressWarnings({"TypeMayBeWeakened",
                     "PersonalData" // okay to log @PersonalData user name in test class
                    })
  public void approve(final User user) {
    LOG.info("Approve userwith name {}", user.getName());
    approve(user, moderationProfileDetailView);
  }

  /**
   * <p>
   * Convenience method to reject a user. Handles waiting for the user in item list and
   * that it disappears afterwards. In addition, if a confirmation dialog pops up, the
   * deletion of the user is confirmed.
   * </p>
   *
   * @param user user to moderate
   */
  @SuppressWarnings({"TypeMayBeWeakened",
                     "PersonalData" // okay to log @PersonalData user name in test class
                    })
  public void reject(final User user) {
    LOG.info("Reject user with name {}", user.getName());
    reject(user, moderationProfileDetailView);
  }

  private void approve(final Model item, final ModerationDetailViewBase detailView) {
    moderate(item, detailView.getApproveButton());
  }

  private void reject(final Model item, final ModerationDetailViewBase detailView) {
    moderate(item, detailView.getRejectButton());
  }

  private void moderate(final Model item, final Button button) {
    select(item);
    button.enabled().waitUntilTrue();
    button.click();
    LOG.info("Clicked button");
    confirmIfApplicable();
    itemsView.getStore().contains(item).waitUntilFalse();
  }

  private void confirmIfApplicable() {
    idleIndicators.idle().waitUntilTrue();
    //noinspection ConstantConditions
    if (messageBox.visible().await()) {
      messageBox.getBottomToolbar().getOkButton().click();
    }
  }

  public void select(final Model item) {
    final ModeratedItemsRowSelectionModel selectionModel = itemsView.getSelectionModel();
    final Long position = itemsView.getStore().position(item).await(Matchers.greaterThanOrEqualTo(0L));
    LOG.info("Select position for {} in moderation list: {}", item, position);
    assert position != null : "Position must not be null";
    selectionModel.selectRow(position);
    idleIndicators.idle().waitUntilTrue();
  }

  /**
   * @deprecated Use {@link #getStatusBar()} and its {@link ModerationStatusBar#getMessageDisplayField()} instead or
   *             its specialized conditions for status bar state indication.
   */
  @Deprecated
  public Condition<String> statusBarText() {
    return getStatusBar().getMessageDisplayField().value();
  }
}
