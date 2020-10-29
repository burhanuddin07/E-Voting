package com.husain.e_voting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

class FingerprintHandler extends FingerprintManager.AuthenticationCallback{
    private Context context;

    public  FingerprintHandler(Context context) {
        this.context=context;
    }

    public void startAuthentication(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject)
    {
        CancellationSignal cancellationSignal= new CancellationSignal();
        if(ActivityCompat.checkSelfPermission(context,Manifest.permission.USE_FINGERPRINT)!=PackageManager.PERMISSION_GRANTED)
            return;
        fingerprintManager.authenticate(cryptoObject,cancellationSignal,0,this,null);
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        Toast.makeText(context,"Fingerprint Authentication Failed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        Toast.makeText(context,"Fingerprint Authentication Successful",Toast.LENGTH_SHORT).show();
        context.startActivity(new Intent(context,Profile.class));
    }
}
