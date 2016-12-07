package org.fraunhofer.cese.madcap.authentication;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.mockito.Mockito.spy;

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
    public void setUp() {
        mockGso = spy(new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build());
        mockMGoogleApiClient = spy(GoogleApiClient.class);

        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testConstructor() {
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


}
