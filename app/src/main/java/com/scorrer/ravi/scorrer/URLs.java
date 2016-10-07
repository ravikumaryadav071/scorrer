package com.scorrer.ravi.scorrer;

import java.util.HashMap;
import java.util.Map;

public class URLs {

    private static final Map<String, String> urls;

    static {
        urls = new HashMap<String, String>();
        urls.put("LogIn", "http://192.168.0.4/scorrer/login.php");
        urls.put("TokenGenerator", "http://192.168.0.4/scorrer/token_generator.php");
        urls.put("Home", "http://192.168.0.4/scorrer/index.php");
        urls.put("Register", "http://192.168.0.4/scorrer/register.php");
        urls.put("SetSession", "http://192.168.0.4/scorrer/session.php");
        urls.put("DistributorRegistration", "http://192.168.0.4/scorrer/distributor_registration.php");
        urls.put("UpdateDistributorLocation", "http://192.168.0.4/scorrer/update_distributor_location.php");
        urls.put("DistributorsCircle", "http://192.168.0.4/scorrer/distributors_circle.php");
        urls.put("PaymentGateway", "http://192.168.0.4/scorrer/pay_here.php");
        urls.put("Notifications", "http://192.168.0.4/scorrer/notifications.php");
        urls.put("MyOrders", "http://192.168.0.4/scorrer/my_orders.php");
        urls.put("MyAddresses", "http://192.168.0.4/scorrer/my_addresses.php");
        urls.put("TeamNameSugg", "http://192.168.0.4/scorrer/team_name_sugg.php");
    }

    public static String getURL(String key){
        return urls.get(key);
    }

}