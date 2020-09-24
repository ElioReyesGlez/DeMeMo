package com.erg.memorized.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.fragment.app.Fragment;

import com.erg.memorized.R;
import com.erg.memorized.adapters.AdapterPickersViewPager;
import com.erg.memorized.helpers.CalendarHelper;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.helpers.RealmHelper;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.helpers.TimeHelper;
import com.erg.memorized.interfaces.OnPickersDateTimeChangeListener;
import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.util.Constants;
import com.erg.memorized.util.SuperUtil;
import com.erg.memorized.views.FixedViewPager;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.erg.memorized.helpers.CalendarHelper.CALENDAR_HELPER_PERMISSION_REQUEST_CODE;
import static com.erg.memorized.util.Constants.DEFAULT_SELECTED_REMAINDER;
import static com.erg.memorized.util.Constants.EDIT_KEY;
import static com.erg.memorized.util.Constants.ONE_HOUR;
import static com.erg.memorized.util.Constants.SPACE;
import static com.erg.memorized.util.Constants.VERSE_COLUMN_ID;

public class NewVerseFragment extends Fragment implements View.OnClickListener, OnPickersDateTimeChangeListener {

    public static String TAG = "NewVerseFragment";

    private View rootView;

    private boolean flagEditing = false;
    private boolean flagStartMemorizing = false;
    private boolean flagSomeTitleChange = false;
    private boolean flagSomeVerseChange = false;
    private long itemId = -1;
    private long notifyDate = -1;
    private TextInputLayout tilTitle, tilVerse;
    private TextInputEditText tiEditTextTitle, tiEditTextVerse;
    private RelativeLayout rlShowPickers;
    private TextView tvDate;
    private Button justSave;
    private Button saveStart;
    private boolean flagTitle, flagVerse;
    private ViewGroup container;

    private FixedViewPager fixedViewPager;
    private AdapterPickersViewPager pickersAdapter;
    private TabLayout tabLayout;

    private MeowBottomNavigation meoBottomBar;

    private Calendar calendar;
    private String calendarName = "";
    private int calendarID = -1;
    private boolean datePicked = false;
    private boolean untilDatePicked = false;
    private boolean daily = false;
    private boolean weekly = false;
    private boolean monthly = false;
    private boolean hasAlarmFlag = false;
    private Date untilDate;

    private ItemVerse currentItemVerse;

    private RealmHelper realmHelper;
    private SharedPreferencesHelper spHelper;

    private Animation animScaleUp, animScaleDown;

    public NewVerseFragment() {
    }

    public static NewVerseFragment newInstance(long itemId, boolean isEditingAction) {
        Bundle args = new Bundle();
        NewVerseFragment fragment = new NewVerseFragment();
        args.putLong(VERSE_COLUMN_ID, itemId);
        args.putBoolean(EDIT_KEY, isEditingAction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        animScaleUp = AnimationUtils.loadAnimation(getContext(), R.anim.less_scale_up);
        animScaleDown = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);

        realmHelper = new RealmHelper(getContext());
        spHelper = new SharedPreferencesHelper(getContext());
        untilDate = new Date();

        flagEditing = args != null && args.getBoolean(Constants.EDIT_KEY);
        itemId = args != null ? args.getLong(VERSE_COLUMN_ID) : -1;

        if (flagEditing && itemId != -1)
            currentItemVerse = realmHelper.findItemVerseById(itemId);
        else
            currentItemVerse = new ItemVerse();

        meoBottomBar = requireActivity().findViewById(R.id.meow_bottom_navigation);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_new_verse, container, false);

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(Objects
                .requireNonNull(getContext()));
        asyncLayoutInflater.inflate(R.layout.fragment_new_verse, container,
                (view, resid, parent) -> setUpView(rootView));

        this.container = container;
        setUpView(rootView);

        return rootView;
    }

    private void setUpView(View rootView) {

        tilTitle = rootView.findViewById(R.id.til_tile);
        tilVerse = rootView.findViewById(R.id.til_verse);
        tiEditTextTitle = rootView.findViewById(R.id.ti_edit_text_title);
        tiEditTextVerse = rootView.findViewById(R.id.ti_edit_text_verse);
        rlShowPickers = rootView.findViewById(R.id.rl_show_pickers);
        tvDate = rootView.findViewById(R.id.tv_date);
        justSave = rootView.findViewById(R.id.save_button);
        saveStart = rootView.findViewById(R.id.save_button_start);

        justSave.setOnClickListener(this);
        saveStart.setOnClickListener(this);
        rlShowPickers.setOnClickListener(this);

        if (flagEditing) {
            tiEditTextTitle.setText(currentItemVerse.getTitle());
            tiEditTextVerse.setText(currentItemVerse.getVerseText());
            if (currentItemVerse.getDateAlarm() != -1) {
                tvDate.setText(TimeHelper.dateFormatterMedium(currentItemVerse.getDateAlarm()));

                daily = spHelper.getRepeatingNotifyStatus(Constants.DAILY_KEY
                        + currentItemVerse.getTitle());
                weekly = spHelper.getRepeatingNotifyStatus(Constants.WEEKLY_KEY
                        + currentItemVerse.getTitle());
                monthly = spHelper.getRepeatingNotifyStatus(Constants.MONTHLY_KEY
                        + currentItemVerse.getTitle());

            }

            untilDatePicked = currentItemVerse.getUntilAlarm() != -1;
            if (untilDatePicked) {
                untilDate.setTime(currentItemVerse.getUntilAlarm());
            }
        }
        setUpTextListener(tiEditTextTitle, tiEditTextVerse);
    }

    private void setUpTextListener(TextInputEditText title, TextInputEditText verse) {

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                flagTitle = s.length() > 0;
                if (flagEditing)
                    flagSomeTitleChange = !currentItemVerse.getTitle().contentEquals(s.toString());

                if (!flagEditing) showButtonsOnOF();
                else showButtonsOnOFEditing();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                flagTitle = s.length() > 0;
                if (flagEditing)
                    flagSomeTitleChange = !currentItemVerse.getTitle().contentEquals(s.toString());

                if (!flagEditing) showButtonsOnOF();
                else showButtonsOnOFEditing();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        verse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                flagVerse = s.length() > 0;
                if (flagEditing)
                    flagSomeVerseChange = !currentItemVerse.getVerseText().contentEquals(s.toString());

                if (!flagEditing) showButtonsOnOF();
                else showButtonsOnOFEditing();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                flagVerse = s.length() > 0;
                if (flagEditing)
                    flagSomeVerseChange = !currentItemVerse.getVerseText().contentEquals(s.toString());

                if (!flagEditing) showButtonsOnOF();
                else showButtonsOnOFEditing();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void showButtonsOnOF() {
        if (flagTitle && flagVerse) {
            justSave.setVisibility(View.VISIBLE);
            saveStart.setVisibility(View.VISIBLE);
        } else {
            justSave.setVisibility(View.GONE);
            saveStart.setVisibility(View.GONE);
        }
    }

    private void showButtonsOnOFEditing() {
        if (flagTitle && flagVerse) {
            if (flagSomeTitleChange || flagSomeVerseChange) {
                justSave.setVisibility(View.VISIBLE);
                saveStart.setVisibility(View.VISIBLE);
            } else {
                justSave.setVisibility(View.GONE);
                saveStart.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        SuperUtil.vibrate(requireContext());
        switch (v.getId()) {
            case R.id.save_button:
                savingProcess();
                break;
            case R.id.save_button_start:
                flagStartMemorizing = true;
                SuperUtil.vibrate(requireContext());
                savingProcess();
                break;
            case R.id.rl_show_pickers:
                if (CalendarHelper.haveCalendarReadWritePermissions(requireActivity())) {
                    if (!calendarName.isEmpty() && calendarID != -1)
                        showPickerDateTimeDialog();
                    else {
                        showPickerCalendarsDialog();
                    }
                } else {
                    if (isVisible())
                        MessagesHelper.showPermissionAskInfoMessage(requireActivity(),
                                getString(R.string.calendar_permissions));
                }
                break;
        }
    }

    private void showPickerDateTimeDialog() {

        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()), R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_date_time_picker_view, null, false);

        tabLayout = dialogView.findViewById(R.id.tabLayout);
        fixedViewPager = dialogView.findViewById(R.id.fixed_viewpager);

        String[] tabsTitles = getResources().getStringArray(R.array.tabs_titles);
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        pickersAdapter = new AdapterPickersViewPager(requireActivity(), tabsTitles, calendar,
                currentItemVerse, this);
        fixedViewPager.setAdapter(pickersAdapter);
        tabLayout.setupWithViewPager(fixedViewPager);

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.less_scale_up);
        dialogView.setAnimation(anim);
        dialog.setContentView(dialogView);

        Button btnSaveDateTime = dialogView.findViewById(R.id.save_date_time_button);
        btnSaveDateTime.setOnClickListener(v -> {
            SuperUtil.vibrate(getContext());

            if (daily || weekly || monthly) {
                if (untilDatePicked) {
                    if (isUntilDateValid()) {
                        notifyDate = calendar.getTimeInMillis();
                        tvDate.setText(TimeHelper.dateFormatterMedium(notifyDate));
                        Animation animScaleDown = AnimationUtils.loadAnimation(getContext(), R.anim.less_scale_up);
                        tvDate.startAnimation(animScaleDown);

                        datePicked = true;

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    } else {
                        if (isVisible())
                            MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                                    getString(R.string.invalid_until_date), dialogView);
                    }
                } else {
                    if (isVisible())
                        MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                                getString(R.string.pick_until_date_first), dialogView);
                }
            } else {

                notifyDate = calendar.getTimeInMillis();

                tvDate.setText(TimeHelper.dateFormatterMedium(notifyDate));
                Animation animScaleDown = AnimationUtils.loadAnimation(getContext(), R.anim.less_scale_up);
                tvDate.startAnimation(animScaleDown);

                datePicked = true;

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }


        });

        dialog.show();
    }


    private void savingProcess() {
        String srtTitle = Objects.requireNonNull(tiEditTextTitle.getText()).toString();
        String srtVerse = Objects.requireNonNull(tiEditTextVerse.getText()).toString();

        if (!srtTitle.isEmpty() && !srtVerse.isEmpty()) {

            if (!flagEditing) {
                currentItemVerse.setId(System.currentTimeMillis());
            }
            currentItemVerse.setTitle(srtTitle);
            currentItemVerse.setVerseText(srtVerse);

            if (datePicked) {
                currentItemVerse.setDateAlarm(notifyDate);
            }

            if (daily || weekly || monthly) {
                if (untilDate != null) {
                    if (untilDatePicked) {
                        if (isUntilDateValid()) {
                            currentItemVerse.setUntilAlarm(untilDate.getTime());
                        } else {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarning(requireActivity(),
                                        getString(R.string.invalid_until_date));
                            return;
                        }
                    }
                    currentItemVerse.setRepeatingAlarmStatus(true);
                }
            } else {
                currentItemVerse.setRepeatingAlarmStatus(false);
            }

            new AsyncTaskSaving(srtTitle, srtVerse).execute();

        } else {

            if (srtTitle.isEmpty())
                tilTitle.setError(getString(R.string.required_field));
            if (srtVerse.isEmpty())
                tilVerse.setError(getString(R.string.required_field));

            if (isVisible())
                MessagesHelper.showInfoMessageError(requireActivity(),
                        getString(R.string.failed_saving));
        }
    }

    private boolean isUntilDateValid() {
        Date currentTime = Calendar.getInstance().getTime();
        return untilDate != null && untilDate.getTime() > currentTime.getTime();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (meoBottomBar != null) {
            meoBottomBar.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_down));
            if (meoBottomBar.getVisibility() == View.VISIBLE)
                meoBottomBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (meoBottomBar != null) {
            meoBottomBar.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_up));
            if (meoBottomBar.getVisibility() == View.GONE)
                meoBottomBar.setVisibility(View.VISIBLE);
        }
    }

    private void loadMemorizingView() {
        SuperUtil.loadView(requireActivity(),
                MemorizingFragment.newInstance(currentItemVerse, false),
                MemorizingFragment.TAG, true);
    }

    @Override
    public void OnDateChangeListener(DatePicker datePicker, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    @Override
    public void OnTimeChangeListener(TimePicker timePicker, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
    }

    @Override
    public boolean OnDailySwitchListener(CompoundButton buttonView, boolean isChecked,
                                         RelativeLayout untilView) {
        daily = isChecked;

        if (daily || weekly || monthly)
            SuperUtil.showView(null, untilView);
        else
            SuperUtil.hideView(null, untilView);

        return isChecked;
    }

    @Override
    public boolean OnWeeklySwitchListener(CompoundButton buttonView, boolean isChecked,
                                          RelativeLayout untilView) {
        weekly = isChecked;

        if (daily || weekly || monthly)
            SuperUtil.showView(null, untilView);
        else
            SuperUtil.hideView(null, untilView);

        return isChecked;
    }

    @Override
    public boolean OnMonthlySwitchListener(CompoundButton buttonView, boolean isChecked,
                                           RelativeLayout untilView) {
        monthly = isChecked;

        if (daily || weekly || monthly)
            SuperUtil.showView(null, untilView);
        else
            SuperUtil.hideView(null, untilView);

        return isChecked;
    }

    @Override
    public boolean OnUntilViewListener(RelativeLayout untilView, TextView tvUntilDate) {
        SuperUtil.vibrate(requireActivity());
        showUntilDatePickerDialog(tvUntilDate);
        return false;
    }

    private void showUntilDatePickerDialog(TextView tvUntilDate) {
        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()), R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.until_date_picker_view, null, false);
        dialog.setContentView(dialogView);

        DatePicker datePicker = dialogView.findViewById(R.id.until_date_picker);

        //default date
        untilDate = calendar.getTime();
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), (datePicker1, year, month, dayOfMonth) -> {
                    Calendar auxCal = Calendar.getInstance();
                    auxCal.setTimeInMillis(System.currentTimeMillis());
                    auxCal.set(Calendar.YEAR, year);
                    auxCal.set(Calendar.MONTH, month);
                    auxCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    untilDate = auxCal.getTime();
                });

        Button btSaveUntilDate = dialogView.findViewById(R.id.until_date_button);
        btSaveUntilDate.setOnClickListener(v -> {
            SuperUtil.vibrate(getContext());
            if (isUntilDateValid()) {
                tvUntilDate.setText(TimeHelper.dateFormatterMedium(untilDate.getTime()));
                untilDatePicked = true;
                if (dialog.isShowing())
                    dialog.dismiss();
            } else {
                if (isVisible())
                    MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                            getString(R.string.invalid_until_date), dialogView);
            }
        });

        dialog.show();
    }


    public class AsyncTaskSaving extends AsyncTask<Void, Void, Void> {


        private String srtTitle, srtVerse;
        private boolean verseExistByTitle = false;
        private boolean verseExistByVerseText = false;
        Dialog progressDialog;

        public AsyncTaskSaving(String srtTitle, String srtVerse) {
            this.srtTitle = srtTitle;
            this.srtVerse = srtVerse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            verseExistByTitle = realmHelper.findItemVerseByTitle(srtTitle) != null;
            verseExistByVerseText = realmHelper.findItemVerseByText(srtVerse) != null;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            if (verseExistByTitle || verseExistByVerseText)
                showDialogItemExit(verseExistByTitle, verseExistByVerseText);
            else {
                saveIntoDB();
                if (flagStartMemorizing) {
                    loadMemorizingView();
                }
            }
        }
    }

    private void saveIntoDB() {
        realmHelper.addVerseToDB(currentItemVerse);
        createCalendarEvent();
        saveSharedPref();
        MessagesHelper.showInfoMessage(requireActivity(),
                getString(R.string.saved));
        SuperUtil.removeViewByTag(requireActivity(), TAG, true);
    }

    private void saveSharedPref() {
        spHelper.setRepeatingNotifyStatus(Constants.DAILY_KEY
                + currentItemVerse.getTitle(), daily);
        spHelper.setRepeatingNotifyStatus(Constants.WEEKLY_KEY
                + currentItemVerse.getTitle(), weekly);
        spHelper.setRepeatingNotifyStatus(Constants.MONTHLY_KEY
                + currentItemVerse.getTitle(), monthly);
    }

    private void createCalendarEvent() {

        if (currentItemVerse.getDateAlarm() != -1) {

            String contentTitle = getString(R.string.app_name)
                    + SPACE + getString(R.string.remainder_msg);

            String contentText = getString(R.string.ready_msg)
                    + SPACE + currentItemVerse.getTitle();


            String formattedUntilDate = "";
            if (daily || weekly || monthly) {
                if (untilDate != null) {
                    formattedUntilDate = TimeHelper.getUntil(untilDate);
                    hasAlarmFlag = true;
                }
            } else if (currentItemVerse != null && currentItemVerse.getUntilAlarm() != -1) {
                formattedUntilDate = TimeHelper.dateFormatterMedium(currentItemVerse.getUntilAlarm());
            }

            CalendarHelper.makeNewCalendarEntry(
                    requireActivity(), contentTitle, contentText, currentItemVerse.getDateAlarm(),
                    ONE_HOUR, false, hasAlarmFlag, daily, weekly, monthly,
                    calendarID, formattedUntilDate, DEFAULT_SELECTED_REMAINDER
            );
        }
    }

    private void showDialogItemExit(boolean existByTitle, boolean existByVerseText) {

        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()), R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_view_repited_item, null, false);
        TextView msg = dialogView.findViewById(R.id.text_dialog);

        if (existByTitle)
            msg.setText(R.string.title_exists);
        else
            msg.setText(R.string.verse_exists);

        if (existByTitle && existByVerseText) {
            msg.setText(getString(R.string.title_and_verse_exists));
            msg.setTextSize(14);
        }

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
        dialogView.setAnimation(anim);
        dialog.setContentView(dialogView);

        /*onClick on dialog cancel button*/
        Button cancelBtn = dialog.findViewById(R.id.cancel_dialog_button);
        cancelBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(Objects.requireNonNull(getContext()));
            if (dialog.isShowing())
                dialog.dismiss();
        });

        /*onClick on dialog override button*/
        Button editBtn = dialog.findViewById(R.id.edit_dialog_button);
        editBtn.setOnClickListener(v -> {

            saveIntoDB();
            if (isVisible())
                MessagesHelper.showInfoMessage(requireActivity(),
                        getString(R.string.saved));
            SuperUtil.removeViewByTag(requireActivity(), TAG, true);

            if (flagStartMemorizing) {
                loadMemorizingView();
            }

            if (dialog.isShowing())
                dialog.dismiss();
        });

        dialog.show();
    }

    /*private void setUpRemainder() {
        if (currentItemVerse.getDateAlarm() != -1) {

            SuperUtil.createNotificationChanel(getContext());

            final int THIRTY_SECOND_IN_MILLI = 30000;
//            long triggerTime = currentItemVerse.getDateAlarm();
            long triggerTime = 120000;

//            Context appContext = getContext().getApplicationContext();
            Intent broadcastReceiver = new Intent(getContext(), RemainderReceiver.class);
            broadcastReceiver.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

            Bundle extras = new Bundle();
            extras.putLong(VERSE_COLUMN_ID, currentItemVerse.getId());
            broadcastReceiver.putExtras(extras);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),
                    0, broadcastReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

            if (am != null) {
                if (daily)
                    am.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime,
                            AlarmManager.INTERVAL_DAY, pendingIntent);
                if (weekly)
                    am.setInexactRepeating(AlarmManager.RTC_WAKEUP, currentItemVerse.getDateAlarm(),
                            AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                if (monthly)
                    am.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime,
                            SuperUtil.getMonthlyDuration(), pendingIntent);

                if (!daily && !weekly && !monthly)
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                            triggerTime, pendingIntent);
            }

            Log.d(TAG, "setUpRemainder: " + SuperUtil.dateFormatter(currentItemVerse.getDateAlarm()));
            Log.d(TAG, "setUpRemainder: " + "daily: " + daily + " weekly: " + weekly + " monthly: " + monthly);
        }
    }*/

    private void showPickerCalendarsDialog() {
        Dialog dialog = new Dialog(Objects.requireNonNull(getContext()), R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_view_calendar_choice, null, false);
        ListView listView = dialogView.findViewById(R.id.list_view_calendars);

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
        dialogView.setAnimation(anim);
        dialog.setContentView(dialogView);

        listView.setAdapter(new ArrayAdapter<>(getContext(),
                R.layout.item_list_calendar,
                R.id.tv_calendar, getCalendarsNameArray()));

        listView.setOnItemClickListener((parent, view, position, id) -> {
            SuperUtil.vibrate(getContext());
            calendarName = getCalendarsNameArray()[position];
            calendarID = getCalendarsIdArray()[position];

            if (dialog.isShowing())
                dialog.dismiss();

            showPickerDateTimeDialog();

            MessagesHelper.showInfoMessage(requireActivity(),
                    calendarName + SPACE + getString(R.string.selected));
        });
        dialog.show();

        if (isVisible())
            MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                    getString(R.string.pick_calendar_needed), dialogView);
    }

    private String[] getCalendarsNameArray() {
        HashMap<String, String> hashCalendars = CalendarHelper.getUserCalendars(requireActivity());
        String[] array = new String[hashCalendars.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : hashCalendars.entrySet()) {
            array[i] = entry.getKey();
            i++;
        }
        return array;
    }

    private int[] getCalendarsIdArray() {
        HashMap<String, String> hashCalendars = CalendarHelper.getUserCalendars(requireActivity());
        int[] array = new int[hashCalendars.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : hashCalendars.entrySet()) {
            array[i] = Integer.parseInt(entry.getValue());
            i++;
        }
        return array;
    }

    private Dialog showProgressDialog() {
        Dialog dialog = new Dialog(Objects.requireNonNull(getContext()), R.style.alert_dialog);
        dialog.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.progress, container, false);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
        dialogView.setAnimation(anim);
        dialog.setContentView(dialogView);
        dialog.show();
        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CALENDAR_HELPER_PERMISSION_REQUEST_CODE) {
            if (!CalendarHelper.haveCalendarReadWritePermissions(requireActivity())) {
                MessagesHelper.showInfoMessageWarning(requireActivity(),
                        getString(R.string.please_calendar_grant_permissions));
            }
        }
    }
}
