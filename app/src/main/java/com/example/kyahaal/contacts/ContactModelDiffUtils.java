package com.example.kyahaal.contacts;


import androidx.annotation.NonNull;

public class ContactModelDiffUtils implements Comparable,Cloneable {
    String UID,NUMBER,MEDIA_URL,PROFILE_LOC,THUMB_LOC,NAME,STATUS;


    public ContactModelDiffUtils() {
    }

    public ContactModelDiffUtils(String UID, String NUMBER,String NAME, String MEDIA_URL, String PROFILE_LOC, String THUMB_LOC,  String STATUS) {
        this.UID = UID;
        this.NUMBER = NUMBER;
        this.MEDIA_URL = MEDIA_URL;
        this.PROFILE_LOC = PROFILE_LOC;
        this.THUMB_LOC = THUMB_LOC;
        this.NAME = NAME;
        this.STATUS = STATUS;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getNUMBER() {
        return NUMBER;
    }

    public void setNUMBER(String NUMBER) {
        this.NUMBER = NUMBER;
    }

    public String getMEDIA_URL() {
        return MEDIA_URL;
    }

    public void setMEDIA_URL(String MEDIA_URL) {
        this.MEDIA_URL = MEDIA_URL;
    }

    public String getPROFILE_LOC() {
        return PROFILE_LOC;
    }

    public void setPROFILE_LOC(String PROFILE_LOC) {
        this.PROFILE_LOC = PROFILE_LOC;
    }

    public String getTHUMB_LOC() {
        return THUMB_LOC;
    }

    public void setTHUMB_LOC(String THUMB_LOC) {
        this.THUMB_LOC = THUMB_LOC;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    @Override
    public int compareTo(Object o) {
        ContactModelDiffUtils compare=(ContactModelDiffUtils) o;
        if (this.NAME!=null && this.NUMBER!=null && this.STATUS!=null && this.MEDIA_URL!=null) {
            if (compare.MEDIA_URL.equals ( this.MEDIA_URL ) && compare.NAME.equals ( this.NAME ) && compare.NUMBER.equals ( this.NUMBER ) && compare.STATUS.equals ( this.STATUS )) {
                return 0;
            }
        }
        return 1;
    }


    @NonNull
    @Override
    protected ContactModelDiffUtils clone() throws CloneNotSupportedException {
        ContactModelDiffUtils clone;
        clone=(ContactModelDiffUtils) super.clone ();
        return clone;
    }
}
