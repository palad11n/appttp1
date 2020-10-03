package com.whenupdate.tools.mvp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

public class HomeFragment extends Fragment implements TasksPresenter.IMainContract {
    public final static String DATABASE = "list_db";

    public static TasksPresenter presenter;
    private TaskAdapter taskAdapter;

    private TextInputLayout inputTextTitle, inputTextLink;
    private TextInputEditText editTextTitle, editTextLink;
    private FloatingActionButton floatingActionButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textEmpty;
    private ImageView imgEmpty;
    private ProgressDialog progressDialog;
    private View view;

    private RecyclerView listView;

    private TaskAdapter.IAdapterCallback callback = new TaskAdapter.IAdapterCallback() {
        @Override
        public void onDelete(Task task) {
            presenter.remove(task);
        }

        @Override
        public void onMove(Task task) {
            presenter.moveTask(task, HomeFragment.DATABASE, FavoritesFragment.DATABASE);
            presenter.remove(task);
        }

        @Override
        public void onShowEmpty() {
            showEmptyText();
        }

        @Override
        public void onLoadUpdate(Task task, TasksPresenter.IUpdateCallback callback) {
            presenter.loadUpdate(task, result -> callback.onComplete(result));
        }

        @Override
        public void onUpdateTask(Task task) {
            presenter.updateTask(task);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskModel.setNewTheme(getActivity());
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    private void init() {
        textEmpty = view.findViewById(R.id.emptyId);
        imgEmpty = view.findViewById(R.id.emptyIdImage);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(v -> {
            initDialogCreateTask();
        });

        taskAdapter = new TaskAdapter();

        listView = view.findViewById(R.id.listView);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(taskAdapter);
        listView.setVisibility(View.VISIBLE);
        listView.addOnScrollListener(new ScrollFABBehavior() {
            @Override
            public void onHide() {
                hideFAB();
            }

            @Override
            public void onShow() {
                showFAB();
            }
        });

        TaskModel taskModel = new TaskModel(getContext(), HomeFragment.DATABASE);

        presenter = new TasksPresenter(taskModel);
        presenter.attachView(this);
        presenter.viewIsReady();
        taskAdapter.setCallback(callback);

        initSwipeRefreshLayout();
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.itemSearch).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                taskAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                taskAdapter.filter(newText);
                return true;
            }
        });

        searchView.setIconifiedByDefault(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorMy),
                getResources().getColor(R.color.delete_btn_on));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.postDelayed(() -> {
                presenter.loadUpdate(result -> {
                    swipeRefreshLayout.setRefreshing(false);
                });
            }, 1000);
        });
    }

    public void hideSwipeRefreshLayout() {
        swipeRefreshLayout.setVisibility(View.GONE);
    }

    public void showSwipeRefreshLayout() {
        swipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    private void initDialogCreateTask() {
        DialogCreateTask dialog = DialogCreateTask.newInstance();
        dialog.setTargetFragment(this, 0);
        FragmentManager fm = getParentFragmentManager();

        dialog.show(fm.beginTransaction(), "Create task - show");
        fm.executePendingTransactions();

        inputTextTitle = dialog.getDialog().findViewById(R.id.textInputLayoutSetName);
        editTextTitle = dialog.getDialog().findViewById(R.id.setName);
        inputTextLink = dialog.getDialog().findViewById(R.id.textInputLayoutSetLink);

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
                        inputTextLink.setError("");
                    } else {
                        inputTextLink.setError("https://path/to/manga_or_other/");
                    }
                }
            });
    }

    private boolean validateLink(String textLink) {
        textLink = textLink.trim();
        return (!textLink.isEmpty() && textLink.startsWith("https")
                && Patterns.WEB_URL.matcher(textLink).matches());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    if (validateLink(editTextLink.getText().toString()))
                        presenter.add(getTaskFromDialog());
                    else
                        showToast(getString(R.string.link_empty_error), R.drawable.ic_sentiment_dissatisfied_toast);
                    // dialog.dismiss();
                } else {

                }
        }
    }

    private void hideFAB() {
        floatingActionButton.hide();
    }

    private void showFAB() {
        floatingActionButton.show();
    }

    @Override
    public void addedTask(Task task) {
        taskAdapter.addItem(task);
        listView.smoothScrollToPosition(taskAdapter.getItemCount() + 1);
    }

    @Override
    public void updatedTask(Task task) {
        taskAdapter.updateItem(task);
        listView.smoothScrollToPosition(0);
    }

    @Override
    public void showTasks(ListTasks listTasks) {
        taskAdapter.setData(listTasks);
    }

    public void onDialogPositiveClick(DialogFragment dialog) {
        if (validateLink(editTextLink.getText().toString()))
            presenter.add(getTaskFromDialog());
        else
            showToast(getString(R.string.link_empty_error), R.drawable.ic_sentiment_dissatisfied_toast);
        dialog.dismiss();
    }

    private Task getTaskFromDialog() {
        Toast.makeText(getContext(), R.string.in_process_notify, Toast.LENGTH_SHORT)
                .show();
        Task newTask = new Task();
        newTask.setLink(inputTextLink.getEditText().getText().toString().trim());
        newTask.setTitle(inputTextTitle.getEditText().getText().toString().trim());
        return newTask;
    }

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

    public void showToastSaveFailed() {
        showToast(getString(R.string.fail_save),
                R.drawable.ic_sentiment_dissatisfied_toast);
    }

    public void showToastSiteRip(String link) {
        showToast(getString(R.string.site_rip) + link,
                R.drawable.ic_sentiment_dissatisfied_toast);
    }

    public void showToast(String textToast) {
        showToast(textToast, R.drawable.ic_sentiment_smale_toast);
    }

    public void showToast(String textToast, int resIdIcon) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) view.findViewById(R.id.toast_root));
        TextView textView = layout.findViewById(R.id.toast_text);
        ImageView imageView = layout.findViewById(R.id.toast_icon);

        textView.setText(textToast);
        imageView.setImageResource(resIdIcon);

        Toast toast = new Toast(getContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(layout);
        toast.show();
    }

    public void showProgress() {
        progressDialog = ProgressDialog.show(getContext(), "", getString(R.string.loading));
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void showEmptyText() {
        textEmpty.setVisibility(View.VISIBLE);
        imgEmpty.setVisibility(View.VISIBLE);
    }

    public void hideEmptyText() {
        textEmpty.setVisibility(View.GONE);
        imgEmpty.setVisibility(View.GONE);
    }
}
