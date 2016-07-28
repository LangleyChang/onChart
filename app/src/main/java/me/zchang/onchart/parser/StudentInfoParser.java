package me.zchang.onchart.parser;

import android.support.annotation.NonNull;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.zchang.onchart.student.Course;
import me.zchang.onchart.student.Exam;
import me.zchang.onchart.student.LabelCourse;

/*
 *    Copyright 2015 Zhehua Chang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/**
 * Parser for the information relative to students.
 * All the html files are from <a href="http://jwc.bit.edu.cn"/>
 */
public class StudentInfoParser {
    private final static String TAG = "StudentInfoParser";

    /**
     * Parse the schedule from a html text.
     * @param htmlText the source html text.
     * @return the list of courses parsed from the source.Return null if failed.
     */
    public static List<Course> parseCourses(@NonNull String htmlText)
    {
	    Document doc = Jsoup.parse(htmlText);
        Element chartTable = doc.select("table#dgrdKb").first();
        if(chartTable != null) {
            Elements lessonEles = chartTable.select("tr");
            List<Course> courses = new ArrayList<>();
            Elements elements = doc.select("[selected=selected]");
            StringBuilder semesterBuilder = new StringBuilder();
            if (!elements.isEmpty()) {
                semesterBuilder.append(elements.first().text().split("-")[0])
                        .append("-")
                        .append(elements.last().text());
            }
            String thisSemester = semesterBuilder.toString();

            List<Course> dupCourses = new ArrayList<>();
            for (Element e : lessonEles) {
                dupCourses.clear();
                if (!e.className().equals("datagridhead")) {
                    Course baseCourse = new LabelCourse();
                    Elements lessonInfo = e.getAllElements();

	                baseCourse.setSemester(thisSemester);
	                baseCourse.setName(lessonInfo.get(1).text());
                    baseCourse.setCredit(Float.parseFloat(lessonInfo.get(2).text()));
                    baseCourse.setDepartment(lessonInfo.get(5).text());
                    baseCourse.setTeacher(lessonInfo.get(7).text());

                    String textTime = lessonInfo.get(8).text();
                    String[] textTimes = textTime.split(";");
                    String textClassroom = lessonInfo.get(9).text();
                    String[] textClassrooms = textClassroom.split(";");
                    int j = 0;
                    for (String s : textClassrooms) {
	                    dupCourses.add(new LabelCourse(baseCourse));
	                    dupCourses.get(j).setClassroom(s);

                        if (textTimes[j].length() == 1)
                            dupCourses.get(j).setWeekDay(7);// the null value
                        else
                            dupCourses.get(j).setWeekDay(Utils.parseIndexFromWeekday(textTimes[j].charAt(1)));

                        String pattern = "(\\d)+(?=,)|(\\d+)*(?=小?节)";
                        Pattern reg = Pattern.compile(pattern);
                        Matcher m = reg.matcher(textTimes[j]);
                        if (m.find()) {
                            dupCourses.get(j).setStartTime(Utils.periodToTime(Integer.parseInt(m.group())));
                        }
                        String endTime = null;
                        while (m.find() && m.group().length() > 0) {
                            endTime = m.group();
                        }
                        if (endTime == null) {
                            dupCourses.get(j).setEndTime(dupCourses.get(j).getStartTime());
                        } else {
                            dupCourses.get(j).setEndTime(Utils.periodToTime(Integer.parseInt(endTime)));
                        }

                        pattern = "\\d+(?=-)|\\d+(?=周)";
                        reg = Pattern.compile(pattern);
                        m = reg.matcher(textTimes[j]);
                        if (m.find()) {
                            dupCourses.get(j).setStartWeek(Integer.parseInt(m.group()));
                        } else {
                            dupCourses.get(j).setStartWeek(0);
                        }
                        if (m.find()) {
                            dupCourses.get(j).setEndWeek(Integer.parseInt(m.group()));
                        } else {
                            dupCourses.get(j).setEndWeek(dupCourses.get(j).getStartWeek());
                        }

                        pattern = "(单|双)(?=周)";
                        reg = Pattern.compile(pattern);
                        m = reg.matcher(textTimes[j]);
                        if (m.find()) {
                            if (m.group().equals("单"))
                                dupCourses.get(j).setWeekParity((byte) 1);
                            else
                                dupCourses.get(j).setWeekParity((byte) 2);
                        } else {
                            dupCourses.get(j).setWeekParity((byte) -1);
                        }
                        j++;
                    }
                }
                courses.addAll(dupCourses);
            }
            Collections.sort(courses);
            return courses;
        } else {
            return null;
        }
    }

    public static Map<String, String> parseParamsInCoursePage(String coursePageHtml) {
        Map<String, String> params = new HashMap<>(5);

        Document page = Jsoup.parse(coursePageHtml);
        Elements paramEles = page.select("input");
        for (Element element : paramEles) {
            params.put(element.attr("name"), element.val());
        }
        return params;
    }

    /**
     * Parse current week number.
     * @param htmlText the source html text.
     * @return the week number. +100 in summer holiday, +200 in winter holiday
     */
    public static int parseWeek(@NonNull String htmlText) {
        Document doc = Jsoup.parse(htmlText);
        Elements rootElements = doc.select("a.black");
        int weekNum = 0;
        try {
            String decodedHtml = new String(htmlText.getBytes(), "UTF-8");
            Log.i(TAG, decodedHtml);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (rootElements.size() > 0) {
            Element rootElement = rootElements.get(0);
            if (rootElement.text().matches("(.*)暑(.*)")) {
                weekNum += 100;
            } else if (rootElement.text().matches("寒假")) {
                weekNum += 200;
            }


            Element childElement;
            if (rootElement != null) {
                childElement = rootElement.select("b").get(0);
                if (childElement != null) {
                    try {
                        weekNum += Integer.parseInt(childElement.text());
                        return weekNum;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Parse cur date from the homepage of jwc.
     *
     * @param htmlText The source html text.
     * @return The date.
     */
    public static void parseDate(@NonNull String htmlText, Integer year, Integer month, Integer day) {

    }

	public static List<String> parseSemesterList(@NonNull String htmlText) {
		Document doc = Jsoup.parse(htmlText);
        Elements yearElements = doc.select("select#xnd");
        List<String> semesters = new ArrayList<>();
        if (!yearElements.isEmpty()) {
            for (Element element : yearElements.get(0).children()) {
                semesters.add(element.val());
            }
        }

        Elements semesterElements = doc.select("select#xqd");
        if (!semesterElements.isEmpty()) {

        }
		return semesters;
	}

    /**
     * Parse student name.
     * @param htmlText the source html text.
     * @return the student name.
     */
    public static String parseName(@NonNull String htmlText) {
        Document doc = Jsoup.parse(htmlText);
        Elements elements = doc.select("span#xhxm");
        if(elements != null && !elements.isEmpty()) {
            Element element = elements.get(0);

            Pattern pattern = Pattern.compile(" .*(?=同学)");
            Matcher m = pattern.matcher(element.text());
            if (m.find()) {
                return m.group().trim();
            }
        }
        return null;
    }

	/**
	 * Parse exams in html.
	 *
	 * @param htmlText The source html content.
	 * @return If null, the account is invalid.
	 */
	public static List<Exam> parseExams(@NonNull String htmlText) {
        Document document = Jsoup.parse(htmlText);
        Elements elements = document.select("table#DataGrid1");
        if (elements != null && !elements.isEmpty()) {
            List<Exam> examList = new ArrayList<>();
            Element tableRoot = elements.first();
            Elements exams = tableRoot.select("tr");
            if (exams != null && !exams.isEmpty()) {
                for (Element examNode : exams) {
                    Elements items = examNode.getAllElements();
                    String examTime = items.get(4).text();
                    if (examTime.equals("&nbsp;") || examTime.equals("考试时间"))
                        continue;
                    Pattern pattern = Pattern.compile("20\\d{2}-\\d{2}-\\d{2}(?=\\))");
                    Matcher m = pattern.matcher(examTime);
                    if (!m.find()) {
                        pattern = Pattern.compile("20\\d{2}年\\d+月\\d+日");
                        m = pattern.matcher(examTime);
                        if (!m.find()) {
                            continue;
                        }
                    }

                    String date = m.group();
                    if (date != null) {
                        String[] yearMonthDay = date.split("-|年|月|日");
                        if (yearMonthDay.length == 3) {
                            pattern = Pattern.compile("\\d{2}:\\d{2}-\\d{2}:\\d{2}");
                            m = pattern.matcher(examTime);
                            if (m.find()) {
                                String time = m.group();
                                if (time != null) {
                                    String[] times = time.split("-");
                                    if (times.length == 2) {
                                        Exam exam = new Exam();
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MM dd kk:mm");
                                        StringBuilder timeString = new StringBuilder();
                                        timeString
                                                .append(yearMonthDay[0])
                                                .append(" ")
                                                .append(yearMonthDay[1].length() == 1 ? "0" + yearMonthDay[1] : yearMonthDay[1])
                                                .append(" ")
                                                .append(yearMonthDay[2].length() == 1 ? "0" + yearMonthDay[2] : yearMonthDay[2])
                                                .append(" ");
                                        String startTimeString = timeString.toString() + times[0];
                                        String endTimeString = timeString.toString() + times[1];
                                        try {
                                            exam.setStartTime(dateFormat.parse(startTimeString));
                                            exam.setEndTime(dateFormat.parse(endTimeString));
                                        } catch (ParseException e) {
                                            Log.e(TAG, "exam time parse error");
                                            e.printStackTrace();
                                        }

                                        exam.setName(items.get(2).text());
                                        exam.setPosition(items.get(5).text());
                                        exam.setSeat(items.get(7).text());
                                        examList.add(exam);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Collections.sort(examList);
            return examList;
        }
        return null;
    }

}
