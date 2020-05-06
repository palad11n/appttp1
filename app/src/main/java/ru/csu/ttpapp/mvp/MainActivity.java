package ru.csu.ttpapp.mvp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
    private ConstraintLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    private void init() {
        if (mContext == null) mContext = MainActivity.this;

        coordinatorLayout = (ConstraintLayout) findViewById(R.id.cl_main);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new DialogOnSaveTask();
                dialog.show(getSupportFragmentManager(), "DialogOnSaveTask show");
            }
        });

        taskAdapter = new TaskAdapter();

        RecyclerView listView = findViewById(R.id.listView);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setTitle(R.string.about_app)
                        .setMessage(R.string.about_description)
                        .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                builder.create();
                builder.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        if (!textLink.isEmpty() && (textLink.startsWith("http://") || textLink.startsWith("https://")
                && textLink.contains(".")))
            presenter.add();
        else
            Snackbar.make(coordinatorLayout, getString(R.string.link_empty_error), Snackbar.LENGTH_LONG).show();
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    public void isUpdate(boolean isExistUpdate) {
        if (isExistUpdate)
            Snackbar.make(coordinatorLayout, getString(R.string.update_exist), Snackbar.LENGTH_SHORT)
                    .show();
        else Snackbar.make(coordinatorLayout, getString(R.string.update_not), Snackbar.LENGTH_SHORT)
                .show();
    }

    private AlertDialog.Builder builder;
    private AlertDialog progressDialog;

    public void showProgressDialog() {
        //  findViewById(R.id.progress_spinner).setVisibility(View.VISIBLE);
        if (progressDialog == null)
            progressDialog = getDialogProgressBar().create();
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        //   findViewById(R.id.progress_spinner).setVisibility(View.GONE);
    }

    private AlertDialog.Builder getDialogProgressBar() {
        if (builder == null) {
            builder = new AlertDialog.Builder(this);
            final ProgressBar progressBar = new ProgressBar(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(lp);
            progressBar.setProgress(0);
            builder.setView(progressBar);
            builder.setCancelable(false);
        }
        return builder;
    }

}

