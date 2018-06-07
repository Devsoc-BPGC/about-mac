package com.macbitsgoa.about.models;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * @author Rushikesh Jogdand.
 */
public class Person extends RealmObject {
    @PrimaryKey
    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;

    @SerializedName("phone")
    public String phone;

    @Nullable
    @SerializedName("postName")
    public String postName;

    @Ignore
    public static final String FIELD_POST_NAME = "postName";

    @SerializedName("photoUrl")
    public String photoUrl;

    @SerializedName("homePage")
    public String homePage;
}
