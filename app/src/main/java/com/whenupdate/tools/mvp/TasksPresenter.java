package com.whenupdate.tools.mvp;

import io.reactivex.Completable;

import com.whenupdate.tools.common.ListTasks;
import com.whenupdate.tools.common.Task;
import com.whenupdate.tools.service.sites.ISite;
import com.whenupdate.tools.service.sites.SiteUpdate;

public class TasksPresenter {
    private IMainContract view;
    private final TaskModel model;

    TasksPresenter(TaskModel model) {
        this.model = model;
    }

    void attachView(IMainContract activity) {
        view = activity;
    }

    void detachView() {
        view = null;
    }

    void applySetting() {
        //TaskModel.setNewTheme(view);
    }

    public void loadTasks() {
        model.loadTasks(listTasks -> {
            if (listTasks != null) {
                if (view == null) return;
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

    void add(Task task) {
        //view.showProgress();
        Completable.fromAction(() -> {
            //Task task = view.getTaskFromDialog();
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
                // 30 секунд
                while (task.getTitle().equals("") && timeConsumedMillis < 30000) {
                    timeConsumedMillis = System.currentTimeMillis() - start;
                }
                if (!task.getTitle().equals(""))
                    saveTask(task);
                else {
                    //view.hideProgress();
                    view.showToastSaveFailed();
                }
            });
        }).subscribe();
    }

    private void saveTask(Task task) {
        model.saveTask(task, () -> {
            //view.hideProgress();
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
                    view.showToastSiteRip(task.getLink());
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

    public void moveTask(Task task, String from, String to) {
        if (from == HomeFragment.DATABASE) {
            FavoritesFragment.presenter.saveTask(task);
        } else {
            HomeFragment.presenter.saveTask(task);
        }
        model.removeTask(task, this::loadTasks);
    }

    interface IMainContract {
        void showTasks(ListTasks listTasks);

        void hideEmptyText();

        void showEmptyText();

        void showSwipeRefreshLayout();

        void hideSwipeRefreshLayout();

        void showProgress();

        void hideProgress();

        void showToast(String textToast, int resIdIcon);

        void isUpdate(boolean isExistUpdate);

        void alertConnection();

        void showToastSiteRip(String link);

        void showToastSaveFailed();
    }

    /**
     * Обратный вызов для уведомления о получении ответа с сайта
     */
    public interface IUpdateCallback {
        void onComplete(int result);
    }
}
