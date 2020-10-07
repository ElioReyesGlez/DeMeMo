package com.erg.memorized.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.erg.memorized.R;
import com.erg.memorized.adapters.AdapterBible;
import com.erg.memorized.adapters.AdapterLanguage;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.helpers.RealmHelper;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.helpers.TimeHelper;
import com.erg.memorized.interfaces.BibleApi;
import com.erg.memorized.model.ItemUser;
import com.erg.memorized.model.bible_api_models.Bible;
import com.erg.memorized.model.bible_api_models.BibleRoot;
import com.erg.memorized.util.Constants;
import com.erg.memorized.util.SuperUtil;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.erg.memorized.util.Constants.BIBLE_API_BASE_URL;
import static com.erg.memorized.util.Constants.SPACE;

public class GeneralSettingsFragment extends Fragment implements View.OnClickListener,
        SearchView.OnQueryTextListener {

    public static final String TAG = "GeneralSettingsFragment";

    private View rootView;
    private ViewGroup container;
    private RelativeLayout rlLanguage, rlBibles;
    private LinearLayout linealLanguageContainer, linealBibleVersionContainer;
    private Animation animScaleUp, animScaleDown, animSlideInFromRight;

    private SharedPreferencesHelper spHelper;
    private RealmHelper realmHelper;
    private ItemUser currentUser;
    private FirebaseAuth fAuth;

    private ListView listViewBibles;
    private AdapterBible adapterBible;
    private ArrayList<Bible> bibles;
    private ArrayList<String> languages;

    public static GeneralSettingsFragment newInstance() {
        return new GeneralSettingsFragment();
    }

    public GeneralSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animScaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up);
        animScaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down);
        animSlideInFromRight = AnimationUtils.loadAnimation(requireContext(),
                R.anim.fab_slide_in_from_right);

        fAuth = FirebaseAuth.getInstance();
        spHelper = new SharedPreferencesHelper(requireContext());
        realmHelper = new RealmHelper(requireContext());
        if (spHelper.getUserLoginStatus()) {
            currentUser = realmHelper.getUser();
        }
        bibles = new ArrayList<>();
        String[] arrayLanguages = getResources().getStringArray(R.array.languages);
        languages = new ArrayList<>(Arrays.asList(arrayLanguages));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.container = container;
        rootView = inflater.inflate(R.layout.fragment_general_settings, container, false);
        setUpView();
        return rootView;
    }

    private void setUpView() {
        rlLanguage = rootView.findViewById(R.id.rl_language);
        linealLanguageContainer = rootView.findViewById(R.id.ll_language_container);
        rlBibles = rootView.findViewById(R.id.rl_bibles);
        linealBibleVersionContainer = rootView.findViewById(R.id.ll_bible_version_container);


        setSavedLanguage();
        setSavedBible();

        linealLanguageContainer.setOnClickListener(this);
        linealBibleVersionContainer.setOnClickListener(this);
    }

    private void setSavedLanguage() {
        int savedLanguagePos = spHelper.getLanguagePos();
        View view = getLayoutInflater().inflate(R.layout.item_language, null, false);
        AdapterLanguage.ViewHolder viewHolder = new AdapterLanguage.ViewHolder(view);
        viewHolder.setLanguage(languages.get(savedLanguagePos));
        viewHolder.setFlag(Constants.FlagResIds[savedLanguagePos]);
        linealLanguageContainer.addView(view);
    }

    private void setSavedBible() {
        Bible bible = spHelper.getBibleVersion();
        View view = getLayoutInflater().inflate(R.layout.item_bible_version, null, false);
        AdapterBible.ViewHolder viewHolder = new AdapterBible.ViewHolder(view);
        viewHolder.setAbbreviationLocal(bible.getAbbreviationLocal());
        viewHolder.setNameLocal(bible.getNameLocal());
        viewHolder.setDescriptionLocal(bible.getDescriptionLocal());
        viewHolder.setLanguage(bible.getLanguage().getNameLocal());
        linealBibleVersionContainer.addView(view);

        Log.d(TAG, "setSavedBible: " + bible.toString());
    }

    @Override
    public void onClick(View v) {
        SuperUtil.vibrate(requireContext());
        switch (v.getId()) {
            case R.id.ll_language_container:
                showPickerLanguageDialog();
                break;
            case R.id.ll_bible_version_container:
                Log.d(TAG, "onClick: GET_BIBLE_HASH_MAP: " +
                        spHelper.getBibleVersion().toString());
                startFetchingBibles();
                break;
        }
    }

    private void showPickerLanguageDialog() {
        Dialog dialog = new Dialog(Objects.requireNonNull(getContext()), R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_view_language_choice, null, false);
        ListView listView = dialogView.findViewById(R.id.list_view_languages);

        String[] arrayLanguagesCodes = getResources().getStringArray(R.array.languages_codes);
        AdapterLanguage adapterLanguage = new AdapterLanguage(getContext(),
                R.layout.item_language, languages);
        listView.setAdapter(adapterLanguage);
        dialog.setContentView(dialogView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            SuperUtil.vibrate(requireContext());
            RadioButton radioButton = view.findViewById(R.id.radioButton);
            radioButton.setChecked(true);
            spHelper.setLanguagePosition(position);
            TimeHelper.setLocale(requireActivity(), arrayLanguagesCodes[position]);

            Log.d(TAG, "showPickerLanguageDialog: " + Locale.getDefault().getLanguage());

            if (dialog.isShowing())
                dialog.dismiss();

            refreshFragment();

            if (isVisible())
                MessagesHelper.showInfoMessage(requireActivity(),
                        languages.get(position) + SPACE + getString(R.string.selected));

        });
        dialog.show();
        dialogView.startAnimation(animScaleUp);
    }

    private void startFetchingBibles() {
        if (spHelper.getUserLoginStatus()) {
            if (spHelper.getEmailVerifiedStatus()) {
                Dialog pgsDialog = SuperUtil.showProgressDialog(getActivity(), container);
                fAuth.signInWithEmailAndPassword(currentUser.getEmail(), currentUser.getPass())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                showPickerBibleVersionDialog(pgsDialog);
                            } else {
                                if (pgsDialog.isShowing())
                                    pgsDialog.dismiss();
                                if (task.getException() instanceof FirebaseNetworkException) {
                                    if (isVisible())
                                        MessagesHelper.showInfoMessageWarning(requireActivity(),
                                                getString(R.string.network_error));
                                } else {
                                    if (isVisible())
                                        MessagesHelper.showInfoMessageError(requireActivity(),
                                                getString(R.string.failed_uploading));
                                }
                            }
                        });
            } else {
                if (isVisible())
                    MessagesHelper.showInfoMessageWarning(getActivity(),
                            getString(R.string.email_is_not_verified_msg));
            }
        } else {
            if (isVisible())
                MessagesHelper.showInfoMessageWarning(getActivity(),
                        getString(R.string.login_needed));
        }
    }

    private void showPickerBibleVersionDialog(Dialog pgsDialog) {

        Dialog dialog = new Dialog(Objects.requireNonNull(getContext()), R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_view_bible_choice,
                null, false);

        SearchView searcher = dialogView.findViewById(R.id.searcher);
        searcher.setOnQueryTextListener(this);
        listViewBibles = dialogView.findViewById(R.id.list_view_bibles);
        dialogView.setAnimation(animScaleUp);
        dialog.setContentView(dialogView);
        listViewBibles.setOnItemClickListener((parent, view, position, id) ->
        {
            SuperUtil.vibrate(requireContext());
            RadioButton radioButton = view.findViewById(R.id.radioButton);
            radioButton.setChecked(true);

            Bible bible = adapterBible.getBibles().get(position);
            spHelper.saveBibleVersion(bible);
            spHelper.setBibleSelectedStatus(bible.getId());
            if (dialog.isShowing())
                dialog.dismiss();

            Log.d(TAG, "showPickerBibleVersionDialog: Selected: " +
                    adapterBible.getBibles().get(position).toString());

            refreshFragment();
        });

        getBiblesFromApi(pgsDialog, dialog);
    }

    private void refreshFragment() {
        SuperUtil.removeViewByTag(requireActivity(), TAG, true);
        SuperUtil.loadView(requireActivity(), GeneralSettingsFragment.newInstance(),
                TAG, true);
    }

    private void getBiblesFromApi(Dialog pgsDialog, Dialog dialog) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BIBLE_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BibleApi bibleApi = retrofit.create(BibleApi.class);
        Call<BibleRoot> call = bibleApi.getBibles();
        call.enqueue(new Callback<BibleRoot>() {
            @Override
            public void onResponse(Call<BibleRoot> call, Response<BibleRoot> response) {
                if (!response.isSuccessful()) {
                    if (isVisible())
                        MessagesHelper.showInfoMessageError(requireActivity(),
                                getString(R.string.failed_uploading));
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }

                bibles.clear();
                BibleRoot bibleRoot = response.body();
                if (bibleRoot != null) {
                    bibles.addAll(bibleRoot.getData());
                    adapterBible = new AdapterBible(getActivity(),
                            R.layout.item_bible_version, bibles);
                    listViewBibles.setAdapter(adapterBible);
                    adapterBible.notifyDataSetChanged();
                }
                if (pgsDialog.isShowing())
                    pgsDialog.dismiss();
                dialog.show();
            }

            @Override
            public void onFailure(Call<BibleRoot> call, Throwable t) {
                if (pgsDialog.isShowing())
                    pgsDialog.dismiss();
                Log.e(TAG, "onFailure: " + t.getMessage());
                if (isVisible())
                    MessagesHelper.showInfoMessageError(requireActivity(),
                            getString(R.string.failed_uploading));
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        filter(query);
        return true;
    }

    private void filter(@NotNull String query) {
        if (!bibles.isEmpty()) {
            final String lowerCaseQuery = query.toLowerCase();
            ArrayList<Bible> filteredBibleList = new ArrayList<>();
            for (Bible bible : bibles) {
                final String abbreviationLocal = bible.getAbbreviationLocal().toLowerCase();
                final String nameLocal = bible.getNameLocal().toLowerCase();
                final String languageLocal = bible.getLanguage().getNameLocal().toLowerCase();
                if (abbreviationLocal.contains(lowerCaseQuery)
                        || nameLocal.contains(lowerCaseQuery)
                        || languageLocal.contains(lowerCaseQuery)) {
                    filteredBibleList.add(bible);
                }
            }
            adapterBible.filterList(filteredBibleList);
        }
    }

    /*private ArrayList<Bible> filterByLanguage() {
        ArrayList<Bible> filteredBibleList = new ArrayList<>();
        String[] arrayLanguages = getResources().getStringArray(R.array.languages);
        ArrayList<String> languages = new ArrayList<>(Arrays.asList(arrayLanguages));
        if (!bibles.isEmpty()) {
            for (Bible bible : bibles) {
                final String language = bible.getLanguage().getNameLocal().toLowerCase();
                for (String lang : languages) {
                    if (language.contains(lang.toLowerCase())) {
                        filteredBibleList.add(bible);
                    }
                }
            }
        }
        return filteredBibleList;
    }*/

    @Override
    public void onStart() {
        super.onStart();
        rootView.startAnimation(animSlideInFromRight);
    }

}
