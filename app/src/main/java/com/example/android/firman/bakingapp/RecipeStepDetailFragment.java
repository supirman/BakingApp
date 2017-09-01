package com.example.android.firman.bakingapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.firman.bakingapp.model.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A fragment representing a single RecipeStep detail screen.
 * This fragment is either contained in a {@link RecipeDetailActivity}
 * in two-pane mode (on tablets) or a {@link RecipeStepDetailActivity}
 * on handsets.
 */
public class RecipeStepDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ITEMS_EXTRA = "items_extra";
    public static final String ITEM_NUMBER_EXTRA = "item_index";

    /**
     * The dummy content this fragment is presenting.
     */
    private List<Step> mItems;
    SimpleExoPlayerView playerView;
    private SimpleExoPlayer player;
    private boolean playWhenReady;
    private int currentWindow;
    private long playbackPosition;
    private int mIndex;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeStepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ITEMS_EXTRA) && getArguments().containsKey(ITEM_NUMBER_EXTRA)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //TODO
            mItems = getArguments().getParcelableArrayList(ITEMS_EXTRA);
            mIndex = getArguments().getInt(ITEM_NUMBER_EXTRA, -1);

            Activity activity = this.getActivity();
            playerView = activity.findViewById(R.id.video_view);
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            Step step=mItems.get(mIndex);
            if (appBarLayout != null) {
                appBarLayout.setTitle(step.getShortDescription());
                if(!TextUtils.isEmpty(step.getThumbnailURL())) {
                    ImageView imageView = activity.findViewById(R.id.iv_thumbnail_details);
                    if(imageView!=null)
                        Picasso.with(getContext())
                                .load(step.getThumbnailURL())
                                .into(imageView);
                }
            }

        }
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getContext()),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerView.setPlayer(player);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);

        Uri uri = Uri.parse(mItems.get(mIndex).getVideoURL());
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipestep_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItems != null) {
            ((TextView) rootView.findViewById(R.id.recipestep_detail)).setText(mItems.get(mIndex).getDescription());
        }
        playerView = rootView.findViewById(R.id.video_view);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23 && mItems != null && !TextUtils.isEmpty(mItems.get(mIndex).getVideoURL())) {
            playerView.setVisibility(View.VISIBLE);
            initializePlayer();
        }
        if (mItems == null || TextUtils.isEmpty(mItems.get(mIndex).getVideoURL())){
            playerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null ) && mItems != null && !TextUtils.isEmpty(mItems.get(mIndex).getVideoURL())) {
            playerView.setVisibility(View.VISIBLE);
            initializePlayer();
        }
        if (mItems == null || TextUtils.isEmpty(mItems.get(mIndex).getVideoURL())){
            playerView.setVisibility(View.GONE);
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }
}
