package com.twitter.android.fibonaccinative;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class FibonacciActivity extends Activity implements OnClickListener,
        FibonacciFragment.OnResultListener {
    private static final String TAG = FibonacciActivity.class.getSimpleName();

    private EditText input;

    private RadioGroup type;

    private TextView output;

    private Button button;

    private FibonacciFragment fibonacciFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fibonacci);
        this.input = (EditText) super.findViewById(R.id.input);
        this.type = (RadioGroup) super.findViewById(R.id.type);
        this.output = (TextView) super.findViewById(R.id.output);
        this.button = (Button) super.findViewById(R.id.button);
        this.button.setOnClickListener(this);
        // reconnect to our fragment (if it exists)
        this.fibonacciFragment = (FibonacciFragment) super.getFragmentManager()
                .findFragmentByTag("fibFrag");
        if (savedInstanceState != null) {
            Log.d(TAG, "Restoring output");
            this.output.setText(savedInstanceState.getCharSequence("output"));
        }
        Log.d(TAG, "onCreate fibonacciFragment=" + this.fibonacciFragment);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("output", this.output.getText());
        Log.d(TAG, "Saved output");
    }

    // called when the user clicks on the button (on the UI thread)
    public void onClick(View view) {
        String s = this.input.getText().toString();
        if (TextUtils.isEmpty(s)) {
            return;
        }
        int type = FibonacciActivity.this.type.getCheckedRadioButtonId();
        try {
            long n = Long.parseLong(s);
            Log.d(TAG, "onClick for type=" + type + " and n=" + n);
            this.button.setEnabled(false);
            // create our fragment and add it
            this.fibonacciFragment = FibonacciFragment.newInstance(type, n);
            super.getFragmentManager().beginTransaction()
                    .add(this.fibonacciFragment, "fibFrag").commit();
            Log.d(TAG, "Passed control to " + this.fibonacciFragment);
        } catch (NumberFormatException e) {
            Log.d(TAG, "Failed onClick for type=" + type + " and n=" + s);
            this.input.setError(super.getText(R.string.input_error));
        }
    }

    // called from fragment (on the UI thread) when the result is available
    public void onResult(String result) {
        Log.d(TAG, "Posting result: " + result);
        this.output.setText(result);
        this.button.setEnabled(true);
        // we no longer need the fragment; git rid of it
        super.getFragmentManager().beginTransaction()
                .remove(this.fibonacciFragment).commit();
        this.fibonacciFragment = null;
        Log.d(TAG, "Removed fragment");
    }
}
