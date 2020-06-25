package ru.csu.ttpapp.mvp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import ru.csu.ttpapp.R;
import ru.csu.ttpapp.common.DialogOnSaveTask;
import ru.csu.ttpapp.common.ListTasks;
import ru.csu.ttpapp.common.Task;
import ru.csu.ttpapp.common.TaskAdapter;
import ru.csu.ttpapp.common.desing.ScrollFABBehavior;


public class MainActivity extends AppCompatActivity implements DialogOnSaveTask.DialogListener {

    public static Context mContext;

    public static TasksPresenter presenter;
    private TaskAdapter taskAdapter;

    private TextInputEditText editTextTitle;
    private TextInputEditText editTextLink;
    private FloatingActionButton floatingActionButton;
    private ConstraintLayout constraintLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textEmpty;

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
            DialogFragment dialog = new DialogOnSaveTask();
            dialog.show(getSupportFragmentManager(), "DialogOnSaveTask show");
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

        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorMy),
                getResources().getColor(R.color.delete_btn_on));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.postDelayed(() -> {
                presenter.loadUpdate();
                swipeRefreshLayout.setRefreshing(false);
            }, 2000);
        });
    }

    public void hideSwipeRefreshLayout() {
        swipeRefreshLayout.setRefreshing(false);
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
        newTask.setLink(editTextLink.getText().toString());
        newTask.setTitle(editTextTitle.getText().toString());
        return newTask;
    }

    public void showTasks(ListTasks listTasks) {
        taskAdapter.setData(listTasks);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        editTextTitle = dialog.getDialog().getWindow().findViewById(R.id.setName);
        editTextLink = dialog.getDialog().getWindow().findViewById(R.id.setLink);
        String textLink = editTextLink.getText().toString();
        if (!textLink.isEmpty() && ((textLink.startsWith("http://") || textLink.startsWith("https://"))
                && textLink.contains(".")))
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

    public void showLoadToast() {
        showToast(getString(R.string.loading), R.drawable.ic_update_load);
    }

    private ProgressDialog progressDialog;

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

