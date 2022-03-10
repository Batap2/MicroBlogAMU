import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String args[]){
        String str = "REPUBLISH author:@RepublishMan msg_id:1";

        Pattern p = Pattern.compile("^REPUBLISH author:(@\\w+) msg_id:(\\d+)$");
        Matcher m = p.matcher(str);

        System.out.println(m.matches());
        System.out.println(m.group(1));
        System.out.println(m.group(2));
        System.out.println(m.group(3));
    }
}
