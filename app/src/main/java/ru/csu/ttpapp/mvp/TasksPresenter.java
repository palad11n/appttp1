package ru.csu.ttpapp.mvp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.preference.PreferenceManager;

import ru.csu.ttpapp.R;
import ru.csu.ttpapp.common.NotifyService;
import ru.csu.ttpapp.common.Task;
import ru.csu.ttpapp.service.sites.ISite;
import ru.csu.ttpapp.service.sites.SiteUpdate;

public class TasksPresenter {
    private MainActivity view;
    private final TaskModel model;
    private boolean flagUpdate = false;

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
            view.setBackground(view.getResources().getColor(R.color.background_dark), Color.WHITE);
        } else {
            view.setBackground(view.getResources().getColor(R.color.background_light), Color.BLACK);
        }
    }

    private void loadTasks() {
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
        Task task = view.getTaskFromDialog();
        ISite update = new SiteUpdate(task.getLink(), task.getDate());
        if (task.getTitle().equals("")) {
            if (checkConnecting()) {
                update.getTitleSite(task::setTitle);
            } else task.setTitle(task.getLink());
        }

        update.findUpDate((result, newDate) -> {
            task.setDate(newDate);
            while (task.getTitle().equals("")){}
            saveTask(task);
        });
    }

    private void saveTask(Task task) {
        view.showProgress();
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
        model.removeTask(task, new TaskModel.ICompleteCallback() {
            @Override
            public void onComplete() {
                loadTasks();
            }
        });
    }

    public boolean loadUpdate(Task task) {
        if (!checkConnecting())
            return false;

        loadingUpdate(task);

        boolean isNotify = flagUpdate;
        flagUpdate = false;

        view.isUpdate(task.isUpdate());
        return isNotify;
    }

    public void loadUpdate() {
        if (!checkConnecting())
            return;
        model.loadTasks(listTasks -> {
            for (Task task : listTasks) {
                loadingUpdate(task);
            }
        });
    }

    private void loadingUpdate(Task task) {
        ISite scu = new SiteUpdate(task.getLink(), task.getDate());
        scu.findUpDate((result, newDate) -> {
            switch (result) {
                case 1:
                    task.setDate(newDate);
                    task.setUpdate(true);
                    flagUpdate = true;
                    updateTask(task);
                    Intent intent = new Intent(view.getApplicationContext(), NotifyService.class);
                    view.startService(intent);
                    break;
                case -1:
                    view.showToast(view.getString(R.string.site_rip) + task.getLink()
                            , R.drawable.ic_sentiment_dissatisfied_toast);
                    break;
                default:
                    break;
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
