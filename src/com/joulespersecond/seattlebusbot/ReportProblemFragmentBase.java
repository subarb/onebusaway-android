package com.joulespersecond.seattlebusbot;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.joulespersecond.oba.ObaApi;
import com.joulespersecond.oba.request.ObaResponse;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.concurrent.Callable;

public abstract class ReportProblemFragmentBase extends SherlockFragment
        implements LoaderManager.LoaderCallbacks<ObaResponse> {
    //private static final String TAG = "ReportProblemFragmentBase";

    private static final int REPORT_LOADER = 100;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup root, Bundle savedInstanceState) {
        if (root == null) {
            // Currently in a layout without a container, so no
            // reason to create our view.
            return null;
        }
        return inflater.inflate(getLayoutId(), null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.report_problem_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.report_problem_send) {
            sendReport();
        }
        return false;
    }

    protected void sendReport() {
        UIHelp.showProgress(this, true);
        getLoaderManager().restartLoader(REPORT_LOADER, getArguments(), this);
    }

    @Override
    public Loader<ObaResponse> onCreateLoader(int id, Bundle args) {
        return createLoader(args);
    }

    @Override
    public void onLoadFinished(Loader<ObaResponse> loader, ObaResponse response) {
        UIHelp.showProgress(this, false);

        if (response.getCode() == ObaApi.OBA_OK) {
            Toast.makeText(getActivity(), R.string.report_problem_sent, Toast.LENGTH_LONG).show();
            getActivity().getSupportFragmentManager().popBackStack();
        } else {
            Toast.makeText(getActivity(), R.string.report_problem_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<ObaResponse> loader) {
        UIHelp.showProgress(this, false);
    }

    //
    // Report loader
    //
    protected static final class ReportLoader extends AsyncTaskLoader<ObaResponse> {
        private final Callable<? extends ObaResponse> mRequest;

        public ReportLoader(Context context, Callable<? extends ObaResponse> request) {
            super(context);
            mRequest = request;
        }

        @Override
        public void onStartLoading() {
            forceLoad();
        }

        @Override
        public ObaResponse loadInBackground() {
            try {
                return mRequest.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    protected abstract int getLayoutId();

    protected abstract ReportLoader createLoader(Bundle args);
}
