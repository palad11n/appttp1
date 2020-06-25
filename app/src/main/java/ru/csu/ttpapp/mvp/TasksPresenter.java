package ru.csu.ttpapp.mvp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.preference.PreferenceManager;

import java.util.Date;

import ru.csu.ttpapp.R;
import ru.csu.ttpapp.common.NotifyService;
import ru.csu.ttpapp.common.Task;
import ru.csu.ttpapp.service.sites.ISite;
import ru.csu.ttpapp.service.sites.SiteUpdate;

public class TasksPresenter {
    private MainActivity view;
    private final TaskModel model;
    private boolean flagUpdate = false;

    public TasksPresenter(TaskModel model) {
        this.model = model;
    }

    public void attachView(MainActivity activity) {
        view = activity;
    }

    public void detachView() {
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
            view.setBackground(Color.WHITE, Color.BLACK);
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

    public void viewIsReady() {
        loadTasks();
    }

    public void add() {
        view.showProgress();
        new Thread() {
            @Override
            public void run() {
                Task task = view.getTaskFromDialog();
                ISite update = new SiteUpdate(task.getLink(), task.getDate());
                if (task.getTitle().equals("")) {
                    if (checkConnecting()) {
                        String title = update.getTitleSite();
                        task.setTitle(title);
                    } else task.setTitle(task.getLink());
                }

                update.findUpDate((result, newDate) -> date1 = newDate);
                task.setDate(date1);
                saveTask(task);
            }
        }.start();
    }

    private Date date1;

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
        model.removeTask(task, () -> loadTasks());
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
        try {
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
                        String link = task.getLink();
                        int index = link.indexOf('/', ((link.contains("https")) ? 8 : 7));
                        String serverOff = link.substring(0, (index == -1) ? link.length() : index);
                        view.showToast(view.getString(R.string.site_rip) + serverOff
                                , R.drawable.ic_sentiment_dissatisfied_toast);
                        break;

                    default:
                        break;
                }
            });
        } catch (Exception e) {
        }
    }

    private boolean checkConnecting() {
        if (!model.isNetworkAvailable()) {
            view.alertConnection();
            return false;
        }

        return true;
    }

}
