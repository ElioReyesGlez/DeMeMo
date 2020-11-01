package com.erg.memorized.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.erg.memorized.R;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.helpers.RealmHelper;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.model.ItemUser;
import com.erg.memorized.util.Constants;
import com.erg.memorized.util.SuperUtil;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
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
import static com.erg.memorized.util.Constants.SPACE;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "SingUpFragment";

    private View rootView;
    private ViewGroup container;
    private TextInputEditText editTextName, editTextEmail, editTextMobile,
            editTextPassword, editTextReEnterPassword;
    private String name, email, mobile, password;
    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPass;
    private TextInputLayout tilRePass;
    private CropImageView cropImageView;
    private ShapeableImageView ivAvatar;

    private FirebaseAuth fAuth;
    private DatabaseReference fReference;
    private Uri imgUri;
    private String base64Image = DEFAULT;

    public SignUpFragment() {
    }

    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.signup_view, container, false);
        this.container = container;
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fab_slide_in_from_right);
        rootView.setAnimation(anim);

        setUpView();

        return rootView;
    }

    private void setUpView() {

        ivAvatar = rootView.findViewById(R.id.iv_avatar);
        tilName = rootView.findViewById(R.id.til_name);
        tilEmail = rootView.findViewById(R.id.til_email);
        tilPass = rootView.findViewById(R.id.til_pass);
        tilRePass = rootView.findViewById(R.id.til_re_pass);

        editTextName = rootView.findViewById(R.id.input_name);
        editTextEmail = rootView.findViewById(R.id.input_email);
        editTextMobile = rootView.findViewById(R.id.input_mobile);
        editTextPassword = rootView.findViewById(R.id.input_password);
        editTextReEnterPassword = rootView.findViewById(R.id.input_re_password);

        Button btnCreateAccount = rootView.findViewById(R.id.btn_create_account);
        btnCreateAccount.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);
    }

    private boolean validate() {

        boolean valid = true;

        String reEnterPassword = "";
        try {
            name = Objects.requireNonNull(editTextName.getText()).toString();
            email = Objects.requireNonNull(editTextEmail.getText()).toString();
            mobile = Objects.requireNonNull(editTextMobile.getText()).toString();
            password = Objects.requireNonNull(editTextPassword.getText()).toString();
            reEnterPassword = Objects.requireNonNull(editTextReEnterPassword.getText()).toString();
        } catch (NullPointerException e) {
            if (isVisible())
                MessagesHelper.showInfoMessageWarning(requireActivity(),
                        getString(R.string.null_pointer));
        }

        if (name.isEmpty() || name.length() < 3) {
            tilName.setError(getString(R.string.error_name));
            valid = false;
        } else {
            tilName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_email));
            valid = false;
        } else {
            tilEmail.setError(null);
        }

        if (mobile.isEmpty()) {
            mobile = DEFAULT;
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            tilPass.setError(getString(R.string.error_pass));
            valid = false;
        } else {
            tilPass.setError(null);
        }

        if (!(reEnterPassword.equals(password))) {
            tilRePass.setError(getString(R.string.error_re_pass));
            valid = false;
        } else {
            tilRePass.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        SuperUtil.vibrate(requireContext());
        switch (v.getId()) {
            case R.id.btn_create_account:
                if (validate()) {
                    ItemUser user = new ItemUser();
                    user.setName(name);
                    user.setEmail(email);
                    user.setMobile(mobile);
                    user.setPass(password);
                    user.setImg(base64Image);
                    saveOnFirebaseDB(user);
                } else {
                    MessagesHelper.showInfoMessageWarning(requireActivity(),
                            getString(R.string.invalid_credentials));
                }
                break;
            case R.id.iv_avatar:
                CropImage.activity().start(requireContext(), this);
                break;
        }
    }

    private void saveOnFirebaseDB(ItemUser user) {
        Dialog dialog = SuperUtil.showProgressDialog(getActivity(), container);
        fAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPass())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.setId(fAuth.getUid());
                        fReference = FirebaseDatabase.getInstance()
                                .getReference(Constants.USER_FIRE_BASE_REFERENCE)
                                .child(user.getId());
                        fReference.setValue(user.getUserIntoHasMap()) // Saving On FireBase
                                .addOnCompleteListener(task1 -> {
                                    if (dialog.isShowing())
                                        dialog.dismiss();
                                    if (task1.isSuccessful()) {
                                        RealmHelper realmHelper = new RealmHelper(requireContext());
                                        realmHelper.addUserToDB(user); // Saving On Realm

                                        SharedPreferencesHelper spHelper =
                                                new SharedPreferencesHelper(requireContext());
                                        spHelper.setUserLoginState(true);

                                        showValidationEmailDialog(user);

                                        Log.d(TAG, "saveOnFirebaseDB: " + task1.getResult());
                                    } else {
                                        if (isVisible())
                                            MessagesHelper.showInfoMessageError(requireActivity(),
                                                    getString(R.string.failed_saving_user));
                                        Log.d(TAG, "Task 1 saveOnFirebaseDB: " + task1.getException());
                                    }
                                });
                    } else {
                        Log.d(TAG, "Task saveOnFirebaseDB: " + task.getException());

                        if (dialog.isShowing())
                            dialog.dismiss();

                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarning(requireActivity(),
                                        getString(R.string.user_collision));
                            tilEmail.setError(getString(R.string.used_email));
                        } else if (task.getException() instanceof FirebaseNetworkException) {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarning(requireActivity(),
                                        getString(R.string.network_error));
                        } else if (task.getException() instanceof FirebaseTooManyRequestsException) {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarning(requireActivity(),
                                        getString(R.string.too_many_requests));
                        } else {
                            if (isVisible())
                                MessagesHelper.showInfoMessageError(requireActivity(),
                                        getString(R.string.failed_creating_user));
                        }
                    }
                });
    }

    private void showValidationEmailDialog(ItemUser user) {

        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()), R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_info_send_email_view, null, false);
        TextView msg = dialogView.findViewById(R.id.text_dialog);
        TextView dialogEmail = dialogView.findViewById(R.id.tv_email);
        msg.setText(R.string.dialog_message_verification_email);
        dialogEmail.setText(user.getEmail());
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
        dialogView.setAnimation(anim);
        dialog.setContentView(dialogView);

        /*onClick on dialog cancel button*/
        Button cancelBtn = dialog.findViewById(R.id.cancel_dialog_button);
        cancelBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            if (dialog.isShowing())
                dialog.dismiss();
        });

        /*onClick on dialog send button*/
        Button editBtn = dialog.findViewById(R.id.send_dialog_button);
        editBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());

            sendConfirmationEmail(dialogView, dialog);
        });

        dialog.show();
    }

    private void sendConfirmationEmail(View dialogView, Dialog dialog) {
        FirebaseUser firebaseCurrentUser = fAuth.getCurrentUser();
        Dialog pgsDialog = SuperUtil.showProgressDialog(getActivity(), container);
        String msg = getString(R.string.email_sent);
        assert firebaseCurrentUser != null;
        firebaseCurrentUser.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "sendConfirmationEmail" + "Email sent");
                        if (pgsDialog.isShowing())
                            pgsDialog.dismiss();
                        showInfoMessageOnDialog(msg, dialogView, dialog);
                    } else {
                        Log.d(TAG, "sendConfirmationEmail: " + task.getException());
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

    private void showInfoMessageOnDialog(String msg, View dialogView, Dialog dialog) {
        Snackbar snackBar = Snackbar.make(dialogView, msg, Snackbar.LENGTH_SHORT);

        snackBar.setBackgroundTint(requireActivity().getColor(R.color.colorPrimary));
        snackBar.setTextColor(requireActivity().getColor(R.color.dark_gray_btn_bg_color));

        snackBar.setDuration(Snackbar.LENGTH_INDEFINITE);

        snackBar.setAction(getString(R.string.ok), v -> {
            SuperUtil.vibrate(requireContext());
            snackBar.dismiss();
            if (dialog.isShowing())
                dialog.dismiss();

            String msgUserCreated = getString(R.string.welcome) +
                    SPACE + name + getString(R.string.successfully_user_created);
            SuperUtil.removeViewByTag(requireActivity(), TAG, false);
            SuperUtil.loadView(requireActivity(),
                    SettingsFragment.newInstance(true),
                    SettingsFragment.TAG, false);

            MessagesHelper.showInfoMessage(requireActivity(), msgUserCreated);

        });

        snackBar.show();
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

            new AsyncTaskLoader().execute();

            if (dialog.isShowing())
                dialog.dismiss();
        });

        dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    public class AsyncTaskLoader extends AsyncTask<Void, Void, Void> {

        private Dialog progressDialog;
        private SuperUtil superUtil;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = SuperUtil.showProgressDialog(getActivity(), container);
            superUtil = new SuperUtil();
        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            if (imgUri != null) {
                base64Image = superUtil.encodeBase64ToString(cropImageView.getCroppedImage(),
                        SuperUtil.getExtensionFromUri(imgUri));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Picasso.get()
                    .load(cropImageView.getImageUri())
                    .resize(ivAvatar.getWidth(), ivAvatar.getHeight())
                    .centerCrop()
                    .placeholder(R.drawable.ic_refresh)
                    .error(R.drawable.ic_user_profile)
                    .into(ivAvatar);

            if (progressDialog.isShowing())
                progressDialog.dismiss();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgUri = result.getUri();
                showCroppingDialog(imgUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE
                    || data == null) {
                if (isVisible())
                    MessagesHelper.showInfoMessageWarning(requireActivity(),
                            getString(R.string.failed_picking_img));
            }
        }
    }
}
