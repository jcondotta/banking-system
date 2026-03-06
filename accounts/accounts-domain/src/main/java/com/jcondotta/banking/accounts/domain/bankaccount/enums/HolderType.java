package com.jcondotta.banking.accounts.domain.bankaccount.enums;

public enum HolderType {
    PRIMARY, JOINT;

    public boolean isPrimary() {
        return this == PRIMARY;
    }
    public boolean isJoint() {
        return this == JOINT;
    }
}
