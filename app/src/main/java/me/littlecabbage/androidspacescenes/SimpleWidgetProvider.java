package me.littlecabbage.androidspacescenes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class SimpleWidgetProvider extends AppWidgetProvider {
    //To be used as a callback function to reset the widget with the loaded image
    private interface CallBack<T> {
        public void run(T arg);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        Toast.makeText(context, "Widget Updating", Toast.LENGTH_LONG).show();
        for (final int widgetId : appWidgetIds) {
            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.simple_widget);
            // FIXME: 4/23/2016, remove key, final variables
            String key = "CD4cC9NEeIUmK8bufx1hvsShTY25RmzVlw2JXA2L";
            String requestUrl = "https://api.nasa.gov/planetary/apod?api_key=" + key;
            //Get the current photo of the dat data
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, requestUrl, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            //TODO
                            try {
                                Object u = response.get("url");
                                String photoUrl = (String)u;
                                new LoadImage(new CallBack<Bitmap>() {
                                    //Once we have gone to the image URL and converted it to a Bitmap,
                                    //we need to update the app with the new pretty bitmap.
                                    //This code, while ugly, is less coupled than previously.
                                    @Override
                                    public void run(Bitmap image) {
                                        if (image != null) {
                                            remoteViews.setImageViewBitmap(R.id.imageView, image);
                                        }
                                        Intent intent = new Intent(context, SimpleWidgetProvider.class);
                                        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                                                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        remoteViews.setOnClickPendingIntent(R.id.imageView, pendingIntent);
                                        appWidgetManager.updateAppWidget(widgetId, remoteViews);
                                    }
                                }).execute(photoUrl);

                            } catch (JSONException e) {
                                Toast.makeText(context, ":(", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "Failed to get a valid JSON response", Toast.LENGTH_LONG).show();
                        }
                    });
            Volley.newRequestQueue(context).add(jsObjRequest);
        }
    }

    //Goes to the URL given to doInBackground in its args, creates a bitmap from its
    //input stream, and once it's loaded, updates the app using the callback function
    //given to its constructor.
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        private Bitmap bitmap;
        private CallBack<Bitmap> cb;

        //Load Image takes a callback function to update the app
        public LoadImage(CallBack<Bitmap> cb) {
            super();
            this.cb = cb;
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
            cb.run(image);
        }
    }
}
