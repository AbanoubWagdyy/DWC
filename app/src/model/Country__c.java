package model;

import com.google.gson.annotations.SerializedName;

//import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Created by M_Ghareeb on 8/25/2015.
 */
public class Country__c implements Serializable{

    @SerializedName("Aramex_Country_Code__c")
    public String aramex_Country_Code__c;
//    @JsonProperty("Country_Name_Arabic__c")
    @SerializedName("Country_Name_Arabic__c")
    public String Country_Name_Arabic__c;
//    @JsonProperty("eDNRD_Name__c")
    @SerializedName("eDNRD_Name__c")
    public String eDNRD_Name__c;
//    @JsonProperty("eForm_Code__c")
    @SerializedName("eForm_Code__c")
    public String eForm_Code__c;
//    @JsonProperty("Is_Active__c")
    @SerializedName("Is_Active__c")
    public String Is_Active__c;
//    @JsonProperty("Nationality_Name__c")
    @SerializedName("Nationality_Name__c")
    public String Nationality_Name__c;
//    @JsonProperty("Nationality_Name_Arabic__c")
    @SerializedName("Nationality_Name_Arabic__c")
    public String Nationality_Name_Arabic__c;
//    @JsonProperty("Id")
    @SerializedName("Id")
    public String id;

//    @JsonProperty("Name")
    @SerializedName("Name")
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAramex_Country_Code__c() {
        return aramex_Country_Code__c;
    }

    public void setAramex_Country_Code__c(String aramex_Country_Code__c) {
        this.aramex_Country_Code__c = aramex_Country_Code__c;
    }

    public String getCountry_Name_Arabic__c() {
        return Country_Name_Arabic__c;
    }

    public void setCountry_Name_Arabic__c(String country_Name_Arabic__c) {
        Country_Name_Arabic__c = country_Name_Arabic__c;
    }

    public String geteDNRD_Name__c() {
        return eDNRD_Name__c;
    }

    public void seteDNRD_Name__c(String eDNRD_Name__c) {
        this.eDNRD_Name__c = eDNRD_Name__c;
    }

    public String geteForm_Code__c() {
        return eForm_Code__c;
    }

    public void seteForm_Code__c(String eForm_Code__c) {
        this.eForm_Code__c = eForm_Code__c;
    }

    public String getIs_Active__c() {
        return Is_Active__c;
    }

    public void setIs_Active__c(String is_Active__c) {
        Is_Active__c = is_Active__c;
    }

    public String getNationality_Name__c() {
        return Nationality_Name__c;
    }

    public void setNationality_Name__c(String nationality_Name__c) {
        Nationality_Name__c = nationality_Name__c;
    }

    public String getNationality_Name_Arabic__c() {
        return Nationality_Name_Arabic__c;
    }

    public void setNationality_Name_Arabic__c(String nationality_Name_Arabic__c) {
        Nationality_Name_Arabic__c = nationality_Name_Arabic__c;
    }

    public String getId() {
        return id;
    }

    public void setId(String ID) {
        this.id = ID;
    }
}
