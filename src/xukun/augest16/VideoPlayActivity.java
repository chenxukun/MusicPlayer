package xukun.augest16;


import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.Provider;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
		public boolean onCreateOptionsMenu(Menu menu){
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.othermenu,menu);
			//getActionBar().setDisplayShowTitleEnabled(false);
			getActionBar().setDisplayHomeAsUpEnabled(true);
			return super.onCreateOptionsMenu(menu);
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		    // Respond to the action bar's Up/Home button
		    case android.R.id.home:
		        Intent upIntent = NavUtils.getParentActivityIntent(this);
		        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
		            // This activity is NOT part of this app's task, so create a new task
		            // when navigating up, with a synthesized back stack.
		            TaskStackBuilder.create(this)
		                    // Add all of this activity's parents to the back stack
		                    .addNextIntentWithParentStack(upIntent)
		                    // Navigate up to the closest parent
		                    .startActivities();
		        } else {
		            // This activity is part of this app's task, so simply
		            // navigate up to the logical parent activity.
		            NavUtils.navigateUpTo(this, upIntent);
		            overridePendingTransition( R.anim.slideinright, R.anim.slideoutright );
		        }
		        return true;
		    }
		    return super.onOptionsItemSelected(item);
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
