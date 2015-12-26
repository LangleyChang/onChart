package me.zchang.onchart.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.zchang.onchart.R;
import me.zchang.onchart.student.Exam;
import me.zchang.onchart.ui.MainActivity;


public class ExamListAdapter extends RecyclerView.Adapter {
    public final static int VIEW_TYPE_HEADER = 0x0;
    public final static int VIEW_TYPE_LIST = 0x1;
    List<Exam> exams;
    Context context;

    public ExamListAdapter(Context context, List<Exam> exams) {
        this.context = context;
        this.exams = exams;
    }

    public void setExams(List<Exam> exams) {
        this.exams = exams;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.blank_short, parent, false));
            case VIEW_TYPE_LIST:
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.cd_exam_item, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            Calendar today = Calendar.getInstance();
            Exam currExamItem = exams.get(position - 1);
            TextView dateText = ((ViewHolder) holder).dateText;
            TextView timeText = ((ViewHolder) holder).timeText;
            TextView courseNameText = ((ViewHolder) holder).courseNameText;
            TextView positionText = ((ViewHolder) holder).positionText;
            CardView cardView = ((ViewHolder) holder).cardView;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
            dateText.setText(dateFormat.format(currExamItem.getStartTime()));
            timeText.setText(timeFormat.format(currExamItem.getStartTime())
                    + "-"
                    + timeFormat.format(currExamItem.getEndTime()));
            courseNameText.setText(currExamItem.getName());
            positionText.setText(currExamItem.getPosition());
            if (currExamItem.getStartTime().getTime() - today.getTimeInMillis() > 0) {
                if (currExamItem.getStartTime().getTime() - today.getTimeInMillis() <= MainActivity.MILLISECONDS_IN_A_DAY * 14) {
                    cardView.setCardBackgroundColor(Color.parseColor("#E53935"));
                    dateText.setTextColor(Color.parseColor("#E53935"));
                    timeText.setTextColor(Color.parseColor("#E53935"));
                    courseNameText.setTextColor(0xffffffff);
                    positionText.setTextColor(0xffffffff);
                } else {
                    cardView.setCardBackgroundColor(Color.parseColor("#2E7D32"));
                    dateText.setTextColor(Color.parseColor("#2E7D32"));
                    timeText.setTextColor(Color.parseColor("#2E7D32"));
                    courseNameText.setTextColor(0xffffffff);
                    positionText.setTextColor(0xffffffff);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (exams != null)
            return exams.size();
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_LIST;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        TextView timeText;
        TextView courseNameText;
        TextView positionText;
        CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            dateText = (TextView) itemView.findViewById(R.id.tv_exam_date);
            timeText = (TextView) itemView.findViewById(R.id.tv_exam_time);
            courseNameText = (TextView) itemView.findViewById(R.id.tv_exam_course_name);
            positionText = (TextView) itemView.findViewById(R.id.tv_exam_position);
            cardView = (CardView) itemView.findViewById(R.id.cd_exam);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
