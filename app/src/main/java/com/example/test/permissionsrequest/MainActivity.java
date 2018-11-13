package com.example.test.permissionsrequest;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
            .ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(arePermissionsEnabled()){
                //                    permissions granted, continue flow normally
            }else {
                new AlertDialog.Builder(this)
                        .setMessage("Afin d'assurer le bon fonctionnement de l'application, veuillez accepter les accès suivants")
                        .setPositiveButton("Suivant", (dialog, which) -> requestMultiplePermissions())
                        .create()
                        .show();
            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean arePermissionsEnabled(){
        for(String permission : permissions){
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestMultiplePermissions(){
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission);
            }
        }
        requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), 101);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101){
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    if(shouldShowRequestPermissionRationale(permissions[i])){
                        new AlertDialog.Builder(this)
                                .setMessage("Merci de valider les accès pour permettre le bon fonctionnement de l'application")
                                .setPositiveButton("Recommencer", (dialog, which) -> requestMultiplePermissions())
                                //.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss())
                                .create()
                                .show();
                    }
                    return;
                }
            }
            //all is good, continue flow
        }
        if(requestCode == 1){
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                boolean requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
                if (requestAgain) {
                    Toast.makeText(this, "Permission non accordée, veuillez accepter", Toast.LENGTH_SHORT).show();
                }  else {
                        showAlertCamera();
                    }
            }
        }
    }


    public void showAlertCamera(){

        new AlertDialog.Builder(this)
                .setMessage("Vos paramètres pour l'appareil photo sont désactivé. \nMerci de l'activer")
                .setPositiveButton("Réglages", (dialog, which) ->permissionSettingIntent())
                //.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public void permissionSettingIntent(){

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);

        }



    public void takePhoto(View v){
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }

    }

}
