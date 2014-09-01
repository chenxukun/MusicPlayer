package xukun.augest16;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HeadsetListener extends BroadcastReceiver{
	MusicDB musicdb = MusicDB.getInstance();

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.hasExtra("state")){
			if(intent.getIntExtra("state", 0) == 0){
				musicdb.setHeadsetPlug(false);
			}else if (intent.getIntExtra("state", 0) == 1){
				musicdb.setHeadsetPlug(true);
			}
		}
		
	}

}
