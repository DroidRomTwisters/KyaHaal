package com.example.kyahaal.essentials;


public class User {
    String isconfiguringcomplete,mobile_no,name,profilepic,status,token_id;
    Long seen;
    Boolean Online,istyping;
    public User() {
    }

    public User(String isconfiguringcomplete, String mobile_no, String name, String profilepic, String status, String token_id ) {
        this.isconfiguringcomplete = isconfiguringcomplete;
        this.mobile_no = mobile_no;
        this.name = name;
        this.profilepic = profilepic;
        this.status = status;
        this.token_id = token_id;
    }

    public Boolean getIstyping() {
        return istyping;
    }

    public void setIstyping(Boolean istyping) {
        this.istyping = istyping;
    }

    public String getIsconfiguringcomplete() {
        return isconfiguringcomplete;
    }

    public void setIsconfiguringcomplete(String isconfiguringcomplete) {
        this.isconfiguringcomplete = isconfiguringcomplete;
    }


    public Boolean getOnline() {
        return Online;
    }

    public void setOnline(Boolean online) {
        Online = online;
    }

    public Long getSeen() {
        return seen;
    }

    public void setSeen(Long seen) {
        this.seen = seen;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }


}
