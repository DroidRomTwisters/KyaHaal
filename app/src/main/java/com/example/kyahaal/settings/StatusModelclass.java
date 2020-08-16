package com.example.kyahaal.settings;

public class StatusModelclass {
    boolean isSelected;
    String Status;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public StatusModelclass() {
    }

    public StatusModelclass(boolean isSelected, String status) {
        this.isSelected = isSelected;
        Status = status;
    }
}
