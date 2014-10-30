package com.psyngo.michael.symondstimetableplus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetSymondsTimetable extends AsyncTask<String, Void, String> {

    private Context context;
    public GetSymondsTimetable(Context context){
        this.context = context;
    }
    String timetableHTMLstring = "";
    int responseCode;
    String title;

    protected void onPreExecute(){
        ((Activity) context).setProgressBarIndeterminateVisibility(true);
    }

    protected String doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://intranet.psc.ac.uk/login.php");


        try {
            // Create HTTP POST request to Symonds Login page
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("ProcessLoginForm", params[0]));

            nameValuePairs.add(new BasicNameValuePair("username", params[1]));
            nameValuePairs.add(new BasicNameValuePair("password", params[2]));
            nameValuePairs.add(new BasicNameValuePair("signin", params[3]));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request

            HttpResponse response = httpclient.execute(httppost);







                //Execute HTTP Get Request for timetable page
                HttpGet request = new HttpGet("https://intranet.psc.ac.uk/records/student/timetable.php");
                HttpResponse timetableResponse = httpclient.execute(request);
                responseCode = timetableResponse.getStatusLine().getStatusCode();




                    timetableHTMLstring = EntityUtils.toString(timetableResponse.getEntity());
                    Document doc = Jsoup.parse(timetableHTMLstring);
                    title = doc.select("title").text();
            Log.d("myapp", title + " " + responseCode);










        } catch (ClientProtocolException e) {
            Log.e("myapp", e.toString());
            return null;
        } catch (IOException e) {
            Log.e("myapp", e.toString());
            return null;
        }

        return timetableHTMLstring;

    }

    protected void onPostExecute(String arg){
        ((Activity) context).setProgressBarIndeterminateVisibility(false);
        if(responseCode == 200) {
            if (title.equals("Student Intranet | Sign In")){
                Toast.makeText(context, "Username or password incorrect.", Toast.LENGTH_LONG).show();
            }
            else{

                Intent intent = new Intent(context, Timetable.class);
                intent.putExtra("timetableHTML", arg);
                context.startActivity(intent);
            }
        }
        else if(responseCode==0){
            Toast.makeText(context, "Not connected to the Internet", Toast.LENGTH_LONG).show();

        }
        else{
            Toast.makeText(context, "Error: " + responseCode, Toast.LENGTH_LONG).show();
        }
    }


}
