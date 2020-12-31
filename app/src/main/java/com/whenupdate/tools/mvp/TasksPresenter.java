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
        Completable.fromAction(() -> {
            //Task task = view.getTaskFromDialog();
            ISite update = new SiteUpdate(task.getLink(), task.getDate(), task.getChapter());
            boolean isConnect = checkConnecting();
            if (task.getTitle().equals("")) {
                if (isConnect) {
                    update.getTitleSite(task::setTitle);
                } else task.setTitle(task.getLink());
            }
            if (checkConnecting())
                update.findDate((result, newDate, chapter, hrefIcon) -> {
                    if (newDate != null)
                        task.setDate(newDate);
                    task.setChapter(chapter);
                    task.setIcon(hrefIcon);
                    long start = System.currentTimeMillis();
                    long timeConsumedMillis = 0;
                    // 35 секунд
                    while (task.getTitle().equals("") && timeConsumedMillis < 35000) {
                        timeConsumedMillis = System.currentTimeMillis() - start;
                    }
                    if (!task.getTitle().equals(""))
                        saveTask(task);
                    else {
                        //view.hideProgress();
                        view.showToastSaveFailed();
                    }
                });
            else saveTask(task);
        }).subscribe();
    }

    private void saveTask(Task task) {
        model.saveTask(task, () -> {
            //view.hideProgress();
            //loadTasks();
            if (view != null)
                view.addedTask(task);
        });
    }

    void updateTask(Task task) {
        model.updateTask(task, () -> {
            view.updatedTask(task);
        });
    }

    void remove(Task task) {
        model.removeTask(task, () -> {
        });
    }

    void loadUpdate(Task task, IUpdateCallback callback) {
        if (!checkConnecting())
            return;
        loadingUpdate(task, callback, null);
    }

    private int size = 0;

    void loadUpdate(IUpdateCallback callback) {
        if (!checkConnecting() && callback != null) {
            callback.onComplete(-1);
            return;
        }

        model.loadTasks(listTasks -> {
            for (Task task : listTasks) {
                if (!checkConnecting()) {
                    if (callback != null) {
                        callback.onComplete(-1);
                    }
                    return;
                }

                loadingUpdate(task, null, () -> {
                    size++;
                    if (size == listTasks.size()) {
                        size = 0;
                        if (callback != null) {
                            callback.onComplete(0);
                        }
                    }
                });
            }
        });
    }

    private void loadingUpdate(Task task, IUpdateCallback callback, ICompleteCallback callbackPrivate) {
        ISite scu = new SiteUpdate(task.getLink(), task.getDate(), task.getChapter());
        scu.findUpDate((result, newDate, chapter, hrefIcon) -> {
            switch (result) {
                case 1:
                    if (newDate != null)
                        task.setDate(newDate);
                    task.setChapter(chapter);
                    task.setIcon(hrefIcon);
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
            if (task.getIcon() == null) {
                task.setIcon(hrefIcon);
                model.updateTask(task, null);
            }

            if (callback != null) {
                view.isUpdate(task.isUpdate());
                callback.onComplete(result);
            }

            if (callbackPrivate != null) {
                callbackPrivate.onComplete();
            }
        });
    }

    private boolean checkConnecting() {
        if (!TaskModel.isNetworkAvailable(MainActivity.mContext)) {
            view.alertConnection();
            return false;
        }
        return true;
    }

    void moveTask(Task task, String from, String to) {
        if (from.equals(HomeFragment.DATABASE)) {
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

        void addedTask(Task task);

        void updatedTask(Task task);
    }

    /**
     * Обратный вызов для уведомления о получении ответа с сайта
     */
    public interface IUpdateCallback {
        void onComplete(int result);
    }

    public interface ICompleteCallback {
        void onComplete();
    }
}
