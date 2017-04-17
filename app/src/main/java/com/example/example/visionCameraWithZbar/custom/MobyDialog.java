package com.example.example.visionCameraWithZbar.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.example.visionCameraWithZbar.R;

/**
 * Created by alphaadmin on 26/9/2559.
 */

public class MobyDialog extends DialogFragment {

    private String mTitle, mMessage, positiveButtonText, negativeButtonText;

    private Button btnPositive, btnNegative;
    private TextView tvTitle, tvMessage;
    private View separateView;
    private OnClickListener positiveClick;
    private OnClickListener negativeClick;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_moby, container, false);
        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvMessage = (TextView) v.findViewById(R.id.tvMessage);
        btnPositive = (Button) v.findViewById(R.id.btnPositive);
        btnNegative = (Button) v.findViewById(R.id.btnNegative);
        separateView = v.findViewById(R.id.separateView);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString("title");
            mMessage = savedInstanceState.getString("message");
            positiveButtonText = savedInstanceState.getString("positive");
            negativeButtonText = savedInstanceState.getString("negative");
        }

        btnPositive.setText(positiveButtonText);
        btnNegative.setText(negativeButtonText);

        if (negativeClick == null) {
            separateView.setVisibility(View.GONE);
            btnNegative.setVisibility(View.GONE);
        }

        btnPositive.setOnClickListener(v -> {
            if (positiveClick != null) {
                positiveClick.onClick(v, MobyDialog.this);
            } else {
                dismiss();
            }
        });

        btnNegative.setOnClickListener(v -> {
            if (negativeClick != null) {
                negativeClick.onClick(v, MobyDialog.this);
            } else {
                dismiss();
            }
        });

        tvTitle.setText(mTitle);
        tvMessage.setText(mMessage);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("title", mTitle);
        outState.putString("message", mMessage);
        outState.putString("positive", positiveButtonText);
        outState.putString("negative", negativeButtonText);
        super.onSaveInstanceState(outState);
    }

    private void setTitle(String text) {
        this.mTitle = text;
    }

    private void setMessage(String text) {
        this.mMessage = text;
    }

    private void setPositiveButton(String text, OnClickListener l) {
        positiveButtonText = text;
        positiveClick = l;
    }

    private void setNegativeButton(String text, OnClickListener l) {
        negativeButtonText = text;
        negativeClick = l;
    }

    public void setPositiveButton(OnClickListener l) {
        positiveClick = l;
    }

    public void setNegativeButton(OnClickListener l) {
        negativeClick = l;
    }

    public interface OnClickListener {
        void onClick(View v, DialogFragment d);
    }

    public static class Builder {
        private String mTitle, mMessage, positiveButtonText,negativeButtonText;
        private boolean isCancelable = true;
        private Context context;

        private OnClickListener positiveClick;

        private OnClickListener negativeClick;
        
        public Builder(Context context){
            this.context = context;
        }

        public Builder setTitle(String text){
            this.mTitle = text;
            return this;
        }

        public Builder setMessage(String text){
            this.mMessage = text;
            return this;
        }

        public Builder setTitle(int stringRes){
            this.mTitle = context.getString(stringRes);
            return this;
        }

        public Builder setMessage(int stringRes){
            this.mMessage = context.getString(stringRes);
            return this;
        }

        public Builder setPositiveButton(String text, OnClickListener l){
            this.positiveClick = l;
            this.positiveButtonText = text;
            return this;
        }

        public Builder setNegativeButton(String text, OnClickListener l){
            this.negativeClick = l;
            this.negativeButtonText = text;
            return this;
        }

        public Builder setPositiveButton(int stringRes, OnClickListener l){
            this.positiveClick = l;
            this.positiveButtonText = context.getString(stringRes);
            return this;
        }

        public Builder setNegativeButton(int stringRes, OnClickListener l){
            this.negativeClick = l;
            this.negativeButtonText = context.getString(stringRes);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            isCancelable = cancelable;
            return this;
        }

        public MobyDialog show(FragmentManager manager, String tag){
            MobyDialog dialog = new MobyDialog();
            dialog.setTitle(mTitle);
            dialog.setMessage(mMessage);
            dialog.setPositiveButton(positiveButtonText, positiveClick);
            dialog.setNegativeButton(negativeButtonText, negativeClick);
            dialog.setCancelable(isCancelable);
            dialog.show(manager, tag);
            return dialog;
        }
    }
}
