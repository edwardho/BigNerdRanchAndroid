package android.bignerdranch.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
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
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_CALL = 3;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckbox;
    private CheckBox mRequiresPoliceCheckbox;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
    private Button mReportButton;
    private FloatingActionButton mDeleteFAButton;

    private String mSuspectId;
    private String mSuspectPhone;

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
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
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

        // Suspect Button
        // Creates intent to open Contacts
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

        mSuspectButton = (Button) view.findViewById(R.id.btn_crime_suspect);

        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        // Prevent crashing if user doesn't have any type of contacts app
        // by disabling suspect button
        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        // Call Suspect Button
        mCallSuspectButton = (Button) view.findViewById(R.id.btn_call_suspect);

        mCallSuspectButton.setEnabled(false);

        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Try to dial the number //
                Uri numberUri = Uri.parse("tel:" + mSuspectPhone);
                Intent intent = new Intent(Intent.ACTION_DIAL, numberUri);
                startActivity(intent);
            }
        });

        // Crime Report Button
        mReportButton = (Button) view.findViewById(R.id.btn_crime_report);

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(getString(R.string.send_report))
                        .createChooserIntent();

                startActivity(intent);
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
        else if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateTime();
        }
        else if (requestCode == REQUEST_TIME) {
            Date date = (Date) data
                    .getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
            updateTime();
        }
        else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();

            // Perform your query - the contactUri is like a where clause here
            Cursor cursor = getActivity().getContentResolver()
                    .query(contactUri,
                            null,
                            null,
                            null,
                            null);

            try {
                // Double check that you actually got results
                if (cursor.getCount() == 0) {
                    return;
                }

                // Pull out the first column of the first row of data -
                // that is your suspect's name
                cursor.moveToFirst();
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String suspect = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String phone = null;

                // Query for phone number
                boolean hasPhone = Integer.parseInt((cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                ))) > 0;

                if (hasPhone) {
                    Cursor pCursor = getActivity().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[] {id},
                            null);

                    if (pCursor.getCount() == 0) {
                        pCursor.close();
                    }
                    mCallSuspectButton.setEnabled(true);
                    pCursor.moveToFirst();
                    phone = pCursor.getString(pCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                    pCursor.close();
                }

                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);

                mSuspectId = id;
                mSuspectPhone = phone;
            }
            finally {
                cursor.close();
            }
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

    private String getCrimeReport() {

        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        }
        else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String requiresPoliceString = null;
        if (mCrime.requiresPolice()) {
            requiresPoliceString = getString(R.string.crime_report_requires_police);
        }
        else {
            requiresPoliceString = getString(R.string.crime_report_no_police);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = android.text.format.DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        }
        else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, requiresPoliceString, suspect);

        return report;
    }
}