package vn.edu.tdtu.buddyblend.utils;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

public class PopupMenuUtils {
    private PopupMenuItemClickListener listener;
    private Context context;
    private int menuRes;
    private View view;

    public PopupMenuUtils(Context context, int menuRes, PopupMenuItemClickListener listener) {
        this.listener = listener;
        this.context = context;
        this.menuRes = menuRes;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public interface PopupMenuItemClickListener{
        public void onClick(MenuItem menuItem);
    }

    public void show(){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(menuRes, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            listener.onClick(item);
            return true;
        });

        popupMenu.show();
    }
}
