package com.example.android.navigationdrawer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by heeye on 2016-09-22.
 */
public class EditDiaryFrag extends Fragment {

    private static final String ADDDIARY_URL = "http://condi.swu.ac.kr/schkr/addDiary.php";

    private static final String ARG_PARAM1 = "diarytxt";
    private static final String ARG_PARAM2 = "startfeel";
    private static final String ARG_PARAM3 = "finishfeel";
    private static final String ARG_PARAM4 = "wktime";
    private static final String ARG_PARAM5 = "wklength";
    private static final String ARG_PARAM6 = "wkcount";
    private static final String ARG_PARAM7 = "calorie";
    private static final String ARG_PARAM8 = "selectName";
    private static final String ARG_PARAM9 = "selectId";

    //별점 따로

    int memberid = 1;

    TextView txt_date;
    String currentDate;
    Button btn_diary_no, btn_diary_done;
    EditText editText;

    String diaryTxt;

    static String startfeel, finishfeel, wktime, wklength, wkcount, calorie, selectName;
    static int selectId, rating;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getArguments();

        startfeel = extra.getString("startfeel");
        wktime = extra.getString("wktime");
        wklength = extra.getString("wklength");
        wkcount = extra.getString("wkcount");
        calorie = extra.getString("calorie");
        selectName = extra.getString("selectName");
        selectId = extra.getInt("selectId");

        //Toast.makeText(getActivity(),startfeel+wktime+calorie,Toast.LENGTH_LONG).show(); 다 넘어옴 ㅇㅇ

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
        TextView txtTitle = (TextView) toolbar.findViewById(R.id.txt_toolbar);

        toolbar.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.top));
        txtTitle.setText("산책 정리하기");

        View rootView = inflater.inflate(R.layout.diary_edit, container, false);


        txt_date = (TextView) rootView.findViewById(R.id.txt_date);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
        Date currentTime = new Date();
        currentDate = formatter.format(currentTime);
        txt_date.setText(currentDate+" 산책");

        editText = (EditText)rootView.findViewById(R.id.edit_diary);
        editText.setSelection ( editText.length());



        btn_diary_done = (Button)rootView.findViewById(R.id.btn_diary_done);
        btn_diary_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              diaryTxt = editText.getText().toString();

                try {
                    selectName = selectName.replaceAll("\\p{Space}|\\p{Punct}", "");
                    diaryTxt = diaryTxt.replaceAll("\\p{Space}|\\p{Punct}", "");
                    addDiary(memberid,currentDate,selectId,selectName,diaryTxt,startfeel,finishfeel,wktime,wklength,wkcount,calorie,rating);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }
        });

        FinishFeelDia mDialog = new FinishFeelDia();
        mDialog.show(getFragmentManager(),"MYTAG");


        return  rootView;

    }//onCreateView() EditDiaryFrag

    public static class FinishFeelDia extends DialogFragment {

        Button btn_diary_next;


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            getDialog().setCanceledOnTouchOutside(false);
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onResume() {
            super.onResume();

            int dialogWidth = getResources().getDimensionPixelSize(R.dimen.popup_widthsize);
            int dialogHeight = getResources().getDimensionPixelSize(R.dimen.popup_heightsize);

            if (getDialog() != null) {
                Window window = getDialog().getWindow();
                window.setLayout(dialogWidth, dialogHeight);
                window.setGravity(Gravity.CENTER);
            }
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
            View view = mLayoutInflater.inflate(R.layout.finishifeel_dia,null);
            mBuilder.setView(view);


            final Spinner fspinner = (Spinner)view.findViewById(R.id.feelingspinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.feeling, R.layout.spin);
            adapter.setDropDownViewResource(R.layout.spin_dropdown);
            fspinner.setPrompt("condition");
            fspinner.setAdapter(adapter);
            fspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String str = (String) fspinner.getSelectedItem();
                    finishfeel = str;

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(getActivity(), "값을 선택해", Toast.LENGTH_SHORT).show();


                }
            });



            btn_diary_next = (Button)view.findViewById(R.id.btn_diary_next);
            btn_diary_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //별점 다이얼로그
                    RatingDialog mRatingDia = new RatingDialog();
                    mRatingDia.show(getFragmentManager(),"MYTAG");
                    dismiss();
                }
            });

            return mBuilder.create();
        }//onCreateDialog() FinishDia
    }//FinishFeelDia

    public static class RatingDialog extends DialogFragment {

        Button btn_rating_next;


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            getDialog().setCanceledOnTouchOutside(false);
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onResume() {
            super.onResume();

            int dialogWidth = getResources().getDimensionPixelSize(R.dimen.popup_widthsize);
            int dialogHeight = getResources().getDimensionPixelSize(R.dimen.popup_heightsize);

            if (getDialog() != null) {
                Window window = getDialog().getWindow();
                window.setLayout(dialogWidth, dialogHeight);
                window.setGravity(Gravity.CENTER);
            }
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
            View view = mLayoutInflater.inflate(R.layout.ratingbar_dia,null);
            mBuilder.setView(view);

            Spinner starspinner = (Spinner)view.findViewById(R.id.starspinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.rating, R.layout.spin);
            adapter.setDropDownViewResource(R.layout.spin_dropdown);
            starspinner.setPrompt("★☆☆☆☆");
            starspinner.setAdapter(adapter);
            starspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    rating = position +1;


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });



            btn_rating_next = (Button)view.findViewById(R.id.btn_rating_next);
            btn_rating_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dismiss();

                }
            });

            return mBuilder.create();
        }
    }

    private void addDiary(int memberid, String currentdate, final int selectid, final String selectname, String diarytxt, final String startfeel, final String finishfeel, final String wktime, final String wklength, final String wkcount, final String calorie, int rating) throws UnsupportedEncodingException {

        String urlSuffix ="?memberid="+memberid+
                "&currentdate="+currentdate+
                "&selectid="+selectid+
                "&selectname="+selectname+
                "&diarytxt="+diarytxt+
                "&startfeel="+startfeel+
                "&finishfeel="+finishfeel+
                "&wktime="+wktime+
                "&wklength="+wklength+
                "&wkcount="+wkcount+
                "&calorie="+calorie+
                "&rating="+rating;

        Log.d("시발",urlSuffix);
        class addPro extends AsyncTask<String, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if (s.equals("success1success2")) {
                    Toast.makeText(getActivity(), "전송 완료", Toast.LENGTH_SHORT).show();


                    //show diary 프래그먼트 보여줘야됨
                    ShowDiaryFrag frag = new ShowDiaryFrag();

                    Bundle args = new Bundle();
                    args.putString(ARG_PARAM1,diaryTxt);
                    args.putString(ARG_PARAM2,startfeel);
                    args.putString(ARG_PARAM3,finishfeel);
                    args.putString(ARG_PARAM4,wktime);
                    args.putString(ARG_PARAM5,wklength);
                    args.putString(ARG_PARAM6,wkcount);
                    args.putString(ARG_PARAM7,calorie);
                    args.putString(ARG_PARAM8,selectName);
                    args.putInt(ARG_PARAM9,selectId);

                    frag.setArguments(args);

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.content_frame, frag);
                    ft.addToBackStack(null);
                    ft.commit();

                }
                else if (s.equals("errrrrroooooooooor")){
                    Toast.makeText(getActivity(), "뭐가 비었는데?", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "몰라 에러야", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader = null;
                Log.d("MyTag",s+"두인백그라운드 진입");
                try {
                    Log.d("MyTag",s+"두인백그라운드 try진입");

                    URL url = new URL(ADDDIARY_URL + s);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String result;

                    result = bufferedReader.readLine();

                    Log.d("시발",result);

                    return result;
                } catch (Exception e) {
                    return null;
                }

            }

        }//end of addPRo

        addPro add = new addPro();
        add.execute(urlSuffix);


    }//addDiary

}
