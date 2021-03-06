package fragment.License.Change;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import RestAPI.JSONConstants;
import com.zcloud.R;
import custom.Images;
import model.OriginalBusinessActivity;

/**
 * Created by M_Ghareeb on 9/14/2015.
 */
public class ActivitiesActivity extends Activity {
    View view;
    RestRequest restRequest = null;
    EditText search;
    ListView activities;
    String searchValue = "";
    String SQL = "select id , name, License_Type__c , Business_Activity_Name__c from Business_activity__c where id in (select Original_Business_Activity__c from License_Activity__c where end_date__c=null) LIMIT %s OFFSET %s";
    ArrayList<OriginalBusinessActivity> oActivities, filtered;
    int offset = 0, limit = 10;
    SwipyRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        oActivities = new ArrayList<OriginalBusinessActivity>();
        filtered = new ArrayList<OriginalBusinessActivity>();
        mSwipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        activities = (ListView) findViewById(R.id.activities);
        activities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Return the selected Activity
                Intent intent = new Intent();
                intent.putExtra("data", filtered.get(i));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        search = (EditText) findViewById(R.id.search);
        search.setHint(Html.fromHtml("<p><img src='search'>Search</p>", new Images(this), null));
        search.clearFocus();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v.getText().toString());
                    return true;
                }
                return false;
            }
        });


        view = findViewById(R.id.view);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                if (direction == SwipyRefreshLayoutDirection.BOTTOM)
                    CallActivitties(limit, offset);
                else mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        CallActivitties(limit, offset);
    }

    private void performSearch(String s) {
        //Getting the search result from the server related to typed text s

        filtered = new ArrayList<OriginalBusinessActivity>();

        if (!TextUtils.isEmpty(s)) {
            searchValue = s;
            String SQLSearch = "select id , name, License_Type__c , Business_Activity_Name__c from Business_activity__c where where id in (select Original_Business_Activity__c from License_Activity__c where end_date__c=null) and ( name like'%" + searchValue + "%' or Business_Activity_Name__c like '%" + searchValue + "%')";


            try {
                restRequest = RestRequest.getRequestForQuery(
                        getString(R.string.api_version), SQLSearch);
                mSwipeRefreshLayout.setRefreshing(true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            new ClientManager(ActivitiesActivity.this, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(ActivitiesActivity.this, new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(final RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(ActivitiesActivity.this);
                        return;
                    } else {
                        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                            @Override
                            public void onSuccess(RestRequest request, RestResponse result) {
                                JSONObject jsonLicenseActivities = null;
                                try {
                                    jsonLicenseActivities = new JSONObject(result.toString());
                                    if (jsonLicenseActivities.optBoolean(JSONConstants.DONE) == true) {

                                        JSONArray jRecords = jsonLicenseActivities.getJSONArray(JSONConstants.RECORDS);
                                        for (int i = 0; i < jRecords.length(); i++) {
                                            Gson gson = new Gson();

                                            OriginalBusinessActivity _originalBusinessActivity = gson.fromJson(jRecords.getJSONObject(i).toString(), OriginalBusinessActivity.class);
                                            filtered.add(_originalBusinessActivity);

                                        }
                                    }


                                    search.setText("");
                                    activities.setAdapter(new MyAdapter(ActivitiesActivity.this, R.layout.licence_activity, 0, filtered));

                                    mSwipeRefreshLayout.setRefreshing(false);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(Exception exception) {

                            }
                        });
                    }
                }
            });


        } else {
            filtered.addAll(oActivities);
            activities.setAdapter(new MyAdapter(ActivitiesActivity.this, R.layout.licence_activity, 0, filtered));

        }
    }


    private void CallActivitties(int limit, int offset) {
//Calling Activities From the server
        try {
            restRequest = RestRequest.getRequestForQuery(
                    getString(R.string.api_version), String.format(SQL, limit, offset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final String[] response = {""};

        new ClientManager(this, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(ActivitiesActivity.this, new ClientManager.RestClientCallback() {
            @Override
            public void authenticatedRestClient(final RestClient client) {
                if (client == null) {
                    SalesforceSDKManager.getInstance().logout(ActivitiesActivity.this);
                    return;
                } else {
                    client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                        @Override
                        public void onSuccess(RestRequest request, RestResponse result) {
                            JSONObject jsonLicenseActivities = null;
                            try {
                                jsonLicenseActivities = new JSONObject(result.toString());
                                if (jsonLicenseActivities.optBoolean(JSONConstants.DONE) == true) {

                                    JSONArray jRecords = jsonLicenseActivities.getJSONArray(JSONConstants.RECORDS);
                                    for (int i = 0; i < jRecords.length(); i++) {
                                        Gson gson = new Gson();

                                        OriginalBusinessActivity _originalBusinessActivity = gson.fromJson(jRecords.getJSONObject(i).toString(), OriginalBusinessActivity.class);
                                        oActivities.add(_originalBusinessActivity);

                                    }
                                }
                                filtered = new ArrayList<OriginalBusinessActivity>();
                                filtered.addAll(oActivities);
                                search.setText("");
                                activities.setAdapter(new MyAdapter(ActivitiesActivity.this, R.layout.licence_activity, 0, filtered));
                                ActivitiesActivity.this.offset += ActivitiesActivity.this.limit;
                                mSwipeRefreshLayout.setRefreshing(false);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(Exception exception) {

                        }
                    });
                }
            }
        });
    }

    public class MyAdapter extends ArrayAdapter<OriginalBusinessActivity> {
//        Ui Adapter

        ArrayList<OriginalBusinessActivity> objects;
        Context context;

        public MyAdapter(Context context, int resource, int textViewResourceId, ArrayList<OriginalBusinessActivity> objects) {
            super(context, resource, textViewResourceId, objects);
            this.objects = objects;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewHeader = li.inflate(R.layout.licence_activity, parent, false);
            TextView tvLabelItem = (TextView) viewHeader.findViewById(R.id.tvLabelItem);
            TextView tvValue = (TextView) viewHeader.findViewById(R.id.tvValueItem);
            tvLabelItem.setText(objects.get(position).getName());
            tvValue.setText(objects.get(position).getBusinessActivityName());
            return viewHeader;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    public void finish(View v) {
        onBackPressed();
    }
}
