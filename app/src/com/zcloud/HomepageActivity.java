//package com.zcloud;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.google.gson.Gson;
//import com.salesforce.androidsdk.app.SalesforceSDKManager;
//import com.salesforce.androidsdk.rest.ClientManager;
//import com.salesforce.androidsdk.rest.RestClient;
//import com.salesforce.androidsdk.rest.RestRequest;
//import com.salesforce.androidsdk.rest.RestResponse;
//import com.salesforce.androidsdk.ui.sfnative.SalesforceActivity;
//
//import org.json.JSONObject;
//
//import java.io.UnsupportedEncodingException;
//
//import RestAPI.JSONConstants;
//import RestAPI.RestMessages;
//import RestAPI.SFResponseManager;
//import RestAPI.SoqlStatements;
//import custom.BadgeButton;
//import utilities.StoreData;
//import utilities.ExceptionHandler;
//import model.User;
//import utilities.ActivitiesLauncher;
//import utilities.Utilities;
//
//public class HomepageActivity extends SalesforceActivity implements
//        OnClickListener {
//
//    BadgeButton _badgeButton;
//    Button btnLogout, btnLogoutTrasparent;
//    RelativeLayout relativeDashboard, relativeMyRequest, relativeNotifications,
//            relativeVisasAndCards, relativeCompanyInfo, relativeReports,
//            relativeNeedHelp, relativeQuickAccess, relativeCompanyDocuments, relativeViewStatement;
//
//    Button btnViewStatement;
//
//    TextView tvCompanyName, tvLicenseNumber, tvLicenseExpiry, tvBalance;
//    ImageView imageDashboard, imageMyRequests, imageVisasAndCards, imageCompanyInfo, imageCompanyDocuments, imageQuickAccess, imageNeedHelp, imageReports, smartCompanyImage;
//
//    private OnClickListener listenerOk1 = new OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            SalesforceSDKManager.getInstance().logout(HomepageActivity.this);
//            new StoreData(getApplicationContext()).reset();
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
//        setContentView(R.layout.homepage);
//        Utilities.showloadingDialog(this);
//        initializeViews();
//    }
//
//    private void initializeViews() {
//
//        _badgeButton = (BadgeButton) findViewById(R.id.btnBadgeCount);
//        _badgeButton.setBadgeDrawable(getResources().getDrawable(
//                R.drawable.notification_image3));
//
//        btnLogout = (Button) findViewById(R.id.btnLogout);
//
//        btnLogoutTrasparent = (Button) findViewById(R.id.btnLogoutTransparent);
//        relativeDashboard = (RelativeLayout) findViewById(R.id.relativeDashboard);
//        relativeMyRequest = (RelativeLayout) findViewById(R.id.relativeMyRequests);
//        relativeNotifications = (RelativeLayout) findViewById(R.id.relativeNotifications);
//        relativeVisasAndCards = (RelativeLayout) findViewById(R.id.relativeVisasAndCards);
//        relativeCompanyInfo = (RelativeLayout) findViewById(R.id.relativeCompanyInfo);
//        relativeReports = (RelativeLayout) findViewById(R.id.relativeReports);
//        relativeNeedHelp = (RelativeLayout) findViewById(R.id.relativeNeedHelp);
//        relativeQuickAccess = (RelativeLayout) findViewById(R.id.relativeQuickAccess);
//        relativeCompanyDocuments = (RelativeLayout) findViewById(R.id.relativeCompanyDocuments);
//        relativeViewStatement = (RelativeLayout) findViewById(R.id.relativeViewStatement);
//
//        btnViewStatement = (Button) findViewById(R.id.btnViewStatement);
//
//        imageCompanyDocuments = (ImageView) findViewById(R.id.imageCompanyDocuments);
//        imageDashboard = (ImageView) findViewById(R.id.imageDashboard);
//        imageMyRequests = (ImageView) findViewById(R.id.imageMyRequests);
//        imageVisasAndCards = (ImageView) findViewById(R.id.imageVisasAndCards);
//        imageCompanyInfo = (ImageView) findViewById(R.id.imageCompanyInfo);
//        imageQuickAccess = (ImageView) findViewById(R.id.imageQuickAccess);
//        imageNeedHelp = (ImageView) findViewById(R.id.imageNeedHelp);
//        imageReports = (ImageView) findViewById(R.id.imageReports);
//
//        relativeDashboard.setOnClickListener(this);
//        relativeMyRequest.setOnClickListener(this);
//        relativeNotifications.setOnClickListener(this);
//        relativeVisasAndCards.setOnClickListener(this);
//        relativeCompanyInfo.setOnClickListener(this);
//        relativeReports.setOnClickListener(this);
//        relativeNeedHelp.setOnClickListener(this);
//        relativeQuickAccess.setOnClickListener(this);
//        relativeCompanyDocuments.setOnClickListener(this);
//        relativeViewStatement.setOnClickListener(this);
//        btnViewStatement.setOnClickListener(this);
//        imageCompanyDocuments.setOnClickListener(this);
//        imageDashboard.setOnClickListener(this);
//        imageMyRequests.setOnClickListener(this);
//        imageVisasAndCards.setOnClickListener(this);
//        imageCompanyInfo.setOnClickListener(this);
//        imageQuickAccess.setOnClickListener(this);
//        imageNeedHelp.setOnClickListener(this);
//        imageReports.setOnClickListener(this);
//
//        tvCompanyName = (TextView) findViewById(R.id.tvCompanyName);
//        tvLicenseExpiry = (TextView) findViewById(R.id.tvLicenseExpiry);
//        tvLicenseNumber = (TextView) findViewById(R.id.tvLicenseNumber);
//        smartCompanyImage = (ImageView) findViewById(R.id.companyImage);
//        tvBalance = (TextView) findViewById(R.id.tvBalance);
//        new ClientManager(this, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(this, new ClientManager.RestClientCallback() {
//            @Override
//            public void authenticatedRestClient(RestClient client) {
//                if (client == null) {
//                    SalesforceSDKManager.getInstance().logout(HomepageActivity.this);
//                    return;
//                } else {
//                    try {
//                        new StoreData(getApplicationContext()).saveUserID(SalesforceSDKManager.getInstance().getUserAccountManager().getCurrentUser().getUserId());
//                        sendRequestCompanyInfo(HomepageActivity.this, client);
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
//
////    @Override
////    public void onResume() {
//////        findViewById(R.id.root).setVisibility(View.INVISIBLE);
////        String str =
////        if (!new StoreData(getApplicationContext()).getUserDataAsString().equals("")) {
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
////                    new ClientManager(HomepageActivity.this, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(HomepageActivity.this, new ClientManager.RestClientCallback() {
////                        @Override
////                        public void authenticatedRestClient(RestClient client) {
////                            if (client == null) {
////                                SalesforceSDKManager.getInstance().logout(HomepageActivity.this);
////                                return;
////                            } else {
////                                Gson gson = new Gson();
////                                User user = gson.fromJson(new StoreData(getApplicationContext()).getUserDataAsString(), User.class);
////                                sendNotificationCountRequestInAnotherThread(user, client);
////                            }
////                        }
////                    });
////
////                }
////            }).start();
////        }
////        super.onResume();
////    }
//
//    @Override
//    public void onResume(RestClient client) {
//        RestClient.ClientInfo _userInfo = client.getClientInfo();
//        new StoreData(getApplicationContext()).saveUserID(_userInfo.userId);
//        new ClientManager(this, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(this, new ClientManager.RestClientCallback() {
//            @Override
//            public void authenticatedRestClient(RestClient client) {
//                if (client == null) {
//                    SalesforceSDKManager.getInstance().logout(HomepageActivity.this);
//                    return;
//                } else {
//                    try {
//                        sendRequestCompanyInfo(HomepageActivity.this, client);
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
//
//    public void onLogoutClick(View v) {
//        Utilities.showNiftyDialog("Logout", this, listenerOk1);
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (v == relativeDashboard || v == imageDashboard) {
//
//            ActivitiesLauncher.openDashboardActivity(getApplicationContext());
//            imageDashboard.setSelected(true);
//
//        } else if (v == relativeMyRequest || v == imageMyRequests) {
//
//            ActivitiesLauncher.openMyRequestsActivity(getApplicationContext());
//            imageMyRequests.setSelected(true);
//
//        } else if (v == relativeNotifications || v == _badgeButton) {
//
//            ActivitiesLauncher.openNotificationsActivity(getApplicationContext());
//            _badgeButton.setSelected(true);
//
//        } else if (v == relativeVisasAndCards || v == imageVisasAndCards) {
//
//            ActivitiesLauncher.openVisasAndCardsActivity(getApplicationContext());
//            imageVisasAndCards.setSelected(true);
//
//        } else if (v == relativeCompanyInfo || v == imageCompanyInfo) {
//
//            ActivitiesLauncher.openCompanyInfoActivity(getApplicationContext());
//            imageCompanyInfo.setSelected(true);
//
//        } else if (v == relativeReports || v == imageReports) {
//
//            ActivitiesLauncher.openReportsActivity(getApplicationContext());
//            imageReports.setSelected(true);
//
//        } else if (v == relativeNeedHelp || v == imageNeedHelp) {
//
//            ActivitiesLauncher.openNeedHelpActivity(getApplicationContext());
//            imageNeedHelp.setSelected(true);
//
//        } else if (v == relativeQuickAccess || v == imageQuickAccess) {
//
//            ActivitiesLauncher.openQuickAccessActivity(getApplicationContext());
//            imageQuickAccess.setSelected(true);
//
//        } else if (v == relativeCompanyDocuments || v == imageCompanyDocuments) {
//
//            ActivitiesLauncher.openHomeCompanyDocumentsActivity(getApplicationContext());
//            imageCompanyDocuments.setSelected(true);
//
//        } else if (v == btnViewStatement || v == relativeViewStatement) {
//            ActivitiesLauncher.openViewStatementActivity(getApplicationContext());
//        }
//    }
//
//    public void sendRequestCompanyInfo(final Activity act, final RestClient client) throws UnsupportedEncodingException {
//
//        String soql = SoqlStatements.soql_company_info + "\'" + new StoreData(act.getApplicationContext()).getUserID() + "\'";
//
//        final RestRequest restRequest = RestRequest.getRequestForQuery(
//                getString(R.string.api_version), soql);
//
//        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
//            @Override
//            public void onSuccess(RestRequest request, RestResponse result) {
//                try {
//                    User _user = SFResponseManager.parseCompanyRestResponse(result.toString());
//                    Gson gson = new Gson();
//                    String json = gson.toJson(_user);
//                    Utilities.setUserPhoto(HomepageActivity.this, _user.get_contact().get_account().getCompany_Logo(), smartCompanyImage);
//                    new StoreData(getApplicationContext()).setUserDataAsString(json);
//                    sendNotificationCountRequest(_user, client);
//                } catch (Exception e) {
//                    onError(e);
//                }
//            }
//
//            @Override
//            public void onError(Exception exception) {
//                error=RestMessages.getInstance().getErrorMessage();
//                finish();
//            }
//        });
//    }
//
//    private void sendNotificationCountRequest(final User user, RestClient client) {
//
//        String soql = "SELECT COUNT(ID) FROM Notification_Management__c WHERE Case__r.AccountId = " + "\'" + user.get_contact().get_account().getID() + "\'" + " AND Is_Push_Notification_Allowed__c = TRUE AND isMessageRead__c = FALSE";
//        try {
//            final RestRequest rest_request_notification = RestRequest.getRequestForQuery(
//                    getString(R.string.api_version), soql);
//
//            client.sendAsync(rest_request_notification, new RestClient.AsyncRequestCallback() {
//                @Override
//                public void onSuccess(RestRequest request, RestResponse result) {
//                    try {
//                        int notificationCount = 0;
//                        JSONObject jsonCountNotifications = new JSONObject(result.toString());
//                        if (jsonCountNotifications.optBoolean(JSONConstants.DONE) == true) {
//                            notificationCount = jsonCountNotifications.getJSONArray(JSONConstants.RECORDS).getJSONObject(0).getInt("expr0");
//                            new StoreData(getApplicationContext()).setNotificationCount(notificationCount + "");
//                            setUICOmponents(user, notificationCount);
//                        }
//                    } catch (Exception e) {
//                        onError(e);
//                    }
//                }
//
//                @Override
//                public void onError(Exception exception) {
//                    error=RestMessages.getInstance().getErrorMessage();
//                    finish();
//                }
//            });
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendNotificationCountRequestInAnotherThread(final User user, RestClient client) {
//
//        String soql = "SELECT COUNT(ID) FROM Notification_Management__c WHERE Case__r.AccountId = " + "\'" + user.get_contact().get_account().getID() + "\'" + " AND Is_Push_Notification_Allowed__c = TRUE AND isMessageRead__c = FALSE";
//        try {
//            final RestRequest rest_request_notification = RestRequest.getRequestForQuery(
//                    getString(R.string.api_version), soql);
//
//            client.sendAsync(rest_request_notification, new RestClient.AsyncRequestCallback() {
//                @Override
//                public void onSuccess(RestRequest request, RestResponse result) {
//                    try {
//                        int notificationCount = 0;
//                        JSONObject jsonCountNotifications = new JSONObject(result.toString());
//                        if (jsonCountNotifications.optBoolean(JSONConstants.DONE) == true) {
//                            notificationCount = jsonCountNotifications.getJSONArray(JSONConstants.RECORDS).getJSONObject(0).getInt("expr0");
//                            new StoreData(getApplicationContext()).setNotificationCount(notificationCount + "");
//                            final int finalNotificationCount = notificationCount;
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    setUICOmponents(user, finalNotificationCount);
//                                }
//                            });
//                        }
//                    } catch (Exception e) {
//                        onError(e);
//                    }
//                }
//
//                @Override
//                public void onError(Exception exception) {
//                    error=RestMessages.getInstance().getErrorMessage();
//                    finish();
//                }
//            });
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void setUICOmponents(User _user, int notificationCount) {
//        Utilities.dismissLoadingDialog();
//        new StoreData(getApplicationContext()).setUsername(_user.get_contact().getName());
//        findViewById(R.id.root).setVisibility(View.VISIBLE);
//        tvCompanyName.setText(_user.get_contact().get_account().getName());
//        tvLicenseNumber.setText(_user.get_contact().get_account().getLicenseNumberFormula());
//        tvLicenseExpiry.setText(_user.get_contact().get_account().get_currentLicenseNumber().getLicense_Expiry_Date());
//        tvBalance.setText(_user.get_contact().get_account().getPortalBalance() + " AED");
//        if (notificationCount == 0) {
//            _badgeButton.hideBadge();
//        } else {
//            _badgeButton.setBadgeText(notificationCount + "");
//            _badgeButton.showBadge();
//        }
//    }
//}
package com.zcloud;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import RestAPI.JSONConstants;
import RestAPI.RestMessages;
import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import custom.BadgeButton;
import custom.customdialog.NiftyDialogBuilder;
import utilities.StoreData;
import utilities.ExceptionHandler;
import model.EstablishmentCard;
import model.User;
import utilities.ActivitiesLauncher;
import utilities.Utilities;

public class HomepageActivity extends Activity implements
        OnClickListener {

    BadgeButton _badgeButton;

    Button btnLogout, btnLogoutTrasparent;
    RelativeLayout relativeDashboard, relativeMyRequest, relativeNotifications,
            relativeVisasAndCards, relativeCompanyInfo, relativeReports,
            relativeNeedHelp, notificationll, relativeQuickAccess, relativeCompanyDocuments, relativeViewStatement;

    Button btnViewStatement;
    String establishment_card_soql = "select Current_Establishment_Card__c , Current_Establishment_Card__r.Status__c , Current_Establishment_Card__r.Establishment_Card_Number__c , Current_Establishment_Card__r.Issue_Date__c , Current_Establishment_Card__r.Expiry_Date__c from Account where id = " + "\'" + "%s" + "\'";
    private RestRequest restRequest;
    TextView tvCompanyName, tvLicenseNumber, tvLicenseExpiry, tvBalance, tv10;
    ImageView imageDashboard, imageMyRequests, imageVisasAndCards, imageCompanyInfo, imageCompanyDocuments, imageQuickAccess, imageNeedHelp, imageReports, smartCompanyImage;

    private OnClickListener listenerOk1 = new OnClickListener() {

        @Override
        public void onClick(View v) {
            builder.dismiss();
            SalesforceSDKManager.getInstance().logout(HomepageActivity.this);
            new StoreData(getApplicationContext()).reset();
        }
    };
    private String error;
    private User user;
    private NiftyDialogBuilder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        if (!new StoreData(getApplicationContext()).getUserID().equals("")) {
            setContentView(R.layout.homepage);
            InitializeViews();
        }
    }

    private void InitializeViews() {
        _badgeButton = (BadgeButton) findViewById(R.id.btnBadgeCount);
        _badgeButton.setBadgeDrawable(getResources().getDrawable(
                R.mipmap.notification_image3));

        btnLogout = (Button) findViewById(R.id.btnLogout);

        btnLogoutTrasparent = (Button) findViewById(R.id.btnLogoutTransparent);
        relativeDashboard = (RelativeLayout) findViewById(R.id.relativeDashboard);
        relativeMyRequest = (RelativeLayout) findViewById(R.id.relativeMyRequests);
        relativeNotifications = (RelativeLayout) findViewById(R.id.relativeNotifications);
        notificationll = (RelativeLayout) findViewById(R.id.notificationll);
        relativeVisasAndCards = (RelativeLayout) findViewById(R.id.relativeVisasAndCards);
        relativeCompanyInfo = (RelativeLayout) findViewById(R.id.relativeCompanyInfo);
        relativeReports = (RelativeLayout) findViewById(R.id.relativeReports);
        relativeNeedHelp = (RelativeLayout) findViewById(R.id.relativeNeedHelp);
        relativeQuickAccess = (RelativeLayout) findViewById(R.id.relativeQuickAccess);
        relativeCompanyDocuments = (RelativeLayout) findViewById(R.id.relativeCompanyDocuments);
        relativeViewStatement = (RelativeLayout) findViewById(R.id.relativeViewStatement);
        tv10 = (TextView) findViewById(R.id.textView10);
        btnViewStatement = (Button) findViewById(R.id.btnViewStatement);
        _badgeButton.setOnClickListener(this);
        imageCompanyDocuments = (ImageView) findViewById(R.id.imageCompanyDocuments);
        imageDashboard = (ImageView) findViewById(R.id.imageDashboard);
        imageMyRequests = (ImageView) findViewById(R.id.imageMyRequests);
        imageVisasAndCards = (ImageView) findViewById(R.id.imageVisasAndCards);
        imageCompanyInfo = (ImageView) findViewById(R.id.imageCompanyInfo);
        imageQuickAccess = (ImageView) findViewById(R.id.imageQuickAccess);
        imageNeedHelp = (ImageView) findViewById(R.id.imageNeedHelp);
        imageReports = (ImageView) findViewById(R.id.imageReports);

        relativeDashboard.setOnClickListener(this);
        relativeMyRequest.setOnClickListener(this);
        relativeNotifications.setOnClickListener(this);
        notificationll.setOnClickListener(this);

        relativeVisasAndCards.setOnClickListener(this);
        relativeCompanyInfo.setOnClickListener(this);
        relativeReports.setOnClickListener(this);
        relativeNeedHelp.setOnClickListener(this);
        relativeQuickAccess.setOnClickListener(this);
        relativeCompanyDocuments.setOnClickListener(this);
        relativeViewStatement.setOnClickListener(this);
        btnViewStatement.setOnClickListener(this);
        imageCompanyDocuments.setOnClickListener(this);
        imageDashboard.setOnClickListener(this);
        imageMyRequests.setOnClickListener(this);
        imageVisasAndCards.setOnClickListener(this);
        imageCompanyInfo.setOnClickListener(this);
        imageQuickAccess.setOnClickListener(this);
        imageNeedHelp.setOnClickListener(this);
        imageReports.setOnClickListener(this);

        tvCompanyName = (TextView) findViewById(R.id.tvCompanyName);
        tvLicenseExpiry = (TextView) findViewById(R.id.tvLicenseExpiry);
        tvLicenseNumber = (TextView) findViewById(R.id.tvLicenseNumber);
        smartCompanyImage = (ImageView) findViewById(R.id.companyImage);
        tvBalance = (TextView) findViewById(R.id.tvBalance);
        new ClientManager(this, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(this, new ClientManager.RestClientCallback() {
            @Override
            public void authenticatedRestClient(RestClient client) {
                if (client == null) {
                    SalesforceSDKManager.getInstance().logout(HomepageActivity.this);
                    return;
                } else {
                    try {
                        if (!new StoreData(HomepageActivity.this.getApplicationContext()).getIsHomepageDataLoaded()) {
                            Utilities.showloadingDialog(HomepageActivity.this);
                        }
                        new StoreData(getApplicationContext()).saveUserID(SalesforceSDKManager.getInstance().getUserAccountManager().getCurrentUser().getUserId());
                        sendRequestCompanyInfo(HomepageActivity.this, client);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        if (new StoreData(getApplicationContext()).getFirstTimeLogin()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new StoreData(getApplicationContext()).setFirstTimeLogin(false);
                            setContentView(R.layout.homepage);
                            new StoreData(getApplicationContext()).saveUserID(SalesforceSDKManager.getInstance().getUserAccountManager().getCurrentUser().getUserId());
                            InitializeViews();
                        }
                    });
                }
            }).start();
        }
        super.onResume();
    }

    public void onLogoutClick(View v) {
        builder = Utilities.showNiftyDialog("Logout", this, listenerOk1);
    }

    @Override
    public void onClick(View v) {
        if (v == relativeDashboard || v == imageDashboard) {

            ActivitiesLauncher.openDashboardActivity(getApplicationContext());
            imageDashboard.setSelected(true);

        } else if (v == relativeMyRequest || v == imageMyRequests) {

            ActivitiesLauncher.openMyRequestsActivity(getApplicationContext());
            imageMyRequests.setSelected(true);

        } else if (v == relativeNotifications | v == _badgeButton | v == notificationll | v == tv10 | v.getId() == R.id.badgebtn_rl || v.getId() == R.id.badgebtn_btn) {

            ActivitiesLauncher.openNotificationsActivity(getApplicationContext());
            _badgeButton.setSelected(true);

        } else if (v == relativeVisasAndCards || v == imageVisasAndCards) {

            ActivitiesLauncher.openVisasAndCardsActivity(getApplicationContext());
            imageVisasAndCards.setSelected(true);

        } else if (v == relativeCompanyInfo || v == imageCompanyInfo) {

            ActivitiesLauncher.openCompanyInfoActivity(getApplicationContext());
            imageCompanyInfo.setSelected(true);

        } else if (v == relativeReports || v == imageReports) {

            ActivitiesLauncher.openReportsActivity(getApplicationContext());
            imageReports.setSelected(true);

        } else if (v == relativeNeedHelp || v == imageNeedHelp) {

            ActivitiesLauncher.openNeedHelpActivity(getApplicationContext());
            imageNeedHelp.setSelected(true);

        } else if (v == relativeQuickAccess || v == imageQuickAccess) {

            ActivitiesLauncher.openQuickAccessActivity(getApplicationContext());
            imageQuickAccess.setSelected(true);

        } else if (v == relativeCompanyDocuments || v == imageCompanyDocuments) {

            ActivitiesLauncher.openHomeCompanyDocumentsActivity(getApplicationContext());
            imageCompanyDocuments.setSelected(true);

        } else if (v == btnViewStatement || v == relativeViewStatement) {
            ActivitiesLauncher.openViewStatementActivity(getApplicationContext());
        }
    }

    public void sendRequestCompanyInfo(final Activity act, final RestClient client) throws UnsupportedEncodingException {
        if (new StoreData(getApplicationContext()).getIsHomepageDataLoaded()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final User _user = SFResponseManager.parseCompanyRestResponse(new StoreData(getApplicationContext()).getHomepageScreenInfo());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new StoreData(getApplicationContext()).setUsername(_user.get_contact().getName());
                            findViewById(R.id.root).setVisibility(View.VISIBLE);
                            if (Utilities.getIsProgressLoading()) {
                                Utilities.dismissLoadingDialog();
                            }
                            Utilities.setUserPhoto(HomepageActivity.this, _user.get_contact().get_account().getCompany_Logo(), smartCompanyImage);
                            tvCompanyName.setText(_user.get_contact().get_account().getName());
                            tvLicenseNumber.setText(_user.get_contact().get_account().getLicenseNumberFormula());
                            tvLicenseExpiry.setText(Utilities.formatVisitVisaDate(_user.get_contact().get_account().get_currentLicenseNumber().getLicense_Expiry_Date()));
                            tvBalance.setText(Utilities.processAmount(_user.get_contact().get_account().getPortalBalance()) + " AED");

                            int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), QuickAccessWidget.class));
                            QuickAccessWidget myWidget = new QuickAccessWidget();
                            myWidget.onUpdate(HomepageActivity.this, AppWidgetManager.getInstance(HomepageActivity.this), ids);
                        }
                    });
                    sendNotificationCountRequestInAnotherThread(_user, client);
                    senCompanyInfoRequestInAnotherThread(client);
                }
            }).start();
        } else {
            String soql = SoqlStatements.soql_company_info + "\'" + new StoreData(act.getApplicationContext()).getUserID() + "\'";

            final RestRequest restRequest = RestRequest.getRequestForQuery(
                    getString(R.string.api_version), soql);

            client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse result) {
                    try {
                        new StoreData(getApplicationContext()).setHomepageScreenInfo(result.toString());
                        new StoreData(getApplicationContext()).setIsHomepageDataLoaded(true);
                        User _user = SFResponseManager.parseCompanyRestResponse(result.toString());
                        Gson gson = new Gson();
                        String json = gson.toJson(_user);
                        Utilities.setUserPhoto(HomepageActivity.this, _user.get_contact().get_account().getCompany_Logo(), smartCompanyImage);
                        new StoreData(getApplicationContext()).setUserDataAsString(json);
                        sendNotificationCountRequest(_user, client);
                    } catch (Exception e) {
                        onError(e);
                    }
                }

                @Override
                public void onError(Exception exception) {
                    error = RestMessages.getInstance().getErrorMessage();
                    finish();
                }
            });
        }
    }

    private void sendNotificationCountRequest(final User user, RestClient client) {

        String soql = "SELECT COUNT(ID) FROM Notification_Management__c WHERE Case__r.AccountId = " + "\'" + user.get_contact().get_account().getID() + "\'" + " AND Is_Push_Notification_Allowed__c = TRUE AND isMessageRead__c = FALSE";
        this.user = user;
        try {
            final RestRequest rest_request_notification = RestRequest.getRequestForQuery(
                    getString(R.string.api_version), soql);

            client.sendAsync(rest_request_notification, new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse result) {
                    try {
                        int notificationCount = 0;
                        JSONObject jsonCountNotifications = new JSONObject(result.toString());
                        if (jsonCountNotifications.optBoolean(JSONConstants.DONE) == true) {
                            notificationCount = jsonCountNotifications.getJSONArray(JSONConstants.RECORDS).getJSONObject(0).getInt("expr0");
                            new StoreData(getApplicationContext()).setNotificationCount(notificationCount + "");
                            DoRequest(true, notificationCount);
                        }
                    } catch (Exception e) {
                        onError(e);
                    }
                }

                @Override
                public void onError(Exception exception) {
                    error = RestMessages.getInstance().getErrorMessage();
                    finish();
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void sendNotificationCountRequestInAnotherThread(final User user, RestClient client) {

        String soql = "SELECT COUNT(ID) FROM Notification_Management__c WHERE Case__r.AccountId = " + "\'" + user.get_contact().get_account().getID() + "\'" + " AND Is_Push_Notification_Allowed__c = TRUE AND isMessageRead__c = FALSE";
        try {
            final RestRequest rest_request_notification = RestRequest.getRequestForQuery(
                    getString(R.string.api_version), soql);

            client.sendAsync(rest_request_notification, new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse result) {
                    try {
                        int notificationCount = 0;
                        JSONObject jsonCountNotifications = new JSONObject(result.toString());
                        if (jsonCountNotifications.optBoolean(JSONConstants.DONE) == true) {
                            notificationCount = jsonCountNotifications.getJSONArray(JSONConstants.RECORDS).getJSONObject(0).getInt("expr0");
                            new StoreData(getApplicationContext()).setNotificationCount(notificationCount + "");
                            final int finalNotificationCount = notificationCount;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setNotificationUICOmponents(finalNotificationCount);
                                }
                            });
                        }
                    } catch (Exception e) {
                        onError(e);
                    }
                }

                @Override
                public void onError(Exception exception) {
                    error = RestMessages.getInstance().getErrorMessage();
                    finish();
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void senCompanyInfoRequestInAnotherThread(RestClient client) {
        String soql = SoqlStatements.soql_company_info + "\'" + new StoreData(this.getApplicationContext()).getUserID() + "\'";

        RestRequest restRequest = null;
        try {
            restRequest = RestRequest.getRequestForQuery(
                    getString(R.string.api_version), soql);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
            @Override
            public void onSuccess(RestRequest request, RestResponse result) {
                try {
                    new StoreData(getApplicationContext()).setHomepageScreenInfo(result.toString());
                    new StoreData(getApplicationContext()).setIsHomepageDataLoaded(true);
                    final User _user = SFResponseManager.parseCompanyRestResponse(result.toString());
                    Gson gson = new Gson();
                    String json = gson.toJson(_user);
                    Utilities.setUserPhoto(HomepageActivity.this, _user.get_contact().get_account().getCompany_Logo(), smartCompanyImage);
                    new StoreData(getApplicationContext()).setUserDataAsString(json);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setCompanyUICOmponents(_user);
                        }
                    });
                } catch (Exception e) {
                    onError(e);
                }
            }

            @Override
            public void onError(Exception exception) {

                error = RestMessages.getInstance().getErrorMessage();
                finish();
            }
        });
    }

    private void setCompanyUICOmponents(User _user) {
        new StoreData(getApplicationContext()).setUsername(_user.get_contact().getName());
        findViewById(R.id.root).setVisibility(View.VISIBLE);
        if (Utilities.getIsProgressLoading()) {
            Utilities.dismissLoadingDialog();
        }
        tvCompanyName.setText(_user.get_contact().get_account().getName());
        tvLicenseNumber.setText(_user.get_contact().get_account().getLicenseNumberFormula());
        tvLicenseExpiry.setText(Utilities.formatVisitVisaDate(Utilities.stringNotNull(_user.get_contact().get_account().get_currentLicenseNumber().getLicense_Expiry_Date())));
        tvBalance.setText(Utilities.processAmount(_user.get_contact().get_account().getPortalBalance()) + " AED");
    }

    public void setUICOmponents(User _user, int notificationCount) {

        new StoreData(getApplicationContext()).setUsername(_user.get_contact().getName());
        findViewById(R.id.root).setVisibility(View.VISIBLE);
        if (Utilities.getIsProgressLoading()) {
            Utilities.dismissLoadingDialog();
        }
        tvCompanyName.setText(_user.get_contact().get_account().getName());
        tvLicenseNumber.setText(_user.get_contact().get_account().getLicenseNumberFormula());
        tvLicenseExpiry.setText(Utilities.formatVisitVisaDate(Utilities.stringNotNull(_user.get_contact().get_account().get_currentLicenseNumber().getLicense_Expiry_Date())));
        tvBalance.setText(Utilities.processAmount(_user.get_contact().get_account().getPortalBalance()) + " AED");
        if (notificationCount == 0) {
            _badgeButton.hideBadge();
        } else {
            _badgeButton.setBadgeText((notificationCount > 99 ? 99 : notificationCount) < 10 ? notificationCount + "" : notificationCount + "");
            _badgeButton.showBadge();
        }
    }

    public void setNotificationUICOmponents(int notificationCount) {
        if (notificationCount == 0) {
            _badgeButton.hideBadge();
        } else {
            _badgeButton.setBadgeText((notificationCount > 99 ? 99 : notificationCount) < 10 ? notificationCount + "" : notificationCount + "");
            _badgeButton.showBadge();
        }
    }

    @Override
    public void finish() {
        setContentView(R.layout.splash);
        Button close = (Button) findViewById(R.id.close);
        TextView properMessage = (TextView) findViewById(R.id.properMessage);
        close.setVisibility(View.VISIBLE);
        properMessage.setVisibility(View.VISIBLE);
        properMessage.setText(error);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                HomepageActivity.super.finish();
            }
        });
    }

    private void DoRequest(final boolean b, final int notificationCount) {

        establishment_card_soql = String.format(establishment_card_soql, user.get_contact().get_account().getID());
        try {
            restRequest = RestRequest.getRequestForQuery(
                    getString(R.string.api_version), establishment_card_soql);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        new ClientManager(this, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(this, new ClientManager.RestClientCallback() {

            @Override
            public void authenticatedRestClient(RestClient client) {
                if (client == null) {
                    SalesforceSDKManager.getInstance().logout(HomepageActivity.this);
                    return;
                } else {
                    client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                        @Override
                        public void onSuccess(RestRequest request, RestResponse response) {
                            Log.d("", response.toString());
                            try {
                                JSONObject json = new JSONObject(response.toString());
                                JSONArray jRecords = json.getJSONArray(JSONConstants.RECORDS);
                                JSONObject jsonRecord = jRecords.getJSONObject(0);
                                EstablishmentCard establishmentCard = new EstablishmentCard();
                                establishmentCard.setCurrent_Establishment_Card__c(jsonRecord.getString("Current_Establishment_Card__c"));
                                if (establishmentCard.getCurrent_Establishment_Card__c() == null || establishmentCard.getCurrent_Establishment_Card__c().equals("null")) {
                                    new StoreData(getApplicationContext()).setEstablishmentCardPageExist(false);
                                } else {
                                    new StoreData(getApplicationContext()).setEstablishmentCardPageExist(true);
                                    establishmentCard.setIssue_Date__c(jsonRecord.getJSONObject("Current_Establishment_Card__r").getString("Issue_Date__c"));
                                    establishmentCard.setExpiry_Date__c(jsonRecord.getJSONObject("Current_Establishment_Card__r").getString("Expiry_Date__c"));
                                    establishmentCard.setStatus(jsonRecord.getJSONObject("Current_Establishment_Card__r").getString("Status__c"));
                                    establishmentCard.setEstablishment_Card_Number__c(jsonRecord.getJSONObject("Current_Establishment_Card__r").getString("Establishment_Card_Number__c"));
                                    user.get_contact().get_account().setEstablishmentCard(establishmentCard);
                                }
                                setUICOmponents(user, notificationCount);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                setUICOmponents(user, notificationCount);
                                user.get_contact().get_account().setEstablishmentCard(null);
                            }
                        }

                        @Override
                        public void onError(Exception exception) {
                            setUICOmponents(user, notificationCount);
                            user.get_contact().get_account().setEstablishmentCard(null);
                        }
                    });
                }
            }
        });
    }
}