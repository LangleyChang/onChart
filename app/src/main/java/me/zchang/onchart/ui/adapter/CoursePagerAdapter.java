package me.zchang.onchart.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.zchang.onchart.parser.Utils;
import me.zchang.onchart.ui.LessonListFragment;

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
 * Created by langley on 11/17/15.
 */
public class CoursePagerAdapter extends FragmentPagerAdapter {
    private List<LessonListFragment> fragments;
    private Context context;
    private int numOfWeekdays;

    public CoursePagerAdapter(Context context, FragmentManager fm, List<LessonListFragment> fragments, int numOfWeekdays) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
        this.numOfWeekdays = numOfWeekdays;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return super.isViewFromObject(view, object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position != 5)
            return context.getResources().getString(Utils.weekdayFromIndex[position]);
        else
            return context.getResources().getString(Utils.weekdayFromIndex[position + (numOfWeekdays == -6 ? 1 : 0)]);
    }
}
