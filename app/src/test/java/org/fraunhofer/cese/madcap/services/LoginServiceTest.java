package org.fraunhofer.cese.madcap.services;

import android.content.Context;
import android.support.v4.app.TaskStackBuilder;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

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
    }

    @Test
    public void onCreate() throws Exception {
        cut.setMadcapAuthManager(mockMadcapAuthManager);

        cut.onCreate();
        verify(mockMadcapAuthManager).setCallbackClass(cut);
        verify(mockMadcapAuthManager).silentLogin();
    }

    @Test
    public void onSilentLoginSuccessfull() throws Exception {
        LoginService spyCut = spy(cut);
        spyCut.setMadcapAuthManager(mockMadcapAuthManager);
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);

        spyCut.onSilentLoginSuccessfull(mockResult);
        verify(spyCut).startService(any(Intent.class));
    }

    @Test
    public void onSilentLoginFailed() throws Exception {
        cut.setMadcapAuthManager(mockMadcapAuthManager);

//        Context mockContext = spy(Context.class);
//        TaskStackBuilder mockStackBuilder = spy(TaskStackBuilder.create(mockContext));
//        cut.setStackBuilder(mockStackBuilder);
//        OptionalPendingResult<GoogleSignInResult> mockOpr = (OptionalPendingResult<GoogleSignInResult>) spy(OptionalPendingResult.class);
//        cut.onSilentLoginFailed(mockOpr);
    }

    @Test
    public void onSignInSucessfull() throws Exception {
        LoginService spyCut = spy(cut);
        spyCut.setMadcapAuthManager(mockMadcapAuthManager);

        spyCut.onSignInSucessfull();
        verify(spyCut).startService(any(Intent.class));
    }

    @Test
    public void onSignOutResults() throws Exception {
        cut.setMadcapAuthManager(mockMadcapAuthManager);

        LoginService cutBefore = cut.clone();
        cut.onSignOutResults(null);
        Assert.assertEquals(cut, cutBefore);
    }

    @Test
    public void onRevokeAccess() throws Exception {
        cut.setMadcapAuthManager(mockMadcapAuthManager);

        LoginService cutBefore = cut.clone();
        cut.onRevokeAccess(null);
        Assert.assertEquals(cut, cutBefore);
    }

    @Test
    public void onSignInIntent() throws Exception {
        cut.setMadcapAuthManager(mockMadcapAuthManager);

        Intent mockIntent = spy(Intent.class);

        LoginService cutBefore = cut.clone();
        cut.onSignInIntent(mockIntent, 2);
        Assert.assertEquals(cut, cutBefore);
    }

    @Test
    public void testHashCode(){
        cut.setMadcapAuthManager(mockMadcapAuthManager);
    }

}