package br.net.cicerosantos.compras2.model;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validar {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);

    public Validar() {
    }

    public static boolean getVCampo(EditText editText){
        String valor = editText.getText().toString();
        if (!valor.isEmpty() && !valor.equals("") && valor.length() > 5){
            return true;
        }else{
            return false;
        }
    }

    public static boolean getVEmail(EditText editText){
        String email = editText.getText().toString().trim();
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
