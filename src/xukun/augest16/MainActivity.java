package xukun.augest16;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import xukun.augest16.R;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.RemoteViews;

public class MainActivity extends FragmentActivity implements
		playFragment.Callbacks, MusicDB.Callbacks, playListFragment.Callbacks,
		menuFragment.Callbacks{
	public MusicDB musicdb;
	//List<Map<String, String>> listItems;
	Handler handler;
	boolean active = false;
	musicService.MusicBinder binder;
	Activity self;
	playFragment fragment;
	menuFragment menu;
	playListFragment listfragment;
	ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			binder = (musicService.MusicBinder) service;
			binder.setUIInitialDB(self);
			musicdb = binder.getMusicDB();
			menu = new menuFragment();
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.menuContainer, menu).commit();
			listfragment = new playListFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.listContainer, listfragment).commit();
			fragment = new playFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.playContainer, fragment).commit();

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self = this;

		setContentView(R.layout.mainlayout);

		Intent intent = new Intent();
		intent.setAction("xukun.augest16.audioservice");

		bindService(intent, conn, Service.BIND_AUTO_CREATE);

	}

	@Override
	public void onButtonAction(int id) {
		if (id == -1) {
			Bundle arguments = new Bundle();
			arguments.putInt("id", musicdb.pre());
			fragment = new playFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.playContainer, fragment).commit();
		} else if (id == 1) {
			Bundle arguments = new Bundle();
			arguments.putInt("id", musicdb.next());
			fragment = new playFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.playContainer, fragment).commit();
		} else if (id == 0) {
			musicdb.togglePlay();
		}
	}

	@Override
	public void onComplete(int id) {
		// Bundle arguments = new Bundle();
		// arguments.putInt("id", id);
		fragment = new playFragment();
		// fragment.setArguments(arguments);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.playContainer, fragment).commit();
	}

	@Override
	public void onItemSelected(int id) {
		Bundle arguments = new Bundle();
		arguments.putInt("id", id);
		fragment = new playFragment();
		fragment.setArguments(arguments);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.playContainer, fragment).commit();

	}

	@Override
	public MusicDB getMusicDB() {
		return musicdb;
	}

	@Override
	public void setMusicList(List<Map<String, String>> s) {
		binder.setMusicList(s);
	}

	public List<Map<String, String>> getMusicList() {
		return binder.getMusicList();
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler h) {
		handler = h;
	}

	public void setPause(boolean b) {
		musicdb.setPause(b);
	}

	public boolean getPause() {
		return musicdb.getPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
		active = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		active = false;
	}

	public boolean isActive() {
		return active;
	}

	public void updateGraphic(int id) {
		fragment.setToggleStatus();
		fragment.updateSongDetail();

	}

	@Override
	public void scanPhone() {
		binder.cleanMusicList();
		listfragment.scanPhone();
	}

}
