package fragment.Cards.NewCard;

import android.os.AsyncTask;
import android.os.Bundle;

import com.zcloud.R;

import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import RestAPI.JSONConstants;
import RestAPI.RelatedServiceType;
import RestAPI.SoqlStatements;
import RestAPI.config.DWCConfiguration;

import com.zcloud.R;

import custom.customdialog.NiftyDialogBuilder;
import fragment.GenericAttachmentPage;
import fragment.BaseFragmentFiveSteps;
import fragment.GenericPayAndSubmitFragment;
import fragment.GenericThankYouFragment;
import fragmentActivity.CardActivity;
import model.Card_Management__c;
import model.FormField;
import utilities.Utilities;

/**
 * Created by M-Ghareeb on 8/25/2015.
 */
public class MainNewCardFragment extends BaseFragmentFiveSteps {

    CardActivity activity;
    NiftyDialogBuilder builder;
    private RestRequest restRequest;
    private View.OnClickListener listenerOkPay = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            builder.dismiss();
            new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(final RestClient client) {
                    if (client == null) {
                        getActivity().finish();
                    } else {
                        new GetPickLists(client).execute();
                    }
                }
            });
        }
    };

    public static Fragment newInstance(String newCard) {
        MainNewCardFragment fragment = new MainNewCardFragment();
        Bundle bundle = new Bundle();
        bundle.putString("text", newCard);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (CardActivity) getActivity();
        tvTitle.setText("New Card");
    }

    @Override
    public Fragment getInitialFragment() {
        tvTitle.setText("New Card");
        return NewCardInitialPage.newInstance("NewCardInitial");
    }

    @Override
    public Fragment getSecondFragment() {
        tvTitle.setText("Details");
        return NewCardFormFieldPage.newInstance("");
    }

    @Override
    public Fragment getThirdFragment() {
        tvTitle.setText("Upload Document");
        return GenericAttachmentPage.newInstance("");
    }

    @Override
    public Fragment getFourthFragment() {
        tvTitle.setText("Preview");
//        return PayAndSubmit.newInstance(activity.getType());

        //1,3
        if (activity.getType().equals("1")) {
            return GenericPayAndSubmitFragment.newInstance(RelatedServiceType.RelatedServiceTypeNewCard, ((CardActivity) getActivity()).getCard().getFull_Name__c(), ((CardActivity) getActivity()).getCaseNumber(), Utilities.getCurrentDate(), null, String.valueOf(((CardActivity) getActivity()).geteServiceAdministration().getTotal_Amount__c()), ((CardActivity) getActivity()).get_webForm().get_formFields(), ((CardActivity) getActivity()).getCard(), null);
        } else {
            return GenericPayAndSubmitFragment.newInstance(RelatedServiceType.RelatedServiceTypeRenewCard, ((CardActivity) getActivity()).getCard().getFull_Name__c(), ((CardActivity) getActivity()).getCaseNumber(), Utilities.getCurrentDate(), null, String.valueOf(((CardActivity) getActivity()).geteServiceAdministration().getTotal_Amount__c()), ((CardActivity) getActivity()).get_webForm().get_formFields(), ((CardActivity) getActivity()).getCard(), null);
        }

    }

    @Override
    public Fragment getFifthFragment(String msg, String fee, String mail) {
        tvTitle.setText("Thank You");
//        return ThankYouFragment.newInstance(msg, String.format(activity.getResources().getString(R.string.ServiceThankYouMessageCard), activity.getTotal()), mail);
        return GenericThankYouFragment.newInstance(activity.getCaseNumber(), activity.getTotal(), mail);
    }

    @Override
    public RelatedServiceType getRelatedService() {
        return RelatedServiceType.RelatedServiceTypeNewCard;
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if (getStatus() == 1) {
                if (activity.getCardType() == null || activity.getCardType().equals("")) {
                    Utilities.showToast(getActivity(), "Please choose valid card type");
                } else if (activity.getDuration() == null || activity.getDuration().equals("")) {
                    Utilities.showToast(getActivity(), "Please choose valid duration type");
                } else {
                    Map<String, String> parameters = activity.getParameters();
                    parameters.put("actName", Utilities.stringNotNull(activity.getUser().get_contact().get_account().getName()));
                    parameters.put("accountID", Utilities.stringNotNull(activity.getUser().get_contact().get_account().getID()));
                    parameters.put("Dur", activity.getDuration());
                    activity.setParameters(parameters);
                    tvTitle.setText("Details");
                    super.onClick(v);
                }
            } else if (getStatus() == 2) {
                if (required())
                    createCaseRecord();
                else {
                    Utilities.showLongToast(getActivity(), "Please fill required fields");
                }
            } else if (getStatus() == 3) {
                if (!isValidAttachments()) {
                    Utilities.showLongToast(activity, "Please fill all attachments");
                } else {
                    super.onClick(v);
                }
            } else if (getStatus() == 4)
                builder = Utilities.showCustomNiftyDialog("Pay Process", getActivity(), listenerOkPay, "Are you sure want to Pay for the service ?");
            else {
                super.onClick(v);
            }
        } else if (v == btnBack || v == btnBackTransparent) {

            if (getStatus() == 3) {
                tvTitle.setText("Details");
            } else if (getStatus() == 4) {
                if (activity.getCompanyDocuments() == null || activity.getCompanyDocuments().size() == 0) {
                    setStatus(3);

                    btnNOC3.setBackground(getActivity().getResources().getDrawable(R.drawable.noc_selector));
                    btnNOC3.setSelected(false);
                    btnNOC3.setTextColor(getActivity().getResources().getColor(R.color.white));
                    btnNOC3.setGravity(Gravity.CENTER);
                    btnNOC3.setText("3");
                    btnNext.setText(("Next"));
                    tvTitle.setText("New Card");
                    btnNOC4.setBackground(getActivity().getResources().getDrawable(R.drawable.noc_selector));
                    btnNOC4.setSelected(false);
                    btnNOC4.setTextColor(getActivity().getResources().getColor(R.color.white));
                    btnNOC4.setGravity(Gravity.CENTER);
                    btnNOC4.setText("4");
                }
            } else if (getStatus() == 2) {
                tvTitle.setText("New Card");
            }
            super.onClick(v);
        } else {
            super.onClick(v);
        }

    }

    private boolean isValidAttachments() {

        // Check if the documents have attachments or not to validate next step
        if (activity.getCompanyDocuments() != null && activity.getCompanyDocuments().size() > 0) {
            for (int i = 0; i < activity.getCompanyDocuments().size(); i++) {
                if (activity.getCompanyDocuments().get(i).getAttachment_Id__c() == null || activity.getCompanyDocuments().get(i).getAttachment_Id__c().equals("")) {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }

    private boolean required() {

        // Check Validation of the Dynamic Form Fields
        boolean result = true;
        for (FormField field : activity.get_webForm().get_formFields()) {
            if (field.isRequired() && !field.isHidden()) {
                String name = field.getName();
                String stringValue = "";
                Field[] fields = Card_Management__c.class.getFields();
                for (int j = 0; j < fields.length; j++)
                    if (name.toLowerCase().equals(fields[j].getName().toLowerCase()))
                        try {
                            stringValue = (String) fields[j].get(activity.getCard());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                            if (field.getType().equals("DOUBLE")) {
                                try {
                                    if (fields[j].getDouble(activity.getCard()) == 0.0) {
                                        result = false;
                                        return false;
                                    } else {
                                        stringValue = String.valueOf(fields[j].getDouble(activity.getCard()));
                                    }
                                } catch (IllegalAccessException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }

                if (stringValue == null) {
                    result = false;
                    return false;
                } else if (stringValue.equals("")) {
                    result = false;
                    return false;
                }
            }
        }
        return result;
    }

    public void createCaseRecord() {

        //Initiating Case Record or update it if already created
        activity = (CardActivity) getActivity();
        if (activity.geteServiceAdministration() != null) {
            Map<String, Object> caseFields = activity.getCaseFields();
            caseFields.put("Service_Requested__c", activity.geteServiceAdministration().getID());
            caseFields.put("AccountId", activity.getUser().get_contact().get_account().getID());
            caseFields.put("RecordTypeId", activity.getCaseRecordTypeId());
            caseFields.put("Status", "Draft");
            caseFields.put("Type", "Access Card Services");
            caseFields.put("Origin", "Mobile");
            activity.setCaseFields(caseFields);
        }
        if (activity.get_webForm() != null) {
            Map<String, Object> caseFields = activity.getCaseFields();
            caseFields.put("Visual_Force_Generator__c", activity.get_webForm().getID());
            activity.setCaseFields(caseFields);
        }

        if (activity.getInsertedCaseId() != null && !activity.getInsertedCaseId().equals("")) {
            restRequest = RestRequest.getRequestForUpdate(getActivity().getString(R.string.api_version), "Case", activity.getInsertedCaseId(), activity.getCaseFields());
        } else {
            restRequest = RestRequest.getRequestForCreate(getActivity().getString(R.string.api_version), "Case", activity.getCaseFields());
        }

        Utilities.showloadingDialog(getActivity());
        new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
            @Override
            public void authenticatedRestClient(final RestClient client) {
                if (client == null) {
                    SalesforceSDKManager.getInstance().logout(getActivity());
                    return;
                } else {
                    client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                        @Override
                        public void onSuccess(RestRequest request, RestResponse response) {
                            try {
                                Log.d("result", response.toString());
                                JSONObject jsonObject = new JSONObject(response.toString());
                                activity.setInsertedCaseId(jsonObject.getString("id"));

                                try {
                                    //Getting Case Number and other fields related to created or updated case
                                    restRequest = RestRequest.getRequestForQuery(getString(R.string.api_version), SoqlStatements.getCaseNumberQuery(activity.getInsertedCaseId()));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                                    @Override
                                    public void onSuccess(RestRequest request, RestResponse response) {
                                        JSONObject jsonObject = null;
                                        try {
                                            jsonObject = new JSONObject(response.toString());
                                            JSONArray jsonArray = jsonObject.getJSONArray(JSONConstants.RECORDS);
                                            JSONObject jsonRecord = jsonArray.getJSONObject(0);
                                            Log.d("resultcase", response.toString());
                                            activity.setCaseNumber(jsonRecord.getString("CaseNumber"));
                                            activity.setTotal(activity.geteServiceAdministration().getTotal_Amount__c() + "");
                                            createCardRecord();
                                        } catch (JSONException e) {
                                            e.printStackTrace();

                                        }

                                    }

                                    @Override
                                    public void onError(Exception exception) {

                                    }
                                });
                                Log.d("", response.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (response.toString().equals("")) {
                                    createCardRecord();
                                }
                            }
                        }

                        @Override
                        public void onError(Exception exception) {
                            VolleyError volleyError = (VolleyError) exception;
                            NetworkResponse response = volleyError.networkResponse;
                            String json = new String(response.data);
                            Log.d("", json);
                            Utilities.dismissLoadingDialog();
                            getActivity().finish();
                        }
                    });


                }
            }
        });

    }

    public void createCardRecord() {
// Create Service (Card) Record
        Map<String, Object> serviceFields = activity.getServiceFields();
        serviceFields = new HashMap<String, Object>();
        serviceFields.put("RecordTypeId", activity.getCardRecordTypeId());
        serviceFields.put("Request__c", activity.getInsertedCaseId());
        serviceFields.put("Card_Type__c", activity.getCardType().replace("_", " "));

        /* Load dynamic fetching */

        for (FormField field : activity.get_webForm().get_formFields()) {
            if (field.getType().equals("CUSTOMTEXT") || field.isCalculated()) {


            } else {


                String stringValue = "";
                String name = field.getName();
                Field[] fields = Card_Management__c.class.getFields();
                for (int j = 0; j < fields.length; j++)
                    if (name.toLowerCase().equals(fields[j].getName().toLowerCase()))
                        try {
                            stringValue = String.valueOf(fields[j].get(activity.getCard()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                serviceFields.put(name, (stringValue.equals("true") || stringValue.equals("false") ? Boolean.valueOf(stringValue) : stringValue));
            }
        }


        /* End of dynamic fetching*/
        activity.setServiceFields(serviceFields);
        if (activity.getInsertedServiceId() != null && !activity.getInsertedServiceId().equals("")) {

            restRequest = RestRequest.getRequestForUpdate(getActivity().getString(R.string.api_version), activity.get_webForm().getObject_Name(), activity.getInsertedServiceId(), serviceFields);

        } else {
            restRequest = RestRequest.getRequestForCreate(getActivity().getString(R.string.api_version), activity.get_webForm().getObject_Name(), serviceFields);
        }

        new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
            @Override
            public void authenticatedRestClient(RestClient client) {
                if (client == null) {
                    SalesforceSDKManager.getInstance().logout(getActivity());
                    return;
                } else {
                    client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                        @Override
                        public void onSuccess(RestRequest request, RestResponse response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                activity.setInsertedServiceId(jsonObject.getString("id"));
                                updateCaseRecord(activity.getInsertedCaseId(), activity.getInsertedServiceId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (response.toString().equals(""))
                                    PerfromParentNext();
                            }
                            Utilities.dismissLoadingDialog();
                        }

                        @Override
                        public void onError(Exception exception) {
                            VolleyError volleyError = (VolleyError) exception;
                            NetworkResponse response = volleyError.networkResponse;
                            String json = new String(response.data);
                            Log.d("", json);
                            Utilities.dismissLoadingDialog();
                            getActivity().finish();
                        }
                    });
                }
            }
        });
    }

    private void updateCaseRecord(String insertedCaseId, String insertedServiceId) {
// Linking Service with Case in Server
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(activity.get_webForm().getObject_Name(), insertedServiceId);

        restRequest = RestRequest.getRequestForUpdate(getString(R.string.api_version), "Case", insertedCaseId, fields);
        new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
            @Override
            public void authenticatedRestClient(RestClient client) {
                if (client == null) {
                    SalesforceSDKManager.getInstance().logout(getActivity());
                    return;
                } else {
                    client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                        @Override
                        public void onSuccess(RestRequest request, RestResponse response) {
                            Utilities.dismissLoadingDialog();
                            Log.d("", response.toString());
                            PerfromParentNext();
                        }

                        @Override
                        public void onError(Exception exception) {
                            VolleyError volleyError = (VolleyError) exception;
                            NetworkResponse response = volleyError.networkResponse;
                            String json = new String(response.data);
                            Log.d("", json);
                            Utilities.dismissLoadingDialog();
                            getActivity().finish();
                        }
                    });
                }
            }
        });
    }

    private void PerfromParentNext() {
        super.onClick(btnNext);
    }

    public class GetPickLists extends AsyncTask<String, Void, String> {

        private final RestClient client;
        String result;

        public GetPickLists(RestClient client) {
            this.client = client;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utilities.showloadingDialog(getActivity());
        }
// Submitting The Case by Calling #MobilePayAndSubmitWebService webservice
// HTTP POST
// param caseId -->> the container activity with inserted case id String value


        @Override
        protected String doInBackground(String... params) {
            String attUrl = client.getClientInfo().resolveUrl(DWCConfiguration.PAY_AND_SUBMIT_WEB_SERVICE).toString();

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(attUrl);
            httppost.setHeader("Authorization", "Bearer " + client.getAuthToken());
            StringEntity entity = null;
            try {
                Map<String, String> map = new HashMap<String, String>();
                map.put("caseId", activity.getInsertedCaseId());
                entity = new StringEntity(new JSONObject(map).toString(), "UTF-8");
                entity.setContentType("application/json");
                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                result = EntityUtils.toString(response.getEntity());
                Log.d("result", result);
                return result.toLowerCase().contains("success") ? "success" : null;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            Utilities.dismissLoadingDialog();
            if (aVoid.equals("success")) {
                tvTitle.setText("Thank You");
                NiftyDialogBuilder
                        .getInstance(getActivity()).dismiss();
                getfifthfragment("", activity.getCaseNumber());
            }

        }
    }
}
