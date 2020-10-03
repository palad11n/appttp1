package com.whenupdate.tools.service;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.whenupdate.tools.R;
import com.whenupdate.tools.common.NotifyService;
import com.whenupdate.tools.common.Task;
import com.whenupdate.tools.mvp.MainActivity;
import com.whenupdate.tools.mvp.TaskModel;
import com.whenupdate.tools.service.sites.ISite;
import com.whenupdate.tools.service.sites.SiteUpdate;

public class UpdateService {
    private Context context;

    public UpdateService(Context context) {
        this.context = context;
    }

    public void check() {
        TaskModel taskModel = new TaskModel(context, MainActivity.DATABASE);
        if (!TaskModel.isNetworkAvailable(context)) {
            Toast.makeText(context, context.getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        taskModel.loadTasks(listTasks -> {
            for (Task task : listTasks) {
                loadingUpdate(task);
            }
            Toast.makeText(context, context.getResources().getString(R.string.update_not), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadingUpdate(Task task) {
        ISite scu = new SiteUpdate(task.getLink(), task.getDate(), task.getChapter());
        scu.findUpDate((result, newDate, chapter, hrefIcon) -> {
            switch (result) {
                case 1:
                    Toast.makeText(context, context.getResources().getString(R.string.update_exist),
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context.getApplicationContext(), NotifyService.class);
                    context.startService(intent);
                    break;
                case -1:
                    Toast.makeText(context, context.getResources().getString(R.string.site_rip)
                                    + task.getLink(),
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        });
    }
}
