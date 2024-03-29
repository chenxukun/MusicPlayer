package xukun.augest16;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class musicService extends Service implements MusicDB.Callbacks{
	public class MusicBinder extends Binder{
		public int getSongID(){
			return id;
		}
		public MusicDB getMusicDB(){
			return musicdb;
		}
		public List<Map<String,String>> getMusicList(){
			return musicList;
		}
		public void setMusicList(List<Map<String,String>> l){
			musicList = l;
		}
		public void setUIInitialDB(Activity ma){
			ui = ma;
			musicdb = MusicDB.getInstance(self,ma);
			AudioManager myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE); 
			boolean b = myAudioManager.isWiredHeadsetOn();
			musicdb.setHeadsetPlug(b);
		    IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
		    HeadsetListener receiver = new HeadsetListener();
		    registerReceiver( receiver, receiverFilter );
			showNotification();
		}
		public void cleanMusicList(){
			musicList = new LinkedList<Map<String, String>>();
		}
	}
	private Activity ui;
	private MusicDB musicdb;
	private List<Map<String,String>> musicList;
	private int id;
	private Service self;
	private NotificationManager nm;
	private int notificationID = 1;
	private MusicBinder binder = new MusicBinder();
	private SensorManager sensor;
	private float mAccel; // acceleration apart from gravity
	private float mAccelCurrent; // current acceleration including gravity
	private float mAccelLast; // last acceleration including gravity
	RemoteViews rv;
	Notification notify;
	Notification.Builder builder;

	private SensorEventListener mSensorListener = new SensorEventListener(){

		@Override
		public void onSensorChanged(SensorEvent event) {
		      float x = event.values[0];
		      float y = event.values[1];
		      float z = event.values[2];
		      mAccelLast = mAccelCurrent;
		      mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
		      float delta = mAccelCurrent - mAccelLast;
		      mAccel = mAccel * 0.9f + delta; // perform low-cut filter
		      if (mAccel > 12) {
		    	    //Toast toast = Toast.makeText(getApplicationContext(), "Device has shaken.", Toast.LENGTH_LONG);
		    	    //toast.show();
		    	    if(!musicdb.getPause())
		    	    	musicdb.startPlay(musicdb.next());
		    	}
		      
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
		}
		
	};
	
	@Override
	public IBinder onBind(Intent intent) {

		return binder;
	}
	@Override
	public void onCreate(){
		self = this;
		musicList = new LinkedList<Map<String,String>>();
		nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	    sensor = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    sensor.registerListener(mSensorListener, sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	    mAccel = 0.00f;
	    mAccelCurrent = SensorManager.GRAVITY_EARTH;
	    mAccelLast = SensorManager.GRAVITY_EARTH;
	}
	@Override
	public void onDestroy(){
		nm.cancel(notificationID);
		sensor.unregisterListener(mSensorListener);
	}
	private void showNotification(){
		
		rv = new RemoteViews(getPackageName(),
				R.layout.notificationlayout);
		// start activity
		Intent intent = new Intent(this,MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
		// toggle music
		Intent toIntent = new Intent();
		toIntent.setAction("xukun.augest16.audiobroadcast");
		toIntent.putExtra("msg","toggle");
		PendingIntent toPi = PendingIntent.getBroadcast(this,1,toIntent,0);
		
		// next music
		Intent nextIntent = new Intent();
		nextIntent.setAction("xukun.augest16.audiobroadcast");
		nextIntent.putExtra("msg","next");
		PendingIntent nextPi = PendingIntent.getBroadcast(this,2,nextIntent,0);
		
		// pre music
		Intent preIntent = new Intent();
		preIntent.setAction("xukun.augest16.audiobroadcast");
		preIntent.putExtra("msg","pre");
		PendingIntent prePi = PendingIntent.getBroadcast(this,3,preIntent,0);
		
		
		rv.setOnClickPendingIntent(R.id.no_pre,prePi);
		rv.setOnClickPendingIntent(R.id.no_next,nextPi);
		rv.setOnClickPendingIntent(R.id.no_play,toPi);
		
		String s = musicdb.getSongName();
		rv.setTextViewText(R.id.no_name, musicdb.getSongName());
		rv.setTextViewText(R.id.no_artist, musicdb.getSongArtist());

		builder = new Notification.Builder(this).
				setContent(rv).setSmallIcon(R.drawable.musicdisk).setOngoing(true).setContentIntent(pi);
		notify = builder.getNotification();
		nm.notify(notificationID,notify);
		startForeground(notificationID,notify);
	}
	@Override
	public void onComplete(int id) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Handler getHandler() {
		return null;
	}
	@Override
	public List<Map<String, String>> getMusicList() {
		return musicList;
	}

	public boolean isActive(){
		return true;
	}
	public void updateGraphic(int id){
		togglePause();
		rv.setTextViewText(R.id.no_name, musicdb.getSongName());
		rv.setTextViewText(R.id.no_artist, musicdb.getSongArtist());

		notify = builder.getNotification();
		nm.notify(notificationID,notify);
	}
	
	public void togglePause(){
		if(musicdb.getPause()){
			rv.setImageViewResource(R.id.no_play, R.drawable.play);
		}else{
			rv.setImageViewResource(R.id.no_play, R.drawable.pause);
		}
	}

}
