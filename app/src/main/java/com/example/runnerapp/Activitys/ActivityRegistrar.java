package com.example.runnerapp.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.runnerapp.Activity_login;
import com.example.runnerapp.Clases.Usuario;
import com.example.runnerapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.UUID;

public class ActivityRegistrar extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    AwesomeValidation awesomeValidation;
    FirebaseAuth firebaseAuth;


    EditText txtNombre, txtEdad, txtEmail, txtPassword2, txtPassword;
    Spinner spnpais;
    RadioButton rbMasculino, rbFemenino;
    Button btnGuardar;
    TextView txtGenero;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        btnGuardar = (Button) findViewById(R.id.btnEditar);
        txtNombre = (EditText) findViewById(R.id.txtNombrePerfil);
        txtEdad = (EditText) findViewById(R.id.txtEdadPerfil);
        spnpais = (Spinner)findViewById(R.id.spnPais);
        txtEmail = (EditText) findViewById(R.id.txtEmailPerfil);
        txtPassword2 = (EditText) findViewById(R.id.txtPasword2);
        rbMasculino = (RadioButton) findViewById(R.id.rbMaculino);
        rbFemenino = (RadioButton) findViewById(R.id.rbFemenino);
        txtPassword = (EditText)findViewById(R.id.txtPasword);
        txtGenero = (TextView) findViewById(R.id.txtGenero);

        inicialisarFirebase();

        firebaseAuth = FirebaseAuth.getInstance();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this,R.id.txtEmailPerfil, Patterns.EMAIL_ADDRESS,R.string.invalid_mail);
        awesomeValidation.addValidation(this,R.id.txtPasword,".{6,}",R.string.invalid_password);


        Spinner spinner = (Spinner) findViewById(R.id.spnPais);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, R.layout.spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = txtEmail.getText().toString();
                String pass = txtPassword.getText().toString();

                if(awesomeValidation.validate()){
                    firebaseAuth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Usuario creado con exito", Toast.LENGTH_SHORT).show();
                                validarPass();
                                finish();
                            }else {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                dameToastdeerror(errorCode);
                            }
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(), "Completa todos los datos..!!", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {

            case R.id.rbMaculino:
                if (checked)
                    txtGenero.setText("Masculino");
                    rbFemenino.setChecked(false);
                    break;
            case R.id.rbFemenino:
                if (checked)
                    txtGenero.setText("Femenino");
                    rbMasculino.setChecked(false);
                    break;
        }
    }

    private void inicialisarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }


    public void agregar() {
        String nombre = txtNombre.getText().toString();
        String edad = txtEdad.getText().toString();
        String genero = txtGenero.getText().toString();
        String email = txtEmail.getText().toString();
        String password = txtPassword2.getText().toString();
        String pais = spnpais.getSelectedItem().toString();
        Usuario u = new Usuario();
        u.setUid(UUID.randomUUID().toString());
        u.setNombres(nombre);
        u.setEdad(edad);
        u.setGenero(genero);
        u.setEmail(email);
        u.setPais(pais);
        u.setPassword(password);
        databaseReference.child("Usuario").child(u.getUid()).setValue(u);
        //Toast.makeText(getApplicationContext(), "OPERACION EXITOSA!!", Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent(getApplicationContext(), Activity_login.class);
        //startActivity(intent);
    }

    public void validarPass(){
        String pw1 = txtPassword.getText().toString();
        String pw2 = txtPassword2.getText().toString();
        if(pw1.equals(pw2)){
            if(txtNombre.getText().toString().equals("")||txtEdad.getText().toString().equals("")||txtGenero.getText().toString().
                    equals("")||txtEmail.getText().toString().equals("")||txtPassword.getText().toString().equals("")||
                    txtPassword2.getText().toString().equals("")){
                ValidarDatos();
            }else{
                agregar();
                clear();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Atencion, los passsword no coinciden", Toast.LENGTH_SHORT).show();
        }
    }

    public void clear(){
        txtNombre.setText("");
        txtEdad.setText("");
        txtEmail.setText("");
        txtGenero.setText("");
        txtPassword.setText("");
        txtPassword2.setText("");
    }

    public void ValidarDatos(){
        txtNombre.setError("Required");
        txtEdad.setError("Required");
        txtGenero.setError("Required");
        txtEmail.setError("Required");
        txtPassword.setError("Required");
        txtPassword2.setError("Required");
    }
    private void dameToastdeerror(String error) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(ActivityRegistrar.this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(ActivityRegistrar.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(ActivityRegistrar.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(ActivityRegistrar.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                txtEmail.setError("La dirección de correo electrónico está mal formateada.");
                txtEmail.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(ActivityRegistrar.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                txtPassword.setError("la contraseña es incorrecta ");
                txtPassword.requestFocus();
                txtPassword.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(ActivityRegistrar.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(ActivityRegistrar.this,"Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(ActivityRegistrar.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(ActivityRegistrar.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                txtEmail.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                txtEmail.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(ActivityRegistrar.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(ActivityRegistrar.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(ActivityRegistrar.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(ActivityRegistrar.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(ActivityRegistrar.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(ActivityRegistrar.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(ActivityRegistrar.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                txtPassword.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                txtPassword.requestFocus();
                break;

        }

    }



}