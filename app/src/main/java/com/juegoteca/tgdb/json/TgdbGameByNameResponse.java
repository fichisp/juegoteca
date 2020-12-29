
package com.juegoteca.tgdb.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TgdbGameByNameResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("include")
    @Expose
    private Include include;
    @SerializedName("pages")
    @Expose
    private Pages pages;
    @SerializedName("remaining_monthly_allowance")
    @Expose
    private Integer remainingMonthlyAllowance;
    @SerializedName("extra_allowance")
    @Expose
    private Integer extraAllowance;
    @SerializedName("allowance_refresh_timer")
    @Expose
    private Object allowanceRefreshTimer;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Include getInclude() {
        return include;
    }

    public void setInclude(Include include) {
        this.include = include;
    }

    public Pages getPages() {
        return pages;
    }

    public void setPages(Pages pages) {
        this.pages = pages;
    }

    public Integer getRemainingMonthlyAllowance() {
        return remainingMonthlyAllowance;
    }

    public void setRemainingMonthlyAllowance(Integer remainingMonthlyAllowance) {
        this.remainingMonthlyAllowance = remainingMonthlyAllowance;
    }

    public Integer getExtraAllowance() {
        return extraAllowance;
    }

    public void setExtraAllowance(Integer extraAllowance) {
        this.extraAllowance = extraAllowance;
    }

    public Object getAllowanceRefreshTimer() {
        return allowanceRefreshTimer;
    }

    public void setAllowanceRefreshTimer(Object allowanceRefreshTimer) {
        this.allowanceRefreshTimer = allowanceRefreshTimer;
    }

}
