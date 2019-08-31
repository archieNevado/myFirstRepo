package com.coremedia.blueprint.contenthub.adapters.youtube;

import com.coremedia.cache.Cache;
import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubAdapterBinding;
import com.coremedia.contenthub.api.ContentHubContext;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.Folder;
import com.coremedia.contenthub.api.Item;
import com.coremedia.contenthub.api.exception.ContentHubException;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.Video;
import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class YouTubeContentHubAdapter implements ContentHubAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeContentHubAdapter.class);
  private static final String HTTPS_WWW_GOOGLEAPIS_COM_AUTH_YOUTUBE_FORCE_SSL = "https://www.googleapis.com/auth/youtube.force-ssl";
  public static final long MAX_RESULTS = 10L; //50 is the max value

  private ContentHubAdapterBinding<YouTubeContentHubSettings> binding;
  private YouTubeFolder rootFolder;
  private YouTubeConnector youTubeConnector;

  YouTubeContentHubAdapter(ContentHubAdapterBinding<YouTubeContentHubSettings> binding, Cache cache) {
    this.binding = binding;

    try {
      List<String> scopes = Lists.newArrayList(HTTPS_WWW_GOOGLEAPIS_COM_AUTH_YOUTUBE_FORCE_SSL);
      String credentialsJson = null;
      if (binding.getSettings() != null) {
        credentialsJson = binding.getSettings().getCredentialsJson();
      }
      if (credentialsJson == null || credentialsJson.length() == 0) {
        throw new ContentHubException("No credentialsJson found for youtube adapter '" + binding.getConnectionId() + "'");
      }

      GoogleCredential credential = GoogleCredential.fromStream(new ByteArrayInputStream(credentialsJson.getBytes()));
      if (credential.createScopedRequired()) {
        credential = credential.createScoped(scopes);
      }
      YouTube youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName("youtubeProvider").build();
      this.youTubeConnector = new YouTubeConnector(youTube, cache);
    } catch (ContentHubException che) {
      throw che;
    } catch (Exception e) {
      LOGGER.error("Error initializing youtube adapter '" + binding.getConnectionId() + "': " + e.getMessage(), e);
      throw new ContentHubException(e);
    }
  }

  @NonNull
  @Override
  public Folder getRootFolder(@NonNull ContentHubContext context) throws ContentHubException {
    ContentHubObjectId rootId = ContentHubObjectId.createRootId(binding.getConnectionId());
    rootFolder = new YouTubeFolder(binding, rootId, getChannelDisplayName());
    return rootFolder;
  }

  @Nullable
  @Override
  public Item getItem(@NonNull ContentHubContext context, @NonNull ContentHubObjectId id) throws ContentHubException {
    Video video = youTubeConnector.getVideo(id.getExternalId());
    return new YouTubeItem(binding, id, video);
  }

  @Nullable
  @Override
  public Folder getFolder(@NonNull ContentHubContext context, @NonNull ContentHubObjectId id) throws ContentHubException {
    if (id.isRootId()) {
      //this forces an invalidation
      return getRootFolder(context);
    }

    Playlist playlist = getPlaylist(id.getExternalId());
    return new YouTubeFolder(binding, id, playlist);
  }

  @NonNull
  @Override
  public List<Item> getItems(@NonNull ContentHubContext context, @NonNull Folder folder) throws ContentHubException {
    List<Item> result = new ArrayList<>();

    String playlistId = folder.getId().getExternalId();

    if (folder.getId().isRootId()) {
      if (binding.getSettings() != null) {
        String channelId = binding.getSettings().getChannelId();
        if (!StringUtils.isEmpty(channelId)) {
          List<Video> videos = youTubeConnector.getVideos(channelId);
          for (Video video : videos) {
            ContentHubObjectId itemId = ContentHubObjectId.createItemId(folder.getId(), video.getId());
            result.add(getItem(context, itemId));
          }
        }
      }
    } else {
      List<PlaylistItem> playlistItems = youTubeConnector.getPlaylistItems(playlistId);
      for (PlaylistItem playlistItem : playlistItems) {
        String vId = playlistItem.getSnippet().getResourceId().getVideoId();
        ContentHubObjectId itemId = ContentHubObjectId.createItemId(folder.getId(), vId);
        result.add(getItem(context, itemId));
      }
    }
    return result;
  }

  @NonNull
  @Override
  public List<Folder> getSubFolders(@NonNull ContentHubContext context, @NonNull Folder folder) throws ContentHubException {
    List<Folder> result = new ArrayList<>();

    if (folder.getId().isRootId()) {
      List<Playlist> playLists = getPlaylists();
      int counter = 0;
      for (Playlist list : playLists) {
        //in order to prevent performance issues, only deliver 1000 items max!
        if (counter == 1000) {
          break;
        }
        ContentHubObjectId categoryId = ContentHubObjectId.createFolderId(binding.getConnectionId(), list.getId());
        String name = list.getSnippet().getTitle();
        YouTubeFolder channel = new YouTubeFolder(binding, categoryId, name);
        result.add(channel);
        counter++;
      }
    }

    return result;
  }

  @Nullable
  @Override
  public Folder getParent(@NonNull ContentHubContext context, @NonNull Folder folder) throws ContentHubException {
    if (folder.getId().isRootId()) {
      return null;
    }

    return rootFolder;
  }

  //------------------------ Helper ------------------------------------------------------------------------------------
  private Playlist getPlaylist(String playlistId) {
    if (StringUtils.isEmpty(playlistId) || playlistId.equals("root")) {
      return null;
    }
    List<Playlist> playlists = getPlaylists();
    for (Playlist playlist : playlists) {
      if (playlist.getId().equals(playlistId)) {
        return playlist;
      }
    }
    return null;
  }

  private String getChannelDisplayName() {
    String name = binding.getSettings().getDisplayName();
    if (StringUtils.isEmpty(name)) {
      LOGGER.warn("No display name set for YouTube adapter '" + binding.getConnectionId() + "'");
      name = binding.getConnectionId();
    }
    return name;
  }

  private List<Playlist> getPlaylists() {
    String channelId = binding.getSettings().getChannelId();
    if (!StringUtils.isEmpty(channelId)) {
      return youTubeConnector.getPlaylistsByChannel(channelId);
    }

    String user = binding.getSettings().getUser();
    if (!StringUtils.isEmpty(user)) {
      return youTubeConnector.getPlaylistsByUser(user);
    }

    return Collections.emptyList();
  }
}
