package vn.edu.tdtu.buddyblend.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.opencensus.trace.Link;
import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.adapters.comment.ReactorFragmentsAdapter;
import vn.edu.tdtu.buddyblend.databinding.ActivityViewReactionsBinding;
import vn.edu.tdtu.buddyblend.enums.EReactionType;
import vn.edu.tdtu.buddyblend.models.React;
import vn.edu.tdtu.buddyblend.ui.fragments.ReactorsFragment;
import vn.edu.tdtu.buddyblend.ui.viewmodel.ViewReactViewModel;
import vn.edu.tdtu.buddyblend.utils.ScreenManager;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;

public class ViewReactionsActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityViewReactionsBinding binding;
    private ViewReactViewModel viewModel;
    private SharedPreferences sharedPreferences;
    private ReactorFragmentsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewReactionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, MODE_PRIVATE);
        viewModel = new ViewModelProvider(this).get(ViewReactViewModel.class);
        adapter = new ReactorFragmentsAdapter(this);

        init();
    }

    private void init(){
        viewModel.setPostId(getIntent().getStringExtra("postId"));

        ScreenManager.enableFullScreen(getWindow());
        renderUI();
        initEvents();
    }

    private void renderUI(){
        viewModel.getReactions(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""), msg -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }).observe(this, data -> {
            Map<EReactionType, Integer> topReactTypes = getTopReactTypes(data);
            int totalReacted = topReactTypes.entrySet()
                    .stream()
                    .mapToInt(Map.Entry::getValue)
                    .sum();

            setUpTabs(totalReacted, topReactTypes, data);
        });
    }

    private Map<EReactionType, Integer> getTopReactTypes(Map<EReactionType, List<React>> data){
        Map<EReactionType, Integer> originalTopTypes = data.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().size()
                ));

        Map<EReactionType, Integer> sortedTopTypes = originalTopTypes.entrySet().stream()
                .sorted(Map.Entry.<EReactionType, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new
                ));

        return sortedTopTypes;
    }

    private List<React> getTotalReactions(Map<EReactionType, List<React>> reactions){
        List<React> total = new ArrayList<>();
        reactions.forEach((key, value) -> {
            total.addAll(value);
        });

        return total;
    }

    private void setUpTabs(int totalReacted, Map<EReactionType, Integer> reactionCount, Map<EReactionType, List<React>> reactions){
        binding.tabLayout.setTabIconTint(null);
        TabLayout tabLayout = binding.tabLayout;
        List<Integer> tabIcons = new ArrayList<>();

        //First tab
        TabLayout.Tab firstTab = tabLayout.newTab();
        firstTab.setText("Tất cả " + totalReacted);
        tabLayout.addTab(firstTab);
        adapter.addFragment(new ReactorsFragment(null, getTotalReactions(reactions)));

        reactionCount.entrySet().forEach(item -> {
            adapter.addFragment(new ReactorsFragment(item.getKey(), reactions.get(item.getKey())));
            tabIcons.add(getTabIconResource(item.getKey()));
        });

        binding.vpReactors.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, binding.vpReactors, (tab, position) -> {
            tab.setCustomView(R.layout.custom_tab_item_view);
            View customView = tab.getCustomView();
            ImageView tabIcon = customView.findViewById(R.id.tab_icon);
            TextView tabText = customView.findViewById(R.id.tab_text);

            tabText.setText(position == 0 ? "Tất cả " + totalReacted : String.valueOf(reactionCount.values().toArray()[position - 1]));
            if(position > 0){
                tabIcon.setImageResource(tabIcons.get(position - 1));
            }else{
                tabIcon.setVisibility(View.GONE);
            }
        }).attach();

    }

    private int getTabIconResource(EReactionType type){
        switch (type) {
            case HAHA -> {
               return (R.drawable.react_haha);
            }
            case SAD -> {
                return (R.drawable.react_sad);
            }
            case LIKE -> {
                return (R.drawable.react_like);
            }
            case HEART -> {
                return (R.drawable.reaction_heart);
            }
            case WOW -> {
                return (R.drawable.react_wow);
            }
            case ANGRY -> {
                return (R.drawable.react_angry);
            }
        }
        return -1;
    }

    private void initEvents(){
        binding.btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == binding.btnBack){
            onBackPressed();
        }
    }
}