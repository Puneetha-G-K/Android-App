package suryasoft.test.com.surya_soft;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by puneetha on 10/12/2017.
 */

    public class JsonActivity extends AppCompatActivity {

        private String TAG = MainActivity.class.getSimpleName();
        private ListView lv;

        ArrayList<HashMap<String, String>> itemList;
        String email_addr = "";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.json_activity);

            itemList = new ArrayList<>();
            lv = (ListView) findViewById(R.id.list);

            Bundle extras = getIntent().getExtras();
            if(extras !=null) {
                email_addr  = extras.getString("email");            }

            new GetContacts().execute();
        }

        private class GetContacts extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(JsonActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();


                String json = readFromFile(getApplicationContext());

                Log.e(TAG, "data from cache: " + json);
                if(!json.isEmpty()){
                    parseJsonData(json);
                    displaylist();
                }
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                HttpHandler sh = new HttpHandler();
                // Making a request to url and getting response
                String url = "http://surya-interview.appspot.com/list";
                String jsonStr = getJsonData(email_addr);
                //String jsonStr = getDummyJsonData(email_addr);
                //String jsonStr = "";

                Log.e(TAG, "Response from url: " + jsonStr);

                if(!jsonStr.isEmpty()) {
                    writeToFile(jsonStr, getApplicationContext());
                    Log.e(TAG, "data comitted: " + jsonStr);
                    parseJsonData(jsonStr);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                displaylist();
            }

            private void displaylist(){
                ListAdapter adapter = new SimpleAdapter(JsonActivity.this, itemList,
                        R.layout.listitem, new String[]{ "emailId","imageUrl","firstName","lastName"},
                        new int[]{R.id.emailId, R.id.imageUrl,R.id.firstName, R.id.lastName});
                lv.setAdapter(adapter);
            }


            private void parseJsonData(String jsonStr){
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // Getting JSON Array node
                        JSONArray items = jsonObj.getJSONArray("items");

                        itemList.clear();

                        // looping through All Contacts
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject c = items.getJSONObject(i);
                            String emailId = c.getString("emailId");
                            String imageUrl = c.getString("imageUrl");
                            String firstName = c.getString("firstName");
                            String lastName = c.getString("lastName");

                            // tmp hash map for single item
                            HashMap<String, String> item = new HashMap<>();

                            // adding each child node to HashMap key => value
                            item.put("emailId", emailId);
                            item.put("imageUrl", imageUrl);
                            item.put("firstName", firstName);
                            item.put("lastName", lastName);

                            // adding contact to contact list
                            itemList.add(item);
                        }
                    } catch (final JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                } else {
                    Log.e(TAG, "Couldn't get json from server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Couldn't get json from server. Check LogCat for possible errors!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            private String getJsonData(String email){
                String text = "";
                BufferedReader reader=null;
                String data = "{" + "\"emailId\":" + email + "}";
                try
                {
                    URL url = new URL("http://surya-interview.appspot.com/list");
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write( data );
                    wr.flush();

                    // Get the server response

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        // Append server response in string
                        sb.append(line + "\n");
                    }
                    text = sb.toString();
                    }
                catch(Exception ex)
                {

                }
                finally
                {
                    try
                    {

                       reader.close();
                        }

                   catch(Exception ex) {}
                   }

                return text;
            }

            private String getDummyJsonData(String email){
                return "{\n" +
                        "    \"items\": [\n" +
                        "        {\n" +
                        "            \"emailId\": \"john@doe.com\",\n" +
                        "            \"imageUrl\": \"http//something.com/foo.jpg\",\n" +
                        "            \"firstName\": \"John\",\n" +
                        "            \"lastName\": \"Doe\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"emailId\": \"jane@doe.com\",\n" +
                        "            \"imageUrl\": \"http//something.com/bar.jpg\",\n" +
                        "            \"firstName\": \"Jane\",\n" +
                        "            \"lastName\": \"Doe\"\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}";
            }
        }

    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("json.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("json.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
    }

