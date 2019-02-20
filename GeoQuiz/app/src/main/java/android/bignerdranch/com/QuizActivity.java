package android.bignerdranch.com;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private TextView mStatementTextView;

    private Question[] mStatementBank = new Question[] {
            new Question(R.string.statement_africa, false),
            new Question(R.string.statement_americas, true),
            new Question(R.string.statement_asia, true),
            new Question(R.string.statement_austrailia, true),
            new Question(R.string.statement_mideast, false),
            new Question(R.string.statement_oceans, true)
    };

    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Configure the true/false statement
        mStatementTextView = (TextView) findViewById(R.id.tv_quiz_statement);
        updateQuestion();

        // Configure the True Button
        mTrueButton = (Button) findViewById(R.id.btn_true);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // True clicked
                checkAnswer(true);
            }
        });

        // Configure the False Button
        mFalseButton = (Button) findViewById(R.id.btn_false);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // False clicked
                checkAnswer(false);
            }
        });

        // Configure the Next Button
        mNextButton = (Button) findViewById(R.id.btn_next);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Next clicked
                mCurrentIndex = (mCurrentIndex + 1) % mStatementBank.length;
                updateQuestion();
            }
        });

    }

    private void updateQuestion() {
        int statement = mStatementBank[mCurrentIndex].getTextResId();
        mStatementTextView.setText(statement);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mStatementBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (userPressedTrue == answerIsTrue) {
            messageResId = R.string.correct_toast;
        }
        else {
            messageResId = R.string.incorrect_toast;
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }
}
