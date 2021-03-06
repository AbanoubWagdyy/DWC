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
import model.ManagementMember;
import model.User;
import utilities.AdapterConfiguration;
import utilities.CallType;

/**
 * Created by Abanoub on 7/2/2015.
 */
public class GeneralManagersFragment extends Fragment {

    private static final String ARG_TEXT = "GeneralManagersFragment";
    ArrayList<ManagementMember> _members;
    private SwipyRefreshLayout swipyRefreshLayout;
    private ExpandableLayoutListView lvGeneralManagers;
    private int offset = 0;
    private int limit = 10;
    private String soqlQuery;
    private RestRequest restRequest;
    private String lastReponseString;
    private int index;
    private int top;

    public static GeneralManagersFragment newInstance(String text) {
        GeneralManagersFragment fragment = new GeneralManagersFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shareholders, container, false);
        InitializeViews(view);
        CallGeneralManagersService(CallType.FIRSTTIME, offset, limit);
        return view;
    }

    private void InitializeViews(View view) {
        swipyRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        lvGeneralManagers = (ExpandableLayoutListView) view.findViewById(R.id.expandableLayoutListView);
        swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
                if (swipyRefreshLayoutDirection == SwipyRefreshLayoutDirection.TOP) {
                    offset = 0;
                    index=0;
                    top=0;
                    getListPosition();
                    CallGeneralManagersService(CallType.REFRESH, offset, limit);
                } else {
                    offset += limit;
                    getListPosition();
                    CallGeneralManagersService(CallType.LOADMORE, offset, limit);
                }
            }
        });
    }

    private void CallGeneralManagersService(final CallType serviceCall, int offset, int limit) {
        if (serviceCall == CallType.FIRSTTIME && !new StoreData(getActivity().getApplicationContext()).getGeneralManagersResponse().equals("")) {
            _members = new ArrayList<ManagementMember>();
            _members = SFResponseManager.parseManagementMemberObject(new StoreData(getActivity().getApplicationContext()).getGeneralManagersResponse());
            if (_members.size() > 0) {
                lvGeneralManagers.setAdapter(new GenericAdapter(getActivity(),_members, AdapterConfiguration.GENERAL_MANAGERS_TAG));
                restoreListPosition();
            }
        } else {
            Gson gson = new Gson();
            User _user = gson.fromJson(new StoreData(getActivity().getApplicationContext()).getUserDataAsString(), User.class);
            soqlQuery = SoqlStatements.getInstance().constructGeneralManagersQuery(_user.get_contact().get_account().getID(), limit, offset);
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
                                        ArrayList<ManagementMember> managementMembers = SFResponseManager.parseManagementMemberObject(response.toString());
                                        if (managementMembers.size() > 0) {
                                            new StoreData(getActivity().getApplicationContext()).setGeneralManagersResponse(response.toString());
                                            for (int i = 0; i < managementMembers.size(); i++) {
                                                boolean found = false;
                                                for (int j = 0; j < _members.size(); j++) {
                                                    if (managementMembers.get(i).getID().equals(_members.get(j).getID())) {
                                                        found = true;
                                                        break;
                                                    }
                                                }
                                                if (!found) {
                                                    _members.add(managementMembers.get(i));
                                                }
                                            }
                                            lvGeneralManagers.setAdapter(new GenericAdapter(getActivity(),_members, AdapterConfiguration.GENERAL_MANAGERS_TAG));
                                            restoreListPosition();
                                        }
                                    } else {
                                        _members = new ArrayList<ManagementMember>();
                                        _members = SFResponseManager.parseManagementMemberObject(response.toString());
                                        if (_members.size() > 0) {
                                            new StoreData(getActivity().getApplicationContext()).setGeneralManagersResponse(response.toString());
                                            lvGeneralManagers.setAdapter(new GenericAdapter(getActivity(), _members, AdapterConfiguration.GENERAL_MANAGERS_TAG));
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
        index = lvGeneralManagers.getFirstVisiblePosition();
        View v = lvGeneralManagers.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - lvGeneralManagers.getPaddingTop());
    }

    public void restoreListPosition() {

        lvGeneralManagers.setSelectionFromTop(index, top);
    }
}
