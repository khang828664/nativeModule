package com.apprn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EMVCard {
    private String pan = "'";
    private String expDate = "";
    private String panSequence = "";
    private String issueDate = "";

    public static EMVCard parse(String bankSequence) {
        String s = bankSequence.substring(90, 96);
        if (bankSequence.length() != 286
                || !bankSequence.substring(0, 2).equals("70")
                || !bankSequence.substring(48, 50).equals("5A")
                || !bankSequence.substring(72, 76).equals("5F24")
                || !bankSequence.substring(106, 110).equals("5F34")
                || !bankSequence.substring(84, 88).equals("5F25")) {
            return null;
        }

        EMVCard emvCard = new EMVCard();
        emvCard.pan = bankSequence.substring(52, 71);
        emvCard.expDate = bankSequence.substring(78, 84);
        emvCard.panSequence = bankSequence.substring(112, 114);
        emvCard.issueDate = bankSequence.substring(90, 96);
        return emvCard;

    }

    public String getPan() {
        return pan;
    }

    public String getExpDate() {
        return expDate;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getDisplayPan() {
        StringBuilder stringBuilder = new StringBuilder(pan.replace("F", " "));
        stringBuilder.insert(8, "  ");
        stringBuilder.insert(18, "  ");
        return stringBuilder.toString();
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getDisplayExpDate() {
        return expDate.substring(2, 4) + "/" + expDate.substring(0, 2);
    }

    public String getDisplayIssueDate() {
        return expDate.substring(2, 4) + "/" + expDate.substring(0, 2);
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getPanSequence() {
        return panSequence;
    }

    public void setPanSequence(String panSequence) {
        this.panSequence = panSequence;
    }

    public int getExpMonth() {
        String month = expDate.substring(2, 4);
        return Integer.parseInt(month);
    }

    public int getExpYear() {
        String year = "20" + expDate.substring(0, 2);
        return Integer.parseInt(year);
    }

    public int getIssueMonth() {
        String month = issueDate.substring(2, 4);
        return Integer.parseInt(month);
    }

    public int getIssueYear() {
        String year = "20" + issueDate.substring(0, 2);
        return Integer.parseInt(year);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s_%s_%s", pan, expDate, panSequence);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof EMVCard) {
            return ((EMVCard) obj).expDate.equals(this.expDate)
                    && ((EMVCard) obj).pan.equals(this.pan)
                    && ((EMVCard) obj).panSequence.equals(this.panSequence);
        }
        return false;
    }
}
