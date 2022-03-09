import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String args[]){
        Pattern p = Pattern.compile("^REPLY author:(@\\w+) reply_to_id:(\\d+)\\r\\n(.+)$");
        Matcher m = p.matcher("REPLY author:@skand reply_to_id:1\r\nsalut a tous");
        System.out.println(m.matches());
        System.out.println(m.group(1));
        System.out.println(m.group(2));
        System.out.println(m.group(3));
    }
}
