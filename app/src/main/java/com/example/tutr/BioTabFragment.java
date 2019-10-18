package com.example.tutr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BioTabFragment extends Fragment {

    BioTabFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentRootView = inflater.inflate(R.layout.fragment_bio_tab, container, false);
        TextView contactInfoTextView = fragmentRootView.findViewById(R.id.contact_information_text_view);
        EditText contactInfoEditText = fragmentRootView.findViewById(R.id.contact_information_edit_text);
        TextView schoolOccupationTextView = fragmentRootView.findViewById(R.id.school_occupation_text_view);
        EditText schoolOccupationEditText = fragmentRootView.findViewById(R.id.school_occupation_edit_text);
        TextView areaOfExperiseTextView = fragmentRootView.findViewById(R.id.area_of_expertise_text_view);
        EditText areaofExpertiseEditText = fragmentRootView.findViewById(R.id.area_of_expertise_text_edit_text);
        TextView descriptionTextView = fragmentRootView.findViewById(R.id.description_text_view);
        EditText descriptionEditText = fragmentRootView.findViewById(R.id.description_edit_text);
        return  fragmentRootView;
    }
}
