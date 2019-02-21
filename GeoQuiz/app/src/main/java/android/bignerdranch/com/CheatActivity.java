package android.bignerdranch.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE =
            "com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN =
            "com.bignerdranch.android.geoquiz.answer_shown";
    private static final String CHEAT_INDEX = "cheat";

    private TextView mAnswerTextView;
    private Button mShowAnswerButton;

    private boolean mAnswerIsTrue;
    private boolean mDidCheat;

    // Creates a new intent with extra answerIsTrue
    public static Intent newIntent(Context context, boolean answerIsTrue) {
        Intent intent = new Intent(context, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return intent;
    }

    // Returns boolean that says whether the answer was shown or not
    public static boolean wasAnswerShown(Intent intent) {
        return intent.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        if (savedInstanceState != null) {
            // log if cheat button was clicked
            boolean cheated = savedInstanceState.getBoolean(CHEAT_INDEX);
            setAnswerShownResult(cheated);
        }

        // Set mAnswerIsTrue variable from intent extras
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        //Configure Answer Text View
        mAnswerTextView = (TextView) findViewById(R.id.tv_answer);

        // Configure Show Answer Button
        mShowAnswerButton = (Button) findViewById(R.id.btn_show_answer);

        // Show Answer Button onClickListener
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set whether answer is true or false
                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                }
                else {
                    mAnswerTextView.setText(R.string.false_button);
                }

                // Show Answer clicked
                mAnswerTextView.setVisibility(View.VISIBLE);
                setAnswerShownResult(true);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CHEAT_INDEX, mDidCheat);
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        mDidCheat = true;
        setResult(RESULT_OK, data);
    }
}
