package com.whenupdate.tools.common.desing;

import androidx.recyclerview.widget.DiffUtil;

import com.whenupdate.tools.common.ListTasks;
import com.whenupdate.tools.common.Task;

public class DiffUtilCallback  extends DiffUtil.Callback {
    private final ListTasks oldList;
    private final ListTasks newList;

    public DiffUtilCallback(ListTasks oldList, ListTasks newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Task oldProduct = oldList.get(oldItemPosition);
        Task newProduct = newList.get(newItemPosition);
        return oldProduct.getId() == newProduct.getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Task oldProduct = oldList.get(oldItemPosition);
        Task newProduct = newList.get(newItemPosition);
        return oldProduct.getChapter().equals(newProduct.getChapter())
                && oldProduct.getDate() == newProduct.getDate();
    }
}
