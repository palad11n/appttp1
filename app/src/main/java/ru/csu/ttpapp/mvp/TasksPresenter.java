package ru.csu.ttpapp.mvp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.preference.PreferenceManager;

import java.util.Date;

import ru.csu.ttpapp.R;
import ru.csu.ttpapp.common.ListTasks;
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

    public void applySetting() {
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
        model.loadTasks(new TaskModel.ILoadCallback() {
            @Override
            public void onLoad(ListTasks listTasks) {
                if (listTasks != null) {
                    view.showTasks(listTasks);
                    if (!listTasks.isEmpty())
                        view.hideEmptyText();
                    else view.showEmptyText();
                }
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
                ISite update = new SiteUpdate(task.getLink());
                if (task.getTitle().equals("")) {
                    if (checkConnecting()) {
                        String title = update.getTitleSite();
                        task.setTitle(title);
                    } else task.setTitle(task.getLink());
                }
                task.setDate(update.findUpDate());
                saveTask(task);
            }
        }.start();
    }

    private void saveTask(Task task) {
        model.saveTask(task, new TaskModel.ICompleteCallback() {
            @Override
            public void onComplete() {
                view.hideProgress();
                loadTasks();
            }
        });
    }

    public void updateTask(Task task) {
        model.updateTask(task, new TaskModel.ICompleteCallback() {
            @Override
            public void onComplete() {
                // loadTasks();
            }
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
        view.isUpdate(task.isUpdate());

        boolean isNotify = flagUpdate;
        flagUpdate = false;

        return isNotify;
    }

    public void loadUpdate() {
        if (!checkConnecting())
            return;

        new Thread(){
            @Override
            public void run() {
                model.loadTasks(new TaskModel.ILoadCallback() {
                    @Override
                    public void onLoad(ListTasks listTasks) {
                        for (Task task : listTasks) {
                            loadingUpdate(task);
                        }
                    }
                });

                view.getSwipeRefreshLayout().setRefreshing(false);
            }
        }.start();
    }

    private void loadingUpdate(Task task) {
        try {
            ISite scu = new SiteUpdate(task.getLink());
            Date newDate = scu.findUpDate();
            if (newDate != null) {
                if (newDate.after(task.getDate())) {
                    task.setDate(newDate);
                    task.setUpdate(true);
                    flagUpdate = true;
                    updateTask(task);

                    Intent intent = new Intent(view.getApplicationContext(), NotifyService.class);
                    view.startService(intent);
                }
            } else {
                String link = task.getLink();
                int index = link.indexOf('/', ((link.contains("https")) ? 8 : 7));
                String serverOff = link.substring(0, (index == -1) ? link.length() : index);
                view.showToast(view.getString(R.string.site_rip) + serverOff
                        , R.drawable.ic_sentiment_dissatisfied_toast);
            }
        } catch (Exception e) {
        }
    }

    private boolean checkConnecting() {
        if (!model.isNetworkAvailable()) {
            view.alertConnection();
            view.getSwipeRefreshLayout().setRefreshing(false);
            return false;
        }

        return true;
    }

}
