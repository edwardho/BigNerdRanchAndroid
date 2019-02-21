package android.bignerdranch.com;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Quizactivity";
    private static final String KEY_INDEX = "index";

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPrevImageButton;
    private ImageButton mNextImageButton;
    private TextView mStatementTextView;

    private Statement[] mStatementBank = new Statement[] {
            new Statement(R.string.statement_africa, false),
            new Statement(R.string.statement_americas, true),
            new Statement(R.string.statement_asia, true),
            new Statement(R.string.statement_austrailia, true),
            new Statement(R.string.statement_mideast, false),
            new Statement(R.string.statement_oceans, true)
    };

    private Boolean[] mAnswers = new Boolean[mStatementBank.length];

    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Log.d(LOG_TAG, "onCreate(Bundle) called");

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        // Configure the true/false statement
        mStatementTextView = (TextView) findViewById(R.id.tv_quiz_statement);

        // Configure the True and False Buttons
        mTrueButton = (Button) findViewById(R.id.btn_true);
        mFalseButton = (Button) findViewById(R.id.btn_false);

        // Configure the Prev and Next Buttons
        mPrevImageButton = (ImageButton) findViewById(R.id.btn_prev);
        mNextImageButton = (ImageButton) findViewById(R.id.btn_next);

        // Statement onClickListener
        mStatementTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TextView clicked, show next statement
                mCurrentIndex = (mCurrentIndex + 1) % mStatementBank.length;
                updateQuestion();
            }
        });
        updateQuestion();

        // True button onClickListener
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // True clicked
                checkAnswer(true);
                checkButtons();
                checkScore();
            }
        });

        // False button onClickListener
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // False clicked
                checkAnswer(false);
                checkButtons();
                checkScore();
            }
        });

        // Prev Button onClickListener
        mPrevImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prev clicked
                mCurrentIndex = (mCurrentIndex - 1) % mStatementBank.length;
                if (mCurrentIndex < 0) {
                    mCurrentIndex = mStatementBank.length-1;
                }
                updateQuestion();
            }
        });

        // Next Button onClickListener
        mNextImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Next clicked
                mCurrentIndex = (mCurrentIndex + 1) % mStatementBank.length;
                updateQuestion();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(LOG_TAG, "onSaveInstanceState(Bundle) called");
        outState.putInt(KEY_INDEX, mCurrentIndex);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart() called");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy() called");
    }

    private void updateQuestion() {
        int statement = mStatementBank[mCurrentIndex].getTextResId();
        mStatementTextView.setText(statement);
        checkButtons();
    }

    private void checkButtons () {

        if (mAnswers[mCurrentIndex] == null) {
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }
        else {
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mStatementBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (userPressedTrue == answerIsTrue) {
            mAnswers[mCurrentIndex] = true;
            messageResId = R.string.correct_toast;
        }
        else {
            mAnswers[mCurrentIndex] = false;
            messageResId = R.string.incorrect_toast;
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private void checkScore() {
        boolean allAnswered = true;
        int correctCount = 0;
        int incorrectCount = 0;
        double score = 0;

        for (int i = 0; i < mAnswers.length; i++) {
            if (mAnswers[i] == null) {
                allAnswered = false;
            }
            else if (mAnswers[i] == true) {
                correctCount++;
            }
            else {
                incorrectCount++;
            }
        }

        if (allAnswered == true) {
            score = (double) (correctCount*100/(correctCount+incorrectCount));
            Toast.makeText(this, "Your score is " + score + " percent!", Toast.LENGTH_LONG).show();
        }
    }
}
