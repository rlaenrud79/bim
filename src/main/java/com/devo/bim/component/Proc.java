package com.devo.bim.component;

import com.devo.bim.model.enumulator.CompanyRoleType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Proc {

    private final MessageSource messageSource;
    private static Gson gsonObj = new Gson();

    public JsonObject getResult(boolean isSuccess, long returnId, String messageProperty) {
        JsonObject jsonObj = getResult(isSuccess, returnId);
        jsonObj.addProperty("message", translate(messageProperty));
        return jsonObj;
    }

    public JsonObject getResult(boolean isSuccess, long returnId) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("result", isSuccess);
        jsonObj.addProperty("returnId", returnId);
        return jsonObj;
    }

    public JsonObject getResult(boolean isSuccess, String messageProperty) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("result", isSuccess);
        jsonObj.addProperty("message", translate(messageProperty));
        return jsonObj;
    }

    public JsonObject getResult(boolean isSuccess, String messageProperty, Object[] args) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("result", isSuccess);
        jsonObj.addProperty("message", translate(messageProperty,args));
        return jsonObj;
    }

    public JsonObject getMessageResult(boolean isSuccess, String message) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("result", isSuccess);
        jsonObj.addProperty("message", message);
        return jsonObj;
    }

    public JsonObject getMessageResult(boolean isSuccess, String message, Object object) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("result", isSuccess);
        jsonObj.addProperty("message", message);
        jsonObj.addProperty("model", gsonObj.toJson(object));
        return jsonObj;
    }

    public void addProperty(JsonObject jsonObject, String property, Object object) {
        jsonObject.addProperty(property, gsonObj.toJson(object));
    }

    public JsonObject getResult(boolean isSuccess) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("result", isSuccess);
        return jsonObj;
    }

    public JsonObject getResult(Object object) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("model", gsonObj.toJson(object)); // view 에서 JSON.parser(data.model) 사용 후 접근
        return jsonObj;
    }

    public JsonObject getResult(boolean isSuccess, Object object) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("result", isSuccess);
        jsonObj.addProperty("model", gsonObj.toJson(object)); // view 에서 JSON.parser(data.model) 사용 후 접근
        return jsonObj;
    }

    public JsonObject getResult(boolean isSuccess, Object object, long value) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("result", isSuccess);
        jsonObj.addProperty("model", gsonObj.toJson(object)); // view 에서 JSON.parser(data.model) 사용 후 접근
        jsonObj.addProperty("value", value);
        return jsonObj;
    }

    public JsonObject getResult(boolean isSuccess, Object object, long value, Object object2) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("result", isSuccess);
        jsonObj.addProperty("model", gsonObj.toJson(object)); // view 에서 JSON.parser(data.model) 사용 후 접근
        jsonObj.addProperty("work", gsonObj.toJson(object2));
        jsonObj.addProperty("value", value);
        return jsonObj;
    }

    public JsonObject getResult(boolean isSuccess, String messageProperty, Object object) {
        JsonObject jsonObj = getResult(isSuccess, messageProperty);
        jsonObj.addProperty("model", gsonObj.toJson(object)); // view 에서 JSON.parser(data.model) 사용 후 접근
        return jsonObj;
    }

    public JsonObject getResult(boolean isSuccess, String messageProperty, JsonObject jsonObject) {
        JsonObject jsonObj = getResult(isSuccess, messageProperty);
        jsonObj.add("model", jsonObject);
        return jsonObj;
    }

    public String translate(String prop) {
        if(StringUtils.isEmpty(prop)) return "";
        return messageSource.getMessage(prop, null, LocaleContextHolder.getLocale());
    }

    public String translate(String prop, @Nullable Object[] args){
        return messageSource.getMessage(prop, args, LocaleContextHolder.getLocale());
    }

    public String translate(CompanyRoleType companyRoleType){
        if(companyRoleType == CompanyRoleType.HEAD)return messageSource.getMessage("system.model.search_user_dto.role_type_head", null, LocaleContextHolder.getLocale());
        if(companyRoleType == CompanyRoleType.LEAD)return messageSource.getMessage("system.model.search_user_dto.role_type_lead", null, LocaleContextHolder.getLocale());
        if(companyRoleType == CompanyRoleType.PARTNER)return  messageSource.getMessage("system.model.search_user_dto.role_type_partner", null, LocaleContextHolder.getLocale());
        return "";
    }
}