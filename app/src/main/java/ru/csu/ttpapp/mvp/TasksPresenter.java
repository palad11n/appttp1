package ru.csu.ttpapp.mvp;

import android.view.View;
import android.widget.TextView;

import ru.csu.ttpapp.R;
import ru.csu.ttpapp.common.ListTasks;
import ru.csu.ttpapp.common.Task;

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
                TextView textEmpty = (TextView) view.findViewById(R.id.emptyId);
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
        Task task = view.getTask();
        model.saveTask(task, new TaskModel.ICompleteCallback() {
            @Override
            public void onComplete() {
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
}
