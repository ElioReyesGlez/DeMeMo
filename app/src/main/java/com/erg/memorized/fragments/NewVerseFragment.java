package com.erg.memorized.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import static com.erg.memorized.util.Constants.ONE_HOUR;
import static com.erg.memorized.util.Constants.SPACE;

public class NewVerseFragment extends Fragment implements View.OnClickListener,
        OnPickersDateTimeChangeListener {

    public static final String TAG = "NewVerseFragment";

    private View rootView;

    private final boolean isEditingAction;

    private TextInputLayout tilTitle, tilVerse;
    private TextInputEditText tiEditTextTitle, tiEditTextVerse;
    private TextView tvDate;
    private ViewGroup container;

    private MeowBottomNavigation meoBottomBar;

    private Calendar calendar;
    private String calendarName = "";
    private int calendarID = -1;
    private boolean isEndDatePicked = false;
    private boolean isDateAndTimePicked = false;
    private boolean daily = false;
    private boolean weekly = false;
    private boolean monthly = false;
    private boolean hasAlarmFlag = false;
    private Date endTime;
    private long notifyDate = -1;

    private final ItemVerse currentItemVerse;
    private RealmHelper realmHelper;
    private SharedPreferencesHelper spHelper;
    private Animation animScaleUp, animScaleDown;

    public NewVerseFragment(ItemVerse verse, boolean isEditingAction) {
        this.isEditingAction = isEditingAction;
        if (isEditingAction && verse != null)
            this.currentItemVerse = verse;
        else
            currentItemVerse = new ItemVerse();
    }

    public static NewVerseFragment newInstance(ItemVerse verse, boolean isEditingAction) {
        return new NewVerseFragment(verse, isEditingAction);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        animScaleUp = AnimationUtils.loadAnimation(getContext(), R.anim.less_scale_up);
        animScaleDown = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);

        realmHelper = new RealmHelper();
        spHelper = new SharedPreferencesHelper(requireContext());
        endTime = new Date();
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

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
        RelativeLayout rlShowPickers = rootView.findViewById(R.id.rl_show_pickers);
        tvDate = rootView.findViewById(R.id.tv_date);
        Button btnSave = rootView.findViewById(R.id.btn_save);
        Button btnCancel = rootView.findViewById(R.id.cancel_button);

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        rlShowPickers.setOnClickListener(this);

        if (isEditingAction) {
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

            isEndDatePicked = currentItemVerse.getEndTimeAlarm() != -1;
            if (isEndDatePicked) {
                endTime.setTime(currentItemVerse.getEndTimeAlarm());
            }
        }
    }


    @Override
    public void onClick(View v) {
        SuperUtil.vibrate(requireContext());
        switch (v.getId()) {
            case R.id.cancel_button:
                requireActivity().onBackPressed();
                break;
            case R.id.btn_save:
                savingProcess();
                break;
            case R.id.rl_show_pickers:
                if (CalendarHelper.haveCalendarReadWritePermissions(requireActivity())) {
                    if (!calendarName.isEmpty() && calendarID != -1) {
                        handleShowPickerDateTimeDialog();
                    } else {
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

    private void handleShowPickerDateTimeDialog() {
        new Handler().postDelayed(this::showPickerDateTimeDialog, 500);
    }

    private void showPickerDateTimeDialog() {
        final Dialog dialog = new Dialog(requireContext(), R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView =
                inflater.inflate(R.layout.dialog_date_time_picker_view, null, false);

        TabLayout tabLayout = dialogView.findViewById(R.id.tabLayout);
        FixedViewPager fixedViewPager = dialogView.findViewById(R.id.fixed_viewpager);

        String[] tabsTitles = getResources().getStringArray(R.array.tabs_titles);
        AdapterPickersViewPager pickersAdapter =
                new AdapterPickersViewPager(requireActivity(), tabsTitles, calendar,
                        currentItemVerse, this);
        fixedViewPager.setAdapter(pickersAdapter);
        tabLayout.setupWithViewPager(fixedViewPager);

        Button btnCancel = dialogView.findViewById(R.id.cancel_dialog_button);
        btnCancel.setOnClickListener(v -> {
            if (isVisible())
                SuperUtil.vibrate(requireActivity());
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

        Button btnSaveDateTime = dialogView.findViewById(R.id.save_date_time_button);
        btnSaveDateTime.setOnClickListener(v -> {
            if (isVisible())
                SuperUtil.vibrate(requireActivity());

            boolean repeatingFlag = false;
            boolean isDateValid = false;

            if (daily || weekly || monthly) {
                repeatingFlag = true;
            }

            if (isAlarmDateValid()) {
                isDateValid = true;
            }

            if (repeatingFlag && !isEndDatePicked) {
                if (isVisible())
                    MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                            getString(R.string.pick_until_date_first), dialogView);
            } else if (isDateValid) {
                notifyDate = calendar.getTimeInMillis();
                tvDate.setText(TimeHelper.dateFormatterMedium(notifyDate));
                tvDate.startAnimation(animScaleDown);
                isDateAndTimePicked = true;
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            } else {
                if (isVisible())
                    MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                            getString(R.string.invalid_date), dialogView);
            }
        });

        resetVariables();
        dialog.setContentView(dialogView);
        dialog.show();
        dialogView.startAnimation(animScaleUp);
    }

    private void resetVariables() {
        daily = false;
        weekly = false;
        monthly = false;
        isEndDatePicked = false;
    }

    private void savingProcess() {
        String srtTitle = Objects.requireNonNull(tiEditTextTitle.getText()).toString();
        String srtVerse = Objects.requireNonNull(tiEditTextVerse.getText()).toString();

        if (!srtTitle.isEmpty() && !srtVerse.isEmpty()) {

            if (!isEditingAction) {
                currentItemVerse.setId(System.currentTimeMillis());
            }
            currentItemVerse.setTitle(srtTitle);
            currentItemVerse.setVerseText(srtVerse);

            if (isDateAndTimePicked) {
                if (isAlarmDateValid()) {
                    currentItemVerse.setDateAlarm(notifyDate);
                } else {
                    if (isVisible())
                        MessagesHelper.showInfoMessageWarning(requireActivity(),
                                getString(R.string.invalid_date));
                    return;
                }
            }

            if (daily || weekly || monthly) {
                if (isEndDatePicked && isEndTimeValid()) {
                    currentItemVerse.setEndTime(endTime.getTime());
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
                MessagesHelper.showInfoMessageWarning(requireActivity(),
                        getString(R.string.failed_saving));
        }
    }

    private boolean isEndTimeValid() {
        return endTime != null && endTime.getTime() > System.currentTimeMillis();
    }

    private boolean isAlarmDateValid() {
        return notifyDate > System.currentTimeMillis();
    }

    @Override
    public void onStart() {
        super.onStart();
        SuperUtil.showView(animScaleUp, meoBottomBar);
    }

    private void loadMemorizingView() {
        SuperUtil.removeViewByTag(requireActivity(), TAG, true);
        SuperUtil.removeViewByTag(requireActivity(), MemorizingFragment.TAG, true);
        SuperUtil.loadView(requireActivity(),
                MemorizingFragment.newInstance(currentItemVerse, false),
                MemorizingFragment.TAG, true);
    }

    @Override
    public void OnDateChangeListener(DatePicker datePicker, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        notifyDate = calendar.getTimeInMillis();
    }

    @Override
    public void OnTimeChangeListener(TimePicker timePicker, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        notifyDate = calendar.getTimeInMillis();
    }

    @Override
    public void OnDailySwitchListener(CompoundButton buttonView, boolean isChecked,
                                      RelativeLayout rlEndDateView) {
        daily = isChecked;
        if (daily || weekly || monthly)
            SuperUtil.showView(null, rlEndDateView);
        else
            SuperUtil.hideView(null, rlEndDateView);

    }

    @Override
    public void OnWeeklySwitchListener(CompoundButton buttonView, boolean isChecked,
                                       RelativeLayout rlEndDateView) {
        weekly = isChecked;

        if (daily || weekly || monthly)
            SuperUtil.showView(null, rlEndDateView);
        else
            SuperUtil.hideView(null, rlEndDateView);

    }

    @Override
    public void OnMonthlySwitchListener(CompoundButton buttonView, boolean isChecked,
                                        RelativeLayout rlEndDateView) {
        monthly = isChecked;

        if (daily || weekly || monthly)
            SuperUtil.showView(null, rlEndDateView);
        else
            SuperUtil.hideView(null, rlEndDateView);

    }

    @Override
    public void OnEndTimeViewListener(RelativeLayout rlEndDateView, TextView tvEndDate) {
        if (isVisible())
            SuperUtil.vibrate(requireActivity());
        showEndTimePickerDialog(tvEndDate);
    }

    private void showEndTimePickerDialog(TextView tvEndDate) {
        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()), R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.until_date_picker_view, null, false);
        dialog.setContentView(dialogView);

        DatePicker datePicker = dialogView.findViewById(R.id.end_date_picker);

        //default date
        endTime = calendar.getTime();
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), (datePicker1, year, month, dayOfMonth) -> {
                    Calendar auxCal = Calendar.getInstance();
                    auxCal.setTimeInMillis(System.currentTimeMillis());
                    auxCal.set(Calendar.YEAR, year);
                    auxCal.set(Calendar.MONTH, month);
                    auxCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    endTime = auxCal.getTime();
                });

        Button btSaveEndDate = dialogView.findViewById(R.id.until_date_button);
        btSaveEndDate.setOnClickListener(v -> {
            if (isVisible())
                SuperUtil.vibrate(requireActivity());
            if (isEndTimeValid()) {
                tvEndDate.setText(TimeHelper.dateFormatterMedium(endTime.getTime()));
                isEndDatePicked = true;
                if (dialog.isShowing())
                    dialog.dismiss();
            } else {
                if (isVisible())
                    MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                            getString(R.string.invalid_date), dialogView);
            }
        });
        dialog.show();
    }


    @SuppressLint("StaticFieldLeak")
    public class AsyncTaskSaving extends AsyncTask<Void, Void, Void> {

        private final String srtTitle;
        private final String srtVerse;
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
                showDialogItemExit(verseExistByTitle, verseExistByVerseText, srtTitle, srtVerse);
            else {
                saveIntoDB(currentItemVerse);
                loadMemorizingView();
            }
        }
    }

    private void saveIntoDB(ItemVerse verse) {
        realmHelper.addVerseToDB(verse);
        createCalendarEvent();
        saveSharedPref();
        MessagesHelper.showInfoMessage(requireActivity(),
                getString(R.string.saved));
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

            String formattedEndDate = "";
            if (daily || weekly || monthly) {
                if (endTime != null) {
                    formattedEndDate = TimeHelper.getUntil(endTime);
                    hasAlarmFlag = true;
                }
            } else if (currentItemVerse.getEndTimeAlarm() != -1) {
                formattedEndDate = TimeHelper.dateFormatterMedium(currentItemVerse.getEndTimeAlarm());
            }

            CalendarHelper.makeNewCalendarEntry(
                    requireActivity(), contentTitle, contentText, currentItemVerse.getDateAlarm(),
                    ONE_HOUR, false, hasAlarmFlag, daily, weekly, monthly,
                    calendarID, formattedEndDate, DEFAULT_SELECTED_REMAINDER
            );
        }
    }

    private void showDialogItemExit(boolean existByTitle, boolean existByVerseText,
                                    String srtTitle, String srtVerse) {
        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()), R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_view_repited_item, null, false);
        TextView msg = dialogView.findViewById(R.id.text_dialog);

        ItemVerse auxVerse = null;
        if (existByTitle) {
            msg.setText(R.string.title_exists);
            auxVerse = realmHelper.findItemVerseByTitle(srtTitle);
        } else if (existByVerseText) {
            msg.setText(R.string.verse_exists);
            auxVerse = realmHelper.findItemVerseByText(srtVerse);
        }

        if (existByTitle && existByVerseText) {
            msg.setText(getString(R.string.title_and_verse_exists));
            msg.setTextSize(14);
            auxVerse = realmHelper.findItemVerseByTitle(srtTitle);
        }

        if (auxVerse != null) {
            currentItemVerse.setId(auxVerse.getId());
        }

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
        dialogView.setAnimation(anim);
        dialog.setContentView(dialogView);

        /*onClick on dialog cancel button*/
        Button cancelBtn = dialog.findViewById(R.id.cancel_dialog_button);
        cancelBtn.setOnClickListener(v -> {
            if (isVisible())
                SuperUtil.vibrate(requireActivity());
            if (dialog.isShowing())
                dialog.dismiss();
        });

        /*onClick on dialog override button*/
        Button editBtn = dialog.findViewById(R.id.edit_dialog_button);
        editBtn.setOnClickListener(v -> {
            if (isVisible())
                SuperUtil.vibrate(requireActivity());

            saveIntoDB(currentItemVerse);

            if (isVisible())
                MessagesHelper.showInfoMessage(requireActivity(),
                        getString(R.string.saved));

            loadMemorizingView();

            if (dialog.isShowing())
                dialog.dismiss();
        });

        dialog.show();
    }

    private void showPickerCalendarsDialog() {
        Dialog dialog = new Dialog(requireContext(), R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView =
                inflater.inflate(R.layout.dialog_view_calendar_choice, null, false);
        ListView listView = dialogView.findViewById(R.id.list_view_calendars);

        Animation anim = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up);
        dialogView.setAnimation(anim);
        dialog.setContentView(dialogView);

        listView.setAdapter(new ArrayAdapter<>(requireContext(),
                R.layout.item_list_calendar,
                R.id.tv_calendar, getCalendarsNameArray()));

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (isVisible())
                SuperUtil.vibrate(requireActivity());
            calendarName = getCalendarsNameArray()[position];
            calendarID = getCalendarsIdArray()[position];

            if (dialog.isShowing())
                dialog.dismiss();

            handleShowPickerDateTimeDialog();
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
        assert hashCalendars != null;
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
        assert hashCalendars != null;
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
