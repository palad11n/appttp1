package ru.csu.ttpapp.mvp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;

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

    private final static String  maxSizeList = "list_size_db";
    private final String LIST_MAX_SIZE = "MaxSize";

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

    void updateTask(Task task, ICompleteCallback callback) {
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

    boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MainActivity.mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    )
                        return true;
                }
            } else {
                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        return true;
                    }
                } catch (Exception e) {
                    //ignore
                }
            }
        }
        return false;
    }

    private class AddTask extends AsyncTask<Task, Void, Void> {

        private final ICompleteCallback callback;

        AddTask(ICompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            Task task = tasks[0];
            long max = getMaxSize();
            task.setId(max);
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

        private long getMaxSize() {
            SharedPreferences  sharedPreferences =
                    mContext.getSharedPreferences(maxSizeList, Context.MODE_PRIVATE);
            String loadSize = sharedPreferences.getString(LIST_MAX_SIZE, "0");

            long maxSaveSize = Long.parseLong(loadSize) + 1;

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(LIST_MAX_SIZE, Long.toString(maxSaveSize));
            editor.apply();
            editor.commit();
            return maxSaveSize;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (callback != null) {
                callback.onComplete();
            }
        }
    }

    private class UpdateTask extends AsyncTask<Task, Void, Void> {

        private final ICompleteCallback callback;

        UpdateTask(ICompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            Task newTask = tasks[0];
            for (int i = 0; i < listTasks.size(); i++) {
                Task oldTask = listTasks.get(i);
                if (newTask.getId() == oldTask.getId()) {
                    // Task findOldTask = oldTask;
                    listTasks.set(i, newTask);
                    break;
                }
            }
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

    private class LoadListTask extends AsyncTask<Void, Void, ListTasks> {

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

    private class RemoveTask extends AsyncTask<Task, Void, Void> {

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
