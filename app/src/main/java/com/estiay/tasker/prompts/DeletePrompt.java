package com.estiay.tasker.prompts;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.estiay.tasker.R;

public class DeletePrompt extends Dialog implements View.OnClickListener {

    public interface Callback{
        void delete();
    }

    public Activity activity;
    public Dialog dialog;
    public Button accept, reject;
    public Callback callback;

    public DeletePrompt(@NonNull Context context, Callback callback) {
        super(context);
        this.activity = (Activity) context;
        this.callback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.delete_prompt);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        accept = findViewById(R.id.btn_okay_delete_item_prompt);
        reject = findViewById(R.id.btn_cancel_delete_item_prompt);
        accept.setOnClickListener((View.OnClickListener) this);
        reject.setOnClickListener((View.OnClickListener) this);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_okay_delete_item_prompt:
                this.callback.delete();
                break;
            case R.id.btn_cancel_delete_item_prompt:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
