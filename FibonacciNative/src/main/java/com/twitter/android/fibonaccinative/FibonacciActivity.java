package com.twitter.android.fibonaccinative;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Locale;

public class FibonacciActivity extends Activity implements OnClickListener {

    private EditText input;

    private RadioGroup type;

    private TextView output;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_fibonacci);
        this.input = (EditText) super.findViewById(R.id.input);
        this.type = (RadioGroup) super.findViewById(R.id.type);
        this.output = (TextView) super.findViewById(R.id.output);
        Button button = (Button) super.findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    public void onClick(View view) {
        String s = this.input.getText().toString();
        if (TextUtils.isEmpty(s)) {
            return;
        }
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Calculating...", true);
        final long n = Long.parseLong(s);
        final Locale locale = super.getResources().getConfiguration().locale;

        // WARNING: fails when rotated; which is OK for our demo purposes. In real-life consider
        // using an AsyncTask within a detachable fragment that retains instance state when its
        // parent activity is destroyed and recreated.
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                long result = 0;
                long t = SystemClock.uptimeMillis();
                switch (FibonacciActivity.this.type.getCheckedRadioButtonId()) {
                    case R.id.type_fib_jr:
                        result = FibLib.fibJR(n);
                        break;
                    case R.id.type_fib_ji:
                        result = FibLib.fibJI(n);
                        break;
                }
                t = SystemClock.uptimeMillis() - t;
                return String.format(locale, "fib(%d)=%d in %d ms", n, result,
                        t);
            }

            @Override
            protected void onPostExecute(String result) {
                dialog.dismiss();
                FibonacciActivity.this.output.setText(result);
            }
        }.execute();
    }
}
