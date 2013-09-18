package com.twitter.android.fibonaccinative;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import java.lang.ref.WeakReference;

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
        super.setRetainInstance(true);
        // parse our arguments (from the factory method
        Bundle arguments = super.getArguments();
        final int type = arguments.getInt("type");
        final long n = arguments.getLong("n");
        Log.d(TAG, "onCreate() with type=" + type + ", n=" + n);
        // kick off the execution
        new FibonacciAsyncTask(this, type, n).execute();
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

    // simple Java bean to hold the result/time of fibonacci calculation
    static final class FibonacciResponse {
        private final long result;
        private final long time;

        FibonacciResponse(long result, long time) {
            this.result = result;
            this.time = time;
        }

        public long getResult() {
            return result;
        }

        public long getTime() {
            return time;
        }
    }

    // async task to perform the fibonacci calculation on a background thread
    static final class FibonacciAsyncTask extends AsyncTask<Void, Void, FibonacciResponse> {
        private final WeakReference<FibonacciFragment> fibonacciFragmentRef;
        private final int type;
        private final long n;

        public FibonacciAsyncTask(FibonacciFragment fibonacciFragment, int type, long n) {
            // save a weak reference to the activity
            // (in case it gets destroyed, we don't want to prevent it from being GC'ed)
            this.fibonacciFragmentRef = new WeakReference<FibonacciFragment>(fibonacciFragment);
            this.type = type;
            this.n = n;
        }

        // do the actual fibonacci calculation (on a background thread)
        @Override
        protected FibonacciResponse doInBackground(Void... params) {
            Log.d(TAG, "starting doInBackground() with for type=" + type + " and n=" + n);
            long result = 0;
            long time = SystemClock.uptimeMillis();
            switch (this.type) {
                case R.id.type_fib_jr:
                    result = FibLib.fibJR(this.n);
                    break;
                case R.id.type_fib_ji:
                    result = FibLib.fibJI(this.n);
                    break;
                case R.id.type_fib_nr:
                    result = FibLib.fibNR(this.n);
                    break;
                case R.id.type_fib_ni:
                    result = FibLib.fibNI(this.n);
                    break;
            }
            time = SystemClock.uptimeMillis() - time;
            Log.d(TAG, "finished doInBackground() for type=" + type + " and n=" + n + " " +
                    "with result=" + result);
            // return the response via a message queue to the UI thread (onPostExecute)
            return new FibonacciResponse(result, time);
        }

        // handle the result (on the UI thread)
        @Override
        protected void onPostExecute(FibonacciResponse fibonacciResponse) {
            FibonacciFragment fibonacciFragment = this.fibonacciFragmentRef.get();
            if (fibonacciFragment == null) {
                Log.d(TAG, "No fragment. Nothing to do with result. Giving up.");
            } else {

                String result = fibonacciFragment.getString(R.string.result_text,
                        this.n, fibonacciResponse.getResult(),
                        fibonacciResponse.getTime());
                if (fibonacciFragment.onResultListener == null) {
                    Log.d(TAG, "Saving pending result: " + result);
                    // save for the activity when it comes back (if
                    // possible?)
                    fibonacciFragment.pendingResult = result;
                } else {
                    // we are done, send the result
                    Log.d(TAG, "Submitting result: " + result);
                    fibonacciFragment.onResultListener.onResult(result);
                }
            }
        }
    }
}