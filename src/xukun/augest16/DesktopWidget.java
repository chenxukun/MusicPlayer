package xukun.augest16;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

public class DesktopWidget extends AppWidgetProvider{
	
	public void onReceive(Context context, Intent intent){
		super.onReceive(context, intent);
		RemoteViews view = new RemoteViews(context.getPackageName(),R.layout.widgetlayout);
		if(intent.getAction().equals("xukun.augest16.widgetbroadcast")){
			Bundle b = intent.getExtras();
			if(b!=null){
				if(b.getString("song")!=null){
					changeSong(b.getString("song"), view);
				}
				if(b.getString("artist")!=null){
					changeArtist(b.getString("artist"), view);
				}
				if(b.getString("pause")!=null){
					if(b.getString("pause").equals("true"))
						togglePlayButton(true,view);
					else
						togglePlayButton(false,view);
				}
				if(b.getString("progress")!=null){
					updateProgress(Integer.parseInt(b.getString("progress")),view);
				}
			}
		}
		
	    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
	    ComponentName thisAppWidget = new ComponentName(context.getPackageName(), DesktopWidget.class.getName());
	    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
	    appWidgetManager.updateAppWidget(thisAppWidget, view);
	    //onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	public void updateProgress(int progress, RemoteViews view){
		view.setProgressBar(R.id.wi_progress,100,progress,false);
	}
	
	public void changeSong(String song,RemoteViews view){
		view.setTextViewText(R.id.wi_name,song);
	}
	public void changeArtist(String artist,RemoteViews view){
		view.setTextViewText(R.id.wi_artist,artist);
	}
	
	public void togglePlayButton(boolean b, RemoteViews view){
		if(b){
			view.setImageViewResource(R.id.wi_play,R.drawable.play);
		}else{
			view.setImageViewResource(R.id.wi_play,R.drawable.pause);
		}
	}
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds){
		 final int N = appWidgetIds.length;

		for(int i=0;i<N;i++){
			int appWidgetId = appWidgetIds[i];
			
			Intent intent = new Intent(context,MainActivity.class);
			PendingIntent pi = PendingIntent.getActivity(context,0,intent,0);
			
			RemoteViews view = new RemoteViews(context.getPackageName(),R.layout.widgetlayout);
			view.setOnClickPendingIntent(R.id.wi_icon,pi);
			
			// toggle music
			Intent toIntent = new Intent();
			toIntent.setAction("xukun.augest16.audiobroadcast");
			toIntent.putExtra("msg","toggle");
			PendingIntent toPi = PendingIntent.getBroadcast(context,1,toIntent,0);
			
			// next music
			Intent nextIntent = new Intent();
			nextIntent.setAction("xukun.augest16.audiobroadcast");
			nextIntent.putExtra("msg","next");
			PendingIntent nextPi = PendingIntent.getBroadcast(context,2,nextIntent,0);
			
			// pre music
			Intent preIntent = new Intent();
			preIntent.setAction("xukun.augest16.audiobroadcast");
			preIntent.putExtra("msg","pre");
			PendingIntent prePi = PendingIntent.getBroadcast(context,3,preIntent,0);
			
			
			view.setOnClickPendingIntent(R.id.wi_pre,prePi);
			view.setOnClickPendingIntent(R.id.wi_next,nextPi);
			view.setOnClickPendingIntent(R.id.wi_play,toPi);
			
			appWidgetManager.updateAppWidget(appWidgetId, view);

		}
	}

}
