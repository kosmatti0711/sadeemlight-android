package com.sadeemlight.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sadeemlight.Models.ExamNameListModel;
import com.sadeemlight.R;
import com.sadeemlight.activity.Exam_que_ans_fragment;
import com.sadeemlight.venus_model.ModelSubject;

import java.util.List;

/**
 * Created by mohammedsalah on 12/20/17.
 */

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.MyViewHolder> {

    private List<ExamNameListModel> mList;
    private Context mContext;

    public ExamAdapter(Context mContext , List<ExamNameListModel> mList) {
        this.mList = mList;
        this.mContext = mContext;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //TODO set the layout item exam name list
        View mView = LayoutInflater.from(mContext).inflate(R.layout.exam_name_list_item_view,parent,false);

        return new MyViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        //TODO set the exam name from the list
        holder.txtExamName.setText(mList.get(position).getExamTitle());

        //TODO trigger the exam name onClick event
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppCompatActivity activity = (AppCompatActivity)v.getContext();

                if(mList.get(position).count == 0)
                {
                    try
                    {
                        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                        alert.setTitle(R.string.exam_dialog_result);
                        alert.setMessage(activity.getString(R.string.exam_dialog_emtpyresult));
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {

                            }
                        });

                        alert.show();
                    }catch (Exception e){}
                    return;
                }


                //TODO set the data of the exam to opent the right question
                Bundle args;
                Fragment fm = new Exam_que_ans_fragment();
                args = new Bundle();

                args.putString("examTitle", mList.get(position).getExamTitle());
                args.putString("subjectName", mList.get(position).getSubjectName());
                args.putInt("examId",mList.get(position).getId());

                fm.setArguments(args);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container,fm).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        //TODO return the size of the list
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtExamName;
        RelativeLayout parentView;

        MyViewHolder(View view){
            super(view);
            //TODO bind the textView to view exam name
            this.txtExamName = view.findViewById(R.id.txtExamName);

            //TODO bind the parent view to  trigger the click event
            this.parentView = view.findViewById(R.id.parentView);
        }
    }
}
