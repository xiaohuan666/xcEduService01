import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;

public class testException {
    public static void main(String[] args) {
        ExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
    }
}
