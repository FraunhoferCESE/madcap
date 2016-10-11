package org.fraunhofer.cese.madcap.authentification;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 10/4/2016.
 */
public class MadcapAuthManagerTest {
    Context mockContext;
    GoogleSignInOptions mockGso;
    GoogleApiClient mockMGoogleApiClient;
    MadcapAuthManager cut;

    @Captor
    ArgumentCaptor<ResultCallback<Status>> resultCallbackStatusCaptor;

    @Captor
    ArgumentCaptor<ResultCallback<GoogleSignInResult>> resultCallbackGoogleSignInResultCaptor;

    @Before
    public void setUp(){
        mockGso = spy(new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build());
        mockMGoogleApiClient = spy(GoogleApiClient.class);

        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown(){
        if(cut != null){
           cut.reset();
        }
    }

    @Test
    public void testGetGso(){
        cut = MadcapAuthManager.getInstance();
        cut.setUp(mockGso, mockMGoogleApiClient);
        Assert.assertEquals("Gso getter should return the right value ", mockGso, cut.getGso());
    }

    @Test
    public void testGetMGoogleApiClient(){
        cut = MadcapAuthManager.getInstance();
        cut.setUp(mockGso, mockMGoogleApiClient);
        Assert.assertEquals("Google Api Client getter should return the same value ", mockMGoogleApiClient, cut.getMGoogleApiClient());
    }

    @Test
    public void testGetSetCallbackClass(){
        cut = MadcapAuthManager.getInstance();
        cut.setUp(mockGso, mockMGoogleApiClient);
        MadcapAuthEventHandler mockMadcapAuthEventHandler= spy(MadcapAuthEventHandler.class);
        cut.setCallbackClass(mockMadcapAuthEventHandler);
        Assert.assertEquals("Callback class should return the same value ", mockMadcapAuthEventHandler, cut.getCallbackClass());
    }

    @Test
    public void testConstructor(){
        //Testing with reflection
        try {
            Constructor<MadcapAuthManager> c = MadcapAuthManager.class.getDeclaredConstructor();
            c.setAccessible(true);
            MadcapAuthManager madcapAuthManager = c.newInstance();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSetUpAuthManager(){
        //Making sure it works for the case if both objects are passed in
        cut = MadcapAuthManager.getInstance();
        cut.setUp(mockGso, mockMGoogleApiClient);
        Assert.assertEquals("Making sure the parameters have been passed correctly ", mockGso, cut.getGso());
        Assert.assertEquals("Making sure the parameters have been passed correctly ", mockMGoogleApiClient, cut.getMGoogleApiClient());

        //Making sure that the GoogleSignInOptions and the GoogleApiClient could only be set once
        GoogleSignInOptions mockGsob = spy(new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build());
        GoogleApiClient mockMGoogleApiClientb = spy(GoogleApiClient.class);
        cut.setUp(mockGsob, mockMGoogleApiClientb);
        Assert.assertEquals("Making sure the second setting up is not possible 1", mockGso, cut.getGso());
        Assert.assertEquals("Making sure the second setting up is not possible 2", mockMGoogleApiClient, cut.getMGoogleApiClient());
    }

    @Test
    public void testSilentLogin(){
        cut = MadcapAuthManager.getInstance();
        cut.setUp(mockGso, mockMGoogleApiClient);
        GoogleSignInApi mockGoogleSignInApi = spy(GoogleSignInApi.class);
        cut.setGoogleSignInApi(mockGoogleSignInApi);
        OptionalPendingResult<GoogleSignInResult> mockOpr = (OptionalPendingResult<GoogleSignInResult>) spy(OptionalPendingResult.class);
        MadcapAuthEventHandler mockMadcapAuthEventHandler = spy(MadcapAuthEventHandler.class);

        //In Case that the the GoogleSignInApi could get the cached credentials and the App could log in silently immetiadtely
        when(mockOpr.isDone()).thenReturn(true);
        when(mockGoogleSignInApi.silentSignIn(mockMGoogleApiClient)).thenReturn(mockOpr);
        cut.setCallbackClass(mockMadcapAuthEventHandler);
        cut.silentLogin();
        verify(mockGoogleSignInApi, atLeastOnce()).silentSignIn(mockMGoogleApiClient);
        verify(mockMadcapAuthEventHandler, atLeastOnce()).onSilentLoginSuccessfull(cut.getLastSignInResult());

        //In Case the credentials are not cached.
        when(mockOpr.isDone()).thenReturn(false);
        cut.silentLogin();
        verify(mockGoogleSignInApi, atLeastOnce()).silentSignIn(mockMGoogleApiClient);
        verify(mockMadcapAuthEventHandler, atLeastOnce()).onSilentLoginSuccessfull(cut.getLastSignInResult());

//        verify(mockOpr).setResultCallback(resultCallbackGoogleSignInResultCaptor.capture());
//        //Status mockStatus = spy(Status.class);
//
//        ResultCallback<GoogleSignInResult> capturedCallback = resultCallbackGoogleSignInResultCaptor.getValue();
//        capturedCallback.onResult(any(GoogleSignInResult.class));
//        // Verify that the method you want called from onResult is actually called
        verify(mockMadcapAuthEventHandler, atLeastOnce()).onSilentLoginSuccessfull(any(GoogleSignInResult.class));
    }

    @Test
    public void testGetInstance(){
        //Making sure that only one time the instance can be instanciated.
        MadcapAuthManager a = MadcapAuthManager.getInstance();
        Assert.assertEquals(a, MadcapAuthManager.getInstance());
    }

    @Test
    public void testSignIn(){
        cut = MadcapAuthManager.getInstance();
        cut.setUp(mockGso, mockMGoogleApiClient);
        GoogleSignInApi mockGoogleSignInApi = spy(GoogleSignInApi.class);
        cut.setGoogleSignInApi(mockGoogleSignInApi);
        Intent mockIntent = spy(Intent.class);
        when(mockGoogleSignInApi.getSignInIntent(mockMGoogleApiClient)).thenReturn(mockIntent);
        MadcapAuthEventHandler mockMadcapAuthEventHandler = spy(MadcapAuthEventHandler.class);
        cut.setCallbackClass(mockMadcapAuthEventHandler);

        cut.signIn();
        verify(mockMadcapAuthEventHandler, atLeastOnce()).onSignInIntent(mockIntent, MadcapAuthManager.RC_SIGN_IN);
    }

    @Test
    public void testSignOut(){
        cut = MadcapAuthManager.getInstance();
        cut.setUp(mockGso, mockMGoogleApiClient);
        GoogleSignInApi mockGoogleSignInApi = spy(GoogleSignInApi.class);
        cut.setGoogleSignInApi(mockGoogleSignInApi);
        MadcapAuthEventHandler mockMadcapAuthEventHandler = spy(MadcapAuthEventHandler.class);
        cut.setCallbackClass(mockMadcapAuthEventHandler);

        PendingResult mockOpr = spy(PendingResult.class);

        when(mockGoogleSignInApi.signOut(mockMGoogleApiClient)).thenReturn(mockOpr);

        cut.signOut();
        verify(mockGoogleSignInApi, atLeastOnce()).signOut(mockMGoogleApiClient);

        verify(mockOpr).setResultCallback(resultCallbackStatusCaptor.capture());
        //Status mockStatus = spy(Status.class);

        ResultCallback<Status> capturedCallback = resultCallbackStatusCaptor.getValue();
        capturedCallback.onResult(any(Status.class));
        // Verify that the method you want called from onResult is actually called
        verify(mockMadcapAuthEventHandler).onSignOutResults(any(Status.class));
    }

    @Test
    public void testGetUserId(){
        cut = MadcapAuthManager.getInstance();
        cut.setUp(mockGso, mockMGoogleApiClient);

        //Make sure if there is no last result, the method doesn't crash.
        Assert.assertNull(cut.getUserId());

        //Make sure if there is a result, it gets invoked
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        cut.setLastSignInResult(mockResult);
        GoogleSignInAccount mockGoogleSignInAccount = mock(GoogleSignInAccount.class);
        when(mockResult.getSignInAccount()).thenReturn(mockGoogleSignInAccount);
        when(mockGoogleSignInAccount.getId()).thenReturn("res");
        Assert.assertEquals("res", cut.getUserId());
    }

    @Test
    public void testRevokeAccess(){
        cut = MadcapAuthManager.getInstance();
        cut.setUp(mockGso, mockMGoogleApiClient);
        GoogleSignInApi mockGoogleSignInApi = spy(GoogleSignInApi.class);
        cut.setGoogleSignInApi(mockGoogleSignInApi);
        PendingResult mockOpr = spy(PendingResult.class);
        when(mockGoogleSignInApi.revokeAccess(mockMGoogleApiClient)).thenReturn(mockOpr);

        MadcapAuthEventHandler mockMadcapAuthEventHandler= spy(MadcapAuthEventHandler.class);
        cut.setCallbackClass(mockMadcapAuthEventHandler);

        cut.revokeAccess();
        verify(mockGoogleSignInApi, times(1)).revokeAccess(mockMGoogleApiClient);

        verify(mockOpr).setResultCallback(resultCallbackStatusCaptor.capture());
        //Status mockStatus = spy(Status.class);

        ResultCallback<Status> capturedCallback = resultCallbackStatusCaptor.getValue();
        capturedCallback.onResult(any(Status.class));
        // Verify that the method you want called from onResult is actually called
        verify(mockMadcapAuthEventHandler).onRevokeAccess(any(Status.class));
    }

    @Test
    public void testGetGsoScopeArray(){
        cut = MadcapAuthManager.getInstance();
        cut.setUp(null, mockMGoogleApiClient);
        Assert.assertNull(cut.getGsoScopeArray());

        cut.reset();
        cut.setUp(mockGso, mockMGoogleApiClient);

        //Due to the fact that scopes cannot be mocked because they are final
        // we have to verify that the statement is called exactly one time.
        Scope scope1 = new Scope("a");
        Scope scope2 = new Scope("b");
        Scope[] scopeArray = new Scope[2];
        scopeArray[0] = scope1;
        scopeArray[1] = scope2;
        when(mockGso.getScopeArray()).thenReturn(scopeArray);
        cut.getGsoScopeArray();
        verify(mockGso, times(1)).getScopeArray();

    }

    @Test
    public void testGetGoogleSignInAccount(){
        cut = MadcapAuthManager.getInstance();
        cut.setUp(mockGso, mockMGoogleApiClient);

        cut.setLastSignInResult(null);
        //When no last result cached
        Assert.assertNull(cut.getSignInAccount());

        //When the last result is cached.
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        cut.setLastSignInResult(mockResult);
        GoogleSignInAccount mockGoogleSignInAccount = mock(GoogleSignInAccount.class);
        when(mockResult.getSignInAccount()).thenReturn(mockGoogleSignInAccount);
        Assert.assertEquals(mockGoogleSignInAccount, cut.getSignInAccount());
    }

    @Test
    public void testGetLastSignedInUsersName(){
        cut = MadcapAuthManager.getInstance();
        cut.setUp(mockGso, mockMGoogleApiClient);
        cut.setLastSignInResult(null);

        //When no last result cached
        Assert.assertNull(cut.getLastSignedInUsersName());

        //When the last result is cached.
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        cut.setLastSignInResult(mockResult);
        GoogleSignInAccount mockGoogleSignInAccount = mock(GoogleSignInAccount.class);
        when(mockResult.getSignInAccount()).thenReturn(mockGoogleSignInAccount);
        when(mockGoogleSignInAccount.getGivenName()).thenReturn("Mary");
        when(mockGoogleSignInAccount.getFamilyName()).thenReturn("Huana");

        Assert.assertEquals("Mary Huana", cut.getLastSignedInUsersName());
    }

    @Test
    public void testHandleSignInResult(){
        cut = MadcapAuthManager.getInstance();
        cut.setUp(mockGso, mockMGoogleApiClient);

        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        cut.handleSignInResult(mockResult);
        Assert.assertEquals(mockResult, cut.getLastSignInResult());
    }

    @Test
    public void testConnect(){
        cut = MadcapAuthManager.getInstance();
        cut.setUp(mockGso, mockMGoogleApiClient);

        cut.connect();
        verify(mockMGoogleApiClient, atLeastOnce()).connect();
    }

    @Test
    public void testOnConnectionFailed(){
        cut = MadcapAuthManager.getInstance();
        cut.setUp(mockGso, mockMGoogleApiClient);
        MadcapAuthManager m = MadcapAuthManager.getInstance();
        //Instanciation because could not be mocked (final).
        ConnectionResult connectionResult = new ConnectionResult(1);

        MadcapAuthManager.getInstance().onConnectionFailed(connectionResult);
        Assert.assertEquals("Madcapauthmanager should not have changed a thing ", m, MadcapAuthManager.getInstance());
    }
}
