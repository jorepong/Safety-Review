package com.example.safetyreview2;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import com.google.android.material.checkbox.MaterialCheckBox;

public class CheckBoxTriStates extends MaterialCheckBox {
    static private final int UNCHECKED = 0;
    static private final int UNKNOW = 1;
    static private final int CHECKED = 2;
    int state;

    public CheckBoxTriStates(Context context) {
        super(context);
        init();
    }

    public CheckBoxTriStates(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckBoxTriStates(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        state = UNCHECKED;
        updateBtn();

        setOnCheckedChangeListener(new OnCheckedChangeListener() {

            // checkbox status is changed from uncheck to checked.
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (state) {
                    default:
                    case UNCHECKED:
                        state = UNKNOW;
                        break;
                    case UNKNOW:
                        state = CHECKED;
                        break;
                    case CHECKED:
                        state = UNCHECKED;
                        break;
                }
                updateBtn();
            }
        });
    }

    private void updateBtn() {
        int btnDrawable = R.drawable.ic_baseline_check_box_outline_blank_24;
        switch (state) {
            default:
            case UNCHECKED:
                btnDrawable = R.drawable.ic_baseline_check_box_outline_blank_24;
                break;
            case UNKNOW:
                btnDrawable = R.drawable.ic_baseline_indeterminate_check_box_24;
                break;
            case CHECKED:
                btnDrawable = R.drawable.ic_baseline_check_box_24;
                break;
        }

        setButtonDrawable(btnDrawable);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        updateBtn();
    }
}
