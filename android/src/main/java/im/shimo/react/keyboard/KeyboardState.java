package im.shimo.react.keyboard;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import com.facebook.react.uimanager.DisplayMetricsHolder;
import com.facebook.react.uimanager.PixelUtil;

import java.util.ArrayList;

public class KeyboardState {
    private Rect mVisibleViewArea;
    private int mMinKeyboardHeightDetected = (int) PixelUtil.toPixelFromDIP(60);
    private int mKeyboardHeight = 0;
    private boolean mKeyboardStatus = false;
    private ViewTreeObserver.OnGlobalLayoutListener mLayoutListener;
    private static ArrayList<OnKeyboardChangeListener> mOnKeyboardChangeListeners;

    public KeyboardState(final View rootView) {
        ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
        mVisibleViewArea = new Rect();
        mOnKeyboardChangeListeners = new ArrayList<>();

        mLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getWindowVisibleDisplayFrame(mVisibleViewArea);
                int keyboardHeight = DisplayMetricsHolder.getWindowDisplayMetrics().heightPixels - mVisibleViewArea.bottom;

                if (keyboardHeight == mKeyboardHeight) {
                    return;
                } else {
                    mKeyboardHeight = keyboardHeight;
                }

                mKeyboardStatus = mKeyboardHeight > mMinKeyboardHeightDetected;

                for (int i = 0; i < mOnKeyboardChangeListeners.size(); i++) {
                    OnKeyboardChangeListener listener = mOnKeyboardChangeListeners.get(i);
                    if (mKeyboardStatus) {
                        listener.onKeyboardShown(DisplayMetricsHolder.getWindowDisplayMetrics().widthPixels, mKeyboardHeight);
                    } else {
                        listener.onKeyboardClosed();
                    }
                }

            }
        };

        mLayoutListener.onGlobalLayout();
        viewTreeObserver.addOnGlobalLayoutListener(mLayoutListener);
    }

    public void addOnKeyboardChangeListener(OnKeyboardChangeListener listener) {
        mOnKeyboardChangeListeners.add(listener);
    }

    public void removeOnKeyboardChangeListener(OnKeyboardChangeListener listener) {
        mOnKeyboardChangeListeners.remove(listener);
    }

    public int getKeyboardHeight() {
        return mKeyboardHeight;
    }

    public int getKeyboardWidth() {
        return DisplayMetricsHolder.getWindowDisplayMetrics().widthPixels;
    }

    public boolean getKeyboardStatus() {
        return mKeyboardStatus;
    }

    public interface OnKeyboardChangeListener {
        void onKeyboardShown(int keyboardWidth, int keyboardHeight);
        void onKeyboardClosed();
    }

    public void destroy(final View rootView) {
        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(mLayoutListener);
    }
}
