package fragment.ShareHolder.ShareAmount;

import android.os.AsyncTask;
import android.os.Bundle;
import com.zcloud.R;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
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
import java.util.HashMap;
import java.util.Map;

import RestAPI.JSONConstants;
import RestAPI.RelatedServiceType;
import RestAPI.RestMessages;
import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import RestAPI.config.DWCConfiguration;
import com.zcloud.R;
import custom.customdialog.NiftyDialogBuilder;
import fragment.GenericAttachmentPage;
import fragment.BaseFragmentFourStepsNew;
import fragment.GenericPayAndSubmitFragment;
import fragment.GenericThankYouFragment;
import fragmentActivity.ShareHolderActivity;
import utilities.Utilities;

/**
 * Created by M_Ghareeb on 9/8/2015.
 */
public class MainShareHolderFragment extends BaseFragmentFourStepsNew {
    private RestRequest restRequest;
    public String eServiceAdmin = "SELECT ID,No_of_Upload_Docs__c, Name, Display_Name__c, Service_Identifier__c, Amount__c, Total_Amount__c, Related_to_Object__c, New_Edit_VF_Generator__c, Renewal_VF_Generator__c, Replace_VF_Generator__c, Cancel_VF_Generator__c, Record_Type_Picklist__c, (SELECT ID, Name, Type__c, Language__c, Document_Type__c, Authority__c FROM eServices_Document_Checklists__r) FROM Receipt_Template__c WHERE Id='%s'";
    private ShareHolderActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (ShareHolderActivity) getActivity();
        //
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle.setText("Share Transfer");
    }

    @Override
    public Fragment getInitialFragment() {
        tvTitle.setText("Share Transfer");
        return InitialPage.newInstance("");
    }

    @Override
    public Fragment getThirdFragment() {
        tvTitle.setText("Upload Document");
        return GenericAttachmentPage.newInstance("Third");
    }

    @Override
    public Fragment getFourthFragment() {
        tvTitle.setText("Preview");
//        return PayAndSubmit.newInstance("1");
        return GenericPayAndSubmitFragment.newInstance(RelatedServiceType.RelatedServiceTypeShareTransfer, null, null, Utilities.getCurrentDate(), null, String.valueOf(activity.geteServiceAdministration().getTotal_Amount__c()), null, null, null);
    }

    @Override
    public Fragment getFifthFragment(String msg, String fee, String mail) {
        tvTitle.setText("Thank You");
//        return ThankYouFragment.newInstance(msg, fee, mail);
        return GenericThankYouFragment.newInstance(msg, fee, mail);
    }

    @Override
    public RelatedServiceType getRelatedService() {
        return RelatedServiceType.RelatedServiceTypeShareTransfer;
    }

    public static Fragment newInstance(String newShareHolder) {
        MainShareHolderFragment fragment = new MainShareHolderFragment();
        return fragment;
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if (getStatus() == 1) {
                if (required())
                    new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                        @Override
                        public void authenticatedRestClient(final RestClient client) {
                            if (client == null) {
                                getActivity().finish();
                            } else {

                                new AsyncCreateCase(client).execute();


                            }
                        }
                    });

            } else if (getStatus() == 3) {
                if (!isValidAttachments()) {
                    Utilities.showLongToast(activity, "Please fill all attachments");
                } else {
                    super.onClick(v);
                }
            } else if (getStatus() == 4)
                Utilities.showCustomNiftyDialog("Pay Process", getActivity(), listenerOkPay, "Are you sure want to Pay for the service ?");
            else {
                super.onClick(v);
            }
        } else if (v == btnBack || v == btnBackTransparent) {

            if (getStatus() == 3) {
                activity.setInsertedCaseId(null);
                activity.setInsertedServiceId(null);
            } else if (getStatus() == 4) {
                if (activity.getCompanyDocuments() == null || activity.getCompanyDocuments().size() == 0) {
                    setStatus(3);
                    activity.setInsertedCaseId(null);
                    activity.setInsertedServiceId(null);
                    btnNOC3.setBackground(getActivity().getResources().getDrawable(R.drawable.noc_selector));
                    btnNOC3.setSelected(false);
                    btnNOC3.setTextColor(getActivity().getResources().getColor(R.color.white));
                    btnNOC3.setGravity(Gravity.CENTER);
                    btnNOC3.setText("3");
                    btnNext.setText(("Next"));
                    tvTitle.setText("Share Transfer");
                    btnNOC4.setBackground(getActivity().getResources().getDrawable(R.drawable.noc_selector));
                    btnNOC4.setSelected(false);
                    btnNOC4.setTextColor(getActivity().getResources().getColor(R.color.white));
                    btnNOC4.setGravity(Gravity.CENTER);
                    btnNOC4.setText("4");
                }


            }
            super.onClick(v);
        } else {
            super.onClick(v);
        }

    }

    private boolean required() {

        //Check the required Fields with valid data
        if (activity.getSelectedShareHolder() == null) {
            Utilities.showLongToast(activity, "Please change the share Holder");
            return false;
        } else if (activity.getShareno() == 0) {
            Utilities.showLongToast(activity, "There is no transferred shared");
            return false;
        } else return true;
    }


    private boolean isValidAttachments() {

        // Check that every document has attachment
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

    private void PerfromParentNext() {
        super.onClick(btnNext);
    }

    private View.OnClickListener listenerOkPay = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(final RestClient client) {
                    if (client == null) {
                        getActivity().finish();
                    } else {

                        new GetPickLists(client).execute();
//


                    }
                }
            });

        }
    };

    public class GetPickLists extends AsyncTask<String, Void, String> {

        private final RestClient client;
        String result;

        public GetPickLists(RestClient client) {
            this.client = client;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utilities.showloadingDialog(activity);

        }
        // Submitting The Case by Calling #MobileServiceUtilityWebService webservice
        // HTTP POST
        // param caseId -->> the container activity with inserted case id String value
        // param actionType -->> value of "SubmitRequestShareTransfer"


        @Override
        protected String doInBackground(String... params) {
            String attUrl = client.getClientInfo().resolveUrl("/services/apexrest/MobileServiceUtilityWebService").toString();

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(attUrl);
            httppost.setHeader("Authorization", "Bearer " + client.getAuthToken());
            StringEntity entity = null;
            try {
                Map<String, String> map = new HashMap<String, String>();
                map.put("caseId", activity.getInsertedCaseId());
                map.put("actionType", DWCConfiguration.SUBMIT_SHARE_TRANSFER);
                entity = new StringEntity("{\"wrapper\":" + new JSONObject(map).toString() + "}", "UTF-8");
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

    // Creating  Case by Calling #MobileServiceUtilityWebService webservice
    // HTTP POST
    // param actionType -->> value of "CreateRequestShareTransfer"
    // param AccountId
    // param transferFrom
    // param transferTo
    // param noOfShares
    public class AsyncCreateCase extends AsyncTask<Void, Void, String> {
        RestClient client;
        private String result;

        public AsyncCreateCase(RestClient client) {
            this.client = client;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utilities.showloadingDialog(activity);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String attUrl = client.getClientInfo().resolveUrl(DWCConfiguration.MOBILE_SERVICE_UTILITY_URL).toString();

            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(attUrl);
            httppost.setHeader("Authorization", "Bearer " + client.getAuthToken());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("AccountId", activity.getUser().get_contact().get_account().getID());
            map.put("transferFrom", activity.getShareHolder().getID());
            map.put("transferTo", activity.getSelectedShareHolder().get_shareholder().getID());
            map.put("noOfShares", activity.getShareno() + "");

            map.put("actionType", DWCConfiguration.METHOD_NAME_SHARE_TRANSFER);

            StringEntity entity = null;
            entity = new StringEntity("{\"wrapper\":" + new JSONObject(map).toString() + "}", "UTF-8");
            entity.setContentType("application/json");
            httppost.setEntity(entity);
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
                result = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("result", result);

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                String resul = s.replace("\"", "");

                if (resul.equals("Duplication")) {
                    Utilities.dismissLoadingDialog();
                    Utilities.showLongToast(activity, "Duplication");
                } else if (resul.startsWith("Success")) {
                    activity.setInsertedCaseId(resul.replace("Success", ""));


                    new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                        @Override
                        public void authenticatedRestClient(final RestClient client) {
                            if (client == null) {
                                SalesforceSDKManager.getInstance().logout(getActivity());
                                return;
                            } else {

                                try {
                                    restRequest = RestRequest.getRequestForQuery(getString(R.string.api_version), SoqlStatements.getCaseNumberQuery(activity.getInsertedCaseId()));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                                //In success case getting other fields related to the created case
                                client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                                    @Override
                                    public void onSuccess(RestRequest request, RestResponse response) {
                                        JSONObject jsonObject = null;
                                        try {
                                            jsonObject = new JSONObject(response.toString());
                                            JSONArray jsonArray = jsonObject.getJSONArray(JSONConstants.RECORDS);
                                            JSONObject jsonRecord = jsonArray.getJSONObject(0);
                                            Log.d("result", response.toString());
                                            activity.setCaseNumber(jsonRecord.getString("CaseNumber"));
                                            activity.setService_Requested__c(jsonRecord.getString("Service_Requested__c"));
                                            activity.setTotal(jsonRecord.getJSONObject("Invoice__r").getDouble("Amount__c") + "");
                                            createVisaRecord(client);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
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
                } else {
                    Utilities.dismissLoadingDialog();
                    Utilities.showLongToast(activity, "There is an error");
                }
            }

        }
    }
/*
Getting E-Service Administrator
 */

    private void createVisaRecord(RestClient client) {
        String SoqlEServiceQuery = String.format(eServiceAdmin, activity.getService_Requested__c());
        try {
            restRequest = RestRequest.getRequestForQuery(getActivity().getString(R.string.api_version), SoqlEServiceQuery);
            client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {

                @Override
                public void onSuccess(RestRequest request, RestResponse result) {

                    activity.seteServiceAdministration(SFResponseManager.parseReceiptObjectResponse(result.toString()).get(0));
                    Utilities.dismissLoadingDialog();
                    performParentNext();

                }

                @Override
                public void onError(Exception exception) {
                    Utilities.showToast(getActivity(), RestMessages.getInstance().getErrorMessage());
                    Utilities.dismissLoadingDialog();
                    getActivity().finish();
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void performParentNext() {
        super.onClick(btnNext);
    }


}
