package com.devo.bim.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class CustomHttpSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {

        HttpSession session = sessionEvent.getSession();
        String sessionId = null;

        if(session.getAttribute("idx") == null) {

        } else {
            System.out.println("session out!!!");
        }
    }
}
