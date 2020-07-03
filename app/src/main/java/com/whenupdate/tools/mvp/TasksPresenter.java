package com.whenupdate.tools.mvp;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import io.reactivex.Completable;

import com.whenupdate.tools.R;
import com.whenupdate.tools.common.NotifyService;
import com.whenupdate.tools.common.Task;
import com.whenupdate.tools.service.sites.ISite;
import com.whenupdate.tools.service.sites.SiteUpdate;

public class TasksPresenter {
    private MainActivity view;
    private final TaskModel model;

    TasksPresenter(TaskModel model) {
        this.model = model;
    }

    void attachView(MainActivity activity) {
        view = activity;
    }

    void detachView() {
        view = null;
    }

    void applySetting() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(view);
        setTheme(prefs);
    }

    private void setTheme(SharedPreferences prefs) {
        String theme = prefs.getString("theme", "dark");
        if (theme.equals("dark")) {
            view.setBackground(view.getResources().getColor(R.color.background_dark),
                    view.getResources().getColor(R.color.background_light));
        } else {
            view.setBackground(view.getResources().getColor(R.color.background_light),
                    view.getResources().getColor(R.color.background_dark));
        }
    }

    public void loadTasks() {
        model.loadTasks(listTasks -> {
            if (listTasks != null) {
                view.showTasks(listTasks);
                if (!listTasks.isEmpty())
                    view.hideEmptyText();
                else view.showEmptyText();
            }
        });
    }

    void viewIsReady() {
        loadTasks();
    }

    void add() {
        view.showProgress();
        Completable.fromAction(() -> {
            Task task = view.getTaskFromDialog();
            ISite update = new SiteUpdate(task.getLink(), task.getDate());
            if (task.getTitle().equals("")) {
                if (checkConnecting()) {
                    update.getTitleSite(task::setTitle);
                } else task.setTitle(task.getLink());
            }

            update.findDate((result, newDate, chapter) -> {
                task.setDate(newDate);
                task.setChapter(chapter);
                while (task.getTitle().equals("")) {}
                saveTask(task);
            });
        }).subscribe();
    }

    private void saveTask(Task task) {
        model.saveTask(task, () -> {
            view.hideProgress();
            loadTasks();
        });
    }

    public void updateTask(Task task) {
        model.updateTask(task, () -> {
            // loadTasks();
        });
    }

    public void remove(Task task) {
        model.removeTask(task, () -> {
        });
    }

    public interface IUpdateCallback {
        void onComplete(int result);
    }

    public void loadUpdate(Task task, IUpdateCallback callback) {
        if (!checkConnecting())
            return;

        loadingUpdate(task, callback);
    }

    public void loadUpdate() {
        if (!checkConnecting())
            return;

        model.loadTasks(listTasks -> {
            for (Task task : listTasks) {
                loadingUpdate(task, null);
            }

            loadTasks();
        });
    }

    private void loadingUpdate(Task task, IUpdateCallback callback) {
        ISite scu = new SiteUpdate(task.getLink(), task.getDate());
        scu.findUpDate((result, newDate, chapter) -> {
            switch (result) {
                case 1:
                    task.setDate(newDate);
                    task.setUpdate(true);
                    updateTask(task);
                    Intent intent = new Intent(view.getApplicationContext(), NotifyService.class);
                    view.startService(intent);
                    break;
                case -1:
                    view.showToast(view.getString(R.string.site_rip) + task.getLink(),
                            R.drawable.ic_sentiment_dissatisfied_toast);
                    break;
                default:
                    break;
            }

            if (callback != null) {
                view.isUpdate(task.isUpdate());
                callback.onComplete(result);
            }
        });
    }

    private boolean checkConnecting() {
        if (!model.isNetworkAvailable()) {
            view.alertConnection();
            return false;
        }

        return true;
    }

}
