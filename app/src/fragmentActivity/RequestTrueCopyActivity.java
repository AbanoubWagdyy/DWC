package fragmentActivity;

import android.os.Bundle;
import com.zcloud.R;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import com.zcloud.R;
import utilities.StoreData;
import utilities.ExceptionHandler;
import fragment.companyDocumentsScreen.RequestTrueCopyFragment;
import model.EServices_Document_Checklist__c;
import model.User;
import model.WebForm;

/**
 * Created by Abanoub Wagdy on 9/9/2015.
 */
public class RequestTrueCopyActivity extends FragmentActivity {

    EServices_Document_Checklist__c eServices_document_checklist__c;
    Gson gson;
    private FragmentManager fragmentManager;
    private Map<String, Object> caseFields = new HashMap<String, Object>();
    private Map<String, String> parameters = new HashMap<String, String>();
    User user;
    private WebForm webForm;
    String insertedCaseId;
    private String caseNumber;

    public String getInsertedCaseId() {
        return insertedCaseId;
    }

    public void setInsertedCaseId(String insertedCaseId) {
        this.insertedCaseId = insertedCaseId;
    }

    public WebForm getWebForm() {
        return webForm;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public User getUser() {
        return user;
    }

    public Map<String, Object> getCaseFields() {
        return caseFields;
    }

    public void setCaseFields(Map<String, Object> caseFields) {
        this.caseFields = caseFields;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.noc);
        gson = new Gson();
        eServices_document_checklist__c = gson.fromJson(getIntent().getExtras().getString("object"), EServices_Document_Checklist__c.class);
        gson = new Gson();
        user = gson.fromJson(new StoreData(getApplicationContext()).getUserDataAsString(), User.class);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, RequestTrueCopyFragment.newInstance("TrueCopyFragment"))
                .commit();
    }

    public EServices_Document_Checklist__c geteServices_document_checklist__c() {
        return eServices_document_checklist__c;
    }

    public void setWebForm(WebForm webForm) {
        this.webForm = webForm;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
