package org.fraunhofer.cese.madcap.authentication;

/**
 * Interface that provides callback support for when the user accepts or declines the EULA.
 * <p>
 * Created by llayman on 4/6/2017.
 */
public interface EULAListener {

    /**
     * The EULA pop-up shows Accept or Cancel. This method is called after the user presses the Accept button.
     */
    void onAccept();

    /**
     * The EULA pop-up shows Accept or Cancel. This method is called after the user presses the Cancel button.
     */
    void onCancel();

}
