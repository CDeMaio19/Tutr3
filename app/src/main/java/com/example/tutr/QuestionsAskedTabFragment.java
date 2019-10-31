package com.example.tutr;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class QuestionsAskedTabFragment extends Fragment {
    private DatabaseReference questionsAskedReference;
    private ListView questionsAskedListView;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View fragmentRootView = inflater.inflate(R.layout.fragment_questions_asked,container,false);
        questionsAskedListView = fragmentRootView.findViewById(R.id.questions_asked_listView);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        questionsAskedReference = FirebaseDatabase.getInstance().getReference("Questions").child(firebaseUser.getUid());
        SetData();
        return fragmentRootView;
    }

    private void SetData()
    {
        FirebaseListAdapter<Question> adapter = new FirebaseListAdapter<Question>(getActivity(),Question.class,
                R.layout.question_asked_list_item,questionsAskedReference) {
            @Override
            protected void populateView(View v, Question model, int position) {
                TextView question = v.findViewById(R.id.question);
                TextView majorSubject = v.findViewById(R.id.major_subject);
                TextView minorSubject = v.findViewById(R.id.minor_subject);
                TextView timeAsked = v.findViewById(R.id.time_asked);

                question.setText(model.getQuestion());
                majorSubject.setText(model.getMajorSubject());
                minorSubject.setText(model.getMinorSubject());
                timeAsked.setText(DateFormat.format("hh:mm - MM-dd-yyyy", model.getTimeAsked()));

            }
        };
        questionsAskedListView.setAdapter(adapter);
    }

}
