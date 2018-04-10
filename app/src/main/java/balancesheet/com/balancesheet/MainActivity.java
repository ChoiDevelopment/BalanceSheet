package balancesheet.com.balancesheet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ListView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.Sheets;
import android.app.ProgressDialog;
/*import android.widget.Toolbar;*/
import android.support.v7.widget.Toolbar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;


import static balancesheet.com.balancesheet.BalanceSheet.FIRST_COLUMN;
import static balancesheet.com.balancesheet.BalanceSheet.FOURTH_COLUMN;
import static balancesheet.com.balancesheet.BalanceSheet.SECOND_COLUMN;
import static balancesheet.com.balancesheet.BalanceSheet.THIRD_COLUMN;

public class MainActivity extends AppCompatActivity {
    ProgressDialog mProgress;
    private TextView mTextMessage;
    private ListView simpleList;
    private String mSpreadSheetID = "1skxXcYdjUZ1ZOuXfea9Kcexk10lUUpPOakkqQrBLMrI";
    private String mApplicationName = "Google Sheets API Android Quickstart";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    /*mTextMessage.setText(R.string.title_home);*/

                    return true;
                case R.id.navigation_dashboard:
                    /*mTextMessage.setText(R.string.title_dashboard);*/
                    return true;
                case R.id.navigation_notifications:
                    /*mTextMessage.setText(R.string.title_notifications);*/
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        simpleList = (ListView)findViewById(R.id.listviewBS);
        registerForContextMenu(simpleList);
        setContentView(R.layout.activity_main);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading ...");
        new GetDataFromGoogleSpreadSheet(((BalanceSheet) getApplication()).getCredential()).execute("BalanceSheet!A1:H");
        /*mTextMessage = (TextView) findViewById(R.id.message);*/
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbarBS);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    //region SpreadSheet Communication

    private class GetDataFromGoogleSpreadSheet extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        GetDataFromGoogleSpreadSheet(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(mApplicationName)
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
            try {
                return getDataFromGoogleSpreadSheet();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * @throws IOException
         */


        private ArrayList<HashMap<String, String>> getDataFromGoogleSpreadSheet(String ... inputRange) throws IOException {
            String spreadsheetId = mSpreadSheetID;
            //String range = "BalanceSheet!A1:H";
            String range = inputRange[0];
            /*List<String> results = new ArrayList<String>();*/
            ArrayList<HashMap<String, String>> returnList=new ArrayList<HashMap<String,String>>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values != null) {
                /*results.add("Name, Major");*/


                for (List row : values) {
                    HashMap<String,String> temp=new HashMap<String, String>();
                    if (row.size() > 0) {
                        temp.put(FIRST_COLUMN, row.get(0).toString());
                    }

                    if (row.size() > 1) {
                        temp.put(SECOND_COLUMN, row.get(1).toString());
                    }

                    if (row.size() > 2) {
                        temp.put(THIRD_COLUMN, row.get(2).toString());
                    }

                    if (row.size() > 3) {
                        temp.put(FOURTH_COLUMN, row.get(3).toString());
                    }
                    returnList.add(temp);
                    /*results.add(row.get(0) + ", " + row.get(4));*/
                }

            /*    ListViewAdapters adapter=new ListViewAdapters(MainActivity.this, returnList);*/

               /* simpleList = (ListView)findViewById(R.id.listviewBS);*/
                /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>( getBaseContext(), R.layout.activity_main, R.id.message, results);*/
               /* simpleList.setAdapter(adapter);*/
            }
            return returnList;
        }

        @Override
        protected void onPreExecute() {
         /*   mOutputText.setText("");*/
            mProgress.show();
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> output) {
            ListViewAdapters adapter=new ListViewAdapters(MainActivity.this, output);

            simpleList.setAdapter(adapter);
            mProgress.hide();
            /*if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
            } else {
                output.add(0, "Data retrieved using the Google Sheets API:");
                mOutputText.setText(TextUtils.join("\n", output));
            }*/
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
           /* if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            Login.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }*/
        }
    }

    private class InsertDataToGoogleSpreadSheet extends AsyncTask<InsertDataInfo, Void, Boolean> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        InsertDataToGoogleSpreadSheet(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(mApplicationName)
                    .build();
        }

        /**
         */
        @Override
        protected Boolean doInBackground(InsertDataInfo... rowsData) {
            try {
                return InsertToGoogleSpreadSheet(rowsData);
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return false;
            }
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * @throws IOException
         */


        private Boolean InsertToGoogleSpreadSheet(InsertDataInfo ... insertRows) throws IOException {
            String spreadsheetId = mSpreadSheetID;
            //String range = "MySheet!A1:A"; // A1:A refers to the 1st column of "MySheet", similarlly B1:B 2nd column. For more understanding https://developers.google.com/sheets/api/guides/concepts
            String range = insertRows[0].range; // A1:A refers to the 1st column of "MySheet", similarlly B1:B 2nd column. For more understanding https://developers.google.com/sheets/api/guides/concepts
            Integer rowCounts;
            //List<Object> row1 = new ArrayList<>();
            //row1.add("Name");
            //row1.add("Rollno");
            //row1.add("Class");
            // similarly create more rows with data
            rowCounts = this.mService.spreadsheets().values().get(spreadsheetId, range).execute().getValues().size();
            List<List<Object>> values = insertRows[0].insertRowsInfo;
            //values.add(row1);

            ValueRange valueRange = new ValueRange();
            valueRange.setMajorDimension("ROWS");
            valueRange.setValues(values);

            UpdateValuesResponse response = this.mService.spreadsheets().values()
                    .update(spreadsheetId, range, valueRange)
                    .setValueInputOption("RAW")
                    .execute();

            /*Log.e("Update_response", response.toString());*/

            return true;
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Boolean output) {
            mProgress.hide();
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
        }
    }

    private class DeleteDataFromGoogleSpreadSheet extends AsyncTask<DeleteDataInfo, Void, Boolean> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        DeleteDataFromGoogleSpreadSheet(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(mApplicationName)
                    .build();
        }

        /**
         */
        @Override
        protected Boolean doInBackground(DeleteDataInfo... rowsDelete) {
            try {
                return DeleteFromGoogleSpreadSheet(rowsDelete);
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return false;
            }
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * @throws IOException
         */


        private Boolean DeleteFromGoogleSpreadSheet(DeleteDataInfo ... deleteRows) throws IOException {
            String spreadsheetId = mSpreadSheetID;

            BatchUpdateSpreadsheetRequest content = new BatchUpdateSpreadsheetRequest();
            Request request = new Request()
                    .setDeleteDimension(new DeleteDimensionRequest()
                            .setRange(new DimensionRange()
                                    .setSheetId(deleteRows[0].spreadSheetID)
                                    .setDimension("ROWS")
                                    .setStartIndex(deleteRows[0].deleteRowsStartIndex)
                                    .setEndIndex(deleteRows[0].deleteRowsEndIndex)
                            )
                    );

            List<Request> requests = new ArrayList<Request>();
            requests.add(request);
            content.setRequests(requests);
            System.out.println(content.getRequests());

            try {
                this.mService.spreadsheets().batchUpdate(spreadsheetId, content);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Boolean output) {
            mProgress.hide();
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
        }
    }

    private class UpdateDataToGoogleSpreadSheet extends AsyncTask<UpdateDataInfo, Void, Boolean> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        UpdateDataToGoogleSpreadSheet(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(mApplicationName)
                    .build();
        }

        /**
         */
        @Override
        protected Boolean doInBackground(UpdateDataInfo ... rowsUpdate) {
            try {
                return UpdateToGoogleSpreadSheet(rowsUpdate);
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return false;
            }
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * @throws IOException
         */


        private Boolean UpdateToGoogleSpreadSheet(UpdateDataInfo ... updateRows) throws IOException {
            String spreadsheetId = mSpreadSheetID;
            String range = updateRows[0].range;
            /*List<String> results = new ArrayList<String>();*/
          /*  List<Object> row1 = new ArrayList<>();
            row1.add("Name");
            row1.add("Rollno");
            row1.add("Class");
            // similarly create more rows with data

            List<List<Object>> vaules = new ArrayList<>();
            vaules.add(row1);*/

          /*  ValueRange valueRange = new ValueRange();
            valueRange.setMajorDimension("ROWS");
            valueRange.setValues(insertRows);*/



            // The A1 notation of the values to update.


            // How the input data should be interpreted.
            String valueInputOption = ""; // TODO: Update placeholder value.

            // TODO: Assign values to desired fields of `requestBody`. All existing
            // fields will be replaced:
            ValueRange requestBody = new ValueRange();

            requestBody.setRange(range).setValues(updateRows[0].updateRowsInfo);
            Sheets.Spreadsheets.Values.Update request =
                    mService.spreadsheets().values().update(spreadsheetId, range, requestBody);
            request.setValueInputOption(valueInputOption);

            UpdateValuesResponse response = request.execute();
            return true;
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Boolean output) {
            mProgress.hide();
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
        }
    }

    private static class InsertDataInfo {
        List<List<Object>> insertRowsInfo;
        String range;

        InsertDataInfo(List<List<Object>> insertRowsInfo, String range) {
            this.insertRowsInfo = insertRowsInfo;
            this.range = range;
        }
    }

    private static class DeleteDataInfo {
        Integer deleteRowsStartIndex;
        Integer deleteRowsEndIndex;
        Integer spreadSheetID;

        DeleteDataInfo(Integer deleteRowsStartIndex, Integer deleteRowsEndIndex, Integer spreadSheetID) {
            this.deleteRowsStartIndex = deleteRowsStartIndex;
            this.deleteRowsEndIndex = deleteRowsEndIndex;
            this.spreadSheetID = spreadSheetID;
        }
    }

    private static class UpdateDataInfo {
        List<List<Object>> updateRowsInfo;
        String range;

        UpdateDataInfo(List<List<Object>> updateRowsInfo, String range) {
            this.updateRowsInfo = updateRowsInfo;
            this.range = range;
        }
    }

    //endregion
}


