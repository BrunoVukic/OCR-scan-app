package com.example.zavrnirad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class document_scan extends AppCompatActivity implements SurfaceHolder.Callback, Detector.Processor {
    private colorData colorRect;
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (Exception e) {

                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_scan);
        cameraView = findViewById(R.id.surface_view);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Došlo je do pogreške!", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1920,1080)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            colorRect = findViewById(R.id.color_data_view);
            cameraView.getHolder().addCallback(this);
            textRecognizer.setProcessor(this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
                return;
            }
            cameraView.setZOrderOnTop(false);
            cameraSource.start(cameraView.getHolder());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraSource.stop();
    }

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(final Detector.Detections detections) {
        final SparseArray sparseArray = detections.getDetectedItems();
        Rect r = colorRect.getRect();	//Pravokutnik koji ima veličinu pravokutnika kao i pravokutnik ružičastih rubova

        for (int i = 0; i < sparseArray.size(); i++) {
                TextBlock textBlock = (TextBlock) sparseArray.valueAt(i);
                for (Text lines : textBlock.getComponents()) {
                    if (lines.getValue().length() >= 30 && lines.getValue().length() <= 44 && (textBlock.getComponents().size() == 3 || textBlock.getComponents().size() == 2)) {
			//^Provjera velicina bloka u linijama i velicine linije u znakoviam
                        if (r.contains(textBlock.getBoundingBox())) {
				//^Spada li skenirani blok u ružičasti okvir
                            if (textBlock.getComponents().size() == 3) {	//Blok ima 3 linije = riječ je o TD1 tipu MRTD-a
                                Card personData = new Card(this);		//Klasa Card (TD1) u kojoj se skenirani podaci ucitaju u niz string-ova 
                                personData.getDat(textBlock);
                                if (checkData(personData.Data)) {		//Provjera skeniranih podataka
                                    Intent intent=new Intent();
                                    intent.putExtra("cardData",personData.Data);	//Slanje podataka u klasu glavne aktivnosti (klasu)
                                    setResult(RESULT_OK,intent);
                                    finish();						//Vraćanje u glavnu aktivnost (klasu)
                                } else {
                                   break;
                                }
                            }
			    if (textBlock.getComponents().size() == 2) {	//blok ima 2 linije = riječ je o TD2/TD3 tipu MRTD-a
                                Card2 personData = new Card2(this);		//klasa Card (TD2/TD3) u kojoj se skenirani podaci ucitaju u niz string-ova 
                                personData.getDat(textBlock);
                                if (checkData(personData.Data)) {
                                    Intent intent=new Intent();
                                    intent.putExtra("cardData",personData.Data);
                                    setResult(RESULT_OK,intent);
                                    finish();
                                } else {
                                   break;
                                }

                            }
                        }

                    }
                }
            }
        }

    public boolean checkData(String[] data) {
        int temp = 0;

        for (int k = 0; k < data.length; k++) {
            switch (k) {
                case 0:
                    if (data[k].matches("[A-Z<]+")) {
                        temp++;
                    }
                    break;
                case 1:
                case 5:
                    if(data[k].matches("[A-Za-z'\\s]+"))
                    {
                        temp++;
                    }
                    break;
                case 7:
                    if (data[k].matches("[A-Za-z]+")) {
                        temp++;
                    }
                    break;
                case 2:
                    if (data[k].matches("^[A-Z0-9\\s]*$")) {
                        temp++;
                    }
                    break;
                case 3:
                    if (data[k].matches("[0-9_.-]+")) {
                        temp++;
                    }
                    break;
                case 4:
                    if(data[k].equals("M") || data[k].equals("F"))
                    {
                        temp++;
                    }
                    break;
                case 6:
                case 8:
                    if (data[k]==null) {
                        temp++;
                        break;
                    } else {
                        if ((data[k].matches("[A-Za-z\\s]+"))) {
                            temp++;
                        }
                        break;
                    }
                default:
                    break;
            }
        }
        if (temp == 9) {
            return true;
        }else {
            return false;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cameraSource.stop();
        cameraSource.release();
        String [] temp=new String[9];
        Intent intent=new Intent();
        intent.putExtra("cardData",temp);
        setResult(RESULT_CANCELED,intent);
        finish();
    }
}
