package xukun.augest16;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import xukun.augest16.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class playListFragment extends ListFragment{
	private Callbacks cb;
	private List<Map<String, String>> newList;
	private OnClickListener listener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			String title = cb.getMusicList().get(position).get("title");
			//Toast.makeText(getActivity(), title,Toast.LENGTH_SHORT).show();
			Bundle data = new Bundle();
			data.putString("title",title);
			Intent intent = new Intent(getActivity(),VideoSearchActivity.class);
			intent.putExtras(data);
			//intent.setAction("xukun.augest16.searchVideo");
			startActivity(intent);
			getActivity().overridePendingTransition( R.anim.slideinleft, R.anim.slideoutleft );
		}
		
	};
	public interface Callbacks{
		public void onItemSelected(int id);
		public MusicDB getMusicDB();
		public List<Map<String,String>> getMusicList();
		public void setMusicList(List<Map<String,String>> s);
		public void updateGraphic(int id);
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		cb = (Callbacks)activity;
	}
	
	@Override
	public void onDetach(){
		super.onDetach();
		cb = null;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		List<Map<String,String>> s= cb.getMusicDB().reconstructList();
		if(s==null){
			//cb.getMusicDB().scanPhone(cb.getMusicList());
		}else{
			cb.setMusicList(s);
		}
		
		updateMusicList();
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id){
		cb.onItemSelected(position);
	}
	

	public void scanPhone(){
		scanOperation task = new scanOperation(getActivity());
		task.execute();
	}
	public void updateMusicList(){
		/*SimpleAdapter sa = new SimpleAdapter(this.getActivity(),cb.getMusicList(),R.layout.listitemlayout,
				new String[]{"title","artist","duration"}, new int[]{R.id.title,R.id.artist,R.id.duration});
		setListAdapter(sa);*/
		BaseAdapter adapter = new BaseAdapter(){
			@Override
			public View getView(int position, View convertView, ViewGroup parent){
				LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = vi.inflate(R.layout.listitemlayout,parent,false);
				TextView title = (TextView) view.findViewById(R.id.title);
				TextView artist = (TextView) view.findViewById(R.id.artist);
				TextView duration = (TextView) view.findViewById(R.id.duration);
				Button video = (Button)view.findViewById(R.id.video);
				
				title.setText(cb.getMusicList().get(position).get("title"));
				artist.setText(cb.getMusicList().get(position).get("artist"));
				duration.setText(cb.getMusicList().get(position).get("duration"));
				video.setTag(position);
				video.setOnClickListener(listener);
				
				return view;
			}

			@Override
			public int getCount() {
				return cb.getMusicList().size();
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
	
	private class scanOperation extends AsyncTask<Void,Void,Void>{
		Context ctx;
		ProgressDialog diag;
		public scanOperation(Context ctx){
			this.ctx = ctx;
		}
		@Override
		protected Void doInBackground(Void... params) {
			cb.getMusicDB().scanPhone(cb.getMusicList());
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			diag.dismiss();
			updateMusicList();
		}
		
		@Override
		protected void onPreExecute(){
			diag = ProgressDialog.show(ctx,"Please wait ...","Scanning Phone...", true);
			diag.setCancelable(false);
		}

	}
}
