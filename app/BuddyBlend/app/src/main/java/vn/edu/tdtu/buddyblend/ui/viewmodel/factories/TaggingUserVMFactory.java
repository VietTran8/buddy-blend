package vn.edu.tdtu.buddyblend.ui.viewmodel.factories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import vn.edu.tdtu.buddyblend.models.MinimizedUser;
import vn.edu.tdtu.buddyblend.ui.viewmodel.TaggingUserViewModel;

public class TaggingUserVMFactory implements ViewModelProvider.Factory {
    private List<MinimizedUser> taggingMinimizedUsers;

    public TaggingUserVMFactory(List<MinimizedUser> taggingMinimizedUsers){
        this.taggingMinimizedUsers = taggingMinimizedUsers;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(TaggingUserViewModel.class)){
            return (T) new TaggingUserViewModel(taggingMinimizedUsers);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
