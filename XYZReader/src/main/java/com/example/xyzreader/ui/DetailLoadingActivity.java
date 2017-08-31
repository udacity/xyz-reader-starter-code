package com.example.xyzreader.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;

import static com.example.xyzreader.ui.ArticleListActivity.EXTRA_ID;
import static com.example.xyzreader.ui.ArticleListActivity.EXTRA_PHOTO_URL;
import static com.example.xyzreader.ui.ArticleListActivity.EXTRA_POSITION;

public class DetailLoadingActivity extends AppCompatActivity {

    private int mPosition;
    private long mId;
    private String mPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_loading);

        Intent receivedIntent = getIntent();
        if (receivedIntent.hasExtra(EXTRA_POSITION)) {
            mPosition = receivedIntent.getIntExtra(EXTRA_POSITION, 0);
        }
        if (receivedIntent.hasExtra(EXTRA_ID)) {
            mId = receivedIntent.getLongExtra(EXTRA_ID, 0);
        }
        if (receivedIntent.hasExtra(EXTRA_PHOTO_URL)) {
            mPhotoUrl = receivedIntent.getStringExtra(EXTRA_PHOTO_URL);
        }

        final ImageView photoView = (ImageView) findViewById(R.id.photo);

        ImageLoaderHelper.getInstance(this).getImageLoader()
                .get(mPhotoUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        Bitmap bitmap = imageContainer.getBitmap();
                        if (bitmap != null) {
                            Palette p = Palette.generate(bitmap, 12);
                            photoView.setImageBitmap(imageContainer.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();


//        Intent startDetailIntent = new Intent(
//                Intent.ACTION_VIEW,
//                ItemsContract.Items.buildItemUri(mId)
//        );
//        startDetailIntent.putExtra(EXTRA_POSITION, mPosition);
//
//        startActivity(startDetailIntent);
    }
}
