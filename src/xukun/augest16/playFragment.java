package xukun.augest16;

import java.util.List;
import java.util.Map;

import xukun.augest16.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class playFragment extends Fragment {
	private Callbacks cb;
	int LOOP = 0;
	int REPEAT = 1;
	int SHUFFLE = 2;
	public interface Callbacks {
		public void onButtonAction(int id);

		public Handler getHandler();

		public void setHandler(Handler h);

		public MusicDB getMusicDB();

		public void setPause(boolean b);

		public boolean getPause();
	}

	View view;
	Button pre;
	Button next;
	ImageButton playmode;
	ToggleButton pause;
	TextView songText;
	TextView artistText;
	SeekBar sb;
	boolean isDrag = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		cb = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		cb = null;
	}

	public void setToggleStatus() {
		boolean s = cb.getPause();
		if (pause != null)
			pause.setChecked(cb.getPause());
	}

	public void updateSongDetail() {
		if (songText != null && artistText != null) {
			String s = cb.getMusicDB().getSongName();
			songText.setText(cb.getMusicDB().getSongName());
			artistText.setText(cb.getMusicDB().getSongArtist());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null && getArguments().containsKey("id")) {
			cb.getMusicDB().startPlay(getArguments().getInt("id"));
			//cb.setPause(false);
		}

		cb.setHandler(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int prog = msg.getData().getInt("prog");
				if (prog >= 0) {
					if (!isDrag)
						sb.setProgress(prog);

				}
			}
		});
	}

	
	private void changePlayMode(){
		cb.getMusicDB().changePlayMode();
	}
	private void getPlayMode(){
		int mode = cb.getMusicDB().getPlayMode();
		if(mode == LOOP)
			playmode.setImageResource(R.drawable.loop);
		else if(mode == REPEAT)
			playmode.setImageResource(R.drawable.repeat);
		else if(mode == SHUFFLE)
			playmode.setImageResource(R.drawable.shuffle);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		pause = (ToggleButton) view.findViewById(R.id.pauseResume);
		pause.setChecked(cb.getPause());
		getPlayMode();
		songText = (TextView) view.findViewById(R.id.songName);
		artistText = (TextView) view.findViewById(R.id.artist);
		songText.setText(cb.getMusicDB().getSongName());
		artistText.setText(cb.getMusicDB().getSongArtist());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.playfragmentlayout, container, false);
		pre = (Button) view.findViewById(R.id.preSong);
		next = (Button) view.findViewById(R.id.nextSong);
		pause = (ToggleButton) view.findViewById(R.id.pauseResume);
		pause.setChecked(cb.getPause());
		playmode = (ImageButton) view.findViewById(R.id.playmode);
		getPlayMode();
		songText = (TextView) view.findViewById(R.id.songName);
		artistText = (TextView) view.findViewById(R.id.artist);
		songText.setText(cb.getMusicDB().getSongName());
		artistText.setText(cb.getMusicDB().getSongArtist());
		sb = (SeekBar) view.findViewById(R.id.progress);

		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				isDrag = true;
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				cb.getMusicDB().resumeFrom(seekBar.getProgress());
				isDrag = false;
			}

		});
		pre.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cb.onButtonAction(-1);
				// cb.getMusicDB().playPre();
			}
		});
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cb.onButtonAction(1);
				// cb.getMusicDB().playNext();
			}
		});
		pause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cb.onButtonAction(0);
			}
		});
		playmode.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				changePlayMode();
				getPlayMode();
			}
			
		});
		return view;

	}
}
