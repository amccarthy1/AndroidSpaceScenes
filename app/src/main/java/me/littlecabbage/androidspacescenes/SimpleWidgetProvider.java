package me.littlecabbage.androidspacescenes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class SimpleWidgetProvider extends AppWidgetProvider {
    private static void error(RemoteViews remoteViews) {
        remoteViews.setImageViewResource(R.id.imageView, R.drawable.error);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        for (final int widgetId : appWidgetIds) {
            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.simple_widget);
            ApodApi api = ApodApi.getInstance(context.getString(R.string.api_key)); // This somewhat masks the API key maybe
            api.getPhoto(new VoidCallBack<JSONObject>() {
                @Override
                public void call(JSONObject response) {
                    try {
                        Object u = response.get("url");
                        String photoUrl = (String)u;
                        new LoadImage(new VoidCallBack<Bitmap>() {
                            //Once we have gone to the image URL and converted it to a Bitmap,
                            //we need to update the app with the new pretty bitmap.
                            //This code, while ugly, is less coupled than previously.
                            @Override
                            public void call(Bitmap image) {
                                if (image != null) {
                                    remoteViews.setImageViewBitmap(R.id.imageView, image);
                                }

                                appWidgetManager.updateAppWidget(widgetId, remoteViews);
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                error(remoteViews);
                                appWidgetManager.updateAppWidget(widgetId, remoteViews);
                            }
                        }).execute(photoUrl);

                    } catch (JSONException e) {
                        error(remoteViews);
                        appWidgetManager.updateAppWidget(widgetId, remoteViews);
                    }
                }
            }, new VoidCallBack<VolleyError>() {

                @Override
                public void call(VolleyError arg) {
                    error(remoteViews);
                    appWidgetManager.updateAppWidget(widgetId, remoteViews);
                }
            });
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent startPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.imageView, startPendingIntent);
        }
    }
}
