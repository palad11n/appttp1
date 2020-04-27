package ru.csu.ttpapp.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
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

    static class TaskHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView lastCheck;
        Button deleteBtn;
        ImageButton syncImgBtn;
        Button updateBtn;
        private View itemView;

        public TaskHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = itemView.findViewById(R.id.nameLink);
            lastCheck = itemView.findViewById(R.id.lastCheck);
            deleteBtn = itemView.findViewById(R.id.delBtn);
            syncImgBtn = itemView.findViewById(R.id.syncBtn);
            updateBtn = itemView.findViewById(R.id.updateBtn);
        }

        void bind(final Task task) {
            title.setText(task.getTitle());
            lastCheck.setText(task.getSimpleDateFormat());
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.presenter.remove(task);
                }
            });
            syncImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.presenter.loadUpdate(task);
                    lastCheck.setText(task.getSimpleDateFormat());
                    if (task.isUpdate()) {
                        itemView.setBackgroundResource(R.drawable.my_on_shape);
                    }
                    else Toast.makeText(MainActivity.mContext,"Update is not...",Toast.LENGTH_SHORT).show();
                }
            });
            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo: переход на сайт
                   task.setUpdate(false);
                   itemView.setBackgroundResource(R.drawable.my_off_shape);
                }
            });
        }
    }
}
