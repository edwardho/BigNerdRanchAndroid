package android.bignerdranch.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.Matrix;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_PHOTO = "DialogPhoto";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;

    private Crime mCrime;
    private File mPhotoFile;
    private ImageView mPhotoView;
    private ImageButton mPhotoButton;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckbox;
    private CheckBox mRequiresPoliceCheckbox;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
    private Button mReportButton;
    private FloatingActionButton mDeleteFAButton;
    private Callbacks mCallbacks;

    private String mSuspectId;
    private String mSuspectPhone;

    // Required interface for hosting activities
    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

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
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        // Set up PackageManager
        PackageManager packageManager = getActivity().getPackageManager();

        // Photo Image View
        mPhotoView = (ImageView) view.findViewById(R.id.iv_crime_photo);
        mPhotoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updatePhotoView();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                DetailPhotoFragment dialog = DetailPhotoFragment
                        .newInstance(mPhotoFile);
                dialog.show(fragmentManager, DIALOG_PHOTO);

            }
        });

        // Photo Image Button
        mPhotoButton = (ImageButton) view.findViewById(R.id.ib_crime_camera);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                "android.bignerdranch.criminalintent.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(
                        captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(
                            activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

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
                updateCrime();
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
                updateCrime();
            }
        });

        // Requires Police Checkbox
        mRequiresPoliceCheckbox = (CheckBox) view.findViewById(R.id.cb_requires_police);
        mRequiresPoliceCheckbox.setChecked(mCrime.requiresPolice());
        mRequiresPoliceCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setRequiresPolice(isChecked);
                updateCrime();
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

        // Only want delete button visible if it's a part of CrimePagerActivity
        if (getActivity() instanceof CrimePagerActivity){
            mDeleteFAButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
        }else{
            // Delete button hidden in twopane, swipe functionality to delete
            mDeleteFAButton.setVisibility(View.GONE);
        }

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
            updateCrime();
            updateTime();
        }
        else if (requestCode == REQUEST_TIME) {
            Date date = (Date) data
                    .getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
            updateCrime();
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
        else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "android.bignerdranch.criminalintent.fileprovider", mPhotoFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updateCrime();
            updatePhotoView();
        }
    }

    // updates Crime after a change has been made in the detail view on tablet, master/detail view
    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
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

    private void updatePhotoView(){
        try {
            if (mPhotoFile == null || !mPhotoFile.exists()) {
                mPhotoView.setImageDrawable(null);
                mPhotoView.setClickable(false);
            }
            else {
                Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());

                // Handle Camera image rotation on some devices
                ExifInterface ei = new ExifInterface(mPhotoFile.getPath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap = null;
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bitmap;
                }

                // Set image on Photo ImageView
                mPhotoView.setImageBitmap(rotatedBitmap);
                mPhotoView.setClickable(true);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}