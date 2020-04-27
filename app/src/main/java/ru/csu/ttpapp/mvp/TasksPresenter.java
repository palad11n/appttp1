package ru.csu.ttpapp.mvp;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import java.util.Date;
import java.util.Locale;

import ru.csu.ttpapp.R;
import ru.csu.ttpapp.common.ListTasks;
import ru.csu.ttpapp.common.Task;
import ru.csu.ttpapp.service.sites.ISite;
import ru.csu.ttpapp.service.sites.SiteUpdate;

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

    public void applySetting(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(view);
        setTheme(prefs);
        setLang(prefs);
    }

    private void setLang(SharedPreferences prefs){
        String lang =  prefs.getString("lang", "default");
        if (lang.equals("default")) {
            lang=view.getResources().getConfiguration().locale.getCountry();
        }
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        view.getBaseContext().getResources().updateConfiguration(config, null);
    }

    private void setTheme(SharedPreferences prefs){
        String theme = prefs.getString("theme", "dark");
        ConstraintLayout cl = view.findViewById(R.id.cl_main);
        TextView empty = view.findViewById(R.id.emptyId);
        if (theme.equals("dark")) {
            cl.setBackgroundColor(Color.BLACK);
            empty.setTextColor(Color.WHITE);

        } else {
            cl.setBackgroundColor(Color.WHITE);
            empty.setTextColor(Color.BLACK);
        }
    }

    public void loadTasks() {
        view.showProgressDialog();
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
        view.hideProgressDialog();
    }

    public void viewIsReady() {
        loadTasks();
    }

    public void add() {
        Task task = view.getTaskFromDialog();
        ISite update = new SiteUpdate(task.getLink());
        if (task.getTitle().equals("")) {
            task.setTitle(update.getTitleSite());
        }
        task.setDate(update.findUpDate());
        saveTask(task);
    }

    private void saveTask(Task task) {

        model.saveTask(task, new TaskModel.ICompleteCallback() {
            @Override
            public void onComplete() {
                loadTasks();
            }
        });
    }

    public void updateTask(Task task){
        model.updateTask(task, new TaskModel.ICompleteCallback() {
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

    public void loadUpdate(Task task) {
        try {
            ISite scu = new SiteUpdate(task.getLink());
            Date newDate = scu.findUpDate();
            if (newDate != null) {
                if (newDate.after(task.getDate())) {
                    task.setUpdate(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateTask(task);
    }
}
