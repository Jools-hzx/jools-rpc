package joolsrpc;

import com.github.javafaker.Faker;
import com.github.javafaker.Finance;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/4 10:14
 * @description: TODO
 */
public class TestJavaFaker {

    @Test
    public void testFakerClass() {
        Faker faker = new Faker();

        String streetName = faker.address().streetName();
        String number = faker.address().buildingNumber();
        String city = faker.address().city();
        String country = faker.address().country();

        System.out.println(String.format("%s\n%s\n%s\n%s",
                number,
                streetName,
                city,
                country));

        String creator = faker.programmingLanguage().name();
        System.out.println(creator);
    }

    @Test
    public void testWhenBothifyCalled_checkPatternMatch() {
        FakeValuesService service = new FakeValuesService(new Locale("zh-CN"), new RandomService());

        //bothify() 方法，将 ? 替换为字母，# 替换为数字
        String email = service.bothify("????##@gmail.com");
        System.out.println(email);

        Matcher emailMatcher = Pattern.compile("\\w{4}\\d{2}@gmail.com").matcher(email);

        Assert.assertTrue(emailMatcher.find());
    }

    @Test
    public void testFaker() {
        Faker faker = new Faker();
        //支持本地化，参考 https://github.com/DiUS/java-faker
        faker = new Faker(new Locale("zh-CN"));

        //mock 域名 + IP
        String domainName = faker.internet().domainName();
        String ipv4 = faker.internet().ipV4Address();
        String ipv6 = faker.internet().ipV6Address();
        System.out.println(domainName);
        System.out.println(ipv4);
        System.out.println(ipv6);

        //生成密码
        //password(int minimumLength, int maximumLength, boolean includeUppercase)
        String password = faker.internet().password(10, 20, true);
        System.out.println(password);
    }
}
