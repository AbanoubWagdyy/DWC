package fragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import com.zcloud.R;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import RestAPI.RestMessages;
import RestAPI.config.DWCConfiguration;
import com.zcloud.R;
import utilities.StoreData;
import fragment.ChangeAndRemovalServiceFragment;
import fragment.companychangeservices.AttachmentPage;
import model.Case;
import model.Company_Documents__c;
import model.Directorship;
import model.Receipt_Template__c;
import model.User;
import utilities.AdapterConfiguration;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/1/2015.
 */
public class ChangeAndRemovalActivity extends FragmentActivity {

    String MethodName = "";
    String SubmitMethodName = "";
    private String ScreenTitle;
    private String caseId;
    private Map<String, Object> caseFields = new HashMap<String, Object>();
    private Case caseObject;
    private ArrayList<Company_Documents__c> companyDocuments;
    private Company_Documents__c company_documents__c;
    private RestRequest restRequest;
    private String currentEmail;
    private String currentFax;
    private String currentMobile;
    private String currentPoBox;
    private String newEmail;
    private String newFax;
    private String newMobile;
    private String newPoBox;
    private String newShareCapital;
    private String companyName;
    private String companyNameArabic;
    private String newCompanyName;
    private String newCompanyNameArabic;
    private String serviceIdentifier;
    private String CardNumber;
    private String LicenseNumber;
    private String IssueDate;
    private String ExpiryDate;
    private String Status;
    private boolean isLostCardChecked;
    private String cardId;
    private String TotalAmount;
    private boolean isNoAttachment;
    private long numberofShares;

    public boolean getisNoAttachment() {
        return isNoAttachment;
    }

    public String getTotalAmount() {
        return TotalAmount;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardId() {
        return cardId;
    }


    public String getCardNumber() {
        return CardNumber;
    }

    public void setCardNumber(String cardNumber) {
        CardNumber = cardNumber;
    }

    public String getLicenseNumber() {
        return LicenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        LicenseNumber = licenseNumber;
    }

    public String getIssueDate() {
        return IssueDate;
    }

    public void setIssueDate(String issueDate) {
        IssueDate = issueDate;
    }

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getServiceIdentifier() {
        return serviceIdentifier;
    }

    public void setServiceIdentifier(String serviceIdentifier) {
        this.serviceIdentifier = serviceIdentifier;
    }

    public Receipt_Template__c geteServiceAdministration() {
        return eServiceAdministration;
    }

    private Receipt_Template__c eServiceAdministration;

    public Directorship getDirectorship() {
        return directorship;
    }

    public String getCurrentEmail() {
        return Utilities.stringNotNull(currentEmail);
    }

    public String getCurrentFax() {
        return Utilities.stringNotNull(currentFax);
    }

    public String getCurrentMobile() {
        return Utilities.stringNotNull(currentMobile);
    }

    public String getCurrentPoBox() {
        return Utilities.stringNotNull(currentPoBox);
    }

    public String getNewEmail() {
        return Utilities.stringNotNull(newEmail);
    }

    public String getNewFax() {
        return Utilities.stringNotNull(newFax);
    }

    public String getNewMobile() {
        return Utilities.stringNotNull(newMobile);
    }

    public String getNewPoBox() {
        return Utilities.stringNotNull(newPoBox);
    }

    public void setDirectorship(Directorship directorship) {
        this.directorship = directorship;
    }

    Directorship directorship;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    User user;
    Gson gson;

    public String getScreenTitle() {
        return ScreenTitle;
    }

    public String getMethodName() {
        return MethodName;
    }

    public String getSubmitMethodName() {
        return SubmitMethodName;
    }

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_card);
        companyDocuments = new ArrayList<>();
        gson = new Gson();
        user = gson.fromJson(new StoreData(getApplicationContext()).getUserDataAsString(), User.class);
        SetMethodNameAndSubmitMethodName();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, ChangeAndRemovalServiceFragment.newInstance("ChangeRemovalFragment"))
                .commit();
    }

    private void SetMethodNameAndSubmitMethodName() {

        if (getIntent().getExtras().getString("MethodType").equals("Address Change")) {

            MethodName = DWCConfiguration.METHOD_NAME_ADDRESS_CHANGE;
            SubmitMethodName = DWCConfiguration.SUBMIT_ADDRESS_CHANGE;

        } else if (getIntent().getExtras().getString("MethodType").equals("Name Change")) {

            MethodName = DWCConfiguration.METHOD_NAME_NAME_CHANGE;
            SubmitMethodName = DWCConfiguration.SUBMIT_NAME_CHANGE;

        } else if (getIntent().getExtras().getString("MethodType").equals("Change Capital")) {

            MethodName = DWCConfiguration.METHOD_NAME_CAPITAL_CHANGE;
            SubmitMethodName = DWCConfiguration.SUBMIT_CAPITAL_CHANGE;

        } else if (getIntent().getExtras().getString("MethodType").equals("Remove Director")) {

            MethodName = DWCConfiguration.METHOD_NAME_DIRECTOR_REMOVAL;
            SubmitMethodName = DWCConfiguration.SUBMIT_DIRECTOR_REMOVAL;

            gson = new Gson();
            directorship = gson.fromJson(getIntent().getExtras().getString("object"), Directorship.class);
        } else if (getIntent().getExtras().getString("MethodType").equals("Renew Card") || getIntent().getExtras().getString("MethodType").equals("Lost Card") || getIntent().getExtras().getString("MethodType").equals("Cancel Card")) {

            MethodName = DWCConfiguration.METHOD_NAME_ESTABLISHMENT_CARD;
            SubmitMethodName = DWCConfiguration.SUBMIT_ESTABLISHMENT_CARD;

            if (getIntent().getExtras().getString("MethodType").equals("Renew Card")) {
                serviceIdentifier = DWCConfiguration.RENEW_CARD_SERVICE_IDENTIFIER;
            } else if (getIntent().getExtras().getString("MethodType").equals("Lost Card")) {
                serviceIdentifier = DWCConfiguration.LOST_CARD_SERVICE_IDENTIFIER;
            } else if (getIntent().getExtras().getString("MethodType").equals("Cancel Card")) {
                serviceIdentifier = DWCConfiguration.CANCEL_CARD_SERVICE_IDENTIFIER;
            }

            CardNumber = getIntent().getExtras().getString("CardNumber");
            LicenseNumber = getIntent().getExtras().getString("LicenseNumber");
            IssueDate = getIntent().getExtras().getString("IssueDate");
            ExpiryDate = getIntent().getExtras().getString("ExpiryDate");
            Status = getIntent().getExtras().getString("Status");
            cardId = getIntent().getExtras().getString("Current_Establishment_Card__c");
        }

        numberofShares = getIntent().getExtras().getLong("numberofShares");
        ScreenTitle = getIntent().getExtras().getString("MethodType");
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public void setCaseObject(Case caseObject) {
        this.caseObject = caseObject;
    }

    public Case getCaseObject() {
        return caseObject;
    }

    public void setEServiceAdmin(Receipt_Template__c eServiceAdministration) {
        this.eServiceAdministration = eServiceAdministration;
    }

    public void setCompanyDocuments(ArrayList<Company_Documents__c> companyDocuments) {

        this.companyDocuments = companyDocuments;
    }

    public ArrayList<Company_Documents__c> getCompanyDocuments() {
        return companyDocuments;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Utilities.showloadingDialog(this, "Uploading ....");
                company_documents__c = new Company_Documents__c();
                String result = data.getStringExtra("result");
                int position = data.getIntExtra("position", -1);
                company_documents__c = gson.fromJson(result, Company_Documents__c.class);
                ConnectAttachmentWithCompanyDocument(company_documents__c, position);
            } else if (resultCode == RESULT_CANCELED) {
                int position = data.getIntExtra("position", -1);
                AttachmentPage.setReturnedCompnayDocument(company_documents__c, position);
            }
        } else if (requestCode == AdapterConfiguration.RESULT_LOAD_IMG_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {
                Utilities.showloadingDialog(this, "Uploading ....");
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                File file = new File(picturePath);
                Bitmap myBitmap = Utilities.decodeFile(file, 300, 300);
//                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                ByteArrayOutputStream bao = new ByteArrayOutputStream();
//                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
//                String imgDecodableString = cursor.getString(columnIndex);
//                String imgDecodableString = Utilities.encodeTobase64(myBitmap);
//                ByteArrayOutputStream bao = new ByteArrayOutputStream();
//                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
//                byte[] ba = bao.toByteArray();
                String encodedImage = Utilities.encodeTobase64(myBitmap);
                cursor.close();
                int position = Integer.valueOf(new StoreData(getApplicationContext()).getCompanyDocumentPosition());
                String result = new StoreData(getApplicationContext()).getCompanyDocumentObject();
                company_documents__c = gson.fromJson(result, Company_Documents__c.class);
                UploadCompanyDocumentAttachment(file.getName(), company_documents__c, position, encodedImage);
            } else if (resultCode == RESULT_CANCELED) {
                int position = Integer.valueOf(new StoreData(getApplicationContext()).getCompanyDocumentPosition());
                String result = new StoreData(getApplicationContext()).getCompanyDocumentObject();
                company_documents__c = gson.fromJson(result, Company_Documents__c.class);
                AttachmentPage.setReturnedCompnayDocument(company_documents__c, position);
            }

        } else if (requestCode == AdapterConfiguration.RESULT_LOAD_IMG_FROM_CAMERA) {
            if (resultCode == RESULT_OK) {
                Utilities.showloadingDialog(this, "Uploading ....");
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                File file = new File(picturePath);
                cursor.close();
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Bitmap myBitmap = Utilities.decodeFile(file, 300, 300);
//                photo = Utilities.encodeTobase64(photo);

//                ByteArrayOutputStream bao = new ByteArrayOutputStream();
//                photo.compress(Bitmap.CompressFormat.JPEG, 100, bao);
//                byte[] ba = bao.toByteArray();
                String encodedImage = Utilities.encodeTobase64(myBitmap);
                int position = Integer.valueOf(new StoreData(getApplicationContext()).getCompanyDocumentPosition());
                String result = new StoreData(getApplicationContext()).getCompanyDocumentObject();
                company_documents__c = gson.fromJson(result, Company_Documents__c.class);
                UploadCompanyDocumentAttachment(file.getName(), company_documents__c, position, encodedImage);
            } else if (resultCode == RESULT_CANCELED) {
                int position = Integer.valueOf(new StoreData(getApplicationContext()).getCompanyDocumentPosition());
                String result = new StoreData(getApplicationContext()).getCompanyDocumentObject();
                company_documents__c = gson.fromJson(result, Company_Documents__c.class);
                AttachmentPage.setReturnedCompnayDocument(company_documents__c, position);
            }
        }
    }

    private void ConnectAttachmentWithCompanyDocument(final Company_Documents__c company_documents__c, final int position) {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("Attachment_Id__c", company_documents__c.getAttachment_Id__c());
        restRequest = RestRequest.getRequestForUpdate(getString(R.string.api_version), "Company_Documents__c", company_documents__c.getId(), fields);
        new ClientManager(this, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(this, new ClientManager.RestClientCallback() {
            @Override
            public void authenticatedRestClient(final RestClient client) {
                if (client == null) {
                    SalesforceSDKManager.getInstance().logout(ChangeAndRemovalActivity.this);
                    return;
                } else {
                    client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                        @Override
                        public void onSuccess(RestRequest request, RestResponse result) {
                            Utilities.dismissLoadingDialog();
                            Log.d("", result.toString());
                            AttachmentPage.setReturnedCompnayDocument(company_documents__c, position);
                        }

                        @Override
                        public void onError(Exception exception) {
                            Utilities.dismissLoadingDialog();
                            Utilities.showToast(ChangeAndRemovalActivity.this, RestMessages.getInstance().getErrorMessage());
                            VolleyError volleyError = (VolleyError) exception;
                            NetworkResponse response = volleyError.networkResponse;
                            String json = new String(response.data);
                            Log.d("", json);
                        }
                    });
                }
            }
        });
    }

    private void UploadCompanyDocumentAttachment(String filename, final Company_Documents__c company_documents__c, final int position, String encodedImage) {
        caseFields = new HashMap<String, Object>();
        caseFields.put("Name", filename);
        caseFields.put("ContentType", "image");
        caseFields.put("ParentId", company_documents__c.getId());
        caseFields.put("Body", encodedImage);
        restRequest = RestRequest.getRequestForCreate(getString(R.string.api_version), "Attachment", caseFields);
        new ClientManager(this, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(this, new ClientManager.RestClientCallback() {
            @Override
            public void authenticatedRestClient(final RestClient client) {
                if (client == null) {
                    SalesforceSDKManager.getInstance().logout(ChangeAndRemovalActivity.this);
                    return;
                } else {
                    client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                        @Override
                        public void onSuccess(RestRequest request, RestResponse result) {
                            Log.d("", result.toString());
                            try {
                                JSONObject jsonObject = new JSONObject(result.toString());
                                company_documents__c.setAttachment_Id__c(jsonObject.getString("id"));
                                company_documents__c.setHasAttachment(true);
                                ConnectAttachmentWithCompanyDocument(company_documents__c, position);
//                                    NOCAttachmentPage.setReturnedCompnayDocument(company_documents__c, position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Exception exception) {
                            Utilities.showToast(ChangeAndRemovalActivity.this, RestMessages.getInstance().getErrorMessage());
                            Utilities.dismissLoadingDialog();
                        }
                    });

//                        String attUrl = client.getClientInfo().resolveUrl("/services/data/v26.0/sobjects/attachment").toString();
                }
            }
        });
    }

    public void setCurrentEmail(String s) {
        this.currentEmail = s;
    }

    public void setCurrentFax(String s) {
        this.currentFax = s;
    }

    public void setCurrentMobile(String s) {
        this.currentMobile = s;
    }

    public void setCurrentPoBox(String s) {
        this.currentPoBox = s;
    }

    public void setNewEmail(String s) {
        this.newEmail = s;
    }

    public void setNewFax(String s) {
        this.newFax = s;
    }

    public void setNewMobile(String s) {
        this.newMobile = s;

    }

    public void setNewPoBox(String s) {
        this.newPoBox = s;
    }

    public void setNewShareCapital(String newShareCapital) {
        this.newShareCapital = newShareCapital;
    }

    public String getNewShareCapital() {
        return newShareCapital;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCompanyNameArabic(String companyNameArabic) {
        this.companyNameArabic = companyNameArabic;
    }

    public void setNewCompanyName(String newCompanyName) {
        this.newCompanyName = newCompanyName;
    }

    public void setNewCompanyNameArabic(String newCompanyNameArabic) {
        this.newCompanyNameArabic = newCompanyNameArabic;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyNameArabic() {
        return companyNameArabic;
    }

    public String getNewCompanyName() {
        return newCompanyName;
    }

    public String getNewCompanyNameArabic() {
        return newCompanyNameArabic;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    public void setisLostCardChecked(boolean isLostCardChecked) {
        this.isLostCardChecked = isLostCardChecked;
    }

    public boolean getIsLostCardChecked() {
        return isLostCardChecked;
    }


    public void setTotalAmount(String string) {
        this.TotalAmount = string;
    }

    public void setNoAttachments(boolean b) {
        isNoAttachment = b;
    }

    public long getNumberofShares() {

        return numberofShares;
    }
}
