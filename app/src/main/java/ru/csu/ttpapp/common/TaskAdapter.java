package ru.csu.ttpapp.common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.csu.ttpapp.R;
import ru.csu.ttpapp.mvp.MainActivity;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {
    private static ListTasks data = new ListTasks();

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView d = parent.findViewById(R.id.listView);
        d.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row, parent, false);
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TaskHolder holder, int position) {
        holder.bind(data.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo смена названия
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void setData(ListTasks listTasks) {
        data.clear();
        data.addAll(listTasks);
        notifyDataSetChanged();
    }

    static class TaskHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView lastCheck;
        Button deleteBtn;
        ImageButton syncImgBtn;
        //  Button updateBtn;
        private View itemView;

        public TaskHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = itemView.findViewById(R.id.nameLink);
            lastCheck = itemView.findViewById(R.id.lastCheck);
            deleteBtn = itemView.findViewById(R.id.delBtn);
            syncImgBtn = itemView.findViewById(R.id.syncBtn);
            //  updateBtn = itemView.findViewById(R.id.updateBtn);
        }

        void bind(final Task task) {
            title.setText(task.getTitle());
            lastCheck.setText(task.getSimpleDateFormat());
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.mContext);
                    builder.setTitle(R.string.setting_row);
                    builder.setMessage(R.string.confirmation_of_delete)
                            .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    MainActivity.presenter.remove(task);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create();
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

            syncImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.presenter.loadUpdate(task);
                    if (task.isUpdate()) {
                        itemView.setBackgroundResource(R.drawable.my_on_shape);
                        lastCheck.setText(task.getSimpleDateFormat());
                        task.setUpdate(false);
                        MainActivity.presenter.updateTask(task);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(task.getLink()));
                    MainActivity.mContext.startActivity(browserIntent);
                    return false;
                }
            });
        }
    }
}
