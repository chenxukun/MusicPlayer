package xukun.augest16;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class YoutubeSearch extends
		AsyncTask<String, Void, Void> {
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final long NUMBER_OF_VIDEOS_RETURNED = 5;
	private static YouTube youtube;
	private static int APIKEYIds = R.string.youtube_key;
	private static String APIKEY;
	List<Map<String, Object>> results;
	VideoSearchActivity ctx;
	ProgressDialog diag;
	public YoutubeSearch(VideoSearchActivity a,List<Map<String, Object>> results) {
		APIKEY = (String) a.getResources().getText(APIKEYIds);
		this.results = results;
		ctx = a;
	}

	public void search(String title) {

		try {
			youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY,
					new HttpRequestInitializer() {
						public void initialize(HttpRequest request)
								throws IOException {
						}
					}).setApplicationName("youtube-cmdline-search-sample")
					.build();

			// Get query term from user.
			String queryTerm = title;

			YouTube.Search.List search = youtube.search().list("id,snippet");
			search.setKey(APIKEY);
			search.setQ(queryTerm);
			search.setType("video");
			search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
			search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
			SearchListResponse searchResponse = search.execute();

			List<SearchResult> searchResultList = searchResponse.getItems();

			if (searchResultList != null) {
				constructItem(searchResultList.iterator());
			}
		} catch (GoogleJsonResponseException e) {
			System.err.println("There was a service error: "
					+ e.getDetails().getCode() + " : "
					+ e.getDetails().getMessage());
		} catch (IOException e) {
			System.err.println("There was an IO error: " + e.getCause() + " : "
					+ e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void constructItem(
			Iterator<SearchResult> iteratorSearchResults) {
		if (!iteratorSearchResults.hasNext()) {
		}
		while (iteratorSearchResults.hasNext()) {
			Map<String, Object> resultItem = new HashMap<String, Object>();
			SearchResult singleVideo = iteratorSearchResults.next();
			ResourceId rId = singleVideo.getId();

			// Double checks the kind is video.
			if (rId.getKind().equals("youtube#video")) {
				Thumbnail thumbnail = (Thumbnail) singleVideo.getSnippet()
						.getThumbnails().get("default");
				resultItem.put("id", rId.getVideoId());
				resultItem.put("title", singleVideo.getSnippet().getTitle());
				resultItem.put("thumbnail",downloadThumbnail(thumbnail.getUrl()));
				results.add(resultItem);
			}
		}
	}

	private Bitmap downloadThumbnail(String param) {
		try {
			URL url = new URL(param);
			URLConnection conn = url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream input = conn.getInputStream();
			Bitmap pic = BitmapFactory.decodeStream(input);
			return pic;
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	protected Void doInBackground(String... params) {
		search(params[0]);
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result){
		diag.dismiss();
		ctx.updateVideoList();
	}
	
	@Override
	protected void onPreExecute(){
		diag = ProgressDialog.show(ctx,"Please wait ...","Scanning Phone...", true);
		diag.setCancelable(false);
	}
}
