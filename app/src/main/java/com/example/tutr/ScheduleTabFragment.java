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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class ScheduleTabFragment extends Fragment {

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

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

    private Button ClearMon;
    private Button ClearTue;
    private Button ClearWed;
    private Button ClearThu;
    private Button ClearFri;
    private Button ClearSat;
    private Button ClearSun;

    ScheduleTabFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentRootView = inflater.inflate(R.layout.fragment_schedule_tab, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");




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

        ClearMon = fragmentRootView.findViewById(R.id.ClearMon);
        ClearTue = fragmentRootView.findViewById(R.id.ClearTue);
        ClearWed = fragmentRootView.findViewById(R.id.ClearWed);
        ClearThu = fragmentRootView.findViewById(R.id.ClearThu);
        ClearFri = fragmentRootView.findViewById(R.id.ClearFri);
        ClearSat = fragmentRootView.findViewById(R.id.ClearSat);
        ClearSun = fragmentRootView.findViewById(R.id.ClearSun);



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
        ClearMon.setVisibility(View.GONE);
        ClearTue.setVisibility(View.GONE);
        ClearWed.setVisibility(View.GONE);
        ClearThu.setVisibility(View.GONE);
        ClearFri.setVisibility(View.GONE);
        ClearSat.setVisibility(View.GONE);
        ClearSun.setVisibility(View.GONE);

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
                ClearMon.setVisibility(View.VISIBLE);
                ClearTue.setVisibility(View.VISIBLE);
                ClearWed.setVisibility(View.VISIBLE);
                ClearThu.setVisibility(View.VISIBLE);
                ClearFri.setVisibility(View.VISIBLE);
                ClearSat.setVisibility(View.VISIBLE);
                ClearSun.setVisibility(View.VISIBLE);


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
                ClearMon.setVisibility(View.GONE);
                ClearTue.setVisibility(View.GONE);
                ClearWed.setVisibility(View.GONE);
                ClearThu.setVisibility(View.GONE);
                ClearFri.setVisibility(View.GONE);
                ClearSat.setVisibility(View.GONE);
                ClearSun.setVisibility(View.GONE);
            }
        });


        SubMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonAv.append(SetAvalibility(Mon1, Mon2));
            }
        });
        ClearMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Clear(MonAv);
            }
        });

        SubTue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TueAv.append(SetAvalibility(Tue1, Tue2));
            }
        });
        ClearTue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Clear(TueAv);
            }
        });

        SubWed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WedAv.append(SetAvalibility(Wed1, Wed2));
            }
        });
        ClearWed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Clear(WedAv);
            }
        });

        SubThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThuAv.append(SetAvalibility(Thu1, Thu2));
            }
        });
        ClearThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Clear(ThuAv);
            }
        });

        SubFri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriAv.append(SetAvalibility(Fri1, Fri2));
            }
        });
        ClearFri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Clear(FriAv);
            }
        });

        SubSat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SatAv.append(SetAvalibility(Sat1, Sat2));
            }
        });
        ClearSat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Clear(SatAv);
            }
        });

        SubSun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SunAv.append(SetAvalibility(Sun1, Sun2));
            }
        });
        ClearSun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Clear(SunAv);
            }
        });











        return  fragmentRootView;


    }

    public String SetAvalibility(Spinner One, Spinner Two)
    {
       String From = One.getSelectedItem().toString();
       String To = Two.getSelectedItem().toString();
       String Av = " " + From + "-" + To ;
       return Av;
    }

    public void  Clear (TextView A)
    {
        A.setText("Availability:");
    }

    public void InsertData (View view, final TextView Av){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = null;
                user = dataSnapshot.child("Tutors").child(firebaseUser.getUid()).getValue(User.class);
                reference = FirebaseDatabase.getInstance().getReference("Users").child("Tutors").child(firebaseUser.getUid());
                if (Av == MonAv)
                {
                    user.setMondayAvalibility(MonAv.getText().toString());
                }
                if (Av == TueAv)
                {
                    user.setTuedayAvalibility(TueAv.getText().toString());
                }
                if (Av == WedAv)
                {
                    user.setWednesdayAvalibility(WedAv.getText().toString());
                }
                if (Av == ThuAv)
                {
                    user.setThursdayAvalibility(ThuAv.getText().toString());
                }
                if (Av == FriAv)
                {
                    user.setFridayAvalibility(FriAv.getText().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
