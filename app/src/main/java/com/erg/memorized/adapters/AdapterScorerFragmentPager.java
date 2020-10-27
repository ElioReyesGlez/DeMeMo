package com.erg.memorized.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.erg.memorized.fragments.ScorerFragment;
import com.erg.memorized.fragments.scorer.BoxSelectorFragment;
import com.erg.memorized.fragments.scorer.DragAndDropFragment;
import com.erg.memorized.fragments.scorer.InfoFragment;
import com.erg.memorized.fragments.scorer.WriterFragment;
import com.erg.memorized.interfaces.BoxTestListener;
import com.erg.memorized.model.ItemVerse;

public class AdapterScorerFragmentPager extends FragmentPagerAdapter {

    private final ItemVerse verse;
    private Fragment currentFrag;
    private final BoxTestListener boxTestListener;
    private final ScorerFragment scorerFragment;
    private int position = -1;

    public AdapterScorerFragmentPager(@NonNull FragmentManager fm, int behavior,
                                      ItemVerse verse,
                                      BoxTestListener boxTestListener,
                                      ScorerFragment scorerFragment) {
        super(fm, behavior);
        this.verse = verse;
        this.boxTestListener = boxTestListener;
        this.scorerFragment = scorerFragment;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        this.position = position;
        switch (position) {
            case 0:
                currentFrag = InfoFragment.newInstance(scorerFragment);
                return currentFrag;
            case 1:
                currentFrag = BoxSelectorFragment.newInstance(verse,
                        boxTestListener, scorerFragment);
                return currentFrag;
            case 2:
                currentFrag = DragAndDropFragment.newInstance(verse,
                        boxTestListener, scorerFragment);
                return currentFrag;
            case 3:
                currentFrag = WriterFragment.newInstance(verse,
                        boxTestListener, scorerFragment);
                return currentFrag;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    public int getCurrentPosition() {
        return position;
    }

    public Fragment getCurrentFrag() {
        return currentFrag;
    }
}
