package xukun.augest16;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class NotifiReceiver extends BroadcastReceiver {
	MusicDB musicdb = MusicDB.getInstance();

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle b = intent.getExtras();
		if (b != null) {
			if (musicdb == null) {
				Intent startIntent = new Intent(context, MainActivity.class);
				//startIntent.setAction("android.intent.action.MAIN");
				startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(startIntent);
			} else {
				if (b.getString("msg").equals("toggle")) {
					musicdb.togglePlay();
				} else if (b.getString("msg").equals("pre")) {
					musicdb.startPlay(musicdb.pre());
				} else if (b.getString("msg").equals("next")) {
					musicdb.startPlay(musicdb.next());
				}
			}
		}

	}

}
