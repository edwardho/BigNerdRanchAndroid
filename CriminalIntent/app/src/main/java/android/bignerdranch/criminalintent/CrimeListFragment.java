package android.bignerdranch.criminalintent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeListFragment extends Fragment {

    private static final String CLICKED_POSITION_ID = "clicked_position_id";
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private static final int VIEWTYPE_REQUIRES_POLICE = 1;
    private static final int VIEWTYPE_DOESNT_REQUIRE_POLICE = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 999;

    private View mLayout;
    private Button mAddCrimeButton;
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private int mClickedPosition;
    private boolean mSubtitleVisible;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mLayout = view.findViewById(R.id.ll_add_crime_view);
        mAddCrimeButton = view.findViewById(R.id.btn_add_crime);
        mAddCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewCrime();
            }
        });

        if (savedInstanceState != null) {
            mClickedPosition = savedInstanceState.getInt(CLICKED_POSITION_ID);
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.itm_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        }
        else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.itm_new_crime:
                addNewCrime();
                return true;
            case R.id.itm_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        //String subtitle = getString(R.string.subtitle_format, crimeCount);
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CLICKED_POSITION_ID, mClickedPosition);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.tv_crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.tv_crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.iv_solved);
        }

        public void bind(Crime crime) {
            mCrime = crime;

            Date date = mCrime.getDate();
            String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);

            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(formattedDate);
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            mClickedPosition = getAdapterPosition();
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }
    }

    private class PoliceCrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Button mContactPoliceButton;

        public PoliceCrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_police_crime, parent, false));

            mTitleTextView = (TextView) itemView.findViewById(R.id.tv_crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.tv_crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.iv_solved);
            mContactPoliceButton = (Button) itemView.findViewById(R.id.btn_contact_police);

            itemView.setOnClickListener(this);
            mContactPoliceButton.setOnClickListener(this);
        }

        public void bind(Crime crime) {
            mCrime = crime;

            Date date = mCrime.getDate();
            String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);

            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(formattedDate);
            mContactPoliceButton.setEnabled(true);
            mContactPoliceButton.setVisibility(crime.isSolved() ? View.GONE : View.VISIBLE);
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_contact_police) {
                Toast.makeText(getActivity(), "Police contacted for crime " + mCrime.getTitle(), Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
                startActivity(intent);
            }
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            switch (viewType) {
                case VIEWTYPE_DOESNT_REQUIRE_POLICE:
                    return new CrimeHolder(layoutInflater, viewGroup);
                case VIEWTYPE_REQUIRES_POLICE:
                    return new PoliceCrimeHolder(layoutInflater, viewGroup);
                default:
                    // Default is not police required
                    return new CrimeHolder(layoutInflater, viewGroup);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Crime crime = mCrimes.get(position);

            if(holder instanceof  CrimeHolder) {
                ((CrimeHolder) holder).bind(crime);
            }
            else if(holder instanceof  PoliceCrimeHolder) {
                ((PoliceCrimeHolder) holder).bind(crime);
            }
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position) {
            Crime crime = mCrimes.get(position);

            if (crime.requiresPolice()) {
                return VIEWTYPE_REQUIRES_POLICE;
            }
            else{
                return VIEWTYPE_DOESNT_REQUIRE_POLICE;
            }
        }

        public void setCrimes(List<Crime> crimes) {
            // Diff Utils to handle list difference on crime deletion
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CrimeDiffCallback(crimes, mCrimes));
            diffResult.dispatchUpdatesTo(this);

            // Swap out list of crimes displayed for new list
            mCrimes = crimes;
        }
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        mLayout.setVisibility(crimes.isEmpty() ? View.VISIBLE : View.GONE);

        if(mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyItemChanged(mClickedPosition);
        }

        updateSubtitle();
    }

    private void addNewCrime() {
        Crime crime = new Crime(UUID.randomUUID());
        CrimeLab.get(getActivity()).addCrime(crime);
        Intent intent = CrimePagerActivity
                .newIntent(getActivity(), crime.getId());
        startActivity(intent);
    }
}
