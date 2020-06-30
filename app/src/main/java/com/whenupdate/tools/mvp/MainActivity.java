package com.whenupdate.tools.mvp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.whenupdate.tools.R;
import com.whenupdate.tools.common.DialogCreateTask;
import com.whenupdate.tools.common.ListTasks;
import com.whenupdate.tools.common.Task;
import com.whenupdate.tools.common.TaskAdapter;
import com.whenupdate.tools.common.desing.ScrollFABBehavior;


public class MainActivity extends AppCompatActivity implements DialogCreateTask.DialogListener {

    public static Context mContext;

    public static TasksPresenter presenter;
    private TaskAdapter taskAdapter;

    private TextInputLayout inputTextTitle, inputTextLink;
    private TextInputEditText editTextTitle, editTextLink;
    private FloatingActionButton floatingActionButton;
    private ConstraintLayout constraintLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textEmpty;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        if (mContext == null) mContext = MainActivity.this;

        constraintLayout = (ConstraintLayout) findViewById(R.id.cl_main);
        textEmpty = findViewById(R.id.emptyId);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(v -> {
            initDialogCreateTask();

        });

        taskAdapter = new TaskAdapter();

        final RecyclerView listView = findViewById(R.id.listView);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(taskAdapter);
        listView.setVisibility(View.VISIBLE);
        listView.addOnScrollListener(new ScrollFABBehavior() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });

        TaskModel taskModel = new TaskModel(mContext);

        presenter = new TasksPresenter(taskModel);
        presenter.attachView(this);
        presenter.viewIsReady();

        initSwipeRefreshLayout();

    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorMy),
                getResources().getColor(R.color.delete_btn_on));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.postDelayed(() -> {
                presenter.loadUpdate();
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }, 2000);
        });
    }

    private void initDialogCreateTask() {
        DialogFragment dialog = new DialogCreateTask();
        dialog.show(getSupportFragmentManager(), "Create task - show");
        dialog.setCancelable(false);
        getSupportFragmentManager().executePendingTransactions();
        inputTextTitle = dialog.getDialog().findViewById(R.id.textInputLayoutSetName);
        editTextTitle = dialog.getDialog().findViewById(R.id.setName);
        inputTextLink = dialog.getDialog().findViewById(R.id.textInputLayoutSetLink);

        inputTextLink.setHelperText(getString(R.string.helper_create_text));

        editTextLink = dialog.getDialog().findViewById(R.id.setLink);
        if (editTextLink != null)
            editTextLink.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (validateLink(s.toString())) {
                        inputTextLink.setError(null);
                    } else {
                        inputTextLink.setError(getString(R.string.text_error_link));
                    }
                }
            });
    }

    private boolean validateLink(String textLink) {
        return (!textLink.isEmpty() && textLink.startsWith("http")
                && Patterns.WEB_URL.matcher(textLink).matches());
    }

    private void hideViews() {
        floatingActionButton.hide();
    }

    private void showViews() {
        floatingActionButton.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.applySetting();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemPreferences:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.itemAbout:
                showDialogPref(R.layout.dialog_about);
                return true;
            case R.id.itemHelp:
                showDialogPref(R.layout.dialog_help);
                return true;
            case R.id.itemRating:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = getLayoutInflater().inflate(R.layout.dialog_rating, null);
                builder.setView(view)
                        .setPositiveButton(R.string.done, (dialog, which) -> {
                            final String appPackageName = getPackageName();
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException ex) {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        })
                        .setNegativeButton(R.string.after_rating, (dialog, id) -> dialog.dismiss());
                builder.create();
                builder.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialogPref(int resId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(resId, null);
        builder.setView(view)
                .setPositiveButton(R.string.cancel, (dialog, id) -> dialog.dismiss());
        builder.create();
        builder.show();
    }

    public Task getTaskFromDialog() {
        Task newTask = new Task();
        newTask.setLink(inputTextLink.getEditText().getText().toString());
        newTask.setTitle(inputTextTitle.getEditText().getText().toString());
        return newTask;
    }

    public void showTasks(ListTasks listTasks) {
        taskAdapter.setData(listTasks);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (validateLink(editTextLink.getText().toString()))
            presenter.add();
        else
            showToast(getString(R.string.link_empty_error), R.drawable.ic_sentiment_dissatisfied_toast);
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    public void isUpdate(boolean isExistUpdate) {
        if (isExistUpdate)
            showToast(getString(R.string.update_exist));
        else showToast(getString(R.string.update_not), R.drawable.ic_sentiment_dissatisfied_toast);
    }

    public void alertConnection() {
        showToast(getString(R.string.check_internet), R.drawable.ic_wifi_off_24px);
    }

    public void showToast(String textToast) {
        showToast(textToast, R.drawable.ic_sentiment_smale_toast);
    }

    public void showToast(String textToast, int resIdIcon) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_root));
        TextView textView = layout.findViewById(R.id.toast_text);
        ImageView imageView = layout.findViewById(R.id.toast_icon);

        textView.setText(textToast);
        imageView.setImageResource(resIdIcon);

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(layout);
        toast.show();
    }

    public void showProgress() {
        progressDialog = ProgressDialog.show(this, "", getString(R.string.loading));
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void setBackground(int colorBackground, int colorText) {
        constraintLayout.setBackgroundColor(colorBackground);
        textEmpty.setTextColor(colorText);
    }

    public void showEmptyText() {
        textEmpty.setVisibility(View.VISIBLE);
    }

    public void hideEmptyText() {
        textEmpty.setVisibility(View.GONE);
    }
}