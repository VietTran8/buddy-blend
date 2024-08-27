package vn.edu.tdtu.buddyblend.models;

import java.io.Serializable;

import vn.edu.tdtu.buddyblend.enums.EReactionType;

public class TopReacts implements Serializable {
    private EReactionType type;
    private int count;

    public TopReacts(EReactionType type, int count) {
        this.type = type;
        this.count = count;
    }

    public TopReacts() {
    }

    public EReactionType getType() {
        return type;
    }

    public void setType(EReactionType type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
