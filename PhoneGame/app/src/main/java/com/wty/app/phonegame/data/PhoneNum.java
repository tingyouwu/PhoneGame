package com.wty.app.phonegame.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PhoneNum{

    private static PhoneNum instance = new PhoneNum();
    private Map<String,Boolean> phoneMap = new LinkedHashMap<>();
    private List<String> phonelist = new ArrayList<>();
    private PhoneNum(){
        phoneMap.put("1",true);
        phoneMap.put("12",false);
        phoneMap.put("123",true);
        phoneMap.put("1234",false);
        phoneMap.put("12345",true);
        phoneMap.put("123456",false);
        phoneMap.put("1234567",true);
        phoneMap.put("12345678",false);
        phoneMap.put("123456789",true);
        phoneMap.put("1234567890",false);
        phoneMap.put("0987654321",true);
        phoneMap.put("987654321",false);
        phoneMap.put("87654321",true);
        phoneMap.put("7654321",false);
        phoneMap.put("654321",true);
        phoneMap.put("54321",false);
        phoneMap.put("4321",true);
        phoneMap.put("321",false);
        phoneMap.put("21",true);

        phonelist.add("1");
        phonelist.add("12");
        phonelist.add("123");
        phonelist.add("1234");
        phonelist.add("12345");
        phonelist.add("123456");
        phonelist.add("1234567");
        phonelist.add("12345678");
        phonelist.add("123456789");
        phonelist.add("1234567890");
        phonelist.add("0987654321");
        phonelist.add("987654321");
        phonelist.add("87654321");
        phonelist.add("7654321");
        phonelist.add("654321");
        phonelist.add("54321");
        phonelist.add("4321");
        phonelist.add("321");
        phonelist.add("21");
    }
    public static PhoneNum getInstance(){
        return instance;
    }

    public String getRandomMoblie(){
        Random random = new Random();
        int randomint = random.nextInt(phonelist.size());
        return phonelist.get(randomint);
    }

    /**
     * @Decription 是否是骚扰电话
     **/
    public boolean isNeedtoRefuse(String mobile){
        return phoneMap.get(mobile);
    }

}
