package vn.edu.tdtu.buddyblend.ui.viewmodel;

import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;

import vn.edu.tdtu.buddyblend.enums.EReactionType;
import vn.edu.tdtu.buddyblend.models.React;

public class ReactorsFragmentViewModel extends ViewModel {
    private EReactionType type;
    private List<React> reactions;

    public EReactionType getType() {
        return type;
    }

    public void setType(EReactionType type) {
        this.type = type;
    }

    public List<React> getReactions() {
        return reactions;
    }

    public void setReactions(List<React> reactions) {
        this.reactions = reactions;
    }
}
