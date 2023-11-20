package com.cedricapp.fragment;

import static com.cedricapp.common.Common.EXCEPTION;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cedricapp.activity.HomeActivity;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.UserStatusInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.model.VisualizationResponse;
import com.cedricapp.R;
import com.cedricapp.utils.DialogUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.UserStatusUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

@SuppressWarnings("ALL")
public class SleepVisualizationFragment extends Fragment implements Runnable, UserStatusInterface {
    private MaterialTextView mTextViewDayTime, mTextViewVisualizationTitle, mTextViewVisualizationDescription, txt_clam, txt_textView1SleepViewCard, txt_time;
    TextView seekBarHint;
    private ImageButton backArrow;
    //for seekbar
    ImageView mSleepVisualizationImg;
    MediaPlayer mediaPlayer = new MediaPlayer();
    SeekBar seekBar;
    CardView sleepVisualCardView;
    boolean wasPlaying = false;
    FloatingActionButton fab;
    String image, dayTime, description, title, currentDate;
    String audio;
    private DBHelper dbHelper;
    public static List<VisualizationResponse> visualizationList = new ArrayList<>();
    VisualizationResponse visualizationResponse;
    int currentPosition;
    ShimmerFrameLayout shimmerForSleep;
    int dayNumber, weekNumber, goal_id, level_id;
    private View view1;
    Call<VisualizationResponse> visaulizationCall;
    Call<SignupResponse> tokenCall;
    String imgURL;
    //private Context mContext;

    Resources resources;

    String TAG = "SLEEP_VISUALIZATION_TAG";

    UserStatusUtil userStatusUtil;

    public SleepVisualizationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedData.canToastShow = false;
        SharedData.redirectToDashboard = true;
        HomeActivity.hideBottomNav();
        if (getActivity() != null) {
            getActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        } else {
            //System.out.println("not working");
        }


        /*if (wasPlaying) {
            mediaPlayer.start();
            mediaPlayer.seekTo(currentPosition);
            wasPlaying = false;
        }*/
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sleep_visualization, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();

        //dbHelper = new DBHelper(getContext());

        init();
        oSBackButton();

        StartShimmer();

        getDataFromBundleAndSetToWidgets();

        //checkNetworkConection();

        //go back to previous
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdded()) {
                    if (getContext() != null) {
                        /*   if (ConnectionDetector.isConnectedWithInternet(getContext())) {*/
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                        if (getFragmentManager().getBackStackEntryCount() != 0) {
                            getFragmentManager().popBackStack();
                        }

                       /* } else {
                            Toast.makeText(getContext(), "Please turn ON your internet", Toast.LENGTH_SHORT).show();
                        }*/

                    }
                }
            }
        });


        //listener on play button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext() != null) {
                    if (SessionUtil.isSubscriptionAvailable(getContext())) {
                        playSong(audio);
                    } else {
                        DialogUtil.showSubscriptionEndDialogBox(getContext(), resources);
                    }
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                seekBarHint.setVisibility(View.VISIBLE);

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                seekBarHint.setVisibility(View.VISIBLE);
                int x = (int) Math.ceil(progress / 1000f);

                if (x < 10) {
                    seekBarHint.setText("0:0" + x);
                } else {
                    seekBarHint.setText("0:" + x);
                }

                double percent = progress / (double) seekBar.getMax();
                int offset = seekBar.getThumbOffset();
                int seekWidth = seekBar.getWidth();
                int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                int labelWidth = seekBarHint.getWidth();
                seekBarHint.setX(offset + seekBar.getX() + val
                        - Math.round(percent * offset)
                        - Math.round(percent * labelWidth / 2));
                //mediaPlayer.seekTo(progress);

                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    // clearMediaPlayer();

                    // fix the visualization player issue
                    mediaPlayer.pause();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAdded()) {
                                if (getContext() != null) {
                                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.ic_media_play));
                                    seekBar.setProgress(currentPosition);
                                }
                            }
                        }
                    });
                    mediaPlayer.seekTo(seekBar.getProgress());

                    //  mediaPlayer.start();
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "hello media");
                    }
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });

    }


    OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {

            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Back Button Pressed");
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            getActivity().onBackPressed();
        }
    };

    private void oSBackButton() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (isAdded()) {
                            if (getContext() != null) {
                                /*   if (ConnectionDetector.isConnectedWithInternet(getContext())) {*/
                                if (mediaPlayer != null) {
                                    mediaPlayer.stop();
                                    mediaPlayer.release();
                                    mediaPlayer = null;
                                }
                                if (getFragmentManager().getBackStackEntryCount() != 0) {
                                    getFragmentManager().popBackStack();
                                }

                       /* } else {
                            Toast.makeText(getContext(), "Please turn ON your internet", Toast.LENGTH_SHORT).show();
                        }*/

                            }
                        }

                        return true;
                    }
                }
                // Toast.makeText(getActivity(), "Please turn ON your internet", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }


    private void init() {
        txt_clam = view1.findViewById(R.id.calm);
        mTextViewDayTime = view1.findViewById(R.id.textViewDayTime);
        sleepVisualCardView = view1.findViewById(R.id.sleepVisualCardView);
        mSleepVisualizationImg = view1.findViewById(R.id.imageSleepVisualization);
        mTextViewVisualizationDescription = view1.findViewById(R.id.textViewVisualizationDescription);
        mTextViewVisualizationTitle = view1.findViewById(R.id.textViewVisualizationTitle);
        txt_textView1SleepViewCard = view1.findViewById(R.id.textView1SleepViewCard);
        txt_time = view1.findViewById(R.id.time);
        shimmerForSleep = view1.findViewById(R.id.shimmerForSleep);
        backArrow = view1.findViewById(R.id.backArrow);

        //seek bar for play music
        fab = view1.findViewById(R.id.button);
        seekBarHint = view1.findViewById(R.id.textView);
        seekBar = view1.findViewById(R.id.seekbar);


        setLanguageToWidgets();
        userStatusUtil = new UserStatusUtil(getContext(), SleepVisualizationFragment.this, resources);
        if (ConnectionDetector.isConnectedWithInternet(getContext()))
            userStatusUtil.getUserStatus("Bearer " + SessionUtil.getAccessToken(getContext()));

        //fab.setBackgroundResource(R.drawable.gradient_drawable_button);
    }

    private void setLanguageToWidgets() {
        txt_clam.setText(resources.getString(R.string.calm));
        txt_textView1SleepViewCard.setText(resources.getString(R.string.sleep_visualization));
        txt_time.setText(resources.getString(R.string.visualization_time));
    }

    private void getDataFromBundleAndSetToWidgets() {

        imgURL = getArguments().getString("imgURL");
        mTextViewDayTime.setText(getArguments().getString("dayTime"));
        mTextViewVisualizationTitle.setText(getArguments().getString("title"));
        mTextViewVisualizationDescription.setText(getArguments().getString("description"));
        audio = getArguments().getString("audioURL");
        Glide.with(getContext())
                .load(imgURL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        FirebaseCrashlytics.getInstance().recordException(e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        StoptShimmer();
                        return false;
                    }
                }).into(mSleepVisualizationImg);
        fab.setVisibility(View.VISIBLE);
    }

    public void playSong(String audio) {
        try {
            if (isAdded()) {
                if (getContext() != null) {
                    if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            seekBar.setProgress(currentPosition);
                            wasPlaying = true;
                            fab.setImageDrawable(ContextCompat.getDrawable(getContext(),
                                    android.R.drawable.ic_media_play));

                            //  mediaPlayer.start();
                        }


                        if (!wasPlaying) {

                            if (mediaPlayer == null) {
                                mediaPlayer = new MediaPlayer();

                            } else {
                                mediaPlayer.start();
                                new Thread(this).start();
                            }
                            if (isAdded()) {
                                if (getContext() != null) {
                                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(),
                                            android.R.drawable.ic_media_pause));
                                }
                            }

                            //String descriptor = getContext().getString(Integer.parseInt(audio));
                            mediaPlayer.setDataSource(audio);
                            //mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                            //descriptor.close();
                            // mediaPlayer.prepareAsync();
                            mediaPlayer.prepare();
                            mediaPlayer.setVolume(0.5f, 0.5f);
                            mediaPlayer.setLooping(false);
                            seekBar.setMax(mediaPlayer.getDuration());

                            mediaPlayer.start();
                            new Thread(this).start();
                            new Thread(this).start();
                        }
                        wasPlaying = false;
                    } else {
                        if (isAdded()) {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
            FirebaseCrashlytics.getInstance().recordException(e);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("SleepVisualization_playSongMethod",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
            }
        }
    }

    public void run() {
        try {
            //  currentPosition = mediaPlayer.getCurrentPosition();
            int total = 0;
            if (mediaPlayer != null) {
                total = mediaPlayer.getDuration();
            }
            while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "hello media ");
                }
                try {
                    Thread.sleep(1000);
                    currentPosition = mediaPlayer.getCurrentPosition();
                } catch (InterruptedException e) {
                    if (Common.isLoggingEnabled) {
                        e.printStackTrace();
                    }
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("SleepVisualization_MediaPlayer",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
                    }
                } catch (IllegalStateException e) {
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("SleepVisualization_MediaPlayer",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
                    }
                    if (Common.isLoggingEnabled) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("SleepVisualization_MediaPlayer",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
                    }
                    if (Common.isLoggingEnabled) {
                        e.printStackTrace();
                    }
                }

                seekBar.setProgress(currentPosition);

            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void clearMediaPlayer() {
        // mediaPlayer.stop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (visaulizationCall != null) {
            if (visaulizationCall.isExecuted()) {
                visaulizationCall.cancel();
            }
        }

        if (tokenCall != null) {
            if (tokenCall.isExecuted()) {
                tokenCall.cancel();
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        HomeActivity.showBottomNav();
    }

    private void StartShimmer() {
        shimmerForSleep.startShimmerAnimation();
        shimmerForSleep.setVisibility(View.VISIBLE);
        mSleepVisualizationImg.setVisibility(View.INVISIBLE);
        sleepVisualCardView.setVisibility(View.INVISIBLE);
    }

    private void StoptShimmer() {

        mSleepVisualizationImg.setVisibility(View.VISIBLE);
        sleepVisualCardView.setVisibility(View.VISIBLE);
        shimmerForSleep.stopShimmerAnimation();
        shimmerForSleep.setVisibility(View.GONE);
    }

    @Override
    public void getStatus(String userStatus, String subscriptionStatus) {

    }

}