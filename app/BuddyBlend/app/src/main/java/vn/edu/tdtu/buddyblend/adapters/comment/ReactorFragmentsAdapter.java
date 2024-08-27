package vn.edu.tdtu.buddyblend.adapters.comment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReactorFragmentsAdapter extends FragmentStateAdapter {
    List<Fragment> fragments;

    public ReactorFragmentsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fragments = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments != null ? fragments.size() : 0;
    }

    public void addFragment(Fragment fragment){
        this.fragments.add(fragment);
    }
}
