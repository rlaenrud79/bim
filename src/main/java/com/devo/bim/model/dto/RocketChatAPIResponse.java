package com.devo.bim.model.dto;

import java.util.List;

@lombok.Data
public class RocketChatAPIResponse {
    private String status;
    private boolean success;

    private Object error;
    private String message;

    private User user;
    private Data data;
    private Team team;

    @lombok.Data
    public static class User {
        private String _id;
        List<Room> rooms;
    }

    @lombok.Data
    public static class Data {
        private String userId;
        private String authToken;
    }

    @lombok.Data
    public static class Team {
        private String roomId;
    }

    @lombok.Data
    public static class Room {
        private String rid;
        private String name;
        private int unread;
    }
}

