package com.whenupdate.tools.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.whenupdate.tools.R;

public class DialogCreateTask extends DialogFragment {

    public interface DialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    private DialogListener mListener;

    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DialogListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DialogListener");
        }
    }

    public static DialogCreateTask newInstance(){
        DialogCreateTask dialogCreateTask = new DialogCreateTask();
//        Bundle args = new Bundle();
//        args.putString("title", title);
//        dialogCreateTask.setArguments(args);
        return dialogCreateTask;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        builder.setCancelable(false)
                .setView(inflater.inflate(R.layout.dialog_create, null))
                .setPositiveButton(R.string.done, (dialog, id) -> mListener.onDialogPositiveClick(DialogCreateTask.this))
                .setNegativeButton(R.string.cancel, (dialog, id) -> mListener.onDialogNegativeClick(DialogCreateTask.this));
        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create, container);
        return view;
    }
}
