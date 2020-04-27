package ru.csu.ttpapp.mvp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;

import ru.csu.ttpapp.common.ListTasks;
import ru.csu.ttpapp.common.Task;

class TaskModel {
    interface ILoadCallback {
        void onLoad(ListTasks listTasks);
    }

    interface ICompleteCallback {
        void onComplete();
    }

    private final static String database = "list_db";
    private final String LIST_TASKS_LOADSAVE = "ListTasks";
    private ListTasks listTasks;
    private Context mContext;

    TaskModel(Context context) {
        mContext = context;
        if (listTasks == null)
            this.listTasks = new ListTasks();
    }

    void loadTasks(ILoadCallback callback) {
        LoadListTask loadListTask = new LoadListTask(callback);
        loadListTask.execute();
    }

    void updateTask(Task task, ICompleteCallback callback){
        UpdateTask updateTask = new UpdateTask(callback);
        updateTask.execute(task);
    }

    void saveTask(Task task, ICompleteCallback callback) {
        AddTask addTask = new AddTask(callback);
        addTask.execute(task);
    }

    void removeTask(Task task, ICompleteCallback callback) {
        RemoveTask removeTask = new RemoveTask(callback);
        removeTask.execute(task);
    }

    class AddTask extends AsyncTask<Task, Void, Void> {

        private final ICompleteCallback callback;

        AddTask(ICompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            Task task = tasks[0];
            listTasks.add(task);
            SharedPreferences sharedPreferences =
                    mContext.getSharedPreferences(database, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String json = new Gson().toJson(listTasks);
            editor.putString(LIST_TASKS_LOADSAVE, json);
            editor.apply();
            editor.commit();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (callback != null) {
                callback.onComplete();
            }
        }
    }

    class UpdateTask extends AsyncTask<Task, Void, Void> {

        private final ICompleteCallback callback;

        UpdateTask(ICompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            Task newTask = tasks[0];
            for (Task t: listTasks){
                if(newTask.getTitle().equals(t.getTitle())){
                    Task oldTask = t;
                    listTasks.remove(oldTask);
                    break;
                }
            }
            listTasks.add(newTask);
            SharedPreferences sharedPreferences =
                    mContext.getSharedPreferences(database, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String json = new Gson().toJson(listTasks);
            editor.putString(LIST_TASKS_LOADSAVE, json);
            editor.apply();
            editor.commit();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (callback != null) {
                callback.onComplete();
            }
        }
    }

    class LoadListTask extends AsyncTask<Void, Void, ListTasks> {

        private final ILoadCallback callback;

        LoadListTask(ILoadCallback callback) {
            this.callback = callback;
        }

        @Override
        protected ListTasks doInBackground(Void... voids) {
            SharedPreferences sharedPreferences =
                    mContext.getSharedPreferences(database, Context.MODE_PRIVATE);
            String loadList = sharedPreferences.getString(LIST_TASKS_LOADSAVE, "");
            if (!loadList.equals(""))
                listTasks = new Gson().fromJson(loadList, ListTasks.class);
            return listTasks;
        }

        @Override
        protected void onPostExecute(ListTasks listTasks) {
            if (callback != null) {
                callback.onLoad(listTasks);
            }
        }
    }

    class RemoveTask extends AsyncTask<Task, Void, Void> {

        private final ICompleteCallback callback;

        RemoveTask(ICompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            Task task = tasks[0];
            listTasks.remove(task);
            SharedPreferences sharedPreferences =
                    mContext.getSharedPreferences(database, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String json = new Gson().toJson(listTasks);
            editor.putString(LIST_TASKS_LOADSAVE, json);
            editor.apply();
            editor.commit();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (callback != null) {
                callback.onComplete();
            }
        }
    }
}
