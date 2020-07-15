package com.whenupdate.tools.mvp;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import io.reactivex.Completable;

import com.whenupdate.tools.R;
import com.whenupdate.tools.common.Task;
import com.whenupdate.tools.service.sites.ISite;
import com.whenupdate.tools.service.sites.SiteUpdate;

public class TasksPresenter {
    private MainActivity view;
    private final TaskModel model;

    /**
     * Обратный вызов для уведомления о получении ответа с сайта
     */
    public interface IUpdateCallback {
        void onComplete(int result);
    }


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
        String theme = prefs.getString("theme", "light");
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
                if (!listTasks.isEmpty()) {
                    view.hideEmptyText();
                    view.showSwipeRefreshLayout();
                } else {
                    view.showEmptyText();
                    view.hideSwipeRefreshLayout();
                }
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
            ISite update = new SiteUpdate(task.getLink(), task.getDate(), task.getChapter());
            boolean isConnect = checkConnecting();
            if (task.getTitle().equals("")) {
                if (isConnect) {
                    update.getTitleSite(task::setTitle);
                } else task.setTitle(task.getLink());
            }

            update.findDate((result, newDate, chapter) -> {
                task.setDate(newDate);
                task.setChapter(chapter);
                long start = System.currentTimeMillis();
                long timeConsumedMillis = 0;
                //30 секунд
                while (task.getTitle().equals("") || timeConsumedMillis > 30000) {
                    timeConsumedMillis = System.currentTimeMillis() - start;
                }
                if (!task.getTitle().equals(""))
                    saveTask(task);
                else view.showToast(view.getString(R.string.fail_save),
                        R.drawable.ic_sentiment_dissatisfied_toast);
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

    public void loadUpdate(Task task, IUpdateCallback callback) {
        if (!checkConnecting())
            return;
        loadingUpdate(task, callback);
    }

    public void loadUpdate(IUpdateCallback callback) {
        if (!checkConnecting()) {
            callback.onComplete(-1);
            return;
        }

        model.loadTasks(listTasks -> {
            for (Task task : listTasks) {
                if (!checkConnecting()) {
                    callback.onComplete(-1);
                    return;
                }
                loadingUpdate(task, null);
            }
            callback.onComplete(0);
        });
    }

    private void loadingUpdate(Task task, IUpdateCallback callback) {
        ISite scu = new SiteUpdate(task.getLink(), task.getDate(), task.getChapter());
        scu.findUpDate((result, newDate, chapter) -> {
            switch (result) {
                case 1:
                    task.setDate(newDate);
                    task.setChapter(chapter);
                    task.setUpdate(true);
                    updateTask(task);
                    model.startNotifyService();
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
        if (!TaskModel.isNetworkAvailable()) {
            view.alertConnection();
            return false;
        }

        return true;
    }

}
