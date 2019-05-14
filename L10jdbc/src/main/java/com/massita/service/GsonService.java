package com.massita.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.massita.service.db.hibernate.HibernateProxyTypeAdapter;
import lombok.Getter;


public class GsonService {

    @Getter
    private static GsonService instance = new GsonService();

    @Getter
    private final Gson gson;

    private GsonService() {
        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        this.gson = b.create();
    }




}
