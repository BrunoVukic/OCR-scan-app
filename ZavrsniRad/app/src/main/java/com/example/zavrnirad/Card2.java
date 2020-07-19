package com.example.zavrnirad;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

public class Card2 {
    public String[] Data=new String[9];
    private Context context;
    Card2(Context current)
    {
        this.context=current;
    }
    public void getDat(TextBlock dat)
    {
        Text []Line=new Text[2];
        for (int i=0;i<2;i++)
        {
            Line[i]=dat.getComponents().get(i);
        }
        fillInfo(Line);
    }
    private void fillInfo(Text [] lines)
    {
        for(int i=0;i<2;i++)
        {
            if(i==0)
            {
                Data[0]=lines[i].getValue().substring(0,2);//Tip dokumenta
                Data[0]=Data[0].toUpperCase();
                Data[1]=getCountryName(lines[i].getValue().substring(2,5));//Puno ime drzave koja je izdala dokument
               // Data[2]=getCountryName(Data[1]);
				int n=0;
                int m=5;
                for(int j=5;j<lines[i].getValue().length();j++)
                {
                    if(lines[i].getValue().substring(j,j+1).equals("<"))
                    {
                        if(lines[i].getValue().substring(j,j+1).equals("<"))
                        {
                            if(Data[6+n]==null)
                            {
                                Data[6 + n] = lowerCaseName(lines[i].getValue().substring(m, j)); //Prezime(na) i ime(na)

                            }else{
                                Data[6 + n] +=" "+lowerCaseName(lines[i].getValue().substring(m, j)); //Prezime(na) i ime(na)

                            }

                            if (lines[i].getValue().substring(j+1,j+2).equals("<"))
                            {
                                n++;
                                if(n==2)
                                {
                                    break;
                                }
                                j++;
                            }
                            j++;
                            m=j;

                        }
                    }

                }
                for (int k=0;k<Data[7].length();k++)
                {
                    if(Data[7].substring(k, k + 1).equals(" "))
                    {
                        Data[8]=Data[7].substring(k+1);
                        Data[7]=Data[7].substring(0,k);
                        break;
                    }
                }
            }
            else
            {
				Data[2]=lines[i].getValue().substring(0,10);//Br. dokumenta
                int checkY;
                String yy = lines[i].getValue().substring(13, 15);
                checkY = Integer.parseInt(yy);
                String mm = lines[i].getValue().substring(15, 17);
                String dd = lines[i].getValue().substring(17, 19);
                if (checkY > 10) {
                    Data[3] = dd + "." + mm + "." + "19" + yy + ".";//Datum rodenja
                } else {
                    Data[3] = dd + "." + mm + "." + "20" + yy + ".";//Datum rodenja
                }
                boolean temp=true;
                do
                {
                    Data[4]=lines[i].getValue().substring(20,21);//Spol
                    if(Data[4].equals(" ") || Data[4].matches("[0-9]+"))
                    {
                        if(temp)
                        {
                            Data[4]=lines[i].getValue().substring(21,22);//Spol
                        }else{
                            Data[4]=lines[i].getValue().substring(19,20);//Spol
                        }
                        temp=!temp;

                    }
                }while(!(Data[4].matches("M|F")));
                Data[5]=getCountryName(lines[i].getValue().substring(10,13));//Puno ime nacionalnosti
            }

        }

    }
    private String getCountryName(String country_code)
    {
        String[] stringID= context.getResources().getStringArray(R.array.country_code);
        int temp=0;
        if(country_code.equals("D<<"))
        {
            country_code="DEU";
        }
        for(String s:stringID)
        {
            if(country_code.equals(s))
            {
                break;
            }
            temp++;
        }
        String idName="country_name"+temp;
        int resID=context.getResources().getIdentifier(idName,"string",context.getPackageName());

        return context.getString(resID);
    }
    private String lowerCaseName(String s)
    {
        return s.substring(0,1)+s.substring(1).toLowerCase();
    }

}
