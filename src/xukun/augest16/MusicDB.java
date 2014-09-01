package xukun.augest16;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MusicDB {
	int LOOP = 0;
	int REPEAT = 1;
	int SHUFFLE = 2;
	int playmode = LOOP;
	private Callbacks cb;
	private Callbacks cb1;
	public interface Callbacks {
		public void onComplete(int id);
		public Handler getHandler();
		public List<Map<String, String>> getMusicList();
		public boolean isActive();
		public void updateGraphic(int id);
	}
	private boolean isPause = true;
	private boolean runTimer = true;
	MediaPlayer player = new MediaPlayer();
	int current = 0;
	boolean initial = false;
	boolean headsetplug = false;
	boolean headsetPause = false;
	private static MusicDB instance = null;
	
	public static MusicDB getInstance(){
		return instance;
	}
	public static MusicDB getInstance(Service service, Activity activity){
	      if(instance == null) {
	          instance = new MusicDB(service,activity);
	       }
	       return instance;
	}
	
	private MusicDB(Service service,Activity activity) {
		cb = (Callbacks) service;
		cb1 = (Callbacks) activity;
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer m) {
				//current++;
				getNextSong();
				if (current >= cb.getMusicList().size())
					current = 0;
				startPlay(current);
				cb.onComplete(current);
				if(cb1.isActive())
					cb1.onComplete(current);
				//startPlay(current);
			}
		});
		player.setOnPreparedListener(new OnPreparedListener(){

			@Override
			public void onPrepared(MediaPlayer mp) {
				player.start();
				runTimer = true;
				new Timer().schedule(new TimerTask(){
					@Override
					public void run() {
						if(runTimer){
							Message m = new Message();
							Bundle b = new Bundle();
							
							long total = getDurationInMill();
							long cur = player.getCurrentPosition ();
							double pos = ((double)cur/(double)total) * 100;
							b.putInt("prog",(int) pos);
							m.setData(b);
							cb1.getHandler().sendMessage(m);
							updateWidgetProgress((int) pos);
						}else{
							this.cancel();
						}
					}},0,100);
			}});
	}
	
	
	public void updateWidgetProgress(int prog){
		Intent proIntent = new Intent();
		proIntent.setAction("xukun.augest16.widgetbroadcast");
		proIntent.putExtra("progress",prog+"");
		((Service)cb).sendBroadcast(proIntent);
	}
	
	public void updateWidgetSong(String song,String artist, boolean isPause){
		Intent proIntent = new Intent();
		proIntent.setAction("xukun.augest16.widgetbroadcast");
		proIntent.putExtra("song",song);
		proIntent.putExtra("artist",artist);
		proIntent.putExtra("pause",isPause?"true":"false");
		((Service)cb).sendBroadcast(proIntent);
	}
	

	public List<Map<String, String>> reconstructList() {
		FileInputStream fileIn;
		try {
			fileIn = new FileInputStream( Environment.getExternalStorageDirectory().getPath()+"/musicdb.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			@SuppressWarnings("unchecked")
			List<Map<String, String>> songs = (List<Map<String, String>>) in.readObject();
			in.close();
			fileIn.close();
			return songs;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;

	}

	public void scanPhone(List<Map<String, String>> songs) {
		try{
		scanAllFolder(songs, null);
		FileOutputStream fout = new FileOutputStream( Environment.getExternalStorageDirectory().getCanonicalPath()+"/musicdb.ser");
		ObjectOutputStream out = new ObjectOutputStream(fout);
		out.writeObject(songs);
		out.close();
		fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void scanAllFolder(List<Map<String, String>> songs, File folder) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File dir = Environment.getExternalStorageDirectory();
			if (folder != null)
				dir = folder;
			File[] flst = dir.listFiles();
			if(flst == null) return;
			for (File file : flst) {
				if (file.isFile()) {
					if (file.getAbsolutePath().endsWith("mp3")) {
						addToList(songs, file);
					}
				} else if (file.isDirectory()) {
					scanAllFolder(songs, file);
				}
			}
		}

	}


	private void addToList(List<Map<String, String>> songs, File file) {
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(file.getAbsolutePath());
		String duration = retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		String rawartist = retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		String rawtitle = (retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
		String title = null;
		String artist = null;
		try {
			title = new String(rawtitle.getBytes("ISO-8859-1"), "GBK");
			artist = new String(rawartist.getBytes("ISO-8859-1"), "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Log.d("file--------------------",
				file.getAbsolutePath()+" "+title);
		Map<String, String> song = new HashMap<String, String>();
		song.put("title", title);
		song.put("artist", artist);
		int minute = (int) (Double.parseDouble(duration) / 1000 / 60);
		int second = (int) (Double.parseDouble(duration) / 1000 - minute * 60);
		//long finalTime = player.getDuration();
		song.put("duration", String.format(
				"%d : %02d",
				TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(duration)),
				TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(duration))
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(Long.parseLong(duration)))));
		song.put("path", file.getAbsolutePath());
		song.put("rawDuration",duration);
		songs.add(song);
	}

	public String retrieveSong(int id) {
		current = id;
		if(current >= cb.getMusicList().size())
			return null;
		return (String) cb.getMusicList().get(id).get("path");
	}

	public String getSongName() {
		if(current >= cb.getMusicList().size())
			return null;
		return (String) cb.getMusicList().get(current).get("title");
	}

	public String getSongArtist() {
		if(current >= cb.getMusicList().size())
			return null;
		return (String) cb.getMusicList().get(current).get("artist");
	}
	public long getDurationInMill(){
		if(current >= cb.getMusicList().size())
			return 0;
		String raw =  (String) cb.getMusicList().get(current).get("rawDuration");
		return Long.parseLong(raw);
	
	}

	public void startPlay(int id) {
		if(cb.getMusicList().size()==0) return;
		try {
			runTimer = false;
			player.reset();
			player.setDataSource(retrieveSong(id));
			player.prepareAsync();
			initial = true;
			isPause = false;
			updateGraphic(0);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int next() {
		if(cb.getMusicList().size()==0) return current;
		if (current + 1 >= cb.getMusicList().size())
			current = 0;
		else
			current += 1;
		//updateGraphic(1);
		return current;
		
	}

	public int pre() {
		if(cb.getMusicList().size()==0) return current;
		if (current - 1 < 0)
			current = cb.getMusicList().size()-1;
		else
			current -= 1;
		
		return current;
		
	}

	public void pausePlay() {
		player.pause();
		isPause = !isPause;
	}

	public void resumePlay() {
		if(initial){
			player.start();
			isPause = !isPause;
		}else{
			startPlay(0);
			
		}
	}
	
	public void togglePlay(){
		if (!isPause) {
			pausePlay();
		} else {
			resumePlay();
		}
		
		headsetPause = false;
		updateGraphic(0);
	}
	public void internalTogglePlay(){
		if (!isPause) {
			pausePlay();
		} else {
			resumePlay();
		}
		headsetPause = isPause;
		updateGraphic(0);
	}
	
	public void setPause(boolean b){
		isPause = b;
	}
	public boolean getPause(){
		return isPause;
	}

	public void resumeFrom(int progress) {
		long total = getDurationInMill();
		double pos = ((double)progress/100.0) * (double)total;
		player.seekTo((int) pos);
	}
	
	public void updateGraphic(int id){
		updateWidgetSong(getSongName(),getSongArtist(),isPause);
		if(cb1.isActive())
			cb1.updateGraphic(id);
		cb.updateGraphic(id);
	}
	public boolean isHeadsetPlug(){
		return headsetplug;
	}
	public void setHeadsetPlug(boolean b){
		headsetplug = b;
		if(headsetplug){
			if(headsetPause){
				internalTogglePlay();
			}
		}else{
			if(!headsetPause && !isPause){
				internalTogglePlay();
			}
		}
	}
	
	public void changePlayMode(){
		playmode = (playmode+1) % (SHUFFLE+1);
	}
	public int getPlayMode(){
		return playmode;
	}
	
	public void getNextSong(){
		if(playmode == REPEAT){
			current = current;
		}else if(playmode == LOOP){
			current+=1;
		}else if(playmode == SHUFFLE){
			Random r = new Random();
			current = r.nextInt(cb.getMusicList().size());
		}
	}
	
}
