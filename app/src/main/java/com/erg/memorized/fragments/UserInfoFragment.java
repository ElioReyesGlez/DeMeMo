package com.erg.memorized.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.erg.memorized.R;
import com.erg.memorized.helpers.CircleTransform;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.helpers.RealmHelper;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.model.ItemUser;
import com.erg.memorized.util.SuperUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.erg.memorized.util.Constants.DEFAULT;
import static com.erg.memorized.util.Constants.LEADER_BOARD_FIRE_BASE_REFERENCE;
import static com.erg.memorized.util.Constants.USER_COLUMN_VERSES;
import static com.erg.memorized.util.Constants.USER_FIRE_BASE_REFERENCE;

public class UserInfoFragment extends Fragment implements View.OnClickListener {

    public static String TAG = "UserInfoFragment";

    private SharedPreferencesHelper spHelper;

    private ImageView ivAvatar;
    private TextView tvEmail;
    private TextView tvName;
    private TextView tvMobile;
    private TextView tvPass;
    private TextView tvSave;

    private RelativeLayout rlMsgEmailNotVerified;
    private View rootView;
    private ViewGroup container;

    private ItemUser currentUser;
    private ItemUser updatedCurrentUser;
    private Uri imgUri;
    private String base64Image = "default";
    private boolean isImgPicked = false;
    private boolean isImgPickingAction = false;

    private CropImageView cropImageView;

    private FirebaseAuth fAuth;
    private DatabaseReference fReference;

    private String name, email, mobile, password;

    private Animation animScaleUp, animScaleDown, animSlideInFromRight;


    private RealmHelper realmHelper;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    public UserInfoFragment(ItemUser itemUser) {
        currentUser = itemUser;
    }

    public static UserInfoFragment newInstance(ItemUser user) {
        return new UserInfoFragment(user);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spHelper = new SharedPreferencesHelper(requireContext());
        realmHelper =  new RealmHelper(requireContext());

        fAuth = FirebaseAuth.getInstance();

        name = currentUser.getName();
        email = currentUser.getEmail();
        mobile = currentUser.getMobile();
        password = currentUser.getPass();
        base64Image = currentUser.getImg();

        animScaleUp = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
        animScaleDown = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);
        animSlideInFromRight = AnimationUtils.loadAnimation(getContext(),
                R.anim.fab_slide_in_from_right);
    }

    private void checkIfEmailIsVerified() {
        if (fAuth.getCurrentUser() != null && currentUser != null) {

            if (!spHelper.getEmailVerifiedStatus()) {
                SuperUtil.showView(animScaleUp, rlMsgEmailNotVerified);
            } else {
                SuperUtil.hideView(animScaleDown, rlMsgEmailNotVerified);
            }

            Log.d(TAG, "checkIfEmailIsVerified: " + spHelper.getEmailVerifiedStatus());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
        this.container = container;

        setUpView();
        loadUserInfo();

        return rootView;
    }

    private void setUpView() {
        ivAvatar = rootView.findViewById(R.id.iv_profile);
        tvName = rootView.findViewById(R.id.tv_name);
        tvEmail = rootView.findViewById(R.id.tv_email);
        tvMobile = rootView.findViewById(R.id.tv_mobile);
        tvPass = rootView.findViewById(R.id.tv_pass);
        LinearLayout llLogOut = rootView.findViewById(R.id.ll_log_out);
        LinearLayout llRemoveAccount = rootView.findViewById(R.id.ll_remove_account);
        tvSave = rootView.findViewById(R.id.tv_save);

        RelativeLayout rlName = rootView.findViewById(R.id.rl_name);
        RelativeLayout rlEmail = rootView.findViewById(R.id.rl_email);
        RelativeLayout rlPass = rootView.findViewById(R.id.rl_pass);
        RelativeLayout rlMobile = rootView.findViewById(R.id.rl_mobile);
        rlMsgEmailNotVerified = rootView.findViewById(R.id.rl_msg_not_verified);


        ivAvatar.setOnClickListener(this);
        rlName.setOnClickListener(this);
        rlEmail.setOnClickListener(this);
        rlPass.setOnClickListener(this);
        rlMobile.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        llLogOut.setOnClickListener(this);
        llRemoveAccount.setOnClickListener(this);
        rlMsgEmailNotVerified.setOnClickListener(this);
    }

    private void loadUserInfo() {
        AsyncTaskLoader loader = new AsyncTaskLoader();
        loader.execute();
    }

    @Override
    public void onClick(View v) {
        SuperUtil.vibrate(requireContext());
        switch (v.getId()) {
            case R.id.ll_log_out:
                SuperUtil.vibrate(requireContext());
                logOut();
                break;
            case R.id.ll_remove_account:
                showDeleteConfirmationDialog();
                break;
            case R.id.iv_profile:
                SuperUtil.vibrate(requireContext());
                CropImage.activity()
                        .start(requireContext(), this);
                break;
            case R.id.rl_name:
                showUpdateDataDialog(R.layout.dialog_update_name_view);
                break;
            case R.id.rl_email:
                showUpdateDataDialog(R.layout.dialog_update_email_view);
                break;
            case R.id.rl_mobile:
                showUpdateDataDialog(R.layout.dialog_update_mobile_view);
                break;
            case R.id.rl_pass:
                showRestoresEmailSentDialog();
                break;
            case R.id.rl_msg_not_verified:
                showValidationEmailDialog();
                break;
            case R.id.tv_save:
                updatedCurrentUser = new ItemUser();
                updatedCurrentUser.setId(currentUser.getId());
                updatedCurrentUser.setName(name);
                updatedCurrentUser.setEmail(email);
                updatedCurrentUser.setMobile(mobile);
                updatedCurrentUser.setPass(password);
                updatedCurrentUser.setImg(base64Image);
                updatedCurrentUser.setVerses(currentUser.getVerses());
                updatedCurrentUser.setPremium(currentUser.isPremium());
                saveOnFirebaseDB(getIfCurrentUserUpdated());
                break;
        }
    }

    private void logOut() {
        RealmHelper realmHelper = new RealmHelper(getContext());
        realmHelper.deleteUserFromRealmDataBase(currentUser);
        spHelper.setUserLoginState(false);
        SuperUtil.signOutUser();
        if (SuperUtil.isFragmentOnBackStack(requireActivity(), TAG))
            SuperUtil.removeViewByTag(requireActivity(), TAG, true);
    }

    private void deleteUser(Dialog dialog, View dialogView) {
        fAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deleteUserFromLeaderBoard();
                        deleteUserInfoOnFirebaseDB(dialog, dialogView);
                    } else {
                        if (task.getException() instanceof FirebaseNetworkException) {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarningOnDialog(
                                        Objects.requireNonNull(requireActivity()),
                                        getString(R.string.network_error),
                                        dialogView);
                            Log.d(TAG, "deleteUser: " + task.getException().getMessage());
                        } else {
                            if (isVisible())
                                MessagesHelper.showInfoMessageErrorOnDialog(requireActivity(),
                                        getString(R.string.failed_deleting), dialogView);
                        }
                    }
                });
    }

    private void deleteUserFromLeaderBoard() {
        fReference = FirebaseDatabase.getInstance()
                .getReference(LEADER_BOARD_FIRE_BASE_REFERENCE)
                .child(currentUser.getId());
        fReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "deleteUserFromLeaderBoard: " + "Success deleting");
            } else {
                Log.d(TAG, "deleteUserFromLeaderBoard: FAILED DELETING ERROR: "
                        + task.getException());
            }
        });

    }

    private void showDeleteConfirmationDialog() {

        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()), R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_info_delete_confirmation, null, false);
        TextView msg = dialogView.findViewById(R.id.text_dialog);
        msg.setText(R.string.delete_confirmation_msg);
        dialog.setContentView(dialogView);

        /*onClick on dialog cancel button*/
        Button cancelBtn = dialog.findViewById(R.id.cancel_dialog_button);
        cancelBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            if (dialog.isShowing())
                dialog.dismiss();
        });

        /*onClick on dialog delete button*/
        Button editBtn = dialog.findViewById(R.id.delete_dialog_button);
        editBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            deleteUser(dialog, dialogView);
        });
        dialog.show();
        dialogView.startAnimation(animScaleUp);
    }

    private void showSuccessDeleteMsgOnDialog(Dialog dialog, View dialogView) {
        Snackbar snackBar = Snackbar.make(dialogView,
                getString(R.string.account_successfully_deleted), Snackbar.LENGTH_SHORT);

        snackBar.setBackgroundTint(getContext().getColor(R.color.colorAccent));
        snackBar.setTextColor(getContext().getColor(R.color.dark_gray_btn_bg_color));
        snackBar.setDuration(Snackbar.LENGTH_INDEFINITE);

        snackBar.setAction(getString(R.string.ok), v -> {
            SuperUtil.vibrate(requireContext());
            if (dialog.isShowing())
                dialog.dismiss();
            spHelper.setUserLoginState(false);
            SuperUtil.removeViewByTag(requireActivity(), TAG, true);
        });

        snackBar.show();
    }

    private ItemUser getIfCurrentUserUpdated() {
        if (
                !updatedCurrentUser.getName().equals(currentUser.getName()) ||
                        !updatedCurrentUser.getEmail().equals(currentUser.getEmail()) ||
                        !updatedCurrentUser.getMobile().equals(currentUser.getMobile()) ||
                        !updatedCurrentUser.getImg().equals(currentUser.getImg()) ||
                        !updatedCurrentUser.getPass().equals(currentUser.getPass())
        )
            return updatedCurrentUser;
        else
            return null;
    }

    private void showValidationEmailDialog() {

        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()), R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_info_send_email_view, null, false);
        TextView msg = dialogView.findViewById(R.id.text_dialog);
        TextView dialogEmail = dialogView.findViewById(R.id.tv_email);
        msg.setText(R.string.dialog_message_verification_email);
        dialogEmail.setText(currentUser.getEmail());
        dialog.setContentView(dialogView);

        /*onClick on dialog cancel button*/
        Button cancelBtn = dialog.findViewById(R.id.cancel_dialog_button);
        cancelBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            if (dialog.isShowing())
                dialog.dismiss();
        });

        /*onClick on dialog edit button*/
        Button editBtn = dialog.findViewById(R.id.send_dialog_button);
        editBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            if (dialog.isShowing())
                dialog.dismiss();

            sendValidationEmail();
        });

        dialog.show();
        dialogView.startAnimation(animScaleUp);
    }

    private void sendValidationEmail() {
        FirebaseUser firebaseCurrentUser = fAuth.getCurrentUser();
        Dialog pgsDialog = SuperUtil.showProgressDialog(getActivity(), container);
        if (firebaseCurrentUser != null) {
            firebaseCurrentUser.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "sendConfirmationEmail" + "Email sent");
                            if (isVisible())
                                MessagesHelper.showInfoMessage(requireActivity(),
                                        getString(R.string.email_sent));
                            if (pgsDialog.isShowing())
                                pgsDialog.dismiss();
                        } else {
                            Log.e(TAG, "sendConfirmationEmail: " + task.getException().getMessage());
                            if (pgsDialog.isShowing())
                                pgsDialog.dismiss();

                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                if (isVisible())
                                    MessagesHelper.showInfoMessageWarning(requireActivity(),
                                            getString(R.string.invalid_user));
                            } else if (task.getException() instanceof FirebaseNetworkException) {
                                if (isVisible())
                                    MessagesHelper.showInfoMessageWarning(requireActivity(),
                                            getString(R.string.network_error));
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                if (isVisible())
                                    MessagesHelper.showInfoMessageWarning(requireActivity(),
                                            getString(R.string.invalid_credentials));
                            }
                        }
                    });
        }
    }

    private void showCroppingDialog(Uri resultUri) {

        Dialog dialog = new Dialog(Objects.requireNonNull(getContext()), R.style.alert_dialog);
        dialog.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_crop_image, container, false);
        TextView msg = dialogView.findViewById(R.id.text_dialog);
        msg.setText(R.string.use_question);

        cropImageView = dialogView.findViewById(R.id.cropImageView);
        cropImageView.setImageUriAsync(resultUri);

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
        dialogView.setAnimation(anim);
        dialog.setContentView(dialogView);

        //onClick on dialog cancel button
        Button cancelBtn = dialog.findViewById(R.id.cancel_dialog_button);
        cancelBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            if (dialog.isShowing())
                dialog.dismiss();
        });

        //onClick Use button
        Button editBtn = dialog.findViewById(R.id.use_button);
        editBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());

            isImgPickingAction = true;
            loadUserInfo();

            if (dialog.isShowing())
                dialog.dismiss();
        });

        dialog.show();
        dialogView.startAnimation(animScaleUp);
    }

    @SuppressLint("StaticFieldLeak")
    public class AsyncTaskLoader extends AsyncTask<Void, Void, Void> {

        private Dialog progressDialog;
        private SuperUtil superUtil;
        private boolean isDefaultImg;

        private Bitmap bitmapFromBase64;
        private Bitmap croppedBitmapImg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = SuperUtil.showProgressDialog(getActivity(), container);
            superUtil = new SuperUtil();
            if (cropImageView != null)
                croppedBitmapImg = cropImageView.getCroppedImage();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (imgUri != null && isImgPickingAction && croppedBitmapImg != null) {
                base64Image = superUtil.encodeBase64ToString(croppedBitmapImg,
                        SuperUtil.getExtensionFromUri(imgUri));
                Log.d(TAG, "doInBackground: base64Image: SELECTED");
            } else {
                if (currentUser.getImg() != null && !currentUser.getImg().equals(DEFAULT))
                    bitmapFromBase64 = SuperUtil.decodeBase64ToBitmap(currentUser.getImg());
                else if (currentUser.getImg() != null && currentUser.getImg().equals(DEFAULT)) {
                    isDefaultImg = true;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (isImgPickingAction) {
                Picasso.get()
                        .load(cropImageView.getImageUri())
                        .transform(new CircleTransform())
                        .resize(ivAvatar.getWidth(), ivAvatar.getHeight())
                        .centerCrop()
                        .placeholder(R.drawable.ic_refresh)
                        .error(R.drawable.ic_user_profile)
                        .into(ivAvatar);
                isImgPickingAction = false;
                isImgPicked = true;
            } else {
                if (!isDefaultImg && !isImgPicked) {
                    ivAvatar.setImageBitmap(bitmapFromBase64);
                }
                tvName.setText(name);
                tvEmail.setText(email);
                tvMobile.setText(mobile);
                tvPass.setText(password);
            }

            if (progressDialog.isShowing())
                progressDialog.dismiss();

        }
    }

    private void saveOnFirebaseDB(ItemUser updatedCurrentUser) {
        if (updatedCurrentUser != null) {
            Dialog pgsDialog = SuperUtil.showProgressDialog(getActivity(), container);
            fReference = FirebaseDatabase.getInstance()
                    .getReference(USER_FIRE_BASE_REFERENCE)
                    .child(updatedCurrentUser.getId());
            fReference.setValue(updatedCurrentUser.getUserIntoHasMap()) // Saving On FireBase
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            realmHelper.addUserToDB(updatedCurrentUser); // Saving On Realm
                            currentUser = updatedCurrentUser;

                            if (realmHelper.getSavedVerses() != null
                                    && !realmHelper.getSavedVerses().isEmpty()) {

                                uploadVerses(updatedCurrentUser);
                            }

                            uploadScore(pgsDialog, updatedCurrentUser);

                            if (pgsDialog.isShowing())
                                pgsDialog.dismiss();

                            if (isVisible())
                                MessagesHelper.showInfoMessage(requireActivity(), getString(R.string.data_updated));

                            Log.d(TAG, "saveOnFirebaseDB: " + task.getResult());
                        } else {
                            Log.d(TAG, "Task saveOnFirebaseDB: " + task.getException());

                            if (pgsDialog.isShowing())
                                pgsDialog.dismiss();

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                if (task.getException().getMessage().contains(getString(R.string.email_low_case))) {
                                    if (isVisible())
                                        MessagesHelper.showInfoMessageWarning(requireActivity(),
                                                getString(R.string.data_collision));
                                }
                            } else if (task.getException() instanceof FirebaseNetworkException) {
                                if (isVisible())
                                    MessagesHelper.showInfoMessageWarning(requireActivity(),
                                            getString(R.string.network_error));
                            } else {
                                if (isVisible())
                                    MessagesHelper.showInfoMessageError(requireActivity(),
                                            getString(R.string.failed_updating));
                            }
                        }
                    });
        }
    }

    private void uploadVerses(ItemUser updatedCurrentUser) {
        fReference = FirebaseDatabase.getInstance()
                .getReference(USER_FIRE_BASE_REFERENCE)
                .child(updatedCurrentUser.getId())
                .child(USER_COLUMN_VERSES);
        fReference.setValue(
                SuperUtil.getVersesIntoHasMapList(
                        realmHelper.getSavedVerses()));
    }

    private void uploadScore(Dialog pgsDialog, ItemUser updatedCurrentUser) {
        DatabaseReference fReferenceLeaderBoard = FirebaseDatabase.getInstance()
                .getReference(LEADER_BOARD_FIRE_BASE_REFERENCE)
                .child(updatedCurrentUser.getId());
        fReferenceLeaderBoard.setValue(updatedCurrentUser.getScoreInfoIntoHasMap())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Leader board uploadScore:Success ");
                    } else {
                        Log.e(TAG, "uploadScore: " + task.getException().getMessage());
                    }
                    if (pgsDialog.isShowing())
                        pgsDialog.dismiss();
                });
    }

    private void deleteUserInfoOnFirebaseDB(Dialog dialog, View dialogView) {
        fReference = FirebaseDatabase.getInstance()
                .getReference(USER_FIRE_BASE_REFERENCE)
                .child(currentUser.getId());
        fReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "deleteUserOnFirebaseDB: " + "Success deleting");
                deleteUserFromFirebase(dialog, dialogView);
            } else {
                Log.d(TAG, "deleteUserOnFirebaseDB: FAILED DELETING ERROR: " + task.getException().getMessage());
            }
        });
    }

    private void deleteUserFromFirebase(Dialog dialog, View dialogView) {
        FirebaseUser user = fAuth.getCurrentUser();
        Dialog pgsDialog = SuperUtil.showProgressDialog(getActivity(), container);
        if (user != null)
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (pgsDialog.isShowing())
                                pgsDialog.dismiss();

                            RealmHelper realmHelper = new RealmHelper(getContext());
                            realmHelper.deleteUserFromRealmDataBase(currentUser);
                            realmHelper.deleteAllVerseFromRealmDataBase();
                            showSuccessDeleteMsgOnDialog(dialog, dialogView);

                            Log.d(TAG, "User account deleted.");
                        } else {
                            Log.d(TAG, "deleteUser: " + task.getException());
                            if (pgsDialog.isShowing())
                                pgsDialog.dismiss();

                            if (task.getException() instanceof FirebaseNetworkException) {
                                if (isVisible())
                                    MessagesHelper.showInfoMessageWarningOnDialog(requireActivity(),
                                            getString(R.string.network_error), dialogView);
                            }
                        }
                    });
    }

    private void showUpdateDataDialog(int resLayout) {
        Dialog dialogUpdateData = new Dialog(requireActivity(), R.style.alert_dialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(resLayout, container, false);

        TextInputEditText editingField = dialogView.findViewById(R.id.dialog_input_field);
        TextInputLayout tilField = dialogView.findViewById(R.id.til_dialog_field);

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
        dialogView.setAnimation(anim);
        dialogUpdateData.setContentView(dialogView);


        switch (resLayout) {
            case R.layout.dialog_update_name_view:
                tilField.setHint(getString(R.string.new_name));
                break;
            case R.layout.dialog_update_email_view:
                tilField.setHint(getString(R.string.new_email));
                break;
            case R.layout.dialog_update_mobile_view:
                tilField.setHint(getString(R.string.new_mobile));
                break;
        }

        //onClick on dialog cancel button
        Button cancelBtn = dialogUpdateData.findViewById(R.id.save_dialog_button);
        cancelBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());

            switch (resLayout) {
                case R.layout.dialog_update_name_view:
                    String auxStrName = editingField.getText().toString();
                    if (validateName(auxStrName, tilField)) {

                        if (!name.equals(auxStrName))
                            showSaveOp();

                        name = auxStrName;
                        if (dialogUpdateData.isShowing())
                            dialogUpdateData.dismiss();
                        loadUserInfo();
                    }
                    break;
                case R.layout.dialog_update_email_view:
                    String auxStrEmail = editingField.getText().toString();
                    if (validateEmail(auxStrEmail, tilField)) {

                        if (!email.equals(auxStrEmail))
                            showSaveOp();

                        email = auxStrEmail;
                        if (dialogUpdateData.isShowing())
                            dialogUpdateData.dismiss();
                        loadUserInfo();
                    }
                    break;
                case R.layout.dialog_update_mobile_view:
                    String auxStrMobile = editingField.getText().toString();
                    if (validateMobile(auxStrMobile, tilField)) {

                        if (!mobile.equals(auxStrMobile))
                            showSaveOp();

                        mobile = auxStrMobile;
                        if (dialogUpdateData.isShowing())
                            dialogUpdateData.dismiss();
                        loadUserInfo();
                    }
                    break;
            }

        });
        dialogUpdateData.show();
        dialogView.startAnimation(animScaleUp);
    }

    private void sendRestorePassByEmail(Dialog rootDialog, View dialogView){
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
                        Log.d(TAG, "sendRestorePassEmail: " + task.getException().getMessage());
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
            String email = editTextEmail.getText().toString();
            if (validateEmail(email, tilEmail)) {
                sendRestorePassByEmail(dialogRestoresEmailSentDialog, dialogView);
            }
        });

        dialogRestoresEmailSentDialog.show();
        dialogView.startAnimation(animScaleUp);
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
            logOut();
        });

        snackBar.show();
    }

    private boolean validateMobile(String auxStrMobile, TextInputLayout tilField) {
        boolean valid = true;

        String regexStr = "^[+]?[0-9]{8,20}$";

        if (auxStrMobile.length() <= 8 || auxStrMobile.length() > 20 ||
                !auxStrMobile.matches(regexStr)) {
            tilField.setError(getString(R.string.error_mobile));
            valid = false;
        } else {
            tilField.setError(null);
        }
        return valid;
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

    private boolean validateName(String name, TextInputLayout tilField) {
        boolean valid = true;

        if (name.isEmpty() || name.length() < 3) {
            tilField.setError(getString(R.string.error_name));
            valid = false;
        } else {
            tilField.setError(null);
        }

        return valid;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgUri = result.getUri();
                showCroppingDialog(imgUri);
                showSaveOp();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE
                    || data == null) {
                if (isVisible())
                    MessagesHelper.showInfoMessageWarning(requireActivity(),
                            getString(R.string.failed_picking_img));
                hideSaveOp();
            }
        }
    }

    private void hideSaveOp() {
        SuperUtil.hideView(null, tvSave);

    }

    private void showSaveOp() {
        SuperUtil.showView(null, tvSave);
    }

    @Override
    public void onStart() {
        super.onStart();
        rootView.startAnimation(animSlideInFromRight);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
        SuperUtil.logInUser(requireActivity(), currentUser);
        checkIfEmailIsVerified();
    }
}
