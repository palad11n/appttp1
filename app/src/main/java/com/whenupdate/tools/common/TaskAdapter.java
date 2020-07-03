package com.whenupdate.tools.common;

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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.whenupdate.tools.R;
import com.whenupdate.tools.mvp.MainActivity;
import com.whenupdate.tools.mvp.TasksPresenter;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {
    private static ListTasks data = new ListTasks();
    private int row_index;

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

    private void removeItem(int position) {
        Task task = data.get(position);
        data.remove(position);
        notifyItemRemoved(position);
        MainActivity.presenter.remove(task);
        if (getItemCount() == 0) MainActivity.presenter.loadTasks();
    }

    private void restoreItem(Task item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    class TaskHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView lastCheck;
        private Button deleteBtn;
        private ImageButton syncImgBtn;
        private View itemView;
        private CardView cardView;


        TaskHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = itemView.findViewById(R.id.nameLink);
            lastCheck = itemView.findViewById(R.id.lastCheck);
            deleteBtn = itemView.findViewById(R.id.delBtn);
            syncImgBtn = itemView.findViewById(R.id.syncBtn);
            cardView = itemView.findViewById(R.id.cardView);
            row_index = -1;
        }

        void bind(final Task task) {
            title.setText(task.getTitle());
            lastCheck.setText(task.getSimpleDateFormat());
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setMessage(R.string.text_conf_delete)
                            .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    removeItem(getAdapterPosition());
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
                    MainActivity.presenter.loadUpdate(task, new TasksPresenter.IUpdateCallback() {
                        @Override
                        public void onComplete(int result) {
                            if (result == 1){
                                row_index = getAdapterPosition();
                                notifyItemChanged(row_index);
                            }
                        }
                    });
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(task.getLink()));
                    itemView.getContext().startActivity(browserIntent);
                    return true;
                }
            });

            if (task.isUpdate()) {
                setColorTask();
                task.setUpdate(false);
                MainActivity.presenter.updateTask(task);
            }
        }

        private void setColorTask() {
            cardView.setCardBackgroundColor(itemView.getContext()
                    .getResources()
                    .getColor(R.color.shape_on_color));
        }
    }
}
