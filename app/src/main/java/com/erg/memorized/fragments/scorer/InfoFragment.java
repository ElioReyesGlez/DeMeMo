package com.erg.memorized.fragments.scorer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.erg.memorized.R;
import com.erg.memorized.adapters.AdapterEvaluator;
import com.erg.memorized.adapters.AdapterScorerFragmentPager;
import com.erg.memorized.fragments.ScorerFragment;
import com.erg.memorized.interfaces.ScorerListener;
import com.erg.memorized.model.Evaluator;
import com.erg.memorized.util.SuperUtil;
import com.erg.memorized.views.CustomViewPager;

import java.util.ArrayList;

public class InfoFragment extends Fragment implements ScorerListener {

    public static final String TAG = "InfoFragment";
    public static final int POS = 0;


    private View rootView;
    private ScorerFragment scorerFragment;

    public InfoFragment(ScorerFragment scorerFragment) {
        this.scorerFragment = scorerFragment;
    }

    public static InfoFragment newInstance(ScorerFragment scorerFragment) {
        return new InfoFragment(scorerFragment);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        FragmentManager childManager = getChildFragmentManager();
        FragmentTransaction transaction = childManager.beginTransaction();
        transaction.addToBackStack(TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scorerFragment.bindInterface(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_info, container, false);
        setUpView();
        return rootView;
    }

    private void setUpView() {
        ListView listView = rootView.findViewById(R.id.list_view_evaluators);
        Button btnGetStarted = rootView.findViewById(R.id.btn_get_started);

        String[] evaluatorsName = getResources().getStringArray(R.array.evaluator_names);
        String[] evaluatorsInfo = getResources().getStringArray(R.array.evaluators_descriptions);
        int[] images = new int[]{
                R.drawable.ic_indent,
                R.drawable.ic_swipe,
                R.drawable.ic_typography
        };

        ArrayList<Evaluator> evaluators = new ArrayList<>();
        for (int i = 0; i < evaluatorsName.length; i++) {
            String name = evaluatorsName[i];
            String description = evaluatorsInfo[i];
            int image = images[i];
            Evaluator evaluator = new Evaluator(name, image, description);
            evaluators.add(evaluator);
        }

        AdapterEvaluator adapterEvaluator = new AdapterEvaluator(
                requireContext(),
                R.layout.item_list_evaluatos,
                evaluators);

        listView.setAdapter(adapterEvaluator);
        adapterEvaluator.notifyDataSetChanged();

        btnGetStarted.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            scorerFragment.scorerViewPager.moveNext();
        });
    }

    @Override
    public void onUndoClick(View v) {

    }

    @Override
    public void onNextClick(View v, CustomViewPager viewPager,
                            AdapterScorerFragmentPager pagerAdapter) {
        SuperUtil.vibrate(requireContext());
        viewPager.moveNext();
    }

    @Override
    public void onFinishClick(View v) {
        //Empty
    }
}
