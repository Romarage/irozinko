package com.shpp.p2p.cs.irozinko.assignment14;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Class unarchive file
 */
class Unarchive {
    /**
     *Method unpacking inputted file to output
     * @param input name of inputted file
     * @param output name of outputted file
     */
    static void unarchive(String input, String output) {
        try(FileInputStream fis = new FileInputStream(input);
            FileWriter fw = new FileWriter(output)){
            //get size of table in bytes
            byte[] sizeOfTableBytes = new byte[4];
            //get size of data in bytes
            byte[] sizeOfDataBytes = new byte [8];
            //get size of table in bytes
            fis.read(sizeOfTableBytes);
            int sizeOfTable = byteArrayToInt(sizeOfTableBytes);
            //get size of data in bytes
            fis.read(sizeOfDataBytes);
            //get table of bits
            byte[] charTableBytes = new byte[sizeOfTable];
            fis.read(charTableBytes);
            HashMap<String,Character> charTable = getCharTableFromBytes(charTableBytes);
            //replace byte data with it values
            byte[] dataBytes = new byte[fis.available()];
            fis.read(dataBytes);
            char [] outputUnArchive = makeOutputData(dataBytes, charTable);
            fw.write(outputUnArchive);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     *Method replacing bytes values with chars
     * @param dataBytes array of bytes - archived data
     * @param charTable char table of values
     * @return char array ready to be saved in file
     */
    private static char[] makeOutputData(byte[] dataBytes, HashMap<String, Character> charTable) {
        char[] result;
        String key =(String) charTable.keySet().toArray()[0];
        int bitLength = key.length();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < dataBytes.length; i ++){
            byte b = dataBytes[i];
            StringBuilder currentByte;
            if (i == dataBytes.length - 1){
                currentByte = new StringBuilder(Integer.toBinaryString((b + 256) % 256));
                while ((sb.length() + currentByte.length()) % bitLength != 0  ){
                    currentByte.insert(0,"0");
                }
            } else {
                currentByte = new StringBuilder(String.format("%8s",
                        Integer.toBinaryString((b + 256) % 256)).replace(' ', '0'));
            }
            sb.append(currentByte);
        }

        result = new char[sb.length() / bitLength];
        int i = 0;
        while(sb.length() > 0){
            result[i] = charTable.get(sb.substring(0, bitLength));
            sb.delete(0,bitLength);
            i++;
        }
        return result;
    }

    /**Gets char table from byte array
     * @param charTableBytes byte array contains bytes of char table
     * @return HashMap with keys - binary sequence and values - char
     */
    private static HashMap<String, Character> getCharTableFromBytes(byte[] charTableBytes) {
        HashMap<String, Character> charTable = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        for (byte b : charTableBytes){
            sb.append((char)b);
        }
        int bitLength = sb.indexOf(",") - sb.indexOf("=");

        for (int i = 0; i < sb.length(); i++){
            if (sb.charAt(i) == '='){
                charTable.put(sb.substring(i+1, i+ bitLength), sb.charAt(i-1));
            }
        }
        return charTable;
    }

    /**Converting byte array to integer
     * @param array  of bytes
     * @return integer stored inside array
     */
    private static int byteArrayToInt(byte[] array ) {
        int result = 0;
        for (byte b : array) {
            result = result << 8;
            result = result|(b & 255);
        }
        return result;
    }


}
