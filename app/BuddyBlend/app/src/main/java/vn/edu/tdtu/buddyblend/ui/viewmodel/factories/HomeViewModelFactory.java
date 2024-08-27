package vn.edu.tdtu.buddyblend.ui.viewmodel.factories;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import vn.edu.tdtu.buddyblend.ui.viewmodel.HomeViewModel;

public class HomeViewModelFactory implements ViewModelProvider.Factory {
    private SharedPreferences sharedPreferences;
    private HomeViewModel.OnCurrentUserFetchedFail onCurrentUserFetchedFail;

    public HomeViewModelFactory(SharedPreferences sharedPreferences, HomeViewModel.OnCurrentUserFetchedFail onCurrentUserFetchedFail){
        this.sharedPreferences = sharedPreferences;
        this.onCurrentUserFetchedFail = onCurrentUserFetchedFail;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(HomeViewModel.class)){
            return (T) new HomeViewModel(sharedPreferences, onCurrentUserFetchedFail);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
