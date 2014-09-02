package xukun.augest16;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class VideoSearchActivity extends ListActivity{
	List<Map<String,Object>> results = new LinkedList<Map<String,Object>>();
	VideoSearchActivity self;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		self =this;
		Bundle b = getIntent().getExtras();
		String title = b.getString("title");
		String artist = b.getString("artist");
		YoutubeSearch ys = new YoutubeSearch(this,results);
		ys.execute(title);
		
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
	
	public void updateVideoList(){
		BaseAdapter adapter = new BaseAdapter(){
			@Override
			public View getView(int position, View convertView, ViewGroup parent){
				LayoutInflater vi = (LayoutInflater)self.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = vi.inflate(R.layout.videolistlayout,parent,false);
				TextView title = (TextView) view.findViewById(R.id.vtitle);
				TextView id = (TextView) view.findViewById(R.id.vowner);
				TextView duration = (TextView) view.findViewById(R.id.vduration);
				ImageView thumb = (ImageView) view.findViewById(R.id.vthumb);
				
				title.setText(results.get(position).get("title")+"");
				duration.setText(results.get(position).get("id")+"");
				Drawable d = new BitmapDrawable(getResources(),(Bitmap)results.get(position).get("thumbnail"));
				thumb.setBackgroundDrawable(d);
				
				return view;
			}

			@Override
			public int getCount() {
				return results.size();
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}
		};
		
		setListAdapter(adapter);
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id){
		Bundle data = new Bundle();
		data.putString("videoid",(String) results.get(position).get("id"));
		Intent intent = new Intent(this,VideoPlayActivity.class);
		intent.putExtras(data);
		//intent.setAction("xukun.augest16.searchVideo");
		startActivity(intent);
		overridePendingTransition( R.anim.slideinleft, R.anim.slideoutleft );
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
