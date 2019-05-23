package com.massita.service.messaging.message;

import lombok.Getter;

public class DbMessage extends ObjectMessage {

    public static Address DB_SERVICE_ADDRESS = new Address("DB_ADDRESS");

    @Getter
    Class<?> objectType;

    @Getter
    DbMessageType messageType;

    public DbMessage(Address from, Address to, DbMessageType messageType, Object body, Class<?> objectType) {
        super(from, to, body);
        this.messageType = messageType;
        this.objectType = objectType;
    }

    public enum DbMessageType {

        SAVE,
        LOAD,
        COUNT
    }

}
