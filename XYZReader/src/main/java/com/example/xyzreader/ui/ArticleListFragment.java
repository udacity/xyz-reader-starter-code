package com.example.xyzreader.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.databinding.ActivityArticleListBinding;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by mike on 12/12/17.
 */

public class ArticleListFragment extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_ITEM = "image_url";
    private static final String ARG_VALUE_ID = "value_id";
    public static final String ARG_IMAGE_TRANSITION_NAME = "image_transition_name";
    public static final String ARG_IMAGE_TRANSITION_NAME_IMAGE = "image_transition_name_image";


    private static final String TAG = ArticleListFragment.class.toString();

    private ActivityArticleListBinding Binding;
    private Context mContext;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private AppBarLayout mAppBarLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MIKE", "OnCreateFragment");
        setContentView(R.layout.activity_article_list);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Binding.swipeRefreshLayout.setRefreshing(false);
                refresh();
            }
        });

        mRecyclerView = findViewById(R.id.recycler_view);

        mAppBarLayout = findViewById(R.id.app_bar_layout);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    Binding.collapsingToolbarLayout.setTitle("xyzreader");
                    isShow = true;
                } else if (isShow) {
                    Binding.collapsingToolbarLayout.setTitle("xyzreader");
                }
            }
        });

        getLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
            refresh();
        }
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    public void onStop() {
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        ArticleListFragment.Adapter adapter = new ArticleListFragment.Adapter(ArticleListFragment.this, cursor);
        adapter.setHasStableIds(true);

        mRecyclerView.setAdapter(adapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Binding.recyclerView.setAdapter(null);
    }

    private class Adapter extends RecyclerView.Adapter<ArticleListFragment.ViewHolder> {
        private Cursor mCursor;
        private Interpolator mInterpolator;
        private int lastAnimatedPosition = -1;
        private Activity mActivity;

        public Adapter(Activity activity, Cursor cursor) {
            mCursor = cursor;
            mActivity = activity;
//            mInterpolator = AnimationUtils.loadInterpolator(getActivity(), android.R.interpolator.linear_out_slow_in);
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);

            Log.d("MIKE 23A:", Long.toString(mCursor.getLong(ArticleLoader.Query._ID)));
            Log.d("MIKE 23B:", mCursor.getString(ArticleLoader.Query.TITLE));
            return mCursor.getLong(ArticleLoader.Query._ID);
        }

        @Override
        public ArticleListFragment.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
            final ArticleListFragment.ViewHolder vh = new ArticleListFragment.ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO, testing this animation remove it
//                    int finalRadius = (int)Math.hypot(view.getWidth()/2, view.getHeight()/2);
//                    Animator anim = ViewAnimationUtils.createCircularReveal(view, (int) view.getWidth()/2,
//                            (int) view.getHeight()/2, 0, finalRadius);
//                    anim.start();

                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(mActivity,
                            vh.thumbnailView,
                            getApplicationContext().getString(R.string.article_image_transition));
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))), transitionActivityOptions.toBundle());
                    ////TODO END, testing this animation remove it

                    long value = getItemId(vh.getAdapterPosition());

                    ImageView imageView = view.findViewById(R.id.thumbnail);

                    Log.d("MIKECLICKB", Long.toString(value));
                    Log.d("MIKECLICKB", imageView.toString());
                }
            });
            return vh;
        }

        private Date parsePublishedDate() {
            try {
                String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
                return dateFormat.parse(date);
            } catch (ParseException ex) {
                Log.e(TAG, ex.getMessage());
                Log.i(TAG, "passing today's date");
                return new Date();
            }
        }

        @Override
        public void onBindViewHolder(final ArticleListFragment.ViewHolder holder, final int position) {
            mCursor.moveToPosition(position);
            holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            Date publishedDate = parsePublishedDate();
            if (!publishedDate.before(START_OF_EPOCH.getTime())) {

                holder.subtitleView.setText(Html.fromHtml(
                        DateUtils.getRelativeTimeSpanString(
                                publishedDate.getTime(),
                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString()
                                + "<br/>" + " by "
                                + mCursor.getString(ArticleLoader.Query.AUTHOR)));
            } else {
                holder.subtitleView.setText(Html.fromHtml(
                        outputFormat.format(publishedDate)
                                + "<br/>" + " by "
                                + mCursor.getString(ArticleLoader.Query.AUTHOR)));
            }

//            ViewCompat.setTransitionName(holder.thumbnailView, mCursor.getString(ArticleLoader.Query._ID));

//            ViewCompat.setTransitionName(holder.thumbnailView, mContext.getString(R.string.article_image_transition));

            Picasso.with(mActivity)
                    .load(mCursor.getString(ArticleLoader.Query.THUMB_URL))
                    .error(R.drawable.empty_detail)
                    .into(holder.thumbnailView);
        }

//        private void setAnimation(View viewToAnimate, int position) {
//            if (position > lastAnimatedPosition) {
//                viewToAnimate.setTranslationY((position + 1) * 1000);
//                viewToAnimate.setAlpha(0.85f);
//                viewToAnimate.animate()
//                        .translationY(0f)
//                        .alpha(1f)
//                        .setInterpolator(mInterpolator)
//                        .setDuration(1000L)
//                        .start();
//                lastAnimatedPosition = position;
//            }
//        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
        }
    }
}
