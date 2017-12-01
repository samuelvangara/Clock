package foxycorp.alarminator;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import android.os.Vibrator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import android.text.format.DateFormat;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import static foxycorp.alarminator.R.layout.activity_home_page;
import static foxycorp.alarminator.R.layout.fragment_home_page;

public class HomePage extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static Boolean isFabOpen = false;
    private static Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private static TextView remindertext, alarmtext;
    private static FloatingActionButton homefab, homealarm, homereminder,stopwatchLap,timerStop;
    private static Calendar calendar;
    private static TextView dateView;
    private static TextClock ClockView;
    private static DatePickerDialog dpd;
    private static TimePickerDialog tpd;
    private static int year, month, day, hour, minute;
    private static TabLayout tabLayout;
    private ViewPager mViewPager;
    private static ListView AlarmListView;
    private static TextView countdownTimerText;
    private static EditText minutes;
    private static TextView textView ;
    private static long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    private static Handler handler;
    private static int Seconds, Minutes, MilliSeconds;
    private static Vibrator v;
    private static MediaPlayer timerTone;
    private static NotificationManager mNM;
    private static Notification mNotify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        TabLayout.Tab alarm = tabLayout.getTabAt(0);
        alarm.setIcon(R.drawable.alarm);
        TabLayout.Tab clock = tabLayout.getTabAt(1);
        clock.setIcon(R.drawable.clock);
        TabLayout.Tab stopwatch = tabLayout.getTabAt(2);
        stopwatch.setIcon(R.drawable.stopwatch);
        TabLayout.Tab timer = tabLayout.getTabAt(3);
        timer.setIcon(R.drawable.timer);
        timerTone = MediaPlayer.create(this, R.raw.timer_ost);
    }

     public static class PlaceholderFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {

         private static final String ARG_SECTION_NUMBER = "section_number";
         ListView listView;
         String[] ListElements = new String[]{};
         List<String> ListElementsArrayList;
         ArrayAdapter<String> adapter;
         private CountDownTimer countDownTimer;

         public PlaceholderFragment() {
         }

         public static PlaceholderFragment newInstance(int sectionNumber) {
             PlaceholderFragment fragment = new PlaceholderFragment();
             Bundle args = new Bundle();
             args.putInt(ARG_SECTION_NUMBER, sectionNumber);
             fragment.setArguments(args);
             return fragment;
         }

         @Override
         public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState) {
             if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                 View rootView = inflater.inflate(fragment_home_page, container, false);
                 dateView = (TextView) rootView.findViewById(R.id.text);
                 AlarmListView = (ListView) rootView.findViewById(R.id.alarmList);
                 homefab = (FloatingActionButton) rootView.findViewById(R.id.homefab);
                 homealarm = (FloatingActionButton) rootView.findViewById(R.id.home_alarm);
                 homereminder = (FloatingActionButton) rootView.findViewById(R.id.home_reminder);
                 alarmtext = (TextView) rootView.findViewById(R.id.test1);
                 remindertext = (TextView) rootView.findViewById(R.id.test2);
                 dpd = new DatePickerDialog(rootView.getContext(), R.style.MyDatePicker, datePickerListener, year, month, day);
                 dpd.getDatePicker().setMinDate(System.currentTimeMillis());
                 tpd = new TimePickerDialog(rootView.getContext(), R.style.MyDialogTheme, mTimeSetListener, hour, minute, DateFormat.is24HourFormat((rootView.getContext())));
                 homefab.setOnClickListener(this);
                 homealarm.setOnClickListener(this);
                 homereminder.setOnClickListener(this);
                 return rootView;

             }
             else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                 View rootView = inflater.inflate(R.layout.fragment_clock_page, container, false);
                 MobileAds.initialize(rootView.getContext(), "ca-app-pub-1941549848807363~7255171136");
                 AdView mAdView = (AdView) rootView.findViewById(R.id.adView2);
                 AdRequest adRequest = new AdRequest.Builder().build();
                 mAdView.loadAd(adRequest);
                 FloatingActionButton clockfab = (FloatingActionButton) rootView.findViewById(R.id.clock);
                 clockfab.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         Snackbar.make(view, "Under Construction!", Snackbar.LENGTH_LONG)
                                 .setAction("Action", null).show();
                     }
                 });
                 return rootView;
             }
             else if (getArguments().getInt(ARG_SECTION_NUMBER) == 3) {
                 View rootView = inflater.inflate(R.layout.fragment_stopwatch_page, container, false);
                 MobileAds.initialize(rootView.getContext(), "ca-app-pub-1941549848807363~7255171136");
                 AdView mAdView = (AdView) rootView.findViewById(R.id.adView1);
                 AdRequest adRequest = new AdRequest.Builder().build();
                 mAdView.loadAd(adRequest);
                 stopwatchLap = (FloatingActionButton) rootView.findViewById(R.id.stopwatchLap);
                 textView = (TextView) rootView.findViewById(R.id.textView);
                 listView = (ListView) rootView.findViewById(R.id.listview1);
                 handler = new Handler();
                 ListElementsArrayList = new ArrayList<>(Arrays.asList(ListElements));
                 adapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, ListElementsArrayList);
                 listView.setAdapter(adapter);
                 textView.setTranslationZ(1);

                 textView.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         if (textView.getTranslationZ()==1) {
                             textView.setTranslationZ(50);
                             textView.setLongClickable(false);
                             textView.setTextColor(getResources().getColor(R.color.stopWatchOpen));
                             StartTime = SystemClock.uptimeMillis();
                             handler.postDelayed(runnable, 0);
                         } else
                             {
                                 textView.setLongClickable(true);
                                 TimeBuff += MillisecondTime;
                                 textView.setTextColor(getResources().getColor(R.color.stopWatchClose));
                                 textView.setTranslationZ(1);
                                 handler.removeCallbacks(runnable);
                         }
                     }
                 });

                 textView.setOnLongClickListener(new View.OnLongClickListener() {
                     @Override
                     public boolean onLongClick(View v) {
                         MillisecondTime = 0L;
                         textView.setTextColor(getResources().getColor(R.color.stopWatchClose));
                         textView.setTranslationZ(1);
                         StartTime = 0L;
                         TimeBuff = 0L;
                         UpdateTime = 0L;
                         Seconds = 0;
                         Minutes = 0;
                         MilliSeconds = 0;
                         textView.setText("0:00:000");
                         ListElementsArrayList.clear();
                         adapter.notifyDataSetChanged();
                         return true;
                     }
                 });

                 stopwatchLap.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         if(textView.getTranslationZ()==1)
                         {
                             Snackbar.make(view, "Start the StopWatch!", Snackbar.LENGTH_LONG)
                                     .setAction("Action", null).show();
                         }
                         else{
                             ListElementsArrayList.add(textView.getText().toString());
                             adapter.notifyDataSetChanged();
                         }
                     }
                 });
                 return rootView;
             }
             else if (getArguments().getInt(ARG_SECTION_NUMBER) == 4) {
                 View rootView = inflater.inflate(R.layout.fragment_timer_page, container, false);
                 Intent intent1 = new Intent(rootView.getContext(), HomePage.class);
                 PendingIntent pIntent = PendingIntent.getActivity(rootView.getContext(), 0, intent1, 0);
                 mNotify  = new Notification.Builder(this.getContext())
                         .setContentTitle("Time's Up" + "!")
                         .setContentText("stop")
                         .setContentIntent(pIntent)
                         .setSmallIcon(R.drawable.timer)
                         .setAutoCancel(true)
                         .build();
                 countdownTimerText = (TextView) rootView.findViewById(R.id.countdownText);
                 minutes = (EditText) rootView.findViewById(R.id.enterMinutes);
                 timerStop = (FloatingActionButton) rootView.findViewById(R.id.timerStop);
                 v = (Vibrator)rootView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                 MobileAds.initialize(rootView.getContext(), "ca-app-pub-1941549848807363~7255171136");
                 AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
                 AdRequest adRequest = new AdRequest.Builder().build();
                 mAdView.loadAd(adRequest);
                 timerStop.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         timerStop.startAnimation(fab_close);
                         v.cancel();
                         timerTone.stop();
                         countdownTimerText.setText(getString(R.string.timer));
                         countdownTimerText.setTranslationZ(1);
                         countdownTimerText.setTextColor(getResources().getColor(R.color.stopWatchClose));
                         countDownTimer = null;
                     }
                 });

                 countdownTimerText.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {

                         if (countDownTimer == null) {
                             String getMinutes = minutes.getText().toString();
                             if (!getMinutes.equals("") && getMinutes.length() > 0) {
                                 countdownTimerText.setTranslationZ(50);
                                 countdownTimerText.setTextColor(getResources().getColor(R.color.stopWatchOpen));
                                 int noOfMinutes = Integer.parseInt(getMinutes) * 60 * 1000;
                                 startTimer(noOfMinutes);
                             }
                             else
                                 Snackbar.make(view, "Enter the timer in Minutes to Start the timer!", Snackbar.LENGTH_LONG)
                                         .setAction("Action", null).show();
                         } else {
                             stopCountdown();
                             countdownTimerText.setTranslationZ(1);
                             countdownTimerText.setTextColor(getResources().getColor(R.color.stopWatchClose));

                         }
                     }
                 });
                 countdownTimerText.setOnLongClickListener(new View.OnLongClickListener() {
                     @Override
                     public boolean onLongClick(View v) {
                         stopCountdown();
                         countdownTimerText.setTranslationZ(1);
                         countdownTimerText.setTextColor(getResources().getColor(R.color.stopWatchClose));
                         countdownTimerText.setText(getString(R.string.timer));
                         return true;
                     }
                 });

                 return rootView;
             }

             View rootView = inflater.inflate(activity_home_page, container, false);
             return rootView;
         }

             public void onClick(View v) {
                 int id = v.getId();
                 switch (id) {
                     case R.id.homefab:
                         animateFAB();
                         break;
                     case R.id.home_alarm:
                         tpd.show();
                         animateFAB();
                         break;
                     case R.id.home_reminder:
                         dpd.show();
                         animateFAB();
                         break;
                 }

             }

         public Runnable runnable = new Runnable() {

             public void run() {
                 MillisecondTime = SystemClock.uptimeMillis() - StartTime;
                 UpdateTime = TimeBuff + MillisecondTime;
                 Seconds = (int) (UpdateTime / 1000);
                 Minutes = Seconds / 60;
                 Seconds = Seconds % 60;
                 MilliSeconds = (int) (UpdateTime % 1000);
                 textView.setText("" + Minutes + ":"
                         + String.format("%02d", Seconds) + ":"
                         + String.format("%03d", MilliSeconds));
                 handler.postDelayed(this, 0);
             }
         };
         private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
             @Override
             public void onDateSet(DatePicker view, int year, int monthOfYear,
                                   int dayOfMonth) {
                 calendar = Calendar.getInstance();
                 calendar.set(Calendar.YEAR, year);
                 calendar.set(Calendar.MONTH, monthOfYear);
                 calendar.set(Calendar.DATE, dayOfMonth);
                 tpd.show();
                 String myFormat = "MM/dd/yy HH:mm";
                 SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                 dateView.setText(sdf.format(calendar.getTime()));

             }
         };

         TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
             @Override
             public void onTimeSet(android.widget.TimePicker view,
                                   int hourOfDay, int minute) {
                 calendar = Calendar.getInstance();
                 calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                 calendar.set(Calendar.MINUTE, minute);
                 String myFormat = "MM/dd/yy HH:mm";
                 SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                 dateView.setText(sdf.format(calendar.getTime()));
             }
         };

         public void animateFAB() {

             if (isFabOpen) {
                 alarmtext.setVisibility(View.INVISIBLE);
                 remindertext.setVisibility(View.INVISIBLE);
                 homefab.startAnimation(rotate_backward);
                 homealarm.startAnimation(fab_close);
                 homereminder.startAnimation(fab_close);
                 alarmtext.startAnimation(fab_close);
                 remindertext.startAnimation(fab_close);
                 homealarm.setClickable(false);
                 homereminder.setClickable(false);
                 alarmtext.setClickable(false);
                 remindertext.setClickable(false);
                 isFabOpen = false;

             } else {

                 alarmtext.setVisibility(View.VISIBLE);
                 remindertext.setVisibility(View.VISIBLE);
                 homefab.startAnimation(rotate_forward);
                 homealarm.startAnimation(fab_open);
                 homereminder.startAnimation(fab_open);
                 alarmtext.startAnimation(fab_open);
                 remindertext.startAnimation(fab_open);
                 homealarm.setClickable(true);
                 homereminder.setClickable(true);
                 alarmtext.setClickable(false);
                 remindertext.setClickable(false);
                 isFabOpen = true;
             }
         }

         @Override
         public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
         }

         @Override
         public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

         }

         private void stopCountdown() {
             if (countDownTimer != null) {
                 countDownTimer.cancel();
                 countDownTimer = null;
             }
         }
         private void startTimer(int noOfMinutes) {
             countDownTimer = new CountDownTimer(noOfMinutes, 1000) {
                 public void onTick(long millisUntilFinished) {
                     long millis = millisUntilFinished;
                     String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                     countdownTimerText.setText(hms);
                 }
                 public void onFinish() {
                     countdownTimerText.setText("TIME'S UP!!");
                     countDownTimer = null;
                     long[] pattern = { 0, 200, 0 };
                     v.vibrate(pattern, 0);
                     timerTone.start();
                     mNM.notify(0, mNotify);
                     timerStop.startAnimation(fab_open);
                 }
             }.start();
         }

     }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Alarm";
                case 1:
                    return "Clock";
                case 2:
                    return "StopWatch";
                case 3:
                    return "Timer";
            }
            return null;
        }
    }

}
