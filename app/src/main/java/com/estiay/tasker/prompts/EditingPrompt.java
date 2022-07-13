package com.estiay.tasker.prompts;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.estiay.tasker.R;

public class EditingPrompt extends Dialog implements View.OnClickListener {
    public Activity activity;
    public Dialog dialog;
    public Button accept, reject;

    public EditingPrompt(@NonNull Context context) {
        super(context);
        this.activity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.discard_changes_prompt);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        accept = findViewById(R.id.btn_okay_edit_item_prompt);
        reject = findViewById(R.id.btn_cancel_edit_item_prompt);
        accept.setOnClickListener((View.OnClickListener) this);
        reject.setOnClickListener((View.OnClickListener) this);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_okay_edit_item_prompt:
                activity.finish();
                break;
            case R.id.btn_cancel_edit_item_prompt:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
