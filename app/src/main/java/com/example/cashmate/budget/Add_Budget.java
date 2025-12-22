<<<<<<< HEAD:app/src/main/java/com/example/login_sigup/budget/Add_Budget.java
package com.example.login_sigup.budget;
=======
package com.example.cashmate.budget;
>>>>>>> 2d60c3f7529677bf7971cd928b5fb9e75167844f:app/src/main/java/com/example/cashmate/budget/Add_Budget.java
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.cashmate.R;


public class Add_Budget extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState)
    {
        View view = inflater.inflate(R.layout.create_a_budget, container, false);

        TextView btnClose = view.findViewById(R.id.btnBack);
        btnClose.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }
}
