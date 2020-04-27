package ru.csu.ttpapp.mvp;

import android.view.View;
import android.widget.TextView;

import java.util.Date;

import ru.csu.ttpapp.R;
import ru.csu.ttpapp.common.ListTasks;
import ru.csu.ttpapp.common.Task;
import ru.csu.ttpapp.service.strategy.IStrategy;
import ru.csu.ttpapp.service.strategy.SiteUpdate;

public class TasksPresenter {
    private MainActivity view;
    private final TaskModel model;

    public TasksPresenter(TaskModel model) {
        this.model = model;
    }

    public void attachView(MainActivity activity) {
        view = activity;
    }

    public void detachView() {
        view = null;
    }

    public void loadTasks() {
        model.loadTasks(new TaskModel.ILoadCallback() {

            @Override
            public void onLoad(ListTasks listTasks) {
                TextView textEmpty = view.findViewById(R.id.emptyId);
                if (listTasks != null) {
                    view.showTasks(listTasks);
                    if (!listTasks.isEmpty())
                        textEmpty.setVisibility(View.GONE);
                    else textEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void viewIsReady() {
        loadTasks();
    }

    public void add() {
        Task task = view.getTaskFromDialog();
        IStrategy update = new SiteUpdate(task.getLink());
        if (task.getTitle().equals("")) {
            task.setTitle(update.getTitleSite());
        }
        task.setDate(update.findUpDate());
        saveTask(task);
    }

    private void saveTask(Task task) {
        //view.showProgressDialog();
        model.saveTask(task, new TaskModel.ICompleteCallback() {
            @Override
            public void onComplete() {
                //view.hideProgressDialog();
                loadTasks();
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

    public void loadUpdate(Task task) {
        try {
            IStrategy scu = new SiteUpdate(task.getLink());
            Date newDate = scu.findUpDate();
            if (newDate != null) {
                if (newDate.after(task.getDate())) {
                    task.setUpdate(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }//todo
        remove(task);
        saveTask(task);
    }
}
