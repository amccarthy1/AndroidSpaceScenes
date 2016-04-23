package me.littlecabbage.androidspacescenes;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleWidgetProvider extends AppWidgetProvider {
    ImageView imageView;
    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        for (final int widgetId : appWidgetIds) {
//            String number = String.format("%03d", (new Random().nextInt(900) + 100));
//      imageView = (ImageView) findViewById(R.id.imageView);
            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.simple_widget);
            // FIXME: 4/23/2016
            String key = "CD4cC9NEeIUmK8bufx1hvsShTY25RmzVlw2JXA2L";
            String requestUrl = "https://api.nasa.gov/planetary/apod?api_key=" + key;
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, requestUrl, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            //TODO
                            try {
                                Object u = response.get("url");
                                String photoUrl = (String)u;
                                new LoadImage(remoteViews, context, appWidgetIds, appWidgetManager, widgetId).execute(photoUrl);

                            } catch (JSONException e) {
                                Toast.makeText(context, ":(", Toast.LENGTH_LONG);
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub

                        }
                    });
        }
    }

    //p

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        private Bitmap bitmap;
        private RemoteViews remoteViews;
        private Context context;
        private int[] appWidgetIds;
        private AppWidgetManager appWidgetManager;
        private int id;

        public LoadImage(RemoteViews remoteViews, Context context, int[] appWidgetIds, AppWidgetManager appWidgetManager, int id) {
            super();
            this.remoteViews = remoteViews;
            this.context = context;
            this.appWidgetIds = appWidgetIds;
            this.appWidgetManager = appWidgetManager;
            this.id = id;
        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if (image != null) {
                this.remoteViews.setImageViewBitmap(R.id.imageView, image);
            }
            Intent intent = new Intent(context, SimpleWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.imageView, pendingIntent);
            appWidgetManager.updateAppWidget(id, remoteViews);
        }
    }
}
