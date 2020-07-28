package com.whenupdate.tools.mvp;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.whenupdate.tools.R;
import com.whenupdate.tools.common.ListTasks;
import com.whenupdate.tools.common.Task;
import com.whenupdate.tools.common.TaskAdapter;
import com.whenupdate.tools.common.desing.ScrollFABBehavior;

public class FavoritesFragment extends Fragment implements TasksPresenter.IMainContract {
    public final static String DATABASE = "list_favorites_db";

    public static TasksPresenter presenter;
    private TaskAdapter taskAdapter;
    private ImageView imgEmpty;
    private TextView textEmpty;
    private TextView textEmpty1;
    private ProgressDialog progressDialog;
    private View view;
    private BottomNavigationView nav;

    private TaskAdapter.IAdapterCallback callback = new TaskAdapter.IAdapterCallback() {
        @Override
        public void onDelete(Task task) {
            presenter.remove(task);
        }

        @Override
        public void onMove(Task task) {
            presenter.moveTask(task, FavoritesFragment.DATABASE, HomeFragment.DATABASE);
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
        return inflater.inflate(R.layout.fragment_favorites, container, false);
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

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init() {
        textEmpty = view.findViewById(R.id.emptyId);
        textEmpty1 = view.findViewById(R.id.emptyId1);
        imgEmpty = view.findViewById(R.id.emptyIdImage);
        taskAdapter = new TaskAdapter();
        nav = getActivity().findViewById(R.id.bottom_nav);

        final RecyclerView listView = view.findViewById(R.id.listView);
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

        TaskModel taskModel = new TaskModel(getContext(), FavoritesFragment.DATABASE);

        presenter = new TasksPresenter(taskModel);
        presenter.attachView(this);
        presenter.viewIsReady();
        taskAdapter.setCallback(callback);

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

    public void hideSwipeRefreshLayout() {
        //swipeRefreshLayout.setVisibility(View.GONE);
    }

    public void showSwipeRefreshLayout() {
        //swipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    private void hideFAB() {
        // floatingActionButton.hide();
    }

    private void showFAB() {
        //floatingActionButton.show();
    }


    @Override
    public void showTasks(ListTasks listTasks) {
        taskAdapter.setData(listTasks);
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
        textEmpty1.setVisibility(View.VISIBLE);
        imgEmpty.setVisibility(View.VISIBLE);
        nav.getOrCreateBadge(R.id.itemFavorites).setVisible(false);
    }

    public void hideEmptyText() {
        textEmpty.setVisibility(View.GONE);
        textEmpty1.setVisibility(View.GONE);
        imgEmpty.setVisibility(View.GONE);
        nav.getOrCreateBadge(R.id.itemFavorites).setVisible(true);
    }
}
