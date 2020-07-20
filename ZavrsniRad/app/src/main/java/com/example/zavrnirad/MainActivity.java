package com.example.zavrnirad;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton camera_btn;
    private int tourist_no=0;
    private WebView webView;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView=findViewById(R.id.webView_container);
        camera_btn=findViewById(R.id.btn_camera);
        camera_btn.hide();
        SharedPreferences preferences=getSharedPreferences("Data",MODE_PRIVATE);
        String user=preferences.getString("userName",null);
        String pass=preferences.getString("password",null);
        CheckBox checkBox=findViewById(R.id.checkBox);
        Log.v("hjsba",""+user+"---"+preferences.getString("Username",null));
        if(user!=null && pass!=null){
            Log.d("ubasd",""+user);
            checkBox.setChecked(true);
        }
        loadWeb(user,pass);


       camera_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {

                String urlToGet="https://www.evisitor.hr/eVisitor/hr-HR/Tourists/CheckIn";
                /*if(webView.getUrl().length()>55 && urlToGet.equals(webView.getUrl().substring(0,55)))
                {
                    getInputNumber(webView);
                    Toast toast = Toast.makeText(getApplicationContext(), "Radi dobro", Toast.LENGTH_SHORT);
                    toast.show();*/
                    Intent intent=new Intent(MainActivity.this,document_scan.class);
                    startActivityForResult(intent,1);

                /*}
                    else{
                        Toast toast = Toast.makeText(getApplicationContext(), "Ne mozes otvoriti ovdje", Toast.LENGTH_SHORT);
                        toast.show();
                    }*/
            }
        });

    }
    @SuppressLint({"SetJavaScriptEnabled", "RestrictedApi"})
    public void loadWeb(String s1, String s2) {
        final String username = s1;
        final String password = s2;
        CheckBox checkBox=findViewById(R.id.checkBox);
        checkBox.setVisibility(View.VISIBLE);

        camera_btn.show();//OVO MAKNI
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.loadUrl("https://www.evisitor.hr");
        webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(final WebView view, String url) {
                    super.onPageFinished(view, url);
                    getInputNumber(webView);
                    if(username!=null && password!=null) {
                        final String js = "javascript:{" +
                                "var x=document.getElementById('userName').value = '" + username + "';" +
                                "var y=document.getElementById('password').value = '" + password + "';};";
                        view.loadUrl(js);
                    }
                }
            });

        chekckUrl(webView,checkBox,0);
    }
    public void chekckUrl(final WebView webView,final CheckBox checkBox,final int a)
    {
        final Handler handler=new Handler();
        final int delay=1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int stop=a;

                if(webView.getUrl().length()>52 && !(webView.getUrl().substring(47,52).equals("Login")))
                {
                    checkBox.setVisibility(View.INVISIBLE);
                    camera_btn.show();
                    stop=1;
                }
                if(stop==0)
                {
                    handler.postDelayed(this,delay);
                }
            }
        },delay);
    }
    public void rememberLogin(View view)
    {
        boolean check=((CheckBox)view).isChecked();
        SharedPreferences prefs=getSharedPreferences("Data",MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        if(check)
        {
            getData(webView,editor,"userName");
            getData(webView,editor,"password");
        }else{
            editor.putString("userName",null);
            editor.putString("password",null);
            editor.apply();
            Toast toast = Toast.makeText(getApplicationContext(), "Podaci za prijavu nisu spremljeni", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public void getData(WebView webView, final SharedPreferences.Editor editor, final String id)
    {
        webView.evaluateJavascript("(function(){return document.getElementById('"+id+"').value;})()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if(value.length()>2)
                {
                    editor.putString(id,value.substring(1,value.length()-1));
                    editor.apply();
                    Toast toast = Toast.makeText(getApplicationContext(), "Podaci za prijavu spremljeni", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Podaci nisu unešeni!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String [] recieved_data=new String[9];
        if(requestCode==1)
        {
            if(resultCode==RESULT_OK)
            {
                recieved_data=data.getStringArrayExtra("cardData");
                inputRecievedData(recieved_data);
                Vibrator v=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }

        if(recieved_data[0]==null)
        {

        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Skenirani podaci");
            builder.setMessage(recieved_data[0] + "\n" + recieved_data[1] + "\n" + recieved_data[2] + "\n" + recieved_data[3] + "\n" + recieved_data[4] + "\n" + recieved_data[5] + "\n" + recieved_data[6] + "\n" + recieved_data[7] + "\n" + recieved_data[8] + "\n" );
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }
    public void inputRecievedData(String [] data)
    {

        tourist_no=tourist_no-40;
        String special_js;

        for (int i = tourist_no; i < 40 + tourist_no; i++) {
            switch (i-tourist_no) {
                case 8:
                    int li=pickDocumentType(data[0],data[1]);
                    if(li!=250) {
                        special_js = "javascript:{" +
                                "$(document).ready(function(){" +
                                "var e=$(\"span:contains('Odaberite vrstu isprave...')\");" +
                                "e.trigger('click');" +                                                                                //dodavanje podataka o ispravi
                                "var p=$('ul').eq('" +(15+(17*(tourist_no/30))) + "');" +//tribat ce poseban switch case za odredit koji <li> biran
                                "p.find('li').eq('"+li+"').trigger('click');" +
                                "});};";
                        Log.v("jansd",""+li+"------"+(15+(17*(tourist_no/30))));
                        inputData(null, 0, special_js);
                    }
                case 9:
                    inputData(data[2],i,null);
                    break;
                case 10:
                        inputData(data[6],i,null);
                    break;
                case 11:
                    inputData(data[7],i,null);
                    break;
                case 12:
                    if (data[8] != null) {
                        inputData(data[8],i,null);
                    }else{
                        special_js="javascript:{" +
                                "var x=document.getElementsByTagName('input')[" + i + "];" +
								"x.value='';"+
								"x.classList.remove('field-changed');};";
                        inputData(null,0,special_js);
                    }
                    break;
                case 13:
                    int pick_sex;
                    if(data[4].equals("M"))
                    {
                        pick_sex=13;
                    }else {
                        pick_sex=14;
                    }
                    special_js="javascript:{" +
                            "$(document).ready(function(){"+
							"var e=$(\"input:eq('"+(pick_sex+tourist_no)+"')\");"+
							"e.click();});};";
                        inputData(null,0,special_js);
                    break;
                case 15:
                    special_js="javascript:{"+
							"$(document).ready(function(){"+
                            "var e=$(\"input\").eq('"+(15+tourist_no)+"');"+
                            "e.select();"+
							"e.val(\" \");"+
							"e.val('"+data[1]+"');"+
							"e.trigger('keydown');"+
							"});};";
                    inputData(null,0,special_js);
                    special_js="javascript:{"+
                            "var t=document.getElementsByTagName('ul')['"+(16+(17*(tourist_no/30)))+"'];"+
                            "setInterval(function(){t.querySelector('li').click();},1000);};";
                    inputData(null,0,special_js);
                    break;
                case 26:
                    inputData(data[4],i,null);
                    break;
                case 27:
                    special_js="javascript:{"+
                            "$(document).ready(function(){"+
                            "var e=$(\"input\").eq('"+(27+tourist_no)+"');"+
                            "e.select();"+
                            "e.val(\" \");"+
                            "e.val('"+data[5]+"');"+
                            "e.trigger('keydown');"+
                            "});};";
                    inputData(null,0,special_js);
                    special_js="javascript:{"+
                            "var t=document.getElementsByTagName('ul')['"+(20+(17*(tourist_no/30)))+"'];"+//ne znan jel 20 triba provjerit to je ja mis broj koji govori koji je <ul> po redu na stranici
                            "setInterval(function(){t.querySelector('li').click();},1000);};";
                    inputData(null,0,special_js);
                    break;
                default:

                    break;

            }
        }

    }
    public void inputData(String val,int x, String js_function) {
        if(js_function==null) {
            js_function = "javascript:{" +
                    "var x=document.getElementsByTagName('input')[" + x + "];" + //Pronalazi odgovarajuće "input" polje
                    "x.classList.remove('field-changed');"+ 			//Brisanje klasa (ako je vec odraden unos ali neuspjesno) 
                    "x.value=\" \";"+						//"Input" polje se očisti 
                    "x.value='" + val + "';" +					//Unosi se vrijednost varijable val u polje
                    "x.classList.remove('k-valid');"+				//Brisanje klasa (ako je vec odraden unos ali neuspjesno)
            "x.className+=\" \"+('k-valid')+\" \"+('field-changed');};";	//Dodaju se klase koja simuliraju ljudski unos 
        }
            webView.evaluateJavascript(js_function, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(final String value) {
                }
            });
    }
    public int pickDocumentType(String type,String nation)
    {
        int numb=0;
        for(int i=0;i<4;i++){
            switch(i)
            {
                case 0:
                    if(type.substring(0,1).matches("I|A|C") && !(type.substring(1,2).equals("P")) && !(nation.equals("Republic of Croatia")))
                    {
                        numb=0;
                    }
                    break;
                case 1:
                    if(type.substring(0,1).matches("I|A|C") && !(type.substring(1,2).equals("P")) && nation.equals("Republic of Croatia"))
                    {
                        numb=1;
                    }
                break;
                case 2:
                    if((type.substring(0,1).equals("P") || type.substring(1,2).equals("P")) && !(nation.equals("Republic of Croatia")))
                    {
                        numb=4;
                    }
                    break;
                case 3:
                    if((type.substring(0,1).equals("P") || type.substring(1,2).equals("P")) && (nation.equals("Republic of Croatia")) )
                    {
                        numb=5;
                    }
                    break;
                default:
                    numb=250;
                    break;
            }
        }
        return numb;
    }
    public void getInputNumber(WebView view)
    {
        view.evaluateJavascript("(function(){return document.getElementsByTagName('input').length;})()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                tourist_no=Integer.parseInt(value);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack())
        {
            webView.goBack();
        }else{
            super.onBackPressed();
        }
    }
}
