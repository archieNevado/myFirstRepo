package com.coremedia.blueprint.studio.uitest.wrappers.moderation.archive;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.archive.archiveditems.ArchivedItemsView;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.archive.details.ArchivedCommentDetailView;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.archive.details.ArchivedDetailViewStatusHeader;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.archive.details.MultiCommentDetailView;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.archive.filter.ArchivedFilterPanel;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.moderateditems.ModeratedItemsRowSelectionModel;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.moderateditems.ModeratedItemsViewStore;
import com.coremedia.elastic.core.api.models.Model;
import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.webdriver.IdleIndicators;
import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

@ExtJSObject
@Scope("prototype")
public class ArchiveTabPanel extends Panel {
  private static final Logger LOG = getLogger(lookup().lookupClass());

  @Inject
  private IdleIndicators idleIndicators;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "gridPanel", global = false)
  private ArchivedItemsView itemsView;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-archive-status-header-view", global = false)
  private ArchivedDetailViewStatusHeader statusHeader;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "com-coremedia-elastic-social-studio-model-impl-CommentImpl", global = false)
  private ArchivedCommentDetailView archiveCommentDetailView;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "filter-panel", global = false)
  private ArchivedFilterPanel filterPanel;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "multi-comment-detail-view", global = false)
  private MultiCommentDetailView multiCommentDetailView;

  public ArchivedItemsView getItemsView() {
    return itemsView;
  }

  public ArchivedDetailViewStatusHeader getStatusHeader() {
    return statusHeader;
  }

  public ArchivedCommentDetailView getCommentDetailView() {
    return archiveCommentDetailView;
  }

  public MultiCommentDetailView getMultiCommentDetailView() {
    return multiCommentDetailView;
  }

  public ArchivedFilterPanel getFilterPanel() {
    return filterPanel;
  }

  public void select(final Model item) {
    final ModeratedItemsRowSelectionModel selectionModel = itemsView.getSelectionModel();
    ModeratedItemsViewStore store = itemsView.getStore();
    final Long position = store.position(item).await(Matchers.greaterThanOrEqualTo(0L));
    LOG.info("Select position for {} in moderation list: {}", item, position);
    assert position != null : "Position must not be null";
    selectionModel.selectRow(position);
    idleIndicators.idle().waitUntilTrue();
  }

}
