package fragment.visasAndCards;

import android.os.Bundle;
import com.zcloud.R;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import RestAPI.RestMessages;
import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import adapter.GenericAdapter;
import adapter.MyRequestSpinnerAdapter;
import com.zcloud.R;
import custom.Images;
import custom.ExpandableLayoutListView;
import utilities.StoreData;
import model.Card_Management__c;
import utilities.ActivitiesLauncher;
import utilities.AdapterConfiguration;
import utilities.CallType;
import utilities.Utilities;

/**
 * Created by Abanoub on 6/24/2015.
 */
public class AccessCardFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_TEXT = "AccessCard";
    static String strFilter;
    LinearLayout LinearAddNewCard;
    ExpandableLayoutListView expandableLayoutListView;
    SwipyRefreshLayout mSwipeRefreshLayout;
    Spinner spinnerFilterAccessCard;
    EditText etSearch;
    String[] Access_Card_visa_validity_status = new String[]{"All", "Draft", "Active", "Expired"};
    RestRequest restRequest;
    GenericAdapter adapter;
    private String soqlQuery;
    private int offset = 0;
    private int limit = 10;
    private ArrayList<Card_Management__c> _cards;
    private ArrayList<Card_Management__c> _Filteredcards;
    ImageView imageNewCard;
    TextView tvNewCard;
    private int index;
    private int top;

    public static AccessCardFragment newInstance(String text) {
        AccessCardFragment fragment = new AccessCardFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.visas_cards_access__cards, container, false);
        offset = 0;
        _cards = new ArrayList<Card_Management__c>();
        _Filteredcards = new ArrayList<Card_Management__c>();
        InitializeViews(view);
        CallAccessCardService(strFilter, CallType.FIRSTTIME);
        return view;
    }

    private void InitializeViews(View view) {

        tvNewCard = (TextView) view.findViewById(R.id.tvNewCard);
        imageNewCard = (ImageView) view.findViewById(R.id.imageNewCard);
        tvNewCard.setOnClickListener(this);
        imageNewCard.setOnClickListener(this);
        expandableLayoutListView = (ExpandableLayoutListView) view.findViewById(R.id.expandableLayoutListView);
//        expandableLayoutListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == _cards.size() - 1) {
//                    expandableLayoutListView.scrollTo(0, expandableLayoutListView.getHeight() - 600);
//
//                }
//            }
//        });
        spinnerFilterAccessCard = (Spinner) view.findViewById(R.id.spinner);
        mSwipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        etSearch = (EditText) view.findViewById(R.id.etSearch);
        etSearch.clearFocus();
        etSearch.setHint(Html.fromHtml("<p><img src='search'>Search</p>", new Images(getActivity()), null));
        LinearAddNewCard = (LinearLayout) view.findViewById(R.id.LinearAddNewCard);
        ArrayAdapter<String> dataAdapter = new MyRequestSpinnerAdapter(getActivity(), R.layout.spinner_item, 0, getActivity().getApplicationContext().getResources().getStringArray(R.array.access_card_filter));

        spinnerFilterAccessCard.setAdapter(dataAdapter);
        strFilter = new StoreData(getActivity().getApplicationContext()).getAccessCardSpinnerFilterValue();
        if (strFilter.equals("")) {
            spinnerFilterAccessCard.setSelection(2, true);
            strFilter = Access_Card_visa_validity_status[2];
            new StoreData(getActivity().getApplicationContext()).setAccessCardSpinnerFilterValue(strFilter);
        } else {
            if (strFilter.equals("All")) {
                spinnerFilterAccessCard.setSelection(0);
            } else if (strFilter.equals("Draft")) {
                spinnerFilterAccessCard.setSelection(1);
            } else if (strFilter.equals("Active")) {
                spinnerFilterAccessCard.setSelection(2);
            } else {
                spinnerFilterAccessCard.setSelection(3);
            }
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    _cards.clear();
                    offset = 0;
                    index = 0;
                    top = 0;
                    getListPosition();
                    if (_Filteredcards != null) {
                        _Filteredcards.clear();
                    }
                    CallAccessCardService(strFilter, CallType.REFRESH);
                } else {
                    offset += limit;
                    getListPosition();
                    CallAccessCardService(strFilter, CallType.LOADMORE);
                }
            }
        });

        spinnerFilterAccessCard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!strFilter.equals(Access_Card_visa_validity_status[position])) {
                    offset = 0;
                    strFilter = Access_Card_visa_validity_status[position];
                    new StoreData(getActivity().getApplicationContext()).setAccessCardSpinnerFilterValue(strFilter);
                    _cards.clear();
                    adapter = new GenericAdapter(getActivity(), _cards, AdapterConfiguration.Access_Card_TAG);
                    expandableLayoutListView.setAdapter(adapter);
                    if (_Filteredcards != null) {
                        _Filteredcards.clear();
                    }
                    index = 0;
                    top = 0;
                    getListPosition();
                    CallAccessCardService(strFilter, CallType.SPINNETCHANGEDDATA);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        LinearAddNewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesLauncher.openNewCardActivity(getActivity().getApplicationContext(), "1");
            }
        });
    }

    public void CallAccessCardService(String visa_validity_status, final CallType callType) {
        if (callType == CallType.FIRSTTIME && !new StoreData(getActivity().getApplicationContext()).getAccessCardResponse().equals("")) {
            ArrayList<Card_Management__c> _Returnedcards = SFResponseManager.parseCardsData(new StoreData(getActivity().getApplicationContext()).getAccessCardResponse());
//            if (_cards.size() == 0) {
            _cards.addAll(_Returnedcards);
//            } else {
//                for (int i = 0; i < _Returnedcards.size(); i++) {
//                    if (_cards.size() > 0) {
//                        boolean isFound = false;
//                        for (int j = 0; j < _cards.size(); j++) {
//                            if (_Returnedcards.get(i).getId().equals(_cards.get(j).getId())) {
//                                isFound = true;
//                                break;
//                            }
//                        }
//                        if (!isFound) {
//                            _cards.add(_Returnedcards.get(i));
//                        }
//                    }
//                }
//            }

            adapter = new GenericAdapter(getActivity(), _cards, AdapterConfiguration.Access_Card_TAG);
            expandableLayoutListView.setAdapter(adapter);

            _Filteredcards.clear();

            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.equals("")) {
                        _Filteredcards.clear();
                        for (int i = 0; i < _cards.size(); i++) {
                            if (_cards.get(i) != null) {
                                if (_cards.get(i).getFull_Name__c().toLowerCase().contains(s.toString().toLowerCase())) {
                                    _Filteredcards.add(_cards.get(i));
                                }
                            }
                        }
                    } else {
                        _Filteredcards.clear();
                        _Filteredcards.addAll(_cards);
                    }
                    adapter = new GenericAdapter(getActivity(), _Filteredcards, AdapterConfiguration.Access_Card_TAG);
                    expandableLayoutListView.setAdapter(adapter);
                    restoreListPosition();
                }
            });

        } else {
            soqlQuery = SoqlStatements.getInstance().constructAccessCardSoqlStatement(new StoreData(getActivity().getApplicationContext()).getUserDataAsString(), visa_validity_status, limit, offset);
            try {
                restRequest = RestRequest.getRequestForQuery(
                        getActivity().getString(R.string.api_version), soqlQuery);
                if (callType == CallType.SPINNETCHANGEDDATA) {
                    if (!Utilities.getIsProgressLoading()) {
                        Utilities.showloadingDialog(getActivity());
                    }
                    _cards = new ArrayList<>();
                    _Filteredcards = new ArrayList<>();
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
                                public void onSuccess(RestRequest request, RestResponse result) {
                                    try {
                                        if (callType == CallType.SPINNETCHANGEDDATA) {
                                            Utilities.dismissLoadingDialog();
                                        } else {
                                            mSwipeRefreshLayout.setRefreshing(false);
                                        }
                                        new StoreData(getActivity().getApplicationContext()).saveAccessCardResponse(result.toString());
                                        ArrayList<Card_Management__c> _Returnedcards = SFResponseManager.parseCardsData(result.toString());
//                                        if (_cards.size() == 0) {
                                        _cards.addAll(_Returnedcards);
//                                        } else {
//                                            for (int i = 0; i < _Returnedcards.size(); i++) {
//                                                if (_cards.size() > 0) {
//                                                    boolean isFound = false;
//                                                    for (int j = 0; j < _cards.size(); j++) {
//                                                        if (_Returnedcards.get(i).getId().equals(_cards.get(j).getId())) {
//                                                            isFound = true;
//                                                            break;
//                                                        }
//                                                    }
//                                                    if (isFound == false) {
//                                                        _cards.add(_Returnedcards.get(i));
//                                                    }
//                                                }
//                                            }
//                                        }

                                        adapter = new GenericAdapter(getActivity(), _cards, AdapterConfiguration.Access_Card_TAG);
                                        expandableLayoutListView.setAdapter(adapter);
                                        restoreListPosition();
                                        _Filteredcards.clear();

                                        etSearch.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {
                                                if (!s.equals("")) {
                                                    _Filteredcards.clear();
                                                    for (int i = 0; i < _cards.size(); i++) {
                                                        if (_cards.get(i) != null) {
                                                            if (_cards.get(i).getFull_Name__c().toString().toLowerCase().contains(s.toString().toLowerCase())) {
                                                                _Filteredcards.add(_cards.get(i));
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    _Filteredcards.clear();
                                                    _Filteredcards.addAll(_cards);
                                                }
                                                adapter = new GenericAdapter(getActivity(), _Filteredcards, AdapterConfiguration.Access_Card_TAG);
                                                expandableLayoutListView.setAdapter(adapter);
                                                restoreListPosition();
                                            }
                                        });
                                    } catch (Exception e) {
                                        onError(e);
                                    }
                                }

                                @Override
                                public void onError(Exception exception) {
                                    Utilities.showToast(getActivity(), RestMessages.getInstance().getErrorMessage());
                                    VolleyError volleyError = (VolleyError) exception;
                                    NetworkResponse response = volleyError.networkResponse;
                                    String json = new String(response.data);
                                    Log.d("hello", json);
                                    Utilities.dismissLoadingDialog();
                                    getActivity().finish();
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

    @Override
    public void onClick(View v) {
        if (v == imageNewCard || v == tvNewCard) {
            ActivitiesLauncher.openNewCardActivity(getActivity().getApplicationContext(), "1");
        }
    }

    public void getListPosition() {
        index = expandableLayoutListView.getFirstVisiblePosition();
        View v = expandableLayoutListView.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - expandableLayoutListView.getPaddingTop());
    }

    public void restoreListPosition() {

        expandableLayoutListView.setSelectionFromTop(index, top);
    }
}
