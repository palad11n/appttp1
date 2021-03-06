package com.whenupdate.tools.service.sites;

import org.jsoup.select.Elements;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.ParseException;
import java.util.Date;

import com.whenupdate.tools.common.Task;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InfoOfSiteTest {
    InfoOfSite info = new InfoOfSite() {
        @Override
        public String getLastDate(Elements rows) {
            return "";
        }

        @Override
        public String getLastChapter(Elements rows) {
            return null;
        }
    };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void setTitle() {
        //https://www.mangareader.net/haikyuu-bu
        //http://m.fanfox.net/manga/how_to_fight
        String testTitle = "Title of user";
        String testLink = "https://127.0.0.1/";

        info.setTitle(testTitle, testLink);
        assertThat(info.getTitle(), is(testTitle));

        info.setTitle("", testLink);
        assertThat(info.getTitle(), is(testLink));

        info.setTitle(null, testLink);
        assertThat(info.getTitle(), is(testLink));
    }

    @Test
    public void setDate() throws ParseException {
        info.setDate("1 Января, 2020");
        assertThat(getFormat(info.getDate()), is("01.01.2020"));

        info.setDate("1 февраля, 2020");
        assertThat(getFormat(info.getDate()), is("01.02.2020"));

        info.setDate("1 марта, 2020");
        assertThat(getFormat(info.getDate()), is("01.03.2020"));

        info.setDate("1 апреля, 2020");
        assertThat(getFormat(info.getDate()), is("01.04.2020"));

        info.setDate("1 мая, 2020");
        assertThat(getFormat(info.getDate()), is("01.05.2020"));

        info.setDate("1 июня, 2020");
        assertThat(getFormat(info.getDate()), is("01.06.2020"));

        info.setDate("1 июля, 2020");
        assertThat(getFormat(info.getDate()), is("01.07.2020"));

        info.setDate("1 августа, 2020");
        assertThat(getFormat(info.getDate()), is("01.08.2020"));

        info.setDate("1 сентября, 2020");
        assertThat(getFormat(info.getDate()), is("01.09.2020"));

        info.setDate("1 октября, 2020");
        assertThat(getFormat(info.getDate()), is("01.10.2020"));

        info.setDate("1 ноября, 2020");
        assertThat(getFormat(info.getDate()), is("01.11.2020"));

        info.setDate("1 декабря, 2020");
        assertThat(getFormat(info.getDate()), is("01.12.2020"));
    }

    @Test
    public void setDateFail() throws ParseException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("ию9н9я"));
        info.setDate("10 ию9н9я, 2020");
    }

    private String getFormat(Date date) {
        Task task = new Task();
        task.setDate(date);

        return task.getSimpleDateFormat();
    }
}