package com.example.armap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.armap.Helper.DBopearations;

public class StartPointQR extends AppCompatActivity {


    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Button BtnRetry,BtnCreatePoint;
    private DBopearations dBopearations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_point_qr);
        BtnRetry=findViewById(R.id.btnRetry);
        BtnCreatePoint=findViewById(R.id.btncreatepoints);
        prefs=getSharedPreferences(getResources().getString(R.string.SHARED_PREF_NAME),MODE_PRIVATE);
        editor=prefs.edit();
        dBopearations=new DBopearations(this,getResources().getString(R.string.DBname));
        BtnRetry.setOnClickListener(v -> {
         openQRreader();
        });
        BtnCreatePoint.setOnClickListener(v -> {
            Intent createPointActivity=new Intent(this,CreatePoints.class);


            startActivity(createPointActivity);
        });

       openQRreader();
    }

    public void openQRreader(){

        try {


            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

            startActivityForResult(intent, 0);

        } catch (Exception e) {

            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                if(dBopearations.isLabelAvailable(contents)){
                    Toast.makeText(getApplicationContext(),contents,Toast.LENGTH_LONG).show();
                    editor.putString("startpoint",contents);
                    editor.apply();
                    Intent destinationActivity=new Intent(StartPointQR.this,DestinationDropDown.class);
                    startActivity(destinationActivity);
                }else{
                    Toast.makeText(getApplicationContext(),"Invalid Source Location",Toast.LENGTH_LONG).show();
                    openQRreader();
                }


            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }
    }
}
