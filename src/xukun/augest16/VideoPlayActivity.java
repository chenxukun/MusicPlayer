package xukun.augest16;


import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.Provider;

import android.os.Bundle;
import android.widget.Toast;

public class VideoPlayActivity extends YouTubeBaseActivity implements
YouTubePlayer.OnInitializedListener{
	 
	private static int APIKEYIds = R.string.youtube_key;
	private static String APIKEY;
	public  String VIDEO_ID = "o7VVHhK9zf0";

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.videoplaylayout);
	        
			Bundle b = getIntent().getExtras();
			VIDEO_ID = b.getString("videoid");
	        YouTubePlayerView youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtubeplayerview);  
	        APIKEY = (String) getResources().getText(APIKEYIds);
	        youTubePlayerView.initialize(APIKEY, this);
	    }

	 @Override
	 public void onInitializationFailure(Provider provider,
	   YouTubeInitializationResult result) {
	  Toast.makeText(getApplicationContext(), 
	    "onInitializationFailure()", 
	    Toast.LENGTH_LONG).show();
	 }

	 @Override
	 public void onInitializationSuccess(Provider provider, YouTubePlayer player,
	   boolean wasRestored) {
	  if (!wasRestored) {
	        player.cueVideo(VIDEO_ID);
	      }
	 }

	}
