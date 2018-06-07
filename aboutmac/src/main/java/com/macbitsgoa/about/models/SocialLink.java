package com.macbitsgoa.about.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Rushikesh Jogdand.
 */
public class SocialLink extends RealmObject {
    @PrimaryKey
    @SerializedName("name")
    public String name;

    @SerializedName("url")
    public String url;

    @SerializedName("thumbnailUrl")
    public String thumbnailUrl;
}
