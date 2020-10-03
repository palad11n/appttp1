package com.whenupdate.tools.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.whenupdate.tools.R;
import com.whenupdate.tools.mvp.MainActivity;
import com.whenupdate.tools.mvp.TaskModel;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {
    private static final List<HelpActivity.Help> QUESTION_ANSWER;
    private static final Resources res = MainActivity.mContext.getResources();

    static {
        QUESTION_ANSWER = new ArrayList<>();
        QUESTION_ANSWER.add(new HelpActivity.Help(
                res.getString(R.string.question_about_view),
                res.getString(R.string.help_click)));

        QUESTION_ANSWER.add(new HelpActivity.Help(
                res.getString(R.string.question_about_move_bookmark),
                res.getString(R.string.answer_about_move_bookmark)));

        QUESTION_ANSWER.add(new HelpActivity.Help(
                res.getString(R.string.question_about_date_chapter),
                res.getString(R.string.answer_about_date_chapter)));

        QUESTION_ANSWER.add(new HelpActivity.Help(
                res.getString(R.string.question_about_tab_bookmark),
                res.getString(R.string.anwer_about_tab_bookmarks)));

        QUESTION_ANSWER.add(new HelpActivity.Help(
                res.getString(R.string.question_about_support_sites),
                res.getString(R.string.answer_about_support_sites)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskModel.setNewTheme(this);
        setContentView(R.layout.activity_question_answer);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.faq_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        init();
    }

    private void init() {
        ListView listView = (ListView) findViewById(R.id.list_question);
        ArrayAdapter<HelpActivity.Help> adapter = new HelpActivity.HelpAdapter(this);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private static class Help {
        public final String question;
        public final String answer;

        Help(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }

    private class HelpAdapter extends ArrayAdapter<HelpActivity.Help> {
        HelpAdapter(@NonNull Context context) {
            super(context, R.layout.item_question_answer, QUESTION_ANSWER);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            HelpActivity.Help helper = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_question_answer, null);
            }
            LinearLayout quiestionLayout = convertView.findViewById(R.id.click_question);
            TextView hideShowAnswer = (TextView) convertView.findViewById(R.id.hide_show_answer);
            TextView question = (TextView) convertView.findViewById(R.id.text_question);

            LinearLayout answerLayout = convertView.findViewById(R.id.answer);
            TextView answer = (TextView) convertView.findViewById(R.id.text_answer);

            question.setText(helper.question);
            answer.setText(helper.answer);
            question.setClickable(true);
            question.setOnClickListener(v -> {
                if (answerLayout.getVisibility() == View.GONE) {
                    hideShowAnswer.setText("-");
                    answerLayout.setVisibility(View.VISIBLE);
                } else {
                    hideShowAnswer.setText("+");
                    answerLayout.setVisibility(View.GONE);
                }
            });

            quiestionLayout.setOnClickListener(v -> {
                if (answerLayout.getVisibility() == View.GONE) {
                    hideShowAnswer.setText("-");
                    answerLayout.setVisibility(View.VISIBLE);
                } else {
                    hideShowAnswer.setText("+");
                    answerLayout.setVisibility(View.GONE);
                }
            });

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String theme = prefs.getString("theme", "light");
            if (theme.contains("dark")) {
                answer.setTextColor(Color.WHITE);
            } else {
                answer.setTextColor(Color.BLACK);
            }

            return convertView;
        }
    }
}
