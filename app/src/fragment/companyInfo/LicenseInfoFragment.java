package fragment.companyInfo;

import android.app.Activity;
import android.os.Bundle;
import com.zcloud.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import com.zcloud.R;
import utilities.StoreData;
import model.CurrentLicenseNumber;
import model.DWCView;
import model.ItemType;
import model.LicenseActivity;
import model.User;
import utilities.Utilities;

/**
 * Created by Abanoub on 7/2/2015.
 */
public class LicenseInfoFragment extends Fragment {

    private static final String ARG_TEXT = "LicenseInfoFragment";
    static ArrayList<DWCView> _views;
    static RestClient client;
    public static ArrayList<LicenseActivity> _licenses;
    private String title;
    private User _user;
    SwipyRefreshLayout swipyRefreshLayout;

    public static LicenseInfoFragment newInstance(String text) {
        LicenseInfoFragment fragment = new LicenseInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        _views = new ArrayList<DWCView>();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.license_info_inner_fragment, container, false);
        _views.clear();
        title = getActivity().getApplicationContext().getResources().getString(R.string.title_license_info);
        Gson gson = new Gson();
        _user = gson.fromJson(new StoreData(getActivity().getApplicationContext()).getUserDataAsString(), User.class);
        swipyRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
                try {

                    sendRequestFromRefresh(view, getActivity(), SoqlStatements.getInstance().constructLicenseInfoStatement(_user.get_contact().get_account().get_currentLicenseNumber().getId()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            CurrentLicenseNumber licenseNumber = _user.get_contact().get_account().get_currentLicenseNumber();
            sendRequest(view, getActivity(), SoqlStatements.getInstance().constructLicenseInfoStatement(_user.get_contact().get_account().get_currentLicenseNumber().getId()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return view;
    }

    public void sendRequest(final View view, final Activity act, String soql) throws UnsupportedEncodingException {

        if (!new StoreData(getActivity().getApplicationContext()).getLicenseActivityResponse().equals("") && !new StoreData(getActivity().getApplicationContext()).getInvoicesResponse().equals("")) {
            try {
                _licenses = SFResponseManager.parseLicenseActivityObject(new StoreData(getActivity().getApplicationContext()).getLicenseActivityResponse());
                String Invoices = SFResponseManager.parseRenewalRequest(new StoreData(getActivity().getApplicationContext()).getInvoicesResponse());
                InitializeViews(view, _licenses, Invoices);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            final RestRequest restRequest = RestRequest.getRequestForQuery(
                    act.getApplicationContext().getString(R.string.api_version), soql);

            final String[] response = {""};

            new ClientManager(act, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(act, new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(final RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(act);
                        return;
                    } else {
                        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                            @Override
                            public void onSuccess(RestRequest request, RestResponse result) {
                                try {
                                    _licenses = SFResponseManager.parseLicenseActivityObject(result.toString());
                                    new StoreData(getActivity().getApplicationContext()).setLicenseActivityResponse(result.toString());
                                    String soqlRenewal = SoqlStatements.getInstance().constructRenewalLicenseQuery(_user.get_contact().get_account().get_currentLicenseNumber().getId());
                                    final RestRequest restRequestGetRenewalLicense = RestRequest.getRequestForQuery(act.getString(R.string.api_version), soqlRenewal);
                                    client.sendAsync(restRequestGetRenewalLicense, new RestClient.AsyncRequestCallback() {
                                        @Override
                                        public void onSuccess(RestRequest request, RestResponse response) {
                                            try {
                                                String Invoices = SFResponseManager.parseRenewalRequest(response.toString());
                                                new StoreData(getActivity().getApplicationContext()).setInvoicesResponse(response.toString());
                                                InitializeViews(view, _licenses, Invoices);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onError(Exception exception) {

                                        }
                                    });

                                } catch (Exception e) {
                                    onError(e);
                                }
                            }

                            @Override
                            public void onError(Exception exception) {
                                Utilities.dismissLoadingDialog();
                                response[0] = "";
                            }
                        });
                    }
                }
            });
        }
    }

    public void sendRequestFromRefresh(final View view, final Activity act, String soql) throws UnsupportedEncodingException {
        final RestRequest restRequest = RestRequest.getRequestForQuery(
                act.getApplicationContext().getString(R.string.api_version), soql);

        final String[] response = {""};

        new ClientManager(act, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(act, new ClientManager.RestClientCallback() {
            @Override
            public void authenticatedRestClient(final RestClient client) {
                if (client == null) {
                    SalesforceSDKManager.getInstance().logout(act);
                    return;
                } else {
                    client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                        @Override
                        public void onSuccess(RestRequest request, RestResponse result) {
                            try {
                                _licenses = SFResponseManager.parseLicenseActivityObject(result.toString());
                                new StoreData(getActivity().getApplicationContext()).setLicenseActivityResponse(result.toString());
                                String soqlRenewal = SoqlStatements.getInstance().constructRenewalLicenseQuery(_user.get_contact().get_account().get_currentLicenseNumber().getId());
                                final RestRequest restRequestGetRenewalLicense = RestRequest.getRequestForQuery(act.getString(R.string.api_version), soqlRenewal);
                                client.sendAsync(restRequestGetRenewalLicense, new RestClient.AsyncRequestCallback() {
                                    @Override
                                    public void onSuccess(RestRequest request, RestResponse response) {
                                        try {
                                            String Invoices = SFResponseManager.parseRenewalRequest(response.toString());
                                            swipyRefreshLayout.setRefreshing(false);
                                            new StoreData(getActivity().getApplicationContext()).setInvoicesResponse(response.toString());
                                            _views.clear();
                                            InitializeViews(view, _licenses, Invoices);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(Exception exception) {

                                    }
                                });

                            } catch (Exception e) {
                                onError(e);
                            }
                        }

                        @Override
                        public void onError(Exception exception) {
                            Utilities.dismissLoadingDialog();
                            response[0] = "";
                        }
                    });
                }
            }
        });
    }

    private void InitializeViews(View view, ArrayList<LicenseActivity> _licenses, String invoices) {

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.content_linear);
        _views.add(new DWCView("License Information", ItemType.HEADER));
        _views.add(new DWCView("Commercial Name", ItemType.LABEL));
        _views.add(new DWCView(_user.get_contact().get_account().get_currentLicenseNumber().getCommercial_Name(), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("License Number", ItemType.LABEL));
        _views.add(new DWCView(_user.get_contact().get_account().get_currentLicenseNumber().getLicense_Number_Value(), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("Issue Date", ItemType.LABEL));
        _views.add(new DWCView(Utilities.formatVisitVisaDate(_user.get_contact().get_account().get_currentLicenseNumber().getLicense_Issue_Date()), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("Expiry Date", ItemType.LABEL));
        _views.add(new DWCView(Utilities.formatVisitVisaDate(_user.get_contact().get_account().get_currentLicenseNumber().getLicense_Expiry_Date()), ItemType.VALUE));

        _views.add(new DWCView("Activity Information", ItemType.HEADER));
        if (_licenses != null && _licenses.size() > 0) {
            for (int i = 0; i < _licenses.size(); i++) {
                _views.add(new DWCView(_licenses.get(i).get_originalBusinessActivity().getName(), ItemType.LABEL));
                _views.add(new DWCView(_licenses.get(i).get_originalBusinessActivity().getBusinessActivityName(), ItemType.VALUE));
                _views.add(new DWCView("", ItemType.LINE));
            }
        }

        String services = "";

        if (Utilities.daysDifference(_user.get_contact().get_account().get_currentLicenseNumber().getLicense_Expiry_Date()) < 60) {
            services += "Renew License,";
        }

        services += "Cancel License,";

        if (Utilities.daysDifference(_user.get_contact().get_account().get_currentLicenseNumber().getLicense_Expiry_Date()) < 60) {
            services += "Renew License Activity,";
        }

        services += "Change License Activity,";
        services = services.substring(0, services.length() - 1);

        _views.add(new DWCView(services, ItemType.HORIZONTAL_LIST_VIEW));

        View viewItems = Utilities.drawViewsOnLayout(getActivity(), _user, getActivity().getApplicationContext(), _views);
        linearLayout.removeAllViews();
        linearLayout.addView(viewItems);
    }
}
