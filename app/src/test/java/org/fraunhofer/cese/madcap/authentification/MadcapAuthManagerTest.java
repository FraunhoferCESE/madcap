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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.pathsense.locationengine.lib.detectionLogic.b.m;
import static org.fraunhofer.cese.madcap.R.id.status;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
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

    @Before
    public void setUp(){
        mockGso = spy(new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build());
        mockMGoogleApiClient = spy(GoogleApiClient.class);
    }

    @After
    public void tearDown(){
        MadcapAuthManager.reset();
    }

    @Test
    public void testGetGso(){
        MadcapAuthManager.setUp(mockGso, mockMGoogleApiClient);
        Assert.assertEquals("Gso getter should return the right value ", mockGso, MadcapAuthManager.getGso());
    }

    @Test
    public void testGetMGoogleApiClient(){
        MadcapAuthManager.setUp(mockGso, mockMGoogleApiClient);
        Assert.assertEquals("Google Api Client getter should return the same value ", mockMGoogleApiClient, MadcapAuthManager.getMGoogleApiClient());
    }

    @Test
    public void testGetSetCallbackClass(){
        MadcapAuthManager.setUp(mockGso, mockMGoogleApiClient);
        MadcapAuthEventHandler mockMadcapAuthEventHandler= spy(MadcapAuthEventHandler.class);
        MadcapAuthManager.setCallbackClass(mockMadcapAuthEventHandler);
        Assert.assertEquals("Callback class should return the same value ", mockMadcapAuthEventHandler,MadcapAuthManager.getCallbackClass());
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
        MadcapAuthManager.setUp(mockGso, mockMGoogleApiClient);
        Assert.assertEquals("Making sure the parameters have been passed correctly ", mockGso, MadcapAuthManager.getGso());
        Assert.assertEquals("Making sure the parameters have been passed correctly ", mockMGoogleApiClient, MadcapAuthManager.getMGoogleApiClient());

        //Making sure that the GoogleSignInOptions and the GoogleApiClient could only be set once
        GoogleSignInOptions mockGsob = spy(new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build());
        GoogleApiClient mockMGoogleApiClientb = spy(GoogleApiClient.class);
        MadcapAuthManager.setUp(mockGsob, mockMGoogleApiClientb);
        Assert.assertEquals("Making sure the second setting up is not possible 1", mockGso, MadcapAuthManager.getGso());
        Assert.assertEquals("Making sure the second setting up is not possible 2", mockMGoogleApiClient, MadcapAuthManager.getMGoogleApiClient());
    }

    @Test
    public void testSilentLogin(){
        MadcapAuthManager.setUp(mockGso, mockMGoogleApiClient);
        GoogleSignInApi mockGoogleSignInApi = spy(GoogleSignInApi.class);
        MadcapAuthManager.setGoogleSignInApi(mockGoogleSignInApi);
        OptionalPendingResult<GoogleSignInResult> mockOpr = (OptionalPendingResult<GoogleSignInResult>) spy(OptionalPendingResult.class);
        MadcapAuthEventHandler mockMadcapAuthEventHandler = spy(MadcapAuthEventHandler.class);

        //In Case that the the GoogleSignInApi could get the cached credentials and the App could log in silently immetiadtely
        when(mockOpr.isDone()).thenReturn(true);
        when(mockGoogleSignInApi.silentSignIn(mockMGoogleApiClient)).thenReturn(mockOpr);
        MadcapAuthManager.setCallbackClass(mockMadcapAuthEventHandler);
        MadcapAuthManager.silentLogin();
        verify(mockGoogleSignInApi, atLeastOnce()).silentSignIn(mockMGoogleApiClient);
        verify(mockMadcapAuthEventHandler, atLeastOnce()).onSilentLoginSuccessfull(MadcapAuthManager.getLastSignInResult());


        //In Case the credentials are not cached.
        when(mockOpr.isDone()).thenReturn(false);
        MadcapAuthManager.silentLogin();
        verify(mockGoogleSignInApi, atLeastOnce()).silentSignIn(mockMGoogleApiClient);
        verify(mockMadcapAuthEventHandler, atLeastOnce()).onSilentLoginSuccessfull(MadcapAuthManager.getLastSignInResult());
    }

    @Test
    public void testGetInstance(){
        //Making sure that only one time the instance can be instanciated.
        MadcapAuthManager a = MadcapAuthManager.getInstance();
        Assert.assertSame(a, MadcapAuthManager.getInstance());
    }

    @Test
    public void testSignIn(){
        MadcapAuthManager.setUp(mockGso, mockMGoogleApiClient);
        GoogleSignInApi mockGoogleSignInApi = spy(GoogleSignInApi.class);
        MadcapAuthManager.setGoogleSignInApi(mockGoogleSignInApi);
        Intent mockIntent = spy(Intent.class);
        when(mockGoogleSignInApi.getSignInIntent(mockMGoogleApiClient)).thenReturn(mockIntent);
        MadcapAuthEventHandler mockMadcapAuthEventHandler = spy(MadcapAuthEventHandler.class);
        MadcapAuthManager.setCallbackClass(mockMadcapAuthEventHandler);

        MadcapAuthManager.signIn();
        verify(mockMadcapAuthEventHandler, atLeastOnce()).onSignInIntent(mockIntent, MadcapAuthManager.RC_SIGN_IN);
    }

    @Test
    public void testSignOut(){
        MadcapAuthManager.setUp(mockGso, mockMGoogleApiClient);
        GoogleSignInApi mockGoogleSignInApi = spy(GoogleSignInApi.class);
        MadcapAuthManager.setGoogleSignInApi(mockGoogleSignInApi);
        MadcapAuthEventHandler mockMadcapAuthEventHandler = spy(MadcapAuthEventHandler.class);
        MadcapAuthManager.setCallbackClass(mockMadcapAuthEventHandler);

        PendingResult mockOpr = spy(PendingResult.class);

        when(mockGoogleSignInApi.signOut(mockMGoogleApiClient)).thenReturn(mockOpr);

        MadcapAuthManager.signOut();
        verify(mockGoogleSignInApi, atLeastOnce()).signOut(mockMGoogleApiClient);

        //TODO: Don't know how to handle the callback
    }

    @Test
    public void testGetUserId(){
        MadcapAuthManager.setUp(mockGso, mockMGoogleApiClient);

        //Make sure if there is no last result, the method doesn't crash.
        Assert.assertNull(MadcapAuthManager.getUserId());

        //Make sure if there is a result, it gets invoked
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        MadcapAuthManager.setLastSignInResult(mockResult);
        GoogleSignInAccount mockGoogleSignInAccount = mock(GoogleSignInAccount.class);
        when(mockResult.getSignInAccount()).thenReturn(mockGoogleSignInAccount);
        when(mockGoogleSignInAccount.getId()).thenReturn("res");
        Assert.assertEquals("res", MadcapAuthManager.getUserId());
    }

    @Test
    public void testRevokeAccess(){
        MadcapAuthManager.setUp(mockGso, mockMGoogleApiClient);
        GoogleSignInApi mockGoogleSignInApi = spy(GoogleSignInApi.class);
        MadcapAuthManager.setGoogleSignInApi(mockGoogleSignInApi);
        PendingResult mockOpr = spy(PendingResult.class);
        when(mockGoogleSignInApi.revokeAccess(mockMGoogleApiClient)).thenReturn(mockOpr);

        MadcapAuthManager.revokeAccess();
        verify(mockGoogleSignInApi, times(1)).revokeAccess(mockMGoogleApiClient);
    }

    @Test
    public void testGetGsoScopeArray(){
        MadcapAuthManager.setUp(null, mockMGoogleApiClient);
        Assert.assertNull(MadcapAuthManager.getGsoScopeArray());

        MadcapAuthManager.reset();
        MadcapAuthManager.setUp(mockGso, mockMGoogleApiClient);

        //Due to the fact that scopes cannot be mocked because they are final
        // we have to verify that the statement is called exactly one time.
        Scope scope1 = new Scope("a");
        Scope scope2 = new Scope("b");
        Scope[] scopeArray = new Scope[2];
        scopeArray[0] = scope1;
        scopeArray[1] = scope2;
        when(mockGso.getScopeArray()).thenReturn(scopeArray);
        MadcapAuthManager.getGsoScopeArray();
        verify(mockGso, times(1)).getScopeArray();

    }

    @Test
    public void testGetGoogleSignInAccount(){
        MadcapAuthManager.setUp(mockGso, mockMGoogleApiClient);

        MadcapAuthManager.setLastSignInResult(null);
        //When no last result cached
        Assert.assertNull(MadcapAuthManager.getSignInAccount());

        //When the last result is cached.
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        MadcapAuthManager.setLastSignInResult(mockResult);
        GoogleSignInAccount mockGoogleSignInAccount = mock(GoogleSignInAccount.class);
        when(mockResult.getSignInAccount()).thenReturn(mockGoogleSignInAccount);
        Assert.assertEquals(mockGoogleSignInAccount, MadcapAuthManager.getSignInAccount());
    }

    @Test
    public void testGetLastSignedInUsersName(){
        MadcapAuthManager.setUp(mockGso, mockMGoogleApiClient);
        MadcapAuthManager.setLastSignInResult(null);

        //When no last result cached
        Assert.assertNull(MadcapAuthManager.getLastSignedInUsersName());

        //When the last result is cached.
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        MadcapAuthManager.setLastSignInResult(mockResult);
        GoogleSignInAccount mockGoogleSignInAccount = mock(GoogleSignInAccount.class);
        when(mockResult.getSignInAccount()).thenReturn(mockGoogleSignInAccount);
        when(mockGoogleSignInAccount.getGivenName()).thenReturn("Mary");
        when(mockGoogleSignInAccount.getFamilyName()).thenReturn("Huana");

        Assert.assertEquals("Mary Huana", MadcapAuthManager.getLastSignedInUsersName());
    }

    @Test
    public void testHandleSignInResult(){
        MadcapAuthManager.setUp(mockGso, mockMGoogleApiClient);

        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        MadcapAuthManager.handleSignInResult(mockResult);
        Assert.assertEquals(mockResult, MadcapAuthManager.getLastSignInResult());
    }

    @Test
    public void testConnect(){
        MadcapAuthManager.setUp(mockGso, mockMGoogleApiClient);

        MadcapAuthManager.connect();
        verify(mockMGoogleApiClient, atLeastOnce()).connect();
    }

    @Test
    public void testOnConnectionFailed(){
        MadcapAuthManager.setUp(mockGso, mockMGoogleApiClient);
        MadcapAuthManager m = MadcapAuthManager.getInstance();
        //Instanciation because could not be mocked (final).
        ConnectionResult connectionResult = new ConnectionResult(1);

        MadcapAuthManager.getInstance().onConnectionFailed(connectionResult);
        Assert.assertEquals("Madcapauthmanager should not have changed a thing ", m, MadcapAuthManager.getInstance());
    }
}
