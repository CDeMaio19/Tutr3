package com.example.tutr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ScheduleTabFragment extends Fragment {

    private TextView MonAv;
    private TextView TueAv;
    private TextView WedAv;
    private TextView ThuAv;
    private TextView FriAv;
    private TextView SatAv;
    private TextView SunAv;

    private Spinner Mon1;
    private Spinner Mon2;
    private Spinner Wed1;
    private Spinner Wed2;
    private Spinner Tue1;
    private Spinner Tue2;
    private Spinner Thu1;
    private Spinner Thu2;
    private Spinner Fri1;
    private Spinner Fri2;
    private Spinner Sat1;
    private Spinner Sat2;
    private Spinner Sun1;
    private Spinner Sun2;

    private Button SubMon;
    private Button SubTue;
    private Button SubWed;
    private Button SubThu;
    private Button SubFri;
    private Button SubSat;
    private Button SubSun;

    private Button addButton;
    private Button subButton;
    private Button clearButton;

    ScheduleTabFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentRootView = inflater.inflate(R.layout.fragment_schedule_tab, container, false);
        MonAv = fragmentRootView.findViewById(R.id.MonAv);
        TueAv = fragmentRootView.findViewById(R.id.TueAv);
        WedAv = fragmentRootView.findViewById(R.id.WedAv);
        ThuAv = fragmentRootView.findViewById(R.id.ThuAv);
        FriAv = fragmentRootView.findViewById(R.id.FriAv);
        SatAv = fragmentRootView.findViewById(R.id.SatAv);
        SunAv = fragmentRootView.findViewById(R.id.SunAv);

        Mon1 = fragmentRootView.findViewById(R.id.Mon1);
        Mon2 = fragmentRootView.findViewById(R.id.Mon2);
        Tue1 = fragmentRootView.findViewById(R.id.Tue1);
        Tue2 = fragmentRootView.findViewById(R.id.Tue2);
        Wed1 = fragmentRootView.findViewById(R.id.Wed1);
        Wed2 = fragmentRootView.findViewById(R.id.Wed2);
        Thu1 = fragmentRootView.findViewById(R.id.Thu1);
        Thu2 = fragmentRootView.findViewById(R.id.Thu2);
        Fri1 = fragmentRootView.findViewById(R.id.Fri1);
        Fri2 = fragmentRootView.findViewById(R.id.Fri2);
        Sat1 = fragmentRootView.findViewById(R.id.Sat1);
        Sat2 = fragmentRootView.findViewById(R.id.Sat2);
        Sun1 = fragmentRootView.findViewById(R.id.Sun1);
        Sun2 = fragmentRootView.findViewById(R.id.Sun2);

        SubMon = fragmentRootView.findViewById(R.id.SubMon);
        SubTue = fragmentRootView.findViewById(R.id.SubTue);
        SubWed = fragmentRootView.findViewById(R.id.SubWed);
        SubThu = fragmentRootView.findViewById(R.id.SubThu);
        SubFri = fragmentRootView.findViewById(R.id.SubFri);
        SubSat = fragmentRootView.findViewById(R.id.SubSat);
        SubSun = fragmentRootView.findViewById(R.id.SubSun);

        addButton = fragmentRootView.findViewById(R.id.addButton);
        subButton = fragmentRootView.findViewById(R.id.addButton2);
        clearButton = fragmentRootView.findViewById(R.id.addButton3);


        ArrayAdapter<CharSequence> Time = ArrayAdapter.createFromResource(getActivity(), R.array.Time, android.R.layout.simple_spinner_item);
        Time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Mon1.setAdapter(Time);
        Mon2.setAdapter(Time);
        Tue1.setAdapter(Time);
        Tue2.setAdapter(Time);
        Wed1.setAdapter(Time);
        Wed2.setAdapter(Time);
        Thu1.setAdapter(Time);
        Thu2.setAdapter(Time);
        Fri1.setAdapter(Time);
        Fri2.setAdapter(Time);
        Sat1.setAdapter(Time);
        Sat2.setAdapter(Time);
        Sun1.setAdapter(Time);
        Sun2.setAdapter(Time);

        Mon1.setVisibility(View.GONE);
        Mon2.setVisibility(View.GONE);
        Tue1.setVisibility(View.GONE);
        Tue2.setVisibility(View.GONE);
        Wed1.setVisibility(View.GONE);
        Wed2.setVisibility(View.GONE);
        Thu1.setVisibility(View.GONE);
        Thu2.setVisibility(View.GONE);
        Fri1.setVisibility(View.GONE);
        Fri2.setVisibility(View.GONE);
        Sat1.setVisibility(View.GONE);
        Sat2.setVisibility(View.GONE);
        Sun1.setVisibility(View.GONE);
        Sun2.setVisibility(View.GONE);
        SubMon.setVisibility(View.INVISIBLE);
        SubTue.setVisibility(View.INVISIBLE);
        SubWed.setVisibility(View.INVISIBLE);
        SubThu.setVisibility(View.INVISIBLE);
        SubFri.setVisibility(View.INVISIBLE);
        SubSat.setVisibility(View.INVISIBLE);
        SubSun.setVisibility(View.INVISIBLE);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Mon1.setVisibility(View.VISIBLE);
                Mon2.setVisibility(View.VISIBLE);
                Tue1.setVisibility(View.VISIBLE);
                Tue2.setVisibility(View.VISIBLE);
                Wed1.setVisibility(View.VISIBLE);
                Wed2.setVisibility(View.VISIBLE);
                Thu1.setVisibility(View.VISIBLE);
                Thu2.setVisibility(View.VISIBLE);
                Fri1.setVisibility(View.VISIBLE);
                Fri2.setVisibility(View.VISIBLE);
                Sat1.setVisibility(View.VISIBLE);
                Sat2.setVisibility(View.VISIBLE);
                Sun1.setVisibility(View.VISIBLE);
                Sun2.setVisibility(View.VISIBLE);
                SubMon.setVisibility(View.VISIBLE);
                SubTue.setVisibility(View.VISIBLE);
                SubWed.setVisibility(View.VISIBLE);
                SubThu.setVisibility(View.VISIBLE);
                SubFri.setVisibility(View.VISIBLE);
                SubSat.setVisibility(View.VISIBLE);
                SubSun.setVisibility(View.VISIBLE);


            }
        });

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mon1.setVisibility(View.GONE);
                Mon2.setVisibility(View.GONE);
                Tue1.setVisibility(View.GONE);
                Tue2.setVisibility(View.GONE);
                Wed1.setVisibility(View.GONE);
                Wed2.setVisibility(View.GONE);
                Thu1.setVisibility(View.GONE);
                Thu2.setVisibility(View.GONE);
                Fri1.setVisibility(View.GONE);
                Fri2.setVisibility(View.GONE);
                Sat1.setVisibility(View.GONE);
                Sat2.setVisibility(View.GONE);
                Sun1.setVisibility(View.GONE);
                Sun2.setVisibility(View.GONE);
                SubMon.setVisibility(View.INVISIBLE);
                SubTue.setVisibility(View.INVISIBLE);
                SubWed.setVisibility(View.INVISIBLE);
                SubThu.setVisibility(View.INVISIBLE);
                SubFri.setVisibility(View.INVISIBLE);
                SubSat.setVisibility(View.INVISIBLE);
                SubSun.setVisibility(View.INVISIBLE);
            }
        });














        return  fragmentRootView;


    }

}
