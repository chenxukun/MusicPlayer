package xukun.augest16;

import xukun.augest16.playFragment.Callbacks;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class menuFragment extends Fragment{
	private Callbacks cb;
	public interface Callbacks {
		public void scanPhone();
	}
	View view;
	Button scan,search,recom,setting;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
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
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.menulayout, container, false);
		scan = (Button) view.findViewById(R.id.scan);
		search = (Button) view.findViewById(R.id.search);
		recom = (Button) view.findViewById(R.id.recom);
		setting = (Button) view.findViewById(R.id.setting);
		
		scan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cb.scanPhone();
				
			}
		});
		return view;
	}

}
