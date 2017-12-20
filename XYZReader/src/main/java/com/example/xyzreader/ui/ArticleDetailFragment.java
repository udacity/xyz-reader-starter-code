package com.example.xyzreader.ui;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ArticleDetailFragment";

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_IMAGE_TRANSITION_NAME = "image_transition_name";
    private static final float PARALLAX_FACTOR = 1.25f;

    private Cursor mCursor;
    private long mItemId;
    private String mSharedAnimation;
    private View mRootView;
    private int mMutedColor = 0xFF333333;
    private ObservableScrollView mScrollView;
    private DrawInsetsFrameLayout mDrawInsetsFrameLayout;
    private ColorDrawable mStatusBarColorDrawable;

    private int mTopInset;
    private View mPhotoContainerView;
    private ImageView mPhotoView;
    private int mScrollY;
    private boolean mIsCard = false;
    private int mStatusBarFullOpacityBottom;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(long itemId, String transitionName) {
        Log.d("MIKE detFrag ", "instabceADF MIKE21");
        Log.d("MIKE detFrag ID ", Long.toString(itemId));
        Log.d("MIKE detFrag  ", "transitionName "+ transitionName );
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        arguments.putString(ARG_IMAGE_TRANSITION_NAME, transitionName);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        supportPostponeEnterTransition(); this for appCompactActivity
        postponeEnterTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }

        Log.d("MIKE frag", "onCreate22");

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
            mSharedAnimation = getArguments().getString(ARG_IMAGE_TRANSITION_NAME);
            Log.d("MIKE sharedAnimation:::", mSharedAnimation);
        }

        mIsCard = getResources().getBoolean(R.bool.detail_is_card);
        mStatusBarFullOpacityBottom = getResources().getDimensionPixelSize(
                R.dimen.detail_card_top_margin);
        setHasOptionsMenu(true);
        //getLoaderManager().initLoader(0, null, this);
        Cursor c = getActivity().getContentResolver().query(ItemsContract.Items.buildItemUri(mItemId), ArticleLoader.Query.PROJECTION, null, null, ItemsContract.Items.DEFAULT_SORT);
        mCursor = c;
        mCursor.moveToFirst();
        Log.d("MIKE ", "  - - - - - - >m68");
        Log.d("MIKE ID",mCursor.getString(ArticleLoader.Query._ID));
        Log.d("MIKE TITLE",mCursor.getString(ArticleLoader.Query.TITLE));
        Log.d("MIKE PHOTO_URL",mCursor.getString(ArticleLoader.Query.PHOTO_URL));
        Log.d("MIKE THUMB_URL",mCursor.getString(ArticleLoader.Query.THUMB_URL));
        Log.d("MIKE PUBLISHED_DATE",mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE));
        Log.d("MIKE ASPECT_RATIO",mCursor.getString(ArticleLoader.Query.ASPECT_RATIO));
//        Log.d("MIKE BODY",mCursor.getString(ArticleLoader.Query.BODY));
        Log.d("MIKE AUTHOR",mCursor.getString(ArticleLoader.Query.AUTHOR));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d("MIKE frag ", " bindViews21 onCreateView24");
//        bindViews();
//        updateStatusBar();
        return inflater.inflate(R.layout.fragment_article_detail, container, false);


//        updateStatusBar();
//        return mRootView;
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("MIKE frag ", " bindViews22 onViewCreated 255");
//        bindViews();
        mRootView = view;
//        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
//        mDrawInsetsFrameLayout = (DrawInsetsFrameLayout)
//                mRootView.findViewById(R.id.draw_insets_frame_layout);
//        mDrawInsetsFrameLayout =
//                view.findViewById(R.id.draw_insets_frame_layout);
//        mDrawInsetsFrameLayout.setOnInsetsCallback(new DrawInsetsFrameLayout.OnInsetsCallback() {
//            @Override
//            public void onInsetsChanged(Rect insets) {
//                mTopInset = insets.top;
//            }
//        });

        mScrollView = (ObservableScrollView) view.findViewById(R.id.scrollview);
        mScrollView.setCallbacks(new ObservableScrollView.Callbacks() {
            @Override
            public void onScrollChanged() {
                mScrollY = mScrollView.getScrollY();
                //TODO fix next lline
                //getContext().getActivityCast().onUpButtonFloorChanged(mItemId, ArticleDetailFragment.this);
                //mPhotoContainerView.setTranslationY((int) (mScrollY - mScrollY / PARALLAX_FACTOR));
                updateStatusBar();
            }
        });

        mPhotoView = (ImageView) view.findViewById(R.id.photo);
        Log.d("MIKE FINAL TRANSITION", mSharedAnimation);
        //mPhotoView.setTransitionName("simple_activity_transition");
       // mPhotoContainerView = view.findViewById(R.id.photo_container);

        mStatusBarColorDrawable = new ColorDrawable(0);

        view.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });
        mCursor.moveToFirst();
        Log.d("MIKE ", " bindViews233 bindingViewB");
        bindViews();
//        updateStatusBar();
    }

    private void updateStatusBar() {
        Log.d("MIKE frag", "updateStatusBar onCreate25");
        int color = 0;
        if (mPhotoView != null && mTopInset != 0 && mScrollY > 0) {
            float f = progress(mScrollY,
                    mStatusBarFullOpacityBottom - mTopInset * 3,
                    mStatusBarFullOpacityBottom - mTopInset);
            color = Color.argb((int) (255 * f),
                    (int) (Color.red(mMutedColor) * 0.9),
                    (int) (Color.green(mMutedColor) * 0.9),
                    (int) (Color.blue(mMutedColor) * 0.9));
        }
        mStatusBarColorDrawable.setColor(color);
//        mDrawInsetsFrameLayout.setInsetBackground(mStatusBarColorDrawable);
    }

    static float progress(float v, float min, float max) {
        Log.d("MIKE floatProgress", "onCreate26");
        return constrain((v - min) / (max - min), 0, 1);
    }

    static float constrain(float val, float min, float max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        } else {
            return val;
        }
    }

    private Date parsePublishedDate() {
        Log.d("MIKE frag", "parsePublishedDate onCreate27");
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    private void bindViews() {
        Log.d("MIKE frag", " bindViews onCreate29");
        if (mRootView == null) {
            return;
        }

        TextView titleView = (TextView) mRootView.findViewById(R.id.article_title);
        TextView bylineView = (TextView) mRootView.findViewById(R.id.article_byline);
        bylineView.setMovementMethod(new LinkMovementMethod());
        TextView bodyView = (TextView) mRootView.findViewById(R.id.article_body);

        bodyView.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));

        if (mCursor != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);
            titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            Log.d("MIKE TITLE",mCursor.getString(ArticleLoader.Query.TITLE));
            Date publishedDate = parsePublishedDate();
            if (!publishedDate.before(START_OF_EPOCH.getTime())) {
                bylineView.setText(Html.fromHtml(
                        DateUtils.getRelativeTimeSpanString(
                                publishedDate.getTime(),
                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString()
                                + " by <font color='#ffffff'>"
                                + mCursor.getString(ArticleLoader.Query.AUTHOR)
                                + "</font>"));

            } else {
                // If date is before 1902, just show the string
                bylineView.setText(Html.fromHtml(
                        outputFormat.format(publishedDate) + " by <font color='#ffffff'>"
                        + mCursor.getString(ArticleLoader.Query.AUTHOR)
                                + "</font>"));

            }

            bodyView.setText("What you're doing is perfect. There are no changes required. " +
                    "But your outermost LinearLayout doesn't make sense if that has only one " +
                    "ScrollView as its child. Except for that, everything is exactly how it should be done." +
                    "What you're doing is perfect. There are no changes required. " +
                    "But your outermost LinearLayout doesn't make sense if that has only one " +
                    "ScrollView as its child. Except for that, everything is exactly how it should be done."+
                    "What you're doing is perfect. There are no changes required. " +
                            "But your outermost LinearLayout doesn't make sense if that has only one " +
                            "ScrollView as its child. Except for that, everything is exactly how it should be done." +
                            "What you're doing is perfect. There are no changes required. " +
                            "But your outermost LinearLayout doesn't make sense if that has only one " +
                            "ScrollView as its child. Except for that, everything is exactly how it should be done." +
            "");
//            bodyView.setText(mCursor.getString(ArticleLoader.Query.BODY));
            //bodyView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY).replaceAll("(\r\n|\n)", "<br />")));
//            ImageLoaderHelper.getInstance(getActivity()).getImageLoader()
//                    .get(mCursor.getString(ArticleLoader.Query.PHOTO_URL), new ImageLoader.ImageListener() {
//                        @Override
//                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
//                            Bitmap bitmap = imageContainer.getBitmap();
//                            if (bitmap != null) {
//                                Log.d("MIKE loadingIMAGE", "onCreate27");
//                                Palette p = Palette.generate(bitmap, 12);
//                                mMutedColor = p.getDarkMutedColor(0xFF333333);
//                                mPhotoView.setImageBitmap(imageContainer.getBitmap());
//                                mRootView.findViewById(R.id.meta_bar)
//                                        .setBackgroundColor(mMutedColor);
//                                updateStatusBar();
//                            }
//                        }
//
//                        @Override
//                        public void onErrorResponse(VolleyError volleyError) {
//                            Log.d("MIKE detFrag", "ErrorResponseonCreate28");
//                        }
//                    });

            Log.d("MIKE setA" , mSharedAnimation);
            Log.d("MIKE setB" , mCursor.getString(ArticleLoader.Query._ID));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                    mSharedAnimation.equals(mCursor.getString(ArticleLoader.Query._ID))) {
//                imageView.setTransitionName(transitionName);
                Log.d("MIKE set" , "transitionFinal");
                Log.d("MIKE set" , "transitionFinal: " +mSharedAnimation);
                mPhotoView.setTransitionName(mSharedAnimation);
            }


            //mPhotoView.setTransitionName("simple_activity_transition");
            Picasso.with(getActivity())
                    .load(mCursor.getString(ArticleLoader.Query.PHOTO_URL))
//                    .placeholder(R.drawable.empty_detail)
                    .noFade()
//                    .error(R.drawable.empty_detail)
//                    .into(mPhotoView);
                    .into(mPhotoView, new Callback() {
                        @Override
                        public void onSuccess() {
                            startPostponedEnterTransition();
                        }

                        @Override
                        public void onError() {
                            startPostponedEnterTransition();
                        }
                    });
//            mPhotoView.setTransitionName("");

        } else {
            mRootView.setVisibility(View.GONE);
            titleView.setText("N/A");
            bylineView.setText("N/A" );
            bodyView.setText("N/A");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d("MIKE frag", "onCreateLoaderonCreate29 ID: " + Long.toString(mItemId));
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d("MIKE fragloadFinished", "onCreate30");

        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }
        Log.d("MIKE ", " bindViews24 bindingViewA");
        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        Log.d("MIKE detFrag", "onLoaderResetonCreate31");
        Log.d("MIKE ", " bindViews25 bindingViewC");
        bindViews();
    }

    public int getUpButtonFloor() {
        Log.d("MIKE fragment ", "getUpButtonFloor onCreate32");

        // account for parallax
        return mIsCard
                ? (int) mPhotoContainerView.getTranslationY() + mPhotoView.getHeight() - mScrollY
                : mPhotoView.getHeight() - mScrollY;
    }
}
