package android.bignerdranch.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class CrimeListFragment extends Fragment {

    private int clickedPosition;
    private static final String CLICKED_POSITION_ID = "clicked_position_id";

    private static final int VIEWTYPE_REQUIRES_POLICE = 1;
    private static final int VIEWTYPE_DOESNT_REQUIRE_POLICE = 0;


    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            clickedPosition = savedInstanceState.getInt(CLICKED_POSITION_ID);
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CLICKED_POSITION_ID, clickedPosition);
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
            clickedPosition = getAdapterPosition();
            Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getId());
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
                Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getId());
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

            if (crime.requiresPolice() == true) {
                return VIEWTYPE_REQUIRES_POLICE;
            }
            else{
                return VIEWTYPE_DOESNT_REQUIRE_POLICE;
            }
        }
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.notifyItemChanged(clickedPosition);
        }
    }
}
