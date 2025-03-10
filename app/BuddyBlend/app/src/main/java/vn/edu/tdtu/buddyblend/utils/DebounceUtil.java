package vn.edu.tdtu.buddyblend.utils;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

public class DebounceUtil implements TextWatcher {
    private static final long DEBOUNCE_DELAY_MS = 300;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    public interface OnDebouncedListener {
        void onDebouncedTextChanged(CharSequence text);
        default void onBeforeTextChanged(CharSequence text){}
        void onTextChanged(CharSequence charSequence);
    }

    private final OnDebouncedListener listener;

    public DebounceUtil(OnDebouncedListener listener) {
        this.listener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        listener.onBeforeTextChanged(charSequence);
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        listener.onTextChanged(charSequence);
    }

    @Override
    public void afterTextChanged(final Editable editable) {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }

        runnable = () -> listener.onDebouncedTextChanged(editable.toString());
        handler.postDelayed(runnable, DEBOUNCE_DELAY_MS);
    }
}
