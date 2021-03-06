package fragment.companyInfo;

import android.os.Bundle;
import com.zcloud.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import adapter.GenericAdapter;
import com.zcloud.R;
import custom.ExpandableLayoutListView;
import utilities.StoreData;
import model.LegalRepresentative;
import model.User;
import utilities.AdapterConfiguration;
import utilities.CallType;

/**
 * Created by Abanoub on 7/2/2015.
 */
public class LegalRepresentativesFragment extends Fragment {

    private static final String ARG_TEXT = "LegalRepresentativesFragment";
    private SwipyRefreshLayout swipyRefreshLayout;
    private ExpandableLayoutListView lvLegalRepresentatives;
    private int offset = 0;
    private int limit = 10;
    private String soqlQuery;
    private RestRequest restRequest;
    private String lastReponseString = "";
    private ArrayList<LegalRepresentative> legalRepresentatives;
    private int index;
    private int top;

    public static LegalRepresentativesFragment newInstance(String text) {
        LegalRepresentativesFragment fragment = new LegalRepresentativesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.legal_representatives, container, false);
        InitializeViews(view);
        CallLegalRepresentativesService(CallType.FIRSTTIME, offset, limit);
        return view;
    }

    private void InitializeViews(View view) {
        swipyRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        lvLegalRepresentatives = (ExpandableLayoutListView) view.findViewById(R.id.expandableLayoutListView);

        swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
                if (swipyRefreshLayoutDirection == SwipyRefreshLayoutDirection.TOP) {
                    offset = 0;
                    index=0;
                    top=0;
                    getListPosition();
                    CallLegalRepresentativesService(CallType.REFRESH, offset, limit);
                } else {
                    offset += limit;
                    getListPosition();
                    CallLegalRepresentativesService(CallType.LOADMORE, offset, limit);
                }
            }
        });
    }

    private void CallLegalRepresentativesService(final CallType serviceCall, int offset, int limit) {
        if (serviceCall == CallType.FIRSTTIME && !new StoreData(getActivity().getApplicationContext()).getLegalRepresentativesResponse().equals("")) {
            legalRepresentatives = new ArrayList<LegalRepresentative>();
            legalRepresentatives = SFResponseManager.parseLegalRepresentativesObject(new StoreData(getActivity().getApplicationContext()).getLegalRepresentativesResponse());
            if (legalRepresentatives.size() > 0) {
                lvLegalRepresentatives.setAdapter(new GenericAdapter(getActivity(), legalRepresentatives, AdapterConfiguration.LEGAL_REPRESENTATIVES_TAG));
            }
        } else {
            Gson gson = new Gson();
            User _user = gson.fromJson(new StoreData(getActivity().getApplicationContext()).getUserDataAsString(), User.class);
            soqlQuery = SoqlStatements.getInstance().constructLegalRepresentativeQuery(_user.get_contact().get_account().getID(), limit, offset);
            try {
                restRequest = RestRequest.getRequestForQuery(
                        getActivity().getString(R.string.api_version), soqlQuery);
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

                                    if (serviceCall == CallType.LOADMORE || serviceCall == CallType.REFRESH)
                                        swipyRefreshLayout.setRefreshing(false);
                                    if (serviceCall == CallType.LOADMORE) {
                                        ArrayList<LegalRepresentative> legalRepresentatives1 = SFResponseManager.parseLegalRepresentativesObject(response.toString());
                                        if (legalRepresentatives1.size() > 0) {
                                            new StoreData(getActivity().getApplicationContext()).setLegalRepresentativesResponse(response.toString());
                                            for (int i = 0; i < legalRepresentatives1.size(); i++) {
                                                boolean found = false;
                                                for (int j = 0; j < legalRepresentatives.size(); j++) {
                                                    if (legalRepresentatives1.get(i).getID().equals(legalRepresentatives.get(j).getID())) {
                                                        found = true;
                                                        break;
                                                    }
                                                }
                                                if (!found) {
                                                    legalRepresentatives.add(legalRepresentatives1.get(i));
                                                }
                                            }
                                            lvLegalRepresentatives.setAdapter(new GenericAdapter(getActivity(), legalRepresentatives, AdapterConfiguration.LEGAL_REPRESENTATIVES_TAG));
                                            restoreListPosition();
                                        }
                                    } else {
                                        legalRepresentatives = new ArrayList<LegalRepresentative>();
                                        legalRepresentatives = SFResponseManager.parseLegalRepresentativesObject(response.toString());
                                        if (legalRepresentatives.size() > 0) {
                                            new StoreData(getActivity().getApplicationContext()).setLegalRepresentativesResponse(response.toString());
                                            lvLegalRepresentatives.setAdapter(new GenericAdapter(getActivity(), legalRepresentatives, AdapterConfiguration.LEGAL_REPRESENTATIVES_TAG));
                                            restoreListPosition();
                                        }
                                    }
                                }

                                @Override
                                public void onError(Exception exception) {

                                }
                            });
                        }
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public void getListPosition() {
        index = lvLegalRepresentatives.getFirstVisiblePosition();
        View v = lvLegalRepresentatives.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - lvLegalRepresentatives.getPaddingTop());
    }

    public void restoreListPosition() {

        lvLegalRepresentatives.setSelectionFromTop(index, top);
    }
}
