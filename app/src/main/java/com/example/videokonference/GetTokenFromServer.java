package com.example.videokonference;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetTokenFromServer extends AsyncTask<String, String, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity currentMainActivity = MainActivity.getInstance();
        currentMainActivity.p = new ProgressDialog(currentMainActivity);
        currentMainActivity.p.setMessage("Fetching Token");
        currentMainActivity.p.setIndeterminate(false);
        currentMainActivity.p.setCancelable(false);
        currentMainActivity.p.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        String receivedToken = null;

        // a potentially time consuming task
        URL obj = null;
        HttpURLConnection con = null;
        try {
            obj = new URL(strings[0]);
            Log.v("TryLog", "Inside Try of Getdata");
            con = (HttpURLConnection) obj.openConnection();
            InputStream in = null;
            in = con.getInputStream();
            Scanner sc = new Scanner(in);
            StringBuffer sb = new StringBuffer();
            while (sc.hasNext()) {
                sb.append(sc.nextLine());
            }
            receivedToken = sb.toString();
            receivedToken = receivedToken.replaceAll("\"", "");
            receivedToken = removeStringfromOrg(receivedToken, "<title>[a-zA-Z0-9\\p{Punct}\\s]*</title>");
            String extractTokenPattern = "[<][!]?[/]?[a-zA-Z0-9\\s=-]*[>]";
            receivedToken = ExtractToken(receivedToken, extractTokenPattern);
            Log.v("ReceivedTokenString", receivedToken);
            sc.close();
        } catch(Exception e)
        {
            e.printStackTrace();
        }

        //out.print("Received Token :- " + receivedToken);
        return receivedToken;
    }

    public String removeStringfromOrg(String Orgstr, String patternStr) {
        String extractedStr = "";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(Orgstr);
        int startIndex = 0, endIndex = 0;
        boolean found = false;
        while (matcher.find()) {
            //System.out.println("I found the text "+matcher.group()+" starting at index " + matcher.start()+" and ending at index "+matcher.end());
            //System.out.println(matcher.group());
            startIndex = matcher.start();
            endIndex = matcher.end();
            found = true;
        }
        if(!found){
            //System.out.println("No match found.");
        }
        extractedStr += Orgstr.substring(0, startIndex) + Orgstr.substring(endIndex);
        return extractedStr;
    }

    public String ExtractToken(String OrgStr, String patternStr) {
        String temp = "";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(OrgStr);
        ArrayList<Integer> startIndex = new ArrayList<>();
        ArrayList<Integer> endIndex = new ArrayList<>();
        boolean found = false;
        while (matcher.find()) {
            //System.out.println("I found the text "+matcher.group()+" starting at index "+ matcher.start()+" and ending at index "+matcher.end());
            //System.out.println(matcher.group());
            startIndex.add(matcher.start());
            endIndex.add(matcher.end());
            found = true;
        }
        for (int i = 0; i < startIndex.size(); i++)
        {
            //System.out.println("startIndex : " + startIndex.get(i) + ", endIndex : " + endIndex.get(i));
        }
        if(!found){
            //System.out.println("No match found.");
        }
        else{
            for(int i = 0; i < startIndex.size() - 1; i++)
            {
                if(startIndex.get(i) != 0 && i == 0)
                    temp += OrgStr.substring(0, startIndex.get(i));
                else
                    temp += OrgStr.substring(endIndex.get(i), startIndex.get(i + 1));

            }
            //System.out.println(temp);
        }
        return temp;
    }

    @Override
    protected void onPostExecute(String tokenString) {
        super.onPostExecute(tokenString);
        if(tokenString != null) {
            MainActivity.t = tokenString;
            MainActivity.getInstance().p.hide();
            MainActivity.getInstance().onResume();
        }
        else
        {
            MainActivity.getInstance().p.show();
        }
    }
}