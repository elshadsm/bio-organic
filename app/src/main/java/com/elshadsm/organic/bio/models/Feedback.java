package com.elshadsm.organic.bio.models;

public class Feedback {

    public String date;
    public String feedback;
    public String fullName;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "date='" + date + '\'' +
                ", feedback='" + feedback + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
