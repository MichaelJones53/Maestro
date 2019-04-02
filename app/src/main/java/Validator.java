import android.text.TextUtils;
import android.util.Patterns;

public class Validator {

    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidUsername(String username)
    {

        return true;
    }

}
