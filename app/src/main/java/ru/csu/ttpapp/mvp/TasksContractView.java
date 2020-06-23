package ru.csu.ttpapp.mvp;

import ru.csu.ttpapp.common.ListTasks;
import ru.csu.ttpapp.common.Task;

public interface TasksContractView {
    Task getTaskFromDialog();
    void showTasks(ListTasks tasks);
    void showToast(String textToast, int resIdIcon);
    void showProgress();
    void hideProgress();
}
