package com.dolphinsolutionsvn.dualiutils;


public class FileSetting {
    public enum CommunicationType { PLAIN, PLAIN_WITH_DES_MAC, FULL_DES_ENCIPHERED }
    private int changeAccessKey;
    private int rwAccessKey;
    private int readAccessKey;
    private int writeAccessKey;
    private int numOfKey;
    private int fileSize;
    private CommunicationType communicationType;

    public FileSetting(int numOfKey, CommunicationType communicationType, int readAccessKey, int writeAccessKey, int rwAccessKey, int changeAccessKey, int fileSize) {
        this.numOfKey = numOfKey;
        this.communicationType = communicationType;
        this.fileSize = fileSize;

        if (changeAccessKey >= numOfKey) {
            this.changeAccessKey = 0;
        } else {
            this.changeAccessKey = changeAccessKey;
        }
        if (rwAccessKey >= numOfKey) {
            this.rwAccessKey = 0;
        } else {
            this.rwAccessKey = rwAccessKey;
        }
        if (readAccessKey >= numOfKey) {
            this.readAccessKey = 0;
        } else {
            this.readAccessKey = readAccessKey;
        }
        if (writeAccessKey >= numOfKey) {
            this.writeAccessKey = 0;
        } else {
            this.writeAccessKey = writeAccessKey;
        }
    }

    public FileSetting(int numOfKey, String hex) {
        this.numOfKey = numOfKey;
        String communicationTypeString = hex.substring(0, 2);
        switch (communicationTypeString) {
            case "00":
                this.communicationType = CommunicationType.PLAIN;
                break;
            case "01":
                this.communicationType = CommunicationType.PLAIN_WITH_DES_MAC;
                break;
            case "03":
                this.communicationType = CommunicationType.FULL_DES_ENCIPHERED;
                break;
        }

        String fileSize = hex.substring(6, 12);
        this.fileSize = getFileSizeInt(fileSize);

        this.changeAccessKey = Integer.parseInt(String.valueOf(hex.charAt(3)), 16);
        this.rwAccessKey = Integer.parseInt(String.valueOf(hex.charAt(2)), 16);
        this.readAccessKey = Integer.parseInt(String.valueOf(hex.charAt(4)), 16);
        this.writeAccessKey = Integer.parseInt(String.valueOf(hex.charAt(5)), 16);
    }

    public String toHexString() {
        String setting = "";
        switch (communicationType) {
            case PLAIN:
                setting += "00";
                break;
            case PLAIN_WITH_DES_MAC:
                setting += "01";
                break;
            case FULL_DES_ENCIPHERED:
                setting += "03";
                break;
        }
        setting += Hex.intToHexString(rwAccessKey).charAt(7);
        setting += Hex.intToHexString(changeAccessKey).charAt(7);
        setting += Hex.intToHexString(readAccessKey).charAt(7);
        setting += Hex.intToHexString(writeAccessKey).charAt(7);
        setting += getFileSizeHex(fileSize);

        return setting;
    }

    private String getFileSizeHex(int fileSize) {
        String hexString = Hex.intToHexString(fileSize).substring(2, 8);
        return hexString.substring(4, 6) + hexString.substring(2, 4) + hexString.substring(0, 2);
    }

    private int getFileSizeInt(String fileSizeString) {
        String fileSizeHex = fileSizeString.substring(4, 6) + fileSizeString.substring(2, 4) + fileSizeString.substring(0, 2) + "00";
        return Hex.hexStringToInt(fileSizeHex);
    }
}
