package com.example.storyteller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.io.FileOutputStream;
import android.content.Context;
import com.android.volley.DefaultRetryPolicy;





public class MainActivity extends AppCompatActivity {
    String Story = "";
    Button Camera, upload;
    int PICK_IMAGE_REQUEST = 111;
    String URL ="http://mahmoudselim.pythonanywhere.com/upload";
    Bitmap bitmap;
    ProgressDialog progressDialog;

    //save
    private EditText mEditName;
    private Button mButtonSave;
    private EditText mEditText;
    private Button mButtonListen;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Camera = findViewById(R.id.Camera);
        upload = findViewById(R.id.upload);

        //save
        mEditName = findViewById(R.id.file_name);
        mButtonSave = findViewById(R.id.Save_file);
        mEditText = findViewById(R.id.edit_text);
        mButtonListen = findViewById(R.id.Listen);

        //opening image chooser option
        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Uploading, please wait...");
                progressDialog.show();

                //converting image to base64 string
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                //sending image to server
                StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Uploaded Successful", Toast.LENGTH_LONG).show();
                        Story = Story + " " + s;
                        mEditText.setText(Story);
                        /**
                         if(s.equals("true")){
                         Toast.makeText(MainActivity.this, "Uploaded Successful", Toast.LENGTH_LONG).show();
                         Story = Story + " " + s;
                         mEditText.setText(Story);
                         }
                         else{
                         Toast.makeText(MainActivity.this, "Some error occurred!", Toast.LENGTH_LONG).show();
                         }
                         **/
                    }
                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MainActivity.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();
                    }
                }) {
                    //adding parameters to send
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("image", imageString);
                        return parameters;
                    }
                };

                RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
                rQueue.add(request);
                request.setRetryPolicy(new DefaultRetryPolicy(
                        40000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                rQueue.add(request);
            }
        });



        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEditName.getText().toString()+".txt";
                String text = mEditText.getText().toString();
                FileOutputStream outputStream;

                try {
                    outputStream = openFileOutput(name, Context.MODE_PRIVATE);
                    outputStream.write(text.getBytes());
                    outputStream.close();
                    Toast.makeText(MainActivity.this, "Saved to " + getFilesDir() + "/" + name, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        mButtonListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            try {
                //getting image from gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                //image.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}