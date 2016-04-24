package me.littlecabbage.androidspacescenes;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ApodApi api = ApodApi.getInstance(context.getString(R.string.api_key));
        api.getPhoto(context, new VoidCallBack<JSONObject>() {
            @Override
            public void call(final JSONObject jsonObject) {
                try {
                    final String url = jsonObject.getString("url");
                    final String dateStr = jsonObject.getString("date");
                    final String titleStr = jsonObject.getString("title");
                    final String descriptionStr = jsonObject.getString("explanation");
                    final String copyrightStr = (jsonObject.has("copyright") ? jsonObject.getString("copyright") : "NASA");
                    new LoadImage(new VoidCallBack<Bitmap>() {
                        @Override
                        public void call(Bitmap arg) {
                            ImageView img = (ImageView) findViewById(R.id.app_apod);
                            assert img != null;
                            img.setImageBitmap(arg);

                            TextView date = (TextView) findViewById(R.id.photo_date);
                            assert date != null;
                            date.setText(dateStr);

                            TextView title = (TextView) findViewById(R.id.photo_title);
                            assert title != null;
                            title.setText(titleStr);

                            TextView description = (TextView) findViewById(R.id.photo_description);
                            assert description != null;
                            description.setText(descriptionStr);

                            TextView copyright = (TextView) findViewById(R.id.photo_copyright);
                            assert copyright != null;
                            copyright.setText(copyrightStr);
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            // do nothing
                        }
                    }).execute(url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new VoidCallBack<VolleyError>() {
            @Override
            public void call(VolleyError arg) {
                arg.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
