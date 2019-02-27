package android.bignerdranch.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckbox;
    private CheckBox mRequiresPoliceCheckbox;
    private FloatingActionButton mDeleteFAButton;

    public CrimeFragment() {
        // Required empty public constructor
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        // Crime Title Field
        mTitleField = (EditText) view.findViewById(R.id.et_crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // This space intentionally left blank
            }
        });

        // Date Button
        mDateButton = (Button) view.findViewById(R.id.btn_crime_date);
        updateDate();

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fragmentManager, DIALOG_DATE);
            }
        });

        // Time Button
        mTimeButton = (Button) view.findViewById(R.id.btn_crime_time);
        updateTime();

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(fragmentManager, DIALOG_TIME);
            }
        });

        // Solved Checkbox
        mSolvedCheckbox = (CheckBox) view.findViewById(R.id.cb_crime_solved);
        mSolvedCheckbox.setChecked(mCrime.isSolved());
        mSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        // Requires Police Checkbox
        mRequiresPoliceCheckbox = (CheckBox) view.findViewById(R.id.cb_requires_police);
        mRequiresPoliceCheckbox.setChecked(mCrime.requiresPolice());
        mRequiresPoliceCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setRequiresPolice(isChecked);
            }
        });

        // Delete Floating Action Button
        mDeleteFAButton = (FloatingActionButton) view.findViewById(R.id.fab_delete);

        mDeleteFAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
            }
        });

        // Return view after view has been set up
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateTime();
        }

        if (requestCode == REQUEST_TIME) {
            Date date = (Date) data
                    .getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
            updateTime();
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private void updateTime() {

        if (mCrime.getDate().getHours() == 0) {
            if (mCrime.getDate().getMinutes() < 10) {
                mTimeButton.setText("12:0" + mCrime.getDate().getMinutes() + " AM");
            }
            else {
                mTimeButton.setText("12:" + mCrime.getDate().getMinutes() + " AM");
            }
        }
        else if (mCrime.getDate().getHours() < 12) {
            if (mCrime.getDate().getMinutes() < 10) {
                mTimeButton.setText(mCrime.getDate().getHours() + ":0" + mCrime.getDate().getMinutes() + " AM");
            }
            else {
                mTimeButton.setText(mCrime.getDate().getHours() + ":" + mCrime.getDate().getMinutes() + " AM");
            }
        }
        else if (mCrime.getDate().getHours() == 12) {
            if (mCrime.getDate().getMinutes() < 10) {
                mTimeButton.setText(mCrime.getDate().getHours() + ":0" + mCrime.getDate().getMinutes() + " PM");
            }
            else {
                mTimeButton.setText(mCrime.getDate().getHours() + ":" + mCrime.getDate().getMinutes() + " PM");
            }
        }
        else {
            if (mCrime.getDate().getMinutes() < 10) {
                mTimeButton.setText(mCrime.getDate().getHours()%12 + ":0" + mCrime.getDate().getMinutes() + " PM");
            }
            else {
                mTimeButton.setText(mCrime.getDate().getHours()%12 + ":" + mCrime.getDate().getMinutes() + " PM");
            }
        }
    }
}