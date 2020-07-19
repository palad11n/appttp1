package com.whenupdate.tools.common;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.whenupdate.tools.R;
import com.whenupdate.tools.mvp.MainActivity;
import com.whenupdate.tools.mvp.ViewActivity;

import static android.content.Context.CLIPBOARD_SERVICE;


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
        Task task = data.get(position);
        holder.bind(task);
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
        private final TextView title;
        private final TextView lastCheck;
        private final TextView options;
//        private Button deleteBtn;
//        private ImageButton syncImgBtn;
        private final View itemView;
        private final CardView cardView;
        private final TextView textChapter;
        private final LinearLayout layoutChapter;


        TaskHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = itemView.findViewById(R.id.nameLink);
            lastCheck = itemView.findViewById(R.id.lastCheck);
            options = itemView.findViewById(R.id.textViewOptions);
            cardView = itemView.findViewById(R.id.cardView);
            textChapter = itemView.findViewById(R.id.nameVolCh);
            layoutChapter = itemView.findViewById(R.id.linearLayoutChapter);
            row_index = -1;

        }

        void bind(final Task task) {
            title.setText(task.getTitle());
            lastCheck.setText(task.getSimpleDateFormat());
            setTextChapter(task.getChapter());

            setClickView(task.getLink());
            setLongClick(task.getLink());

            options.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(itemView.getContext(), options);
                popup.inflate(R.menu.menu_options);
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.itemDelete:
                            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                            builder.setMessage(R.string.text_conf_delete)
                                    .setPositiveButton(R.string.done, (dialog, id) -> {
                                        removeItem(getAdapterPosition());
                                        dialog.dismiss();
                                    })
                                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());
                            builder.create();
                            AlertDialog alert = builder.create();
                            alert.show();
                            break;
                        case R.id.itemUpdate:
                            MainActivity.presenter.loadUpdate(task, result -> {
                                if (result == 1) {
                                    row_index = getAdapterPosition();
                                    notifyItemChanged(row_index);
                                }
                            });
                            break;
                        case R.id.itemCopy:
                            ClipboardManager clipboard =
                                    (ClipboardManager) itemView.getContext().getSystemService(CLIPBOARD_SERVICE);
                            String link = task.getLink();
                            ClipData clipData = ClipData.newPlainText("Link tasks", link);
                            clipboard.setPrimaryClip(clipData);
                            Toast.makeText(itemView.getContext(),
                                    itemView.getContext().getString(R.string.link_copied),
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return false;
                });
                popup.show();
            });

            if (task.isUpdate()) {
                setColorTask();
                lastCheck.setText(task.getSimpleDateFormat());
                setTextChapter(task.getChapter());
                task.setUpdate(false);
                MainActivity.presenter.updateTask(task);
            }
        }

        private void setColorTask() {
            cardView.setCardBackgroundColor(itemView.getContext()
                    .getResources()
                    .getColor(R.color.shape_on_color));
        }

        private void setTextChapter(String text) {
            if (text != null && !text.isEmpty()) {
                layoutChapter.setVisibility(View.VISIBLE);
                textChapter.setText(text);
            } else layoutChapter.setVisibility(View.GONE);
        }

        private boolean goToBrowser(String link) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                itemView.getContext().startActivity(browserIntent);
                return true;
            } catch (Exception e) {
            }
            return false;
        }

        private void goToView(String linkTasks) {
            Intent intentView = new Intent(itemView.getContext(), ViewActivity.class);
            intentView.putExtra(ViewActivity.LINK_KEY, linkTasks);
            itemView.getContext().startActivity(intentView);
        }

        private void setClickView(String link) {
            itemView.setOnClickListener(v -> goToView(link));
            title.setOnClickListener(v -> goToView(link));
            textChapter.setOnClickListener(v -> goToView(link));
        }

        private void setLongClick(String link) {
            itemView.setOnLongClickListener(v -> goToBrowser(link));
            title.setOnLongClickListener(v -> goToBrowser(link));
            textChapter.setOnLongClickListener(v -> goToBrowser(link));
        }
    }
}
