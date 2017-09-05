package com.example.xyzreader.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ArticleUtils;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.pojo.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Log tag
    private static final String TAG = ArticleListActivity.class.getSimpleName();

    // Views
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private Activity mActivity;
    private Context mContext;
    private FrameLayout mToolbarContainer;

    // Bundle args
    public final static String EXTRA_INITIAL_POSITION = "extra_initial_position";
    public final static String EXTRA_CLICKED_IMAGE_URL = "extra_clicked_image_url";

    // class member variable for articles
    private ArrayList<Article> mArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        if (savedInstanceState == null) {
            // TODO: Why does this always get called? Why does is cause the UI to scroll to top?
            refresh();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        getLoaderManager().initLoader(0, null, this);

        mActivity = this;
        mContext = this;
        mToolbarContainer = (FrameLayout) findViewById(R.id.toolbar_container);
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }

    private boolean mIsRefreshing = false;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };

    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "Loader started for all articles");

        // TODO: create this loader so it loads everything except the body stuff... too big & unnecessary
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "Loader finished.");

        // store articles in arraylist
        mArticles = ArticleUtils.generateArticlesFromCursor(cursor);

        Adapter adapter = new Adapter(mArticles);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        ArrayList<Article> mArticles;
        public Adapter(ArrayList<Article> articles) {
            mArticles = articles;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
            final ViewHolder vh = new ViewHolder(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /* START DETAIL ACTIVITY WITH INITIAL POSITION AND LIST OF ARTICLES */
                    Intent startDetailIntent = new Intent(mContext, ArticleDetailActivity.class);

                    int initialPosition = vh.getAdapterPosition();
                    startDetailIntent.putExtra(EXTRA_INITIAL_POSITION, initialPosition);
                    // instead of passing articles as intent data (too big)
                    // just create a loader for all articles in detail activity
                    // startDetailIntent.putParcelableArrayListExtra(EXTRA_ARTICLES, mArticles);
                    // COMPLETED: Pass url as stringExtra so loading screen can show it as default image
                    String clickedImageUrl = mArticles.get(initialPosition).getPhotoUrl();
                    startDetailIntent.putExtra(EXTRA_CLICKED_IMAGE_URL, clickedImageUrl);


                    // TODO: Add shared element transition
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        // Dynamically set transition name of list item on Click
                        final String transitionName = getString(R.string.transitionImage);
                        final ImageView imageView = vh.thumbnailView;
                        ViewCompat.setTransitionName(imageView, transitionName);

                        // add scene transition options
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(mActivity, imageView, transitionName);

//                        startActivity(startDetailIntent, options.toBundle());
                        startActivity(startDetailIntent);
                    } else {
                        startActivity(startDetailIntent);
                    }

                }
            });

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Article currentArticle = mArticles.get(position);

            // TODO: set image to some default image so initial loading doesn't look weird
            String imageUrl = currentArticle.getThumbnailUrl();
            if (imageUrl != null) {
                Picasso.with(mContext).load(imageUrl).into(holder.thumbnailView);
            }

            holder.titleView.setText(currentArticle.getTitle());

            String subtitleText = currentArticle.getDate() + "\nby " + currentArticle.getAuthor();
            holder.subtitleView.setText(subtitleText);
        }

        @Override
        public int getItemCount() {
            return mArticles.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnailView;
        private TextView titleView;
        private TextView subtitleView;

        private ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
        }
    }
}
