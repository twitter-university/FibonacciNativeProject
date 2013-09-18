package com.twitter.android.fibonaccinative;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

// used to maintain the state of the running async task in case the activity gets destroyed
public class FibonacciFragment extends Fragment {
    private static final String TAG = FibonacciFragment.class.getSimpleName();

    // factory method, since we must have a no-arg constructor
    public static FibonacciFragment newInstance(int algorithmType, long n) {
        Log.d(TAG, "Creating a new instance for type=" + algorithmType
                + " and n=" + n);
        FibonacciFragment fibonacciFragment = new FibonacciFragment();
        Bundle args = new Bundle();
        args.putInt("type", algorithmType);
        args.putLong("n", n);
        fibonacciFragment.setArguments(args);
        return fibonacciFragment;
    }

    // to be implemented by our activity
    public static interface OnResultListener {
        public void onResult(String result);
    }

    private Dialog dialog;
    private OnResultListener onResultListener;
    private String pendingResult;

    // invoked when the fragment is first created - not on configuration change - on the UI thread
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // indicate that we want to survive configuration changes!
        setRetainInstance(true);
        // parse our arguments (from the factory method
        Bundle arguments = super.getArguments();
        final int type = arguments.getInt("type");
        final long n = arguments.getLong("n");
        Log.d(TAG, "onCreate() with type=" + type + ", n=" + n);

        // run the expensive operation asynchronously
        new AsyncTask<Void, Void, String>() {

            // run on a background thread (must not touch the UI)
            @Override
            protected String doInBackground(Void... params) {
                Log.d(TAG, "starting doInBackground() with " + type + ", n=" + n);
                long result = 0;
                long t = SystemClock.uptimeMillis();
                switch (type) {
                    case R.id.type_fib_jr:
                        result = FibLib.fibJR(n);
                        break;
                    case R.id.type_fib_ji:
                        result = FibLib.fibJI(n);
                        break;
                    case R.id.type_fib_nr:
                        result = FibLib.fibNR(n);
                        break;
                    case R.id.type_fib_ni:
                        result = FibLib.fibNI(n);
                        break;
                }
                t = SystemClock.uptimeMillis() - t;
                String ret = FibonacciFragment.this.getString(R.string.result_text, n, result, t);
                Log.d(TAG, "finished doInBackground() with " + type + ", n="
                        + n + " and result=" + result);
                // send the result (ret) to the UI thread
                return ret;
            }

            // handle the result (on the UI thread)
            @Override
            protected void onPostExecute(String result) {
                // if there is no listener (i.e. activity)
                if (FibonacciFragment.this.onResultListener == null) {
                    Log.d(TAG, "Saving pending result: " + result);
                    // save for the activity when it comes back (if
                    // possible?)
                    FibonacciFragment.this.pendingResult = result;
                } else {
                    // we are done, send the result
                    Log.d(TAG, "Submitting result: " + result);
                    FibonacciFragment.this.onResultListener
                            .onResult(result);
                }
            }
        }.execute();
    }

    // invoked on configuration changes as well as state changes (on the UI thread)
    @Override
    public void onStart() {
        super.onStart();
        // get our activity (as a listener)
        this.onResultListener = (OnResultListener) super.getActivity();
        if (this.pendingResult == null) {
            Log.d(TAG, "No pending result. Saving listener for future result");
        } else {
            // send the result if we have one
            Log.d(TAG, "Submitting pending result: " + this.pendingResult);
            this.onResultListener.onResult(this.pendingResult);
            this.pendingResult = null;
        }
        // pop up a progress dialog
        Log.d(TAG, "Showing dialog");
        this.dialog = ProgressDialog.show(super.getActivity(), "", super
                .getActivity().getText(R.string.progress_text), true);
    }

    // invoked on configuration changes as well as state changes (on the UI thread)
    @Override
    public void onStop() {
        super.onStop();
        // dismiss our dialog; it can go away along with our listener
        Log.d(TAG, "Stopped. Dismissing the listener and the dialog.");
        this.dialog.dismiss();
        this.dialog = null;
        this.onResultListener = null;
    }
}