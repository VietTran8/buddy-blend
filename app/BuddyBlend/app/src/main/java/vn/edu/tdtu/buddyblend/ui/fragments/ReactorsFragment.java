package vn.edu.tdtu.buddyblend.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.adapters.comment.ReactorAdapter;
import vn.edu.tdtu.buddyblend.databinding.FragmentReactorsBinding;
import vn.edu.tdtu.buddyblend.enums.EReactionType;
import vn.edu.tdtu.buddyblend.models.React;
import vn.edu.tdtu.buddyblend.ui.viewmodel.ReactorsFragmentViewModel;
import vn.edu.tdtu.buddyblend.ui.viewmodel.ViewReactViewModel;

public class ReactorsFragment extends Fragment {
    private ReactorsFragmentViewModel viewModel;
    private FragmentReactorsBinding binding;
    private ReactorAdapter adapter;

    public ReactorsFragment(EReactionType type, List<React> reactions) {
        viewModel = new ReactorsFragmentViewModel();
        viewModel.setReactions(reactions);
        viewModel.setType(type);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReactorsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    private void init(){
        initObjects();
        renderUI();
    }

    private void renderUI() {
        binding.rcvReactors.setAdapter(adapter);
        binding.rcvReactors.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initObjects() {
        adapter = new ReactorAdapter(getContext());
        adapter.setReacts(viewModel.getReactions());
    }
}