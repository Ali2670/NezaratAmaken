// Decompiled by:       Fernflower v0.8.6
// Date:                30.11.2016 21:13:24
// Copyright:           2008-2012, Stiver
// Home page:           http://www.neshkov.com/ac_decompiler.html

package rabbitMQ;

import java.util.Iterator;
import java.util.Properties;

public class RabbitPropsHolder {

    private static final Properties PROPS = new Properties();


    static {
        try {
            PROPS.load(RabbitPropsHolder.class.getClassLoader().getResourceAsStream("rabbitmq.properties"));
        } catch (Exception var1) {
            throw new RuntimeException(var1);
        }
    }

    public static Properties getProps() {
        return PROPS;
    }

    public static Properties getPropsWithPrefix(String var0) {
        Properties var1 = new Properties();
        Iterator var2 = PROPS.keySet().iterator();

        while (var2.hasNext()) {
            String var3 = (String) var2.next();
            if (var3.startsWith(var0)) {
                var1.setProperty(var3.substring(var0.length()), PROPS.getProperty(var3));
            }
        }

        return var1;
    }

    public static String property(String var0) {
        return PROPS.getProperty(var0);
    }
}
