package com.dolphinsolutionsvn.dualiutils;

public class TransparentResponse {
    public int readerResponseCode;
    public int desfireResponseCode;
    public String desfireResponseData;

    public TransparentResponse(int readerResponse, int desfireResponse, String desfireResponseData)
    {
        this.readerResponseCode = readerResponse;
        this.desfireResponseCode = desfireResponse;
        this.desfireResponseData = desfireResponseData;
    }

    public static boolean VerifyResponse(TransparentResponse transparentResponse)
    {
//        if (transparentResponse.ReaderResponseCode == ReaderResponse.DE_OK && transparentResponse.desfireResponseCode == DesfireResponse.OPERATION_OK)
//        {
//            return true;
//        }
//
        return false;
    }
}
