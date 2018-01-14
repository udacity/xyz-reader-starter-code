package com.example.xyzreader.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
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

    private int mMutedColor = 0xFF333333;
    private ObservableScrollView mScrollView;
    private DrawInsetsFrameLayout mDrawInsetsFrameLayout;
    private ColorDrawable mStatusBarColorDrawable;
    private FloatingActionButton mFloatingActionButton;

    private int mTopInset;
    private View mPhotoContainerView;
    private ImageView mPhotoView;
    private int mScrollY;
    private View mRootView;
    private boolean mIsCard = false;
    private int mStatusBarFullOpacityBottom;


    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(long itemId, String transitionName) {
        Log.d("MIKE detFrag ", "instabceADF MIKE21");
        Log.d("MIKE detFrag ID ", Long.toString(itemId));
        Log.d("MIKE detFrag  ", "transitionName " + transitionName);
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
        Log.d("MIKE TITLE", mCursor.getString(ArticleLoader.Query.TITLE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MIKE frag ", " bindViews21 onCreateView24");
//        bindViews();
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

        mPhotoView = (ImageView) getActivity().findViewById(R.id.imgTitle);

        mFloatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.share_fab);
        bindViews();
        return mRootView;
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
        final TextView bodyView = (TextView) mRootView.findViewById(R.id.article_body);
        TextView seeMore = mRootView.findViewById(R.id.seeMoreTextView);

        bodyView.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));

        if (mCursor != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);
            titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            Log.d("MIKE TITLE", mCursor.getString(ArticleLoader.Query.TITLE));
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

            final String bodyDetails = mCursor.getString(ArticleLoader.Query.BODY);
            bodyView.setText(Html.fromHtml(
                    bodyDetails.substring(0,
                            (bodyDetails.length() <= 250
                                    ? bodyDetails.length() :
                                    250))));

            Log.d("MIKE-TEXT: ", bodyDetails);

            seeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bodyView.setText(bodyDetails);
                    view.setVisibility(View.INVISIBLE);

                }
            });

            Log.d("MIKE setA", mSharedAnimation);
            Log.d("MIKE setB", mCursor.getString(ArticleLoader.Query._ID));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                    mSharedAnimation.equals(mCursor.getString(ArticleLoader.Query._ID))) {
//                imageView.setTransitionName(transitionName);
                Log.d("MIKE set", "transitionFinal");
                Log.d("MIKE set", "transitionFinal: " + mSharedAnimation);
                // mPhotoView.setTransitionName(mSharedAnimation);
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
            bylineView.setText("N/A");
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
