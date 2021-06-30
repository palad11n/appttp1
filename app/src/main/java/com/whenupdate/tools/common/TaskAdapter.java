package com.whenupdate.tools.common;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import com.whenupdate.tools.R;
import com.whenupdate.tools.mvp.TasksPresenter;
import com.whenupdate.tools.mvp.ViewActivity;

import java.util.Collections;

import static android.content.Context.CLIPBOARD_SERVICE;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {
    private static ListTasks data = new ListTasks();
    private static ListTasks dataCopy = new ListTasks();
    private int row_index;

    @NonNull
    private IAdapterCallback callback;

    public interface IAdapterCallback {
        void onDelete(Task task);

        void onMove(Task task);

        void onShowEmpty();

        void onLoadUpdate(Task task, TasksPresenter.IUpdateCallback callback);

        void onUpdateTask(Task task);
    }

    public void setCallback(@NonNull IAdapterCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView recyclerView = parent.findViewById(R.id.listView);
        recyclerView.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_listview, parent, false);
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

    public void filter(String text) {
        data.clear();
        if (text.isEmpty()) {
            data.addAll(dataCopy);
        } else {
            text = text.toLowerCase();
            for (Task item : dataCopy) {
                if (item.getTitle().toLowerCase().contains(text) || item.getTitle().toLowerCase().contains(text)) {
                    data.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setData(ListTasks listTasks) {
        data.clear();
        data.addAll(listTasks);

        dataCopy.clear();
        dataCopy.addAll(listTasks);
        notifyDataSetChanged();
    }

    private void swapeItem(int fromPosition, int toPosition) {
        int size = getItemCount();
        if (size > 1) {
            for (int j = fromPosition; j > toPosition; j--) {
                Collections.swap(data, j, j - 1);
            }
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    private void removeItem(int position) {
        Task task = data.get(position);
        data.remove(position);
        notifyItemRemoved(position);
        callback.onDelete(task);
        if (getItemCount() == 0)
            callback.onShowEmpty();
    }

    public void addItem(Task task) {
        data.add(task);
        notifyItemInserted(getItemCount());
    }

    /***
     * Обновление задачи в разработке
     * @param task
     */
    public void updateItem(Task task) {
        int pos = -1;
        for (int i = 0; i < getItemCount(); i++) {
            pos = i;
            if (task.getId() == data.get(i).getId())
                break;
        }
        if (task.isUpdate() && pos != -1) {
            data.remove(pos);
            data.add(pos, task);
            notifyItemChanged(pos);
            swapeItem(pos, 0);
        }

    }

    private void moveItemInDB(int position) {
        Task task = data.get(position);
        data.remove(position);
        notifyItemRemoved(position);
        callback.onMove(task);
        if (getItemCount() == 0)
            callback.onShowEmpty();
    }

    private void restoreItem(Task item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    class TaskHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView lastCheck;
        private final TextView options;
        private final View itemView;
        private final CardView cardView;
        private final TextView textChapter;
        private final LinearLayout layoutChapter;
        private final ImageView imageView;
        SwipeLayout swipeLayout;

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
            swipeLayout = itemView.findViewById(R.id.simp);
            imageView = itemView.findViewById(R.id.picasso_img);
        }

        void bind(final Task task) {
            title.setText(task.getTitle());
            lastCheck.setText(task.getSimpleDateFormat());
            setTextChapter(task.getChapter());

            setClickView(task.getLink());
            setSwipeLayoutLeft(task.getLink());
            //setLongClick(task.getLink());
            String hrefIcon = task.getIcon();
            try {
                if (hrefIcon != null && !hrefIcon.isEmpty())
                    Picasso.with(itemView.getContext())
                            .load(task.getIcon())
                            .placeholder(R.drawable.ic_info_setting)
                            .error(R.drawable.ic_info_setting)
                            .into(imageView);
            } catch (Exception exception) {
                Log.e("picasso", hrefIcon + ": " + exception.getMessage());
            }

            options.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(itemView.getContext(), options);
                popup.inflate(R.menu.menu_options);
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.itemOpen:
                            goToBrowser(task.getLink());
                            return true;
                        case R.id.itemUpdate:
                            callback.onLoadUpdate(task, result -> {
                                if (result == 1) {
                                    row_index = getAdapterPosition();
                                    notifyItemChanged(row_index);
                                    swapeItem(row_index, 0);
                                }
                            });
                            return true;
                        case R.id.itemCopy:
                            ClipboardManager clipboard =
                                    (ClipboardManager) itemView.getContext().getSystemService(CLIPBOARD_SERVICE);
                            String link = task.getLink();
                            ClipData clipData = ClipData.newPlainText("Link tasks", link);
                            clipboard.setPrimaryClip(clipData);
                            Toast.makeText(itemView.getContext(),
                                    itemView.getContext().getString(R.string.link_copied),
                                    Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.itemEdit:
                            Context context = itemView.getContext();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_task, null);
                            TextInputEditText editTitle = view.findViewById(R.id.editName);
                            editTitle.setText(task.getTitle());
                            TextInputLayout inputEditTitle = view.findViewById(R.id.textInputLayoutEditName);
                            inputEditTitle.setHelperText(context.getString(R.string.edit_not_empty));

                            builder.setView(view)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.done, (dialog, id) -> {
                                        String newTitle = editTitle.getText().toString().trim();
                                        if (!newTitle.isEmpty()) {
                                            task.setTitle(newTitle);
                                            callback.onUpdateTask(task);
                                            notifyItemChanged(getAdapterPosition());
                                        }
                                        dialog.dismiss();
                                    })
                                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());
                            builder.create();
                            AlertDialog alert = builder.create();
                            alert.show();
                            return true;
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
                callback.onUpdateTask(task);

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
            } catch (Exception ignored) {
            }
            return false;
        }

        private void goToView(String linkTasks) {
            Intent intentView = new Intent(itemView.getContext(), ViewActivity.class);
            intentView.putExtra(ViewActivity.LINK_KEY, linkTasks);
            itemView.getContext().startActivity(intentView);
        }

        private void setClickView(String link) {
            title.setOnClickListener(v -> goToView(link));
            textChapter.setOnClickListener(v -> goToView(link));
        }

        private void setSwipeLayoutLeft(String link) {
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
//            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, itemView.findViewById(R.id.bottom_wrapper_deferred));

            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onClose(SwipeLayout layout) {
                    //when the SurfaceView totally cover the BottomView.
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                    //you are swiping.
                    if (leftOffset > 0 && layout.getOpenStatus() == SwipeLayout.Status.Open) {
                        int position = getAdapterPosition();
                        if (position != -1)
                            moveItemInDB(getAdapterPosition());
                    }
                }

                @Override
                public void onStartOpen(SwipeLayout layout) {
                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    layout.findViewById(R.id.swipeDelete).setOnClickListener(v -> {
                        Context context = itemView.getContext();
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View view = LayoutInflater.from(context).inflate(R.layout.dialog_conf_delete, null);

                        builder.setView(view)
                                .setPositiveButton(R.string.done, (dialog, id) -> {
                                    removeItem(getAdapterPosition());
                                    dialog.dismiss();
                                })
                                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());
                        builder.create();
                        AlertDialog alert = builder.create();
                        alert.show();
                    });

//                    layout.findViewById(R.id.swipeGo).setOnClickListener(v -> {
//                        goToBrowser(link);
//                    });

                    layout.findViewById(R.id.swipeMove).setOnClickListener(v -> {
                        moveItemInDB(getAdapterPosition());
                    });
                }

                @Override
                public void onStartClose(SwipeLayout layout) {
                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                    //when user's hand released.
                }
            });
        }
    }
}
