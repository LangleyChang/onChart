package me.zchang.onchart.ui;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ArcMotion;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.TransitionSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import me.zchang.onchart.R;
import me.zchang.onchart.config.MainApp;
import me.zchang.onchart.config.PreferenceManager;
import me.zchang.onchart.student.Course;
import me.zchang.onchart.ui.utils.DialogToCard;

public class DetailActivity extends AppCompatActivity {

    Intent retIntent;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        intent = getIntent();
        int startColor = intent.getIntExtra("color", -1);
        final Course course = intent.getParcelableExtra(getString(R.string.intent_lesson));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ArcMotion arcMotion = new ArcMotion();
            arcMotion.setMinimumHorizontalAngle(50f);
            arcMotion.setMinimumVerticalAngle(50f);
            Interpolator easeInOut = new AccelerateDecelerateInterpolator();
            TransitionSet sharedEnterSet = new TransitionSet();
            //CardToDialog sharedEnter = new CardToDialog(startColor);
            ChangeBounds sharedEnter = new ChangeBounds();
            sharedEnter.setPathMotion(arcMotion);
            sharedEnter.setInterpolator(easeInOut);
            sharedEnterSet.addTransition(sharedEnter);
            ChangeImageTransform imgTrans = new ChangeImageTransform();
            imgTrans.addTarget(R.id.iv_label);

            TransitionSet sharedExitSet = new TransitionSet();
            //ChangeBounds sharedExit = new ChangeBounds();
            DialogToCard sharedExit = new DialogToCard(startColor);
            sharedExit.setPathMotion(arcMotion);
            sharedExitSet.addTransition(sharedExit);
            sharedExitSet.addTransition(imgTrans);
            //sharedExit.setInterpolator(easeInOut);
            getWindow().setSharedElementEnterTransition(sharedEnterSet);
            getWindow().setSharedElementReturnTransition(sharedExit);
            //getWindow().setSharedElementExitTransition(sharedExit);
        }

        retIntent = new Intent();

        setResult(RESULT_CANCELED, retIntent);
        retIntent.putExtra(getString(R.string.intent_position), intent.getIntExtra(getString(R.string.intent_position), 0));
        if(course != null) {
            retIntent.putExtra(getString(R.string.intent_lesson_id), course.getId());
            final TextView lessonNameText = (TextView) findViewById(R.id.tv_lesson_name);
            TextView teacherText = (TextView) findViewById(R.id.tv_teacher);
            TextView classroomText = (TextView) findViewById(R.id.tv_classroom);
            TextView weekText = (TextView) findViewById(R.id.tv_week_cycle);
            TextView creditText = (TextView) findViewById(R.id.tv_credit);
            final ImageView labelImage = (ImageView) findViewById(R.id.iv_label);

            lessonNameText.setText(course.getName());
            teacherText.setText(course.getTeacher());
            classroomText.setText(course.getClassroom());
            weekText.setText(course.getStartWeek() + " - " + course.getEndWeek() + getString(R.string.weekday_week));
            creditText.setText(course.getCredit() + "");
            labelImage.setImageResource(PreferenceManager.labelImgs[course.getLabelImgIndex()]);

            labelImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    course.setToNextLabelImg();
                    // update local storage only.
                    ((MainApp) getApplication()).getPreferenceManager().savePicPathIndex(course.getId(), course.getLabelImgIndex());
                    labelImage.setImageResource(PreferenceManager.labelImgs[course.getLabelImgIndex()]);
                    //((MainActivity)getActivity()).getListFragment().adapter.notifyItemChanged(position);
                    //retIntent.putExtra(getString(R.string.intent_frag_index), fragIndex);
                    retIntent.putExtra(getString(R.string.intent_label_image_index), course.getLabelImgIndex());
                    setResult(RESULT_OK, retIntent);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
            dismissCompat(null);
    }

    public void dismissCompat(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }
}
