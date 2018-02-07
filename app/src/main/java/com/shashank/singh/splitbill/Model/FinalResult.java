package com.shashank.singh.splitbill.Model;

/**
 * Created by shashank on 4/26/2017.
 */

public class FinalResult {
    private String firstName,lastName;
    private long amount;

    public FinalResult(String firstName, String lastName, long amount) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.amount = amount;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public long getAmount() {
        return amount;
    }
}
