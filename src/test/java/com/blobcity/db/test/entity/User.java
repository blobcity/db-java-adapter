/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.test.entity;

import com.blobcity.db.fieldannotations.Column;
import com.blobcity.db.fieldannotations.Primary;
import com.blobcity.db.CloudStorage;
import java.util.List;

/**
 *
 * @author Sanket Sarang <sanket@blobcity.net>
 */
public class User extends CloudStorage {

    @Primary
    @Column(name = "email")
    private String email;
    @Column(name = "name")
    private String name;
    @Column(name = "intField")
    private int intField;
    @Column(name = "floatField")
    private float floatField;
    @Column(name = "doubleField")
    private double doubleField;
    @Column(name = "charField")
    private char charField;
    @Column(name = "listIntField")
    private List<Integer> listIntField;
    @Column(name = "listFloatField")
    private List<Float> listFloatField;
    @Column(name = "listDoubleField")
    private List<Double> listDoubleField;
    @Column(name = "listStringField")
    private List<String> listStringField;
    @Column(name = "listCharField")
    private List<Character> listCharField;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIntField() {
        return intField;
    }

    public void setIntField(int intField) {
        this.intField = intField;
    }

    public float getFloatField() {
        return floatField;
    }

    public void setFloatField(float floatField) {
        this.floatField = floatField;
    }

    public double getDoubleField() {
        return doubleField;
    }

    public void setDoubleField(double doubleField) {
        this.doubleField = doubleField;
    }

    public char getCharField() {
        return charField;
    }

    public void setCharField(char charField) {
        this.charField = charField;
    }

    public List<Integer> getListIntField() {
        return listIntField;
    }

    public void setListIntField(List<Integer> listIntField) {
        this.listIntField = listIntField;
    }

    public List<Float> getListFloatField() {
        return listFloatField;
    }

    public void setListFloatField(List<Float> listFloatField) {
        this.listFloatField = listFloatField;
    }

    public List<Double> getListDoubleField() {
        return listDoubleField;
    }

    public void setListDoubleField(List<Double> listDoubleField) {
        this.listDoubleField = listDoubleField;
    }

    public List<String> getListStringField() {
        return listStringField;
    }

    public void setListStringField(List<String> listStringField) {
        this.listStringField = listStringField;
    }

    public List<Character> getListCharField() {
        return listCharField;
    }

    public void setListCharField(List<Character> listCharField) {
        this.listCharField = listCharField;
    }
}
