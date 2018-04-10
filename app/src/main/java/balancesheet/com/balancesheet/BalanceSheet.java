package balancesheet.com.balancesheet;

import android.app.Application;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

/**
 * Created by edward.choi on 21/02/2018.
 */

public class BalanceSheet extends Application {
    private GoogleAccountCredential mCredential;

    public GoogleAccountCredential getCredential() {
        return mCredential;
    }

    public void setCredential(GoogleAccountCredential someVariable) {
        this.mCredential = someVariable;
    }

    public static final String FIRST_COLUMN="First";
    public static final String SECOND_COLUMN="Second";
    public static final String THIRD_COLUMN="Third";
    public static final String FOURTH_COLUMN="Fourth";
    public static final String FIFTH_COLUMN="Fourth";
    public static final String SIXTH_COLUMN="Fourth";
    public static final String SEVENTH="Fourth";
}
