package edu.umd.fcmd.sensorlisteners.model.notification;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 12/2/2016.
 * <p>
 *  Model class for a Network state.
 *  Has Status ON/OFF, SSID and Security Level.
 */

public class NotificationProbe extends Probe {
    private String pack;
    private String ticker;
    private Bundle extras;
    private String title;
    private String text;
    private StatusBarNotification sbn;
    private static final String NOTIFICATION_TYPE = "Notification";

    /**
     * Gets the state (ON/OFF).
     *
     * @return the state.
     */
    public String getPack() {
        return pack;
    }

    /**
     * Sets the state (ON/OFF).
     */
    public void setPack(String pack) {
        this.pack = pack;
    }

    public StatusBarNotification getSBN() {
        return sbn;
    }

    /**
     * Sets the state (ON/OFF).
     */
    public void setSBN(StatusBarNotification sbn) {
        this.sbn = sbn;
    }



    /**
     * Gets the SSID.
     *
     * @return the SSID.
     */
    public String getTicker() {
        return ticker;
    }

    /**
     * Sets the SSID.
     */
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }


    public Bundle getExtras() {
        return extras;
    }

    /**
     * Sets the state (ON/OFF).
     * @param extras
     */
    public void setExtras(Bundle extras) {
        this.extras = extras;
    }

    /**
     * Gets the SecurityLevel.
     *
     * @return the SecurityLevel.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the SecurityLevel.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the Ip address.
     * @return Ip address.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the ip address.
     * @param //ip address.
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getType() {
        return NOTIFICATION_TYPE;
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "{\"pack\": " + (pack != null ? pack : "-") +
                ", \"ticker\": " + (ticker != null ? ticker : "-") +
                ", \"title\": " + (title != null ? "\""+title+"\"" : "-") +
                ", \"extras\": " + (title != null ? "\""+title+"\"" : "-") +
                ", \"text\": " + (text != null ? "\""+text+"\"" : "-") +
                '}';
    }
}
