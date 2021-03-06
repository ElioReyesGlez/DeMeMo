package com.erg.memorized.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.erg.memorized.R;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.helpers.RealmHelper;
import com.erg.memorized.helpers.ScoreHelper;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.helpers.TimeHelper;
import com.erg.memorized.model.ItemUser;
import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.model.LeaderboardItem;
import com.erg.memorized.util.Constants;
import com.erg.memorized.util.SuperUtil;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.erg.memorized.util.Constants.EXCLAMATION_MARK_CHAR_DOWN;
import static com.erg.memorized.util.Constants.LAST_SYNC;
import static com.erg.memorized.util.Constants.LEADER_BOARD_COLUMN_IMG;
import static com.erg.memorized.util.Constants.LEADER_BOARD_COLUMN_IS_PREMIUM;
import static com.erg.memorized.util.Constants.LEADER_BOARD_COLUMN_NAME;
import static com.erg.memorized.util.Constants.LEADER_BOARD_COLUMN_SCORE;
import static com.erg.memorized.util.Constants.LEADER_BOARD_FIRE_BASE_REFERENCE;
import static com.erg.memorized.util.Constants.SPACE;
import static com.erg.memorized.util.Constants.USER_COLUMN_VERSES;
import static com.erg.memorized.util.Constants.USER_COLUMN_VERSES_SCORE;
import static com.erg.memorized.util.Constants.USER_FIRE_BASE_REFERENCE;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "SettingsFragment";
    private View rootView;
    private ViewGroup container;
    private ShapeableImageView ivImageProfile;
    private ImageView ivRightArrowUser;
    private ImageView ivBadge;
    private ImageView ivSyncNeeded;
    private TextView tvUserName;
    private TextView tvUser;
    private TextView tvVerseCont;
    private TextView tvUserScore;
    private TextView tvLastUploadDate;
    private Button btnSignUp;
    private RelativeLayout rlUserSettings;
    private RelativeLayout rlSync;
    private ProgressBar progressBar, syncProgress;

    private SharedPreferencesHelper spHelper;
    private boolean isLoginAction = false;
    private boolean isSigningAction;
    private boolean isDownloadNeeded;
    private boolean isUploadNeeded;
    private boolean isLeaderBoardSyncNeeded;
    private ItemUser currentUser;
    private RealmHelper realmHelper;
    private FirebaseAuth fAuth;

    private ArrayList<ItemVerse> cloudVerses;
    private ArrayList<ItemVerse> localVerses;
    private Animation animScaleUp, animScaleDown;
    private Snackbar snackbarSyncAlert = null;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(boolean isSigningAction) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.SIGN_ACTION_KEY, isSigningAction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spHelper = new SharedPreferencesHelper(requireContext());
        fAuth = FirebaseAuth.getInstance();
        realmHelper = new RealmHelper();

        Bundle args = getArguments();
        if (args != null) {
            isSigningAction = args.getBoolean(Constants.SIGN_ACTION_KEY, false);
        }

        animScaleUp = AnimationUtils.loadAnimation(requireContext(),
                R.anim.scale_up);
        animScaleDown = AnimationUtils.loadAnimation(requireContext(),
                R.anim.scale_down);

        localVerses = realmHelper.getSavedVerses();
        cloudVerses = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        this.container = container;
        setUpView();

        return rootView;
    }

    private void setUpView() {

        ivImageProfile = rootView.findViewById(R.id.iv_profile);
        ivBadge = rootView.findViewById(R.id.iv_premium_badge);
        ivRightArrowUser = rootView.findViewById(R.id.iv_right_arrow_user);
        tvUserName = rootView.findViewById(R.id.tv_username);
        tvUser = rootView.findViewById(R.id.tv_user);
        btnSignUp = rootView.findViewById(R.id.bt_login);
        rlUserSettings = rootView.findViewById(R.id.rl_user_settings);
        RelativeLayout rlLeaderBoard = rootView.findViewById(R.id.rl_leader_board);
        RelativeLayout rlAbout = rootView.findViewById(R.id.rl_about);
        RelativeLayout rlGeneralSettings = rootView.findViewById(R.id.rl_general_settings);
        progressBar = rootView.findViewById(R.id.progressBar);
        syncProgress = rootView.findViewById(R.id.sync_progress_circular);
        tvVerseCont = rootView.findViewById(R.id.tv_verses_cont);
        tvUserScore = rootView.findViewById(R.id.tv_user_score);
        tvLastUploadDate = rootView.findViewById(R.id.tv_upload_date);
        rlSync = rootView.findViewById(R.id.rl_sync);
        ivSyncNeeded = rootView.findViewById(R.id.iv_sync_needed);
        setUpSnackBarSyncNeededAlert();

        rlSync.setOnClickListener(this);
        ivSyncNeeded.setOnClickListener(this);
        rlLeaderBoard.setOnClickListener(this);
        rlAbout.setOnClickListener(this);
        rlGeneralSettings.setOnClickListener(this);

        if (spHelper.getUserLoginStatus()) {
            currentUser = realmHelper.getUser();
            isLoginAction = false;
            if (currentUser != null) {
                new AsyncTaskViewLoader(currentUser).execute();
                startDataListener();
                startLeaderBoarDataListener();
            } else {
                spHelper.setUserLoginState(false);
                activateSignUpButton();
            }
        } else {
            activateSignUpButton();
        }
    }

    private void activateSignUpButton() {
        SuperUtil.showView(null, btnSignUp);
        rlUserSettings.setFocusable(true);
        rlUserSettings.setClickable(true);
        btnSignUp.setOnClickListener(this);
    }

    private void deactivateLoginButton(@NotNull View dialogView) {
        Button loginBtn = dialogView.findViewById(R.id.btn_login);
        loginBtn.setBackgroundResource(R.drawable.background_gray);
        loginBtn.setClickable(false);
        loginBtn.setFocusable(false);
    }

    private void activateUserSettings() {
        rlUserSettings.setBackgroundResource(R.drawable.selector_light_green);
        rlUserSettings.setOnClickListener(this);
        rlUserSettings.setFocusable(true);
        rlUserSettings.setClickable(true);
        if (isLoginAction) {
            rlUserSettings.startAnimation(animScaleUp);
        }
    }

    @Override
    public void onClick(View v) {
        SuperUtil.vibrate(requireContext());
        switch (v.getId()) {
            case R.id.rl_user_settings:
                if (spHelper.getUserLoginStatus()) {
                    SuperUtil.loadView(requireActivity(),
                            UserInfoFragment.newInstance(currentUser),
                            UserInfoFragment.TAG, true);
                }
                break;
            case R.id.bt_login:
                showLoginDialog();
                break;
            case R.id.rl_sync:
                if (spHelper.getUserLoginStatus()) {
                    if (spHelper.getEmailVerifiedStatus()) {
                        if (isDownloadNeeded) {
                            download();
                            Log.d(TAG, "onClick: Download");
                        }
                        if (isUploadNeeded) {
                            upload();
                            Log.d(TAG, "onClick: Upload");
                        } else if (isLeaderBoardSyncNeeded) {
                            upload();
                            Log.d(TAG, "onClick: LeaderBoardSync Upload");
                        }
                        if (!isUploadNeeded && !isDownloadNeeded && !isLeaderBoardSyncNeeded) {
                            MessagesHelper.showInfoMessage(requireActivity(),
                                    getString(R.string.sync_not_needed));
                        }
                    } else {
                        MessagesHelper.showInfoMessageWarning(requireActivity(),
                                getString(R.string.email_is_not_verified_msg));
                    }
                } else {
                    MessagesHelper.showInfoMessageWarning(requireActivity(),
                            getString(R.string.login_needed));
                }
                break;
            case R.id.iv_sync_needed:
                if (isVisible())
                    MessagesHelper.showInfoMessageWarning(requireActivity(),
                            getString(R.string.changes_to_be_sync));
                break;
            case R.id.rl_leader_board:
                if (spHelper.getUserLoginStatus()) {
                    if (spHelper.getEmailVerifiedStatus()) {
                        if (currentUser.isPremium()) {
                            SuperUtil.loadView(requireActivity(),
                                    LeaderBoardFragment.newInstance(),
                                    LeaderBoardFragment.TAG, true);
                        } else {
                            SuperUtil.loadView(requireActivity(),
                                    AdMobFragment.newInstance(currentUser, true),
                                    AdMobFragment.TAG, true);
                        }
                        Log.d(TAG, "onClick: leader_board: " + "Premium: "
                                + currentUser.isPremium());
                    } else {
                        if (isVisible())
                            MessagesHelper.showInfoMessageWarning(requireActivity(),
                                    getString(R.string.email_is_not_verified_msg));
                    }
                } else {
                    if (isVisible())
                        MessagesHelper.showInfoMessageWarning(requireActivity(),
                                getString(R.string.login_needed));
                }
                break;
            case R.id.rl_about:
                SuperUtil.loadView(requireActivity(), AboutFragment.newInstance(),
                        AboutFragment.TAG, true);
                break;
            case R.id.rl_general_settings:
                SuperUtil.loadView(requireActivity(),
                        GeneralSettingsFragment.newInstance(),
                        GeneralSettingsFragment.TAG, true);
                break;
        }
    }

    private void download() {
        DatabaseReference fReference = FirebaseDatabase.getInstance()
                .getReference(USER_FIRE_BASE_REFERENCE)
                .child(currentUser.getId());

        SuperUtil.showView(null, syncProgress);
        fAuth.signInWithEmailAndPassword(currentUser.getEmail(), currentUser.getPass())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                currentUser = new ItemUser();
                                currentUser.setId(Objects.requireNonNull(fAuth.getCurrentUser())
                                        .getUid());
                                currentUser.setEmail(dataSnapshot.child(Constants.USER_COLUMN_EMAIL)
                                        .getValue(String.class));
                                currentUser.setMobile(dataSnapshot.child(Constants.USER_COLUMN_MOBILE)
                                        .getValue(String.class));
                                currentUser.setName(dataSnapshot.child(Constants.USER_COLUMN_NAME)
                                        .getValue(String.class));
                                currentUser.setPass(dataSnapshot.child(Constants.USER_COLUMN_PASS)
                                        .getValue(String.class));
                                currentUser.setImg(dataSnapshot.child(Constants.USER_COLUMN_IMG)
                                        .getValue(String.class));
                                currentUser.setPremium(Boolean.parseBoolean(
                                        dataSnapshot.child(Constants.USER_COLUMN_PREMIUM_STATUS)
                                                .getValue(String.class)));

                                GenericTypeIndicator<HashMap<String, String>> t =
                                        new GenericTypeIndicator<HashMap<String, String>>() {
                                        };
                                localVerses = new ArrayList<>();

                                for (DataSnapshot snap :
                                        dataSnapshot.child(USER_COLUMN_VERSES).getChildren()) {
                                    localVerses.add(ItemVerse.getVerseFromHasMap(
                                            Objects.requireNonNull(snap.getValue(t))));
                                }

                                if (currentUser != null) {
                                    realmHelper.addUserToDB(currentUser);
                                    if (!localVerses.isEmpty()) {
                                        realmHelper.addVerseListToDB(localVerses);
                                    }
                                    isLoginAction = false;
                                    new AsyncTaskViewLoader(currentUser).execute();

                                    spHelper.setLastSync(LAST_SYNC + currentUser.getId(),
                                            System.currentTimeMillis());
                                }
                                SuperUtil.hideViewInvisibleWay(null, syncProgress);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                SuperUtil.hideViewInvisibleWay(null, syncProgress);
                                Log.e(TAG, "synchronize onCancelled: " +
                                        databaseError.getMessage());
                            }
                        });

                    } else {
                        SuperUtil.hideViewInvisibleWay(null, syncProgress);
                        if (task.getException() instanceof FirebaseNetworkException) {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarning(requireActivity(),
                                        getString(R.string.network_error));
                        } else {
                            if (isVisible())
                                MessagesHelper.showInfoMessageError(requireActivity(),
                                        getString(R.string.failed_synchronizing));
                        }
                    }
                });
    }

    private void upload() {
        DatabaseReference fReference = FirebaseDatabase.getInstance()
                .getReference(USER_FIRE_BASE_REFERENCE)
                .child(currentUser.getId())
                .child(USER_COLUMN_VERSES);

        SuperUtil.showView(null, syncProgress);
        fAuth.signInWithEmailAndPassword(currentUser.getEmail(), currentUser.getPass())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fReference.setValue(
                                SuperUtil.getVersesIntoHasMapList(realmHelper.getSavedVerses()))
                                .addOnCompleteListener(task_2 -> {
                                    if (task_2.isSuccessful()) {
                                        spHelper.setLastSync(LAST_SYNC + currentUser.getId(),
                                                System.currentTimeMillis());

                                        isLoginAction = false;
                                        new AsyncTaskViewLoader(currentUser).execute();

                                        uploadScoreOnLeaderBoardTable();
                                    }
                                });
                    } else {

                        //hiding progress cirlce
                        SuperUtil.hideViewInvisibleWay(null, syncProgress);

                        if (task.getException() instanceof FirebaseNetworkException) {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarning(requireActivity(),
                                        getString(R.string.network_error));
                            Log.d(TAG, "upload: " + task.getException().getMessage());
                        } else {
                            if (isVisible())
                                MessagesHelper.showInfoMessageError(requireActivity(),
                                        getString(R.string.failed_synchronizing));
                        }
                    }
                });
    }

    private void uploadScoreOnLeaderBoardTable() {
        DatabaseReference fReferenceLeaderBoard = FirebaseDatabase.getInstance()
                .getReference(LEADER_BOARD_FIRE_BASE_REFERENCE)
                .child(currentUser.getId());

        SuperUtil.showView(null, syncProgress);
        fReferenceLeaderBoard.setValue(currentUser.getScoreInfoIntoHasMap())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Leader board uploadScore:Success ");
                        loadUserDashBoard();
                    } else {
                        Log.e(TAG, "uploadScore: " + task.getException());
                    }

                    //hiding progress cirlce
                    SuperUtil.hideViewInvisibleWay(null, syncProgress);
                });

        updateUserScoreOnUserTable();
    }

    private void updateUserScoreOnUserTable() {
        DatabaseReference fReferenceUser = FirebaseDatabase.getInstance()
                .getReference(USER_FIRE_BASE_REFERENCE)
                .child(currentUser.getId())
                .child(USER_COLUMN_VERSES_SCORE);

        SuperUtil.showView(null, syncProgress);
        fReferenceUser.setValue(String.valueOf(ScoreHelper.round(currentUser.getScore())))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        isLeaderBoardSyncNeeded = false;
                        loadUserDashBoard();
                        Log.d(TAG, "User Score uploadScore: Success ");

                        if (isVisible()) {
                            MessagesHelper.showInfoMessage(requireActivity(),
                                    getString(R.string.sync_success));
                        }
                    } else {
                        Log.e(TAG, "User uploadScore: " + task.getException());
                    }
                    //hiding progress cirlce
                    SuperUtil.hideViewInvisibleWay(null, syncProgress);
                });
    }


    private void showLoginDialog() {

        Dialog dialog = new Dialog(requireActivity(), R.style.alert_dialog);
        dialog.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_login_view, container, false);

        TextInputLayout tilMail = dialogView.findViewById(R.id.til_dialog_email);
        TextInputLayout tilPass = dialogView.findViewById(R.id.til_dialog_pass);

        TextInputEditText etMail = dialogView.findViewById(R.id.dialog_input_email);
        TextInputEditText etPass = dialogView.findViewById(R.id.dialog_input_password);
        dialog.setContentView(dialogView);

        //onClick on dialog cancel button
        Button cancelBtn = dialog.findViewById(R.id.cancel_dialog_button);
        cancelBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            if (dialog.isShowing())
                dialog.dismiss();
        });

        //onClick on login button
        Button loginBtn = dialogView.findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(Objects.requireNonNull(getContext()));

            String email = Objects.requireNonNull(etMail.getText()).toString();
            String password = Objects.requireNonNull(etPass.getText()).toString();

            if (validate(tilMail, tilPass, email, password)) {
                login(email, password, dialog, dialogView);
            }
        });

        //onClick on link  button
        TextView linkToSingUp = dialogView.findViewById(R.id.link_sign_up);
        linkToSingUp.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            SignUpFragment signUpFragment = SignUpFragment.newInstance();
            SuperUtil.loadView(requireActivity(), signUpFragment,
                    SignUpFragment.TAG, true);
            if (dialog.isShowing())
                dialog.dismiss();
        });

        TextView linkToRestorePass = dialogView.findViewById(R.id.link_restore);
        linkToRestorePass.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            showRestoresEmailSentDialog();
        });

        dialog.show();
        dialogView.startAnimation(animScaleUp);
    }

    private void showRestoresEmailSentDialog() {

        final Dialog dialogRestoresEmailSentDialog = new Dialog(requireActivity(), R.style.alert_dialog);
        dialogRestoresEmailSentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogRestoresEmailSentDialog.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_restor_pass_via_email_view, null, false);
        TextInputEditText editTextEmail = dialogView.findViewById(R.id.dialog_input_email);
        TextInputLayout tilEmail = dialogView.findViewById(R.id.til_dialog_email);
        dialogRestoresEmailSentDialog.setContentView(dialogView);

        /*onClick on dialog cancel button*/
        Button cancelBtn = dialogRestoresEmailSentDialog.findViewById(R.id.cancel_dialog_button);
        cancelBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            if (dialogRestoresEmailSentDialog.isShowing())
                dialogRestoresEmailSentDialog.dismiss();
        });

        /*onClick on dialog send button*/
        Button editBtn = dialogRestoresEmailSentDialog.findViewById(R.id.send_dialog_button);
        editBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            String email = Objects.requireNonNull(editTextEmail.getText()).toString();
            if (validateEmail(email, tilEmail)) {
                sendRestorePassByEmail(email, dialogRestoresEmailSentDialog, dialogView);
            }
        });

        dialogRestoresEmailSentDialog.show();
        dialogView.startAnimation(animScaleUp);
    }

    private void sendRestorePassByEmail(String email, Dialog rootDialog, View dialogView) {
        Dialog pgsDialog = SuperUtil.showProgressDialog(getActivity(), container);
        String msg = getString(R.string.email_sent);

        fAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (pgsDialog.isShowing())
                            pgsDialog.dismiss();
                        showInfoMessageOnDialog(msg, dialogView, rootDialog);
                        Log.d(TAG, "sendRestorePassEmail. Email sent.");
                    } else {
                        Log.d(TAG, "sendRestorePassEmail: " + task.getException());
                        if (pgsDialog.isShowing())
                            pgsDialog.dismiss();

                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                                        getString(R.string.invalid_user), dialogView);
                        } else if (task.getException() instanceof FirebaseNetworkException) {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                                        getString(R.string.network_error), dialogView);
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                                        getString(R.string.invalid_credentials), dialogView);
                        }
                    }
                });
    }

    private void showInfoMessageOnDialog(String msg, View dialogView,
                                         Dialog rootDialog) {
        Snackbar snackBar = Snackbar.make(dialogView, msg, Snackbar.LENGTH_SHORT);

        snackBar.setBackgroundTint(requireActivity().getColor(R.color.colorPrimary));
        snackBar.setTextColor(requireActivity().getColor(R.color.dark_gray_btn_bg_color));

        snackBar.setDuration(Snackbar.LENGTH_INDEFINITE);

        snackBar.setAction(getString(R.string.ok), v -> {
            SuperUtil.vibrate(requireContext());
            snackBar.dismiss();
            if (rootDialog.isShowing())
                rootDialog.dismiss();
        });

        snackBar.show();
    }

    private boolean validateEmail(String email, TextInputLayout tilField) {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilField.setError(getString(R.string.error_email));
            valid = false;
        } else {
            tilField.setError(null);
        }

        return valid;
    }

    private void login(String email, String password, Dialog dialog, View dialogView) {
        Dialog pgsDialog = SuperUtil.showProgressDialog(getActivity(), container);

        fAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser fUser = fAuth.getCurrentUser();
                        currentUser = new ItemUser();
                        currentUser.setPass(password);
                        assert fUser != null;
                        DatabaseReference fReference = FirebaseDatabase.getInstance()
                                .getReference(USER_FIRE_BASE_REFERENCE)
                                .child(fUser.getUid());
                        fReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (pgsDialog.isShowing())
                                    pgsDialog.dismiss();
                                if (dialog.isShowing())
                                    dialog.dismiss();

                                currentUser.setId(dataSnapshot.getKey());
                                currentUser.setEmail(dataSnapshot.child(Constants.USER_COLUMN_EMAIL)
                                        .getValue(String.class));
                                currentUser.setMobile(dataSnapshot.child(Constants.USER_COLUMN_MOBILE)
                                        .getValue(String.class));
                                currentUser.setName(dataSnapshot.child(Constants.USER_COLUMN_NAME)
                                        .getValue(String.class));
                                currentUser.setImg(dataSnapshot.child(Constants.USER_COLUMN_IMG)
                                        .getValue(String.class));
                                currentUser.setPremium(Boolean.parseBoolean(
                                        dataSnapshot.child(Constants.USER_COLUMN_PREMIUM_STATUS)
                                                .getValue(String.class)));

                                if (currentUser != null && currentUser.getEmail() != null) {
                                    spHelper.setUserLoginState(true);
                                    realmHelper.addUserToDB(currentUser);
                                    isLoginAction = true;
                                    new AsyncTaskViewLoader(currentUser).execute();
                                    startDataListener();
                                    startLeaderBoarDataListener();
                                } else {
                                    if (isVisible())
                                        MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                                                getString(R.string.invalid_user),
                                                dialogView);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e(TAG, "login onCancelled: " + databaseError.getMessage());
                            }
                        });
                    } else {
                        Log.e(TAG, "login: " + task.getException());
                        if (pgsDialog.isShowing())
                            pgsDialog.dismiss();

                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                                        getString(R.string.invalid_user),
                                        dialogView);
                        } else if (task.getException() instanceof FirebaseNetworkException) {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                                        getString(R.string.network_error),
                                        dialogView);
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                                        getString(R.string.invalid_credentials), dialogView);
                        } else if (task.getException() instanceof FirebaseTooManyRequestsException) {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                                        getString(R.string.too_many_requests),
                                        dialogView);
                            deactivateLoginButton(dialogView);
                        } else if (task.getException() instanceof FirebaseException) {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                                        getString(R.string.something_bad),
                                        dialogView);
                        }
                    }
                });
    }

    @SuppressLint("StaticFieldLeak")
    public class AsyncTaskViewLoader extends AsyncTask<Void, Void, Void> {

        private final ItemUser currentUser;
        private Bitmap bitmapFromBase64;

        AsyncTaskViewLoader(ItemUser currentUser) {
            this.currentUser = currentUser;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SuperUtil.showView(null, progressBar);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (currentUser.getImg() != null && !currentUser.getImg().equals(Constants.DEFAULT))
                bitmapFromBase64 = SuperUtil.decodeBase64ToBitmap(currentUser.getImg());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            SuperUtil.hideView(null, progressBar);
            showUserInfo(currentUser, bitmapFromBase64);

            if (isLoginAction) {
                if (isVisible()) {
                    if (isLoginAction) {
                        String welcomeMsg = getString(R.string.welcome)
                                + SPACE + this.currentUser.getName()
                                + EXCLAMATION_MARK_CHAR_DOWN;
                        MessagesHelper.showInfoMessage(requireActivity(), welcomeMsg);
                    }
                }
            }
        }
    }

    private void loadUserDashBoard() {
        tvVerseCont.setText(String.valueOf(realmHelper.getSavedVerses().size()));

        long lastUploadDate = spHelper.getLastSyncDate(LAST_SYNC + currentUser.getId());
        if (lastUploadDate != 0)
            tvLastUploadDate.setText(TimeHelper.dateFormatterShort(lastUploadDate));

        float score = ScoreHelper.getUserScoreByVersesList(localVerses);
        tvUserScore.setText(String.valueOf(ScoreHelper.round(score)));
        currentUser.setScore(score);

        if (isUploadNeeded || isDownloadNeeded || isLeaderBoardSyncNeeded) {
            SuperUtil.showView(animScaleUp, ivSyncNeeded);
            rlSync.setBackgroundResource(R.drawable.selector_light_green);
        } else {
            SuperUtil.hideViewInvisibleWay(animScaleDown, ivSyncNeeded);
            rlSync.setBackgroundResource(R.drawable.selector_gray);
            if (snackbarSyncAlert != null && snackbarSyncAlert.isShown()) {
                snackbarSyncAlert.dismiss();
            }
        }
    }

    private void handleShowSyncAlert() {
        new Handler().postDelayed(this::checkIfShowSyncAlert, 1700);
    }

    private void startDataListener() {
        DatabaseReference fReference = FirebaseDatabase.getInstance()
                .getReference(USER_FIRE_BASE_REFERENCE)
                .child(currentUser.getId())
                .child(USER_COLUMN_VERSES);

        SuperUtil.showView(null, syncProgress);
        fReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                GenericTypeIndicator<HashMap<String, String>> t =
                        new GenericTypeIndicator<HashMap<String, String>>() {
                        };
                cloudVerses = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    cloudVerses.add(ItemVerse.getVerseFromHasMap(
                            Objects.requireNonNull(snap.getValue(t))));
                }

                localVerses = realmHelper.getSavedVerses();
                checkIfEmailIsVerified();
                checkIfDownloadIsNeeded();
                checkIfUploadIsNeeded();
                loadUserDashBoard();
                handleShowSyncAlert();

                SuperUtil.hideViewInvisibleWay(null, syncProgress);
                Log.d(TAG, "onDataChange: Cloud Verses: " + cloudVerses.toString());
                Log.d(TAG, "onDataChange: Local Verses: " + localVerses.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                SuperUtil.hideViewInvisibleWay(null, syncProgress);
                Log.e(TAG, "startDataListener onCancelled: DatabaseError: "
                        + databaseError.getMessage());
            }
        });
    }

    private void startLeaderBoarDataListener() {
        DatabaseReference fReference = FirebaseDatabase.getInstance()
                .getReference(LEADER_BOARD_FIRE_BASE_REFERENCE)
                .child(currentUser.getId());

        SuperUtil.showView(null, syncProgress);
        fReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    LeaderboardItem leaderboardItem = new LeaderboardItem();
                    leaderboardItem.setId(dataSnapshot.getKey());
                    leaderboardItem.setImg(
                            dataSnapshot.child(LEADER_BOARD_COLUMN_IMG).getValue(String.class));
                    leaderboardItem.setName(dataSnapshot.child(LEADER_BOARD_COLUMN_NAME)
                            .getValue(String.class));
                    leaderboardItem.setPremium(Boolean.parseBoolean(
                            dataSnapshot.child(LEADER_BOARD_COLUMN_IS_PREMIUM).getValue(String.class)));
                    leaderboardItem.setScore(Float.parseFloat(
                            Objects.requireNonNull(dataSnapshot
                                    .child(LEADER_BOARD_COLUMN_SCORE).getValue(String.class))));

                    isLeaderBoardSyncNeeded = !leaderboardItem.getName().equals(currentUser.getName())
                            || !leaderboardItem.getImg().equals(currentUser.getImg())
                            || leaderboardItem.getScore() != currentUser.getScore()
                            || leaderboardItem.isPremium() != currentUser.isPremium();

                    loadUserDashBoard();
                    handleShowSyncAlert();

                    Log.d(TAG, "onDataChange: LeaderBoardItem: " + leaderboardItem.toString());
                } else {
                    if (currentUser.getScore() != 0) {
                        isLeaderBoardSyncNeeded = true;
                        loadUserDashBoard();
                        handleShowSyncAlert();
                    }
                    Log.d(TAG, "onDataChange: dataSnapshot DO NOT EXISTS");
                }

                Log.d(TAG, "onDataChange: LeaderBoarDataListener DataSnap: "
                        + dataSnapshot.toString());
                SuperUtil.hideViewInvisibleWay(null, syncProgress);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                SuperUtil.hideViewInvisibleWay(null, syncProgress);
                Log.e(TAG, "startLeaderBoarDataListener onCancelled: DatabaseError: "
                        + databaseError.getMessage());
            }
        });
    }

    private void checkIfEmailIsVerified() {
        if (fAuth.getCurrentUser() != null) {
            boolean isVerified = fAuth.getCurrentUser().isEmailVerified();
            spHelper.setEmailVerifiedStatus(isVerified);
        }
    }

    private void checkIfUploadIsNeeded() {
        isDownloadNeeded = !SuperUtil.containsAll(cloudVerses, localVerses) && !cloudVerses.isEmpty();
    }

    private void checkIfDownloadIsNeeded() {
        isUploadNeeded = !SuperUtil.containsAll(localVerses, cloudVerses);
    }

    private void checkIfShowSyncAlert() {
        if (spHelper.getUserLoginStatus()) {
            if (isUploadNeeded || isDownloadNeeded || isLeaderBoardSyncNeeded) {
                if (snackbarSyncAlert != null && !spHelper.isSyncAlertShowedAlready()
                        && !snackbarSyncAlert.isShown()) {
                    snackbarSyncAlert.show();
                }
            } else {
                if (snackbarSyncAlert != null && snackbarSyncAlert.isShown()) {
                    snackbarSyncAlert.dismiss();
                }
            }
        }
    }

    private void showUserInfo(ItemUser itemUser, Bitmap bitmapFromBase64) {

        SuperUtil.hideView(null, btnSignUp);
        SuperUtil.showView(null, ivImageProfile);
        SuperUtil.showView(null, tvUserName);
        SuperUtil.showView(null, tvUser);
        SuperUtil.showView(null, ivRightArrowUser);

        if (currentUser.isPremium()) {
            SuperUtil.showView(null, ivBadge);
        } else {
            SuperUtil.hideViewInvisibleWay(null, ivBadge);
        }

        if (itemUser.getImg() != null && itemUser.getImg().equals(Constants.DEFAULT)) {
            ivImageProfile.setImageResource(R.drawable.ic_user_profile);
        } else {
            ivImageProfile.setImageBitmap(bitmapFromBase64);
        }

        tvUserName.setText(itemUser.getName());
        tvUser.setText(itemUser.getEmail());
        activateUserSettings();

        if (spHelper.getUserLoginStatus()) {
            checkIfEmailIsVerified();
            checkIfDownloadIsNeeded();
            checkIfUploadIsNeeded();
            loadUserDashBoard();
        }
    }

    private boolean validate(TextInputLayout tilMail, TextInputLayout tilPass,
                             String email, String password) {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilMail.setError(getString(R.string.error_email));
            valid = false;
        } else {
            tilMail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            tilPass.setError(getString(R.string.error_pass));
            valid = false;
        } else {
            tilPass.setError(null);
        }
        return valid;
    }

    private void setUpSnackBarSyncNeededAlert() {
        snackbarSyncAlert = Snackbar.make(requireActivity().findViewById(R.id.placeSnackBar)
                , getString(R.string.changes_to_be_sync), Snackbar.LENGTH_SHORT);
        snackbarSyncAlert.setBackgroundTint(requireContext().getColor(R.color.yellow_bg_color));
        snackbarSyncAlert.setTextColor(requireContext().getColor(R.color.dark_gray_btn_bg_color));
        snackbarSyncAlert.setDuration(Snackbar.LENGTH_INDEFINITE);
        snackbarSyncAlert.setAction(requireContext().getString(R.string.ok), v -> {
            if (isVisible()) {
                SuperUtil.vibrateMin(requireActivity());
            }
            spHelper.setSyncAlertAlreadyShowedFlag(true);
            snackbarSyncAlert.dismiss();
        });
    }

    private void setUpAppLanguage() {
        String[] arrayLanguagesCodes = getResources().getStringArray(R.array.languages_codes);
        int langPos = spHelper.getLanguagePos();
        TimeHelper.setLocale(requireActivity(), arrayLanguagesCodes[langPos]);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpAppLanguage();
    }

    @Override
    public void onStart() {
        super.onStart();
        SuperUtil.showMeoBottomBar(requireActivity(), animScaleUp);
        if (isSigningAction) {
            rlUserSettings.startAnimation(animScaleUp);
        }
    }
}
