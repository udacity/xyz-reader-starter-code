package com.example.xyzreader.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
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

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

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
    private boolean mIsVisibleToUser = false;
    private int mStatusBarFullOpacityBottom;
    private PopupWindow mPopupWindow;
    private ProgressDialog dialog;
    private String bodyDetails;
    private String imageUrl;


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
        imageUrl = mCursor.getString(ArticleLoader.Query.PHOTO_URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MIKE frag ", " bindViews21 onCreateView24");
//        bindViews();
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

        mPhotoView = (ImageView) getActivity().findViewById(R.id.imgTitle);

        mFloatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.share_fab);
        
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });

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

            bodyDetails = mCursor.getString(ArticleLoader.Query.BODY);
            bodyView.setText(Html.fromHtml(
                    bodyDetails.substring(0,
                            (bodyDetails.length() <= 250
                                    ? bodyDetails.length() :
                                    250))));

            seeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    dialog = ProgressDialog.show(getActivity(), "",
//                            "Loading. Please wait...", true);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d("MIKE run", "test1");
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {

                                        showPopupBodyInfo(bodyDetails);
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
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

            Log.d("MIKE IMAGE2: ", mCursor.getString(ArticleLoader.Query.PHOTO_URL));

            if (mIsVisibleToUser) {
                Log.d("MIKE IMAGE3: ", mCursor.getString(ArticleLoader.Query.PHOTO_URL));
                updateImage();
            }

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

    public void showPopupBodyInfo(String bodytext) {

        dialog = ProgressDialog.show(getActivity(), "",
                "Loading. Please wait...", true);

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.custom_popup, null);
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }

        ImageButton closeButton = customView.findViewById(R.id.ib_close);
        TextView bodyText = customView.findViewById(R.id.body_message);

        bodyText.setText(bodytext);

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.setAnimationStyle(android.R.style.Animation_Translucent);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.showAtLocation(mRootView, Gravity.CENTER, 0, 0);
        dialog.dismiss();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;
        if (mRootView == null) {
            Log.d("MIKE ", "mRootView is null");
            return;
        }
        if (isVisibleToUser && mRootView != null) {
            Log.d("MIKE ", "updateImage");
            updateImage();
        }
    }

    private void updateImage() {
        Picasso.with(getActivity())
                .load(imageUrl)
                .noFade()
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
    }
}
