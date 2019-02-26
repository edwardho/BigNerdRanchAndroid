package android.bignerdranch.criminalintent;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.tv_crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.tv_crime_date);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), mCrime.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();
        }
    }

    private class PoliceCrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Button mContactPoliceButton;

        public PoliceCrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_police_crime, parent, false));

            mTitleTextView = (TextView) itemView.findViewById(R.id.tv_crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.tv_crime_date);
            mContactPoliceButton = (Button) itemView.findViewById(R.id.btn_contact_police);

            itemView.setOnClickListener(this);
            mContactPoliceButton.setOnClickListener(this);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mContactPoliceButton.setEnabled(true);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_contact_police) {
                Toast.makeText(getActivity(), "Police contacted for " + mCrime.getTitle(), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), mCrime.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();
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
                case 0:
                    return new CrimeHolder(layoutInflater, viewGroup);
                case 1:
                    return new PoliceCrimeHolder(layoutInflater, viewGroup);
                default:
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
                // Requires police
                return 1;
            }
            else{
                // Does not require police
                return 0;
            }
        }
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        mAdapter = new CrimeAdapter(crimes);
        mCrimeRecyclerView.setAdapter(mAdapter);
    }
}
