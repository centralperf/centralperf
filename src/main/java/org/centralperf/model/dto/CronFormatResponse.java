package org.centralperf.model.dto;

import java.util.Date;

/**
 * API response of a cron validation and format request
 *
 * @since 1.3
 */
public class CronFormatResponse {

    private boolean isValid;

    private String cronExpression;

    private String humanReadable;

    private Date next;

    private String validationErrorMesssage;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getHumanReadable() {
        return humanReadable;
    }

    public void setHumanReadable(String humanReadable) {
        this.humanReadable = humanReadable;
    }

    public Date getNext() {
        return next;
    }

    public void setNext(Date next) {
        this.next = next;
    }

    public String getValidationErrorMesssage() {
        return validationErrorMesssage;
    }

    public void setValidationErrorMesssage(String validationErrorMesssage) {
        this.validationErrorMesssage = validationErrorMesssage;
    }

}
