package com.coremedia.blueprint.taxonomies.cycleprevention;

import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.common.CapSession;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class TaxonomyCycleValidatorImplTest {

  private static final ContentType TAXONOMY_CONTENT_TYPE = mock(ContentType.class);
  private static final String ROOT_ID = "rootId";
  private static final String CHILD_ID_1 = "CHILD_1";

  @Spy
  private TaxonomyCycleValidatorImpl taxonomyCycleValidator;

  @Test
  void onlyOneTaxonomyIsNotACycle() {
    boolean isCyclic = taxonomyCycleValidator.isCyclic(mockTaxonomyContentNoChildren(ROOT_ID), TAXONOMY_CONTENT_TYPE);
    assertThat(isCyclic).isFalse();
  }

  @Test
  void twoTimesTheSameTaxonomyIdInPathIsACycle() {
    Content leaf = mockTaxonomyContentNoChildren(ROOT_ID);
    Content root = mockRootWithChildren(leaf);
    boolean isCyclic = taxonomyCycleValidator.isCyclic(root, TAXONOMY_CONTENT_TYPE);
    assertThat(isCyclic).isTrue();
  }

  @Test
  void twoTimesDifferentTaxonomyIdsInPathIsACycle() {
    Content leaf = mockTaxonomyContentNoChildren(CHILD_ID_1);
    Content root = mockRootWithChildren(leaf);
    boolean isCyclic = taxonomyCycleValidator.isCyclic(root, TAXONOMY_CONTENT_TYPE);
    assertThat(isCyclic).isFalse();
  }

  @NonNull
  private Content mockTaxonomyContentNoChildren(@NonNull String id) {
    Content content = mockContent();
    doReturn(true).when(taxonomyCycleValidator).isTaxonomy(content, TAXONOMY_CONTENT_TYPE);
    doReturn(false).when(content).isDestroyed();
    doReturn(true).when(content).isInProduction();
    when(content.getId()).thenReturn(id);
    return content;
  }

  @NonNull
  private Content mockRootWithChildren(@NonNull Content... children) {
    Content content = mockContent();
    when(content.isDestroyed()).thenReturn(false);
    when(content.isInProduction()).thenReturn(true);
    doReturn(true).when(taxonomyCycleValidator).isTaxonomy(content, TAXONOMY_CONTENT_TYPE);
    when(content.getLinks(TaxonomyCycleValidatorImpl.CHILDREN_ATTRIBUTE_IDENTIFIER)).thenReturn(Arrays.asList(children));
    when(content.getId()).thenReturn(ROOT_ID);
    return content;
  }

  @NonNull
  private static Content mockContent() {
    Content content = mock(Content.class);
    CapSession session = mock(CapSession.class);
    ContentRepository repository = mock(ContentRepository.class);
    CapConnection connection = mock(CapConnection.class);

    doReturn(repository).when(content).getRepository();
    doReturn(connection).when(repository).getConnection();
    doReturn(session).when(connection).getConnectionSession();
    doReturn(session).when(session).activate();

    return content;
  }
}
