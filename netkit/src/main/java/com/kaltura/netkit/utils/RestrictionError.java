package com.kaltura.netkit.utils;

/**
 * Created by tehilarozin on 15/02/2017.
 */

public class RestrictionError extends ErrorElement {

    public enum Restriction{
        NotAllowed(533),
        NotEntitled(534),
        ConcurrencyLimitation(535),
        Suspended(536);

        private final int id;
        Restriction(int id) {
            this.id = id;
        }
        public int getValue() {
            return id;
        }
    }


    public RestrictionError(String message, Restriction restriction) {

        super("RestrictionError", message, restriction.getValue());
        this.extra = restriction;
    }

    public Restriction getRestriction() {
        return (Restriction) extra;
    }
}
