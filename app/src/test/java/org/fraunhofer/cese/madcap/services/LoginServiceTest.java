package org.fraunhofer.cese.madcap.services;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import junit.framework.Assert;

import org.fraunhofer.cese.madcap.authentification.MadcapAuthEventHandler;
import org.fraunhofer.cese.madcap.authentification.MadcapAuthManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.os.Build.VERSION_CODES.M;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.fraunhofer.cese.madcap.authentification.MadcapAuthManager;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * Created by MMueller on 10/11/2016.
 */
public class LoginServiceTest {
    LoginService cut;
    GoogleSignInOptions mockGso;
    GoogleApiClient mockGoogleApiClient;
    MadcapAuthManager mockMadcapAuthManager;

    @Before
    public void setUp() throws Exception {
        mockMadcapAuthManager = mock(MadcapAuthManager.class);
        mockGso = spy(new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build());
        mockGoogleApiClient = spy(GoogleApiClient.class);

        mockMadcapAuthManager.setUp(mockGso, mockGoogleApiClient);
        cut = new LoginService();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void onBind() throws Exception {
        Intent mockIntent = spy(Intent.class);
        Assert.assertNull(cut.onBind(mockIntent));
    }

    @Test
    public void onDestroy() throws Exception {
        cut.onDestroy();
        Log mockLog = mock(Log.class);

    }

    @Test
    public void onCreate() throws Exception {
        cut.setMadcapAuthManager(mockMadcapAuthManager);

        cut.onCreate();
        verify(mockMadcapAuthManager).setCallbackClass(cut);
    }

    @Test
    public void onSilentLoginSuccessfull() throws Exception {

    }

    @Test
    public void onSilentLoginFailed() throws Exception {

    }

    @Test
    public void onSignInSucessfull() throws Exception {

    }

    @Test
    public void onSignOutResults() throws Exception {

    }

    @Test
    public void onRevokeAccess() throws Exception {

    }

    @Test
    public void onSignInIntent() throws Exception {

    }

}