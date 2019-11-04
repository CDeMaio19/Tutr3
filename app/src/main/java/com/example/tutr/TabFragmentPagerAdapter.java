package com.example.tutr;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabFragmentPagerAdapter extends FragmentPagerAdapter{

    private boolean isTutor;

    private Context context;

    TabFragmentPagerAdapter(Context context, FragmentManager fm, boolean isTutor)
    {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        this.isTutor = isTutor;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

            if (position == 0) {
                return new BioTabFragment();
            } else {
                if (isTutor) {
                    return new ScheduleTabFragment();
                } else {
                    return new QuestionsAskedTabFragment();
                }
            }
    }


    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return context.getString(R.string.Bio);
            case 1:
                if(isTutor) {
                    return context.getString(R.string.Schedule);
                }
                else{
                    return context.getString(R.string.questions_asked_tab_title);
                }
                default:
                    return null;
        }
    }



}

