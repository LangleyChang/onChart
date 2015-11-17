package org.oo.onchart.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.oo.onchart.R;
import org.oo.onchart.student.Lesson;
import org.oo.onchart.ui.adapter.LessonListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LessonListFragment extends Fragment {

    RecyclerView lessonList;
    LessonListAdapter adapter;
    List<Lesson> lessons;

    public LessonListFragment() {
        lessons = new ArrayList<>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter = new LessonListAdapter(getActivity(), lessons);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lesson_list, container, false);
        lessonList = (RecyclerView) rootView.findViewById(R.id.rv_lessons);
        lessonList.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.setLessons(lessons);
        lessonList.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
        return rootView;
    }

    public void addLesson(Lesson lesson) {
        if(lesson != null)
            lessons.add(lesson);
    }

    public void updateList() {
        //adapter.setList();
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }
}