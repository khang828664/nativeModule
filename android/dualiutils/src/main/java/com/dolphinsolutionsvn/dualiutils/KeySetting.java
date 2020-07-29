package com.dolphinsolutionsvn.dualiutils;

public class KeySetting
{
    public enum ChangeKeyAccess { MASTER_REQUIRE, SPECIFY_KEY, SAME_KEY, ALL_FROZEN }
    public enum CryptoMethod { DES, THREEKTHREEDES, AES }
    private ChangeKeyAccess access;
    private int specifyKey = -1;
    private boolean configChange;
    private boolean freeCD;
    private boolean freeDirList;
    private boolean masterKeyChange;

    private CryptoMethod cryptoMethod;
    private boolean fileIdentifer;
    private String rfu;
    public int numOfKey;

    public KeySetting(ChangeKeyAccess access, int specifyKey, boolean configChange, boolean freeCD, boolean freeDirList, boolean masterKeyChange, CryptoMethod method, boolean fileIdentifer, String rfu, int numOfKey)
    {

        this.access = access;
        this.specifyKey = specifyKey;
        this.configChange = configChange;
        this.freeCD = freeCD;
        this.freeDirList = freeDirList;
        this.masterKeyChange = masterKeyChange;
        this.cryptoMethod = method;
        this.fileIdentifer = fileIdentifer;
        this.rfu = rfu;
        this.numOfKey = numOfKey;
    }

    public String toHexString()
    {
        String setting = "";

        switch (access)
        {
            case MASTER_REQUIRE:
                setting += "0000";
                break;
            case SPECIFY_KEY:
                setting += Integer.toBinaryString(specifyKey);
                break;
            case SAME_KEY:
                setting += "1110";
                break;
            case ALL_FROZEN:
                setting += "1111";
                break;
        }

        setting += configChange ? "1" : "0";
        setting += freeCD ? "1" : "0";
        setting += freeDirList ? "1" : "0";
        setting += masterKeyChange ? "1" : "0";

        switch (cryptoMethod)
        {
            case DES:
                setting += "00";
                break;
            case THREEKTHREEDES:
                setting += "01";
                break;
            case AES:
                setting += "10";
                break;
        }

        setting += fileIdentifer ? "1" : "0";
        setting += rfu;
        setting += Integer.toBinaryString(numOfKey);
        return Hex.intToHexString(Integer.parseInt(setting, 2)).substring(4, 8);
    }

    public KeySetting(String hex)
    {
        String bin = Integer.toBinaryString(Integer.parseInt(hex, 16));
        if (bin.length() < 16) {

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16 - bin.length(); ++i) {
                sb.append("0");
            }
            sb.append(bin);
            bin = sb.toString();
        }
        String access = bin.substring(0, 4);
        String cryptoMethod = bin.substring(8, 10);
        String numOfKey = bin.substring(12, 16);

        switch(access)
        {
            case "0000":
                this.access = KeySetting.ChangeKeyAccess.MASTER_REQUIRE;
                break;
            case "1110":
                this.access = KeySetting.ChangeKeyAccess.SAME_KEY;
                break;
            case "1111":
                this.access = KeySetting.ChangeKeyAccess.ALL_FROZEN;
                break;
            default:
                this.access = KeySetting.ChangeKeyAccess.SPECIFY_KEY;
                this.specifyKey = Integer.parseInt(access, 2);
                break;
        }

        this.configChange = bin.charAt(4) == '1';
        this.freeCD =  bin.charAt(5) == '1';
        this.freeDirList =  bin.charAt(6) == '1';
        this.masterKeyChange =  bin.charAt(7) == '1';

        switch (cryptoMethod)
        {
            case "00":
                this.cryptoMethod = CryptoMethod.DES;
                break;
            case "01":
                this.cryptoMethod = CryptoMethod.THREEKTHREEDES;
                break;
            case "10":
                this.cryptoMethod = CryptoMethod.AES;
                break;
        }

        this.fileIdentifer =  bin.charAt(10) == '1';
        this.rfu = Character.toString(bin.charAt(11));
        this.numOfKey = Integer.parseInt(numOfKey, 2);
    }

    public byte computeFlag()
    {
        String flag = "00000";

        switch (cryptoMethod)
        {
            case DES:
                flag += "011";
                break;
            case THREEKTHREEDES:
            case AES:
                flag += "101";
                break;
        }
        return Hex.hexStringToByte(Hex.intToHexString(Integer.parseInt(flag, 2)));
    }
}