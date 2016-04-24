package me.littlecabbage.androidspacescenes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.URL;

//Goes to the URL given to doInBackground in its args, creates a bitmap from its
//input stream, and once it's loaded, updates the app using the callback function
//given to its constructor.
class LoadImage extends AsyncTask<String, String, Bitmap> {
    private Bitmap bitmap;
    private VoidCallBack<Bitmap> cb;
    private Runnable errorHandler;

    //Load Image takes a callback function to update the app
    public LoadImage(VoidCallBack<Bitmap> cb, Runnable error) {
        super();
        this.cb = cb;
        this.errorHandler = error;
    }

    protected Bitmap doInBackground(String... args) {
        try {
            bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

        } catch (Exception e) {
            e.printStackTrace();
            errorHandler.run();
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap image) {
        if (image == null) {
            errorHandler.run();
        }
        cb.call(image);
    }
}