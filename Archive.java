package com.shpp.p2p.cs.irozinko.assignment14;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Class archive file
 */
 class Archive {

    /**Method adds to archive nput file to output file
     * @param input name file
     * @param output name of file
     */
    public static void archive(String input, String output) {
        long startTime = System.currentTimeMillis();
        //selecting unique chars from text file
        Character[] charsUnique = getUniqueChars(input);
        //for each unique char make bit sequence
        String[] bits = makeBitsArray(charsUnique);
        //using to write bytes and bits sequence to table
        HashMap<Character, String > charTable = makeCharTable(charsUnique, bits);
        //size of inputted file
        long sizeOfInputFile = new File(input).length();

        try (FileInputStream fis = new FileInputStream(input); FileOutputStream fos = new FileOutputStream(output)){

            //read all file to byte array\

            byte[] buffer =  new byte[fis.available()];
            fis.read(buffer);
            //replace all chars in buffer array to binary sequences
            byte[] outputArchive = archiveBuffer(buffer, charTable);
            //write size of table to output file
            int sizeOfTable = charTable.toString().length();
            fos.write(intToByteArray(sizeOfTable));
            //write size of data of inputted file
            fos.write(longToByteArray(sizeOfInputFile));
            //write table to output file
            fos.write(charTable.toString().getBytes());
            //write file data to output file
            fos.write(outputArchive);

            long stopTime = System.currentTimeMillis();
            long archiveTime = stopTime - startTime;
            long sizeOfOutputFile = new File(output).length();
            showStatistics(input, output, sizeOfInputFile,sizeOfOutputFile, archiveTime);


        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void showStatistics(String input, String output, long sizeOfInputFile, long sizeOfOutputFile, long archiveTime) {
        System.out.println("Input file: [" + input + "]");
        System.out.println("Output file: [" + output + "]");
        System.out.println("Size of inputted file: [" + sizeOfInputFile + "] bytes");
        System.out.println("Size of outputted file: [" + sizeOfOutputFile + "] bytes");
        System.out.println("Compression efficiency: [" + Math.floor((double)sizeOfOutputFile / sizeOfInputFile * 100) + "]%");
        System.out.println("Time to pack file is: [" + archiveTime / 1000.0 + "] sec" );
    }

    /**Making char table for each unique char it unique bit sequence
     * @param charsUnique array of unique chars in file
     * @param bits bit sequences
     * @return hashmap as a char table
     */
    private static HashMap<Character, String> makeCharTable(Character[] charsUnique, String[] bits) {
        HashMap<Character, String> charTable= new HashMap<>();
        for(int i = 0; i < bits.length; i++){
            charTable.put(charsUnique[i],bits[i]);
        }
        return charTable;
    }

    /**Converting long to byte array
     * @param i number in long format
     * @return array of bytes
     */
    private static byte[] longToByteArray(long i) {
        return new byte[]{
                (byte) (i >>> 56),
                (byte) (i >>> 48),
                (byte) (i >>> 40),
                (byte) (i >>> 32),
                (byte) (i >>> 24),
                (byte) (i >>> 16),
                (byte) (i >>> 8),
                (byte) i
        };
    }


    /**Converting int to byte array
     * @param i number in int format
     * @return array of bytes
     */
    private static byte[] intToByteArray(int i) {
        return new byte[] {
                (byte) (i >>> 24),
                (byte) (i >>> 16),
                (byte) (i >>> 8),
                (byte) i
        };
    }

    /**Replacing all bytes in buffer array with bits sequences getting from char table
     * @param buffer array of bytes from inputted file
     * @param charTable hashmap with chars and its bits sequence
     * @return byte array
     */
    private static byte[] archiveBuffer(byte[] buffer, HashMap<Character, String> charTable) {
        StringBuilder string = new StringBuilder();
        byte[] result;
        //replace all bytes in buffer to value of "0" and "1"
        for (byte current : buffer) {
            string.append(charTable.get((char)current));
        }
        //set the length ob byte array
        result = string.length() % 8 == 0 ?  new byte[(string.length()/8)] :  new byte[(string.length()/8)+1];
        int j = 0;
        int endOfByte = 8;
        //split string to 8 chars and push it to bytes
        while(string.length() > 0) {
            if (string.length() <= endOfByte){
                endOfByte = string.length();
            }
            result[j] = (byte) Integer.parseInt(string.substring(0, endOfByte),2);
            string.delete(0, endOfByte);
            j++;
        }
        return result;
    }


    /** Making  bits sequence for each symbol in charsUnique array
     * @param charsUnique array of bytes
     * @return String array composed of "0" and "1"
     */
    private static String[] makeBitsArray(Character[] charsUnique) {
        String[] bits = new String[charsUnique.length];
        int max = 0;
        for (int i = 0; i < charsUnique.length; i++){
            bits[i] = Integer.toBinaryString(i);
            if (bits[i].length() > max){
                max = bits[i].length();
            }
        }

        for (int i = 0; i < bits.length; i++){
            StringBuilder sb = new StringBuilder(bits[i]);
            while (sb.length() < max){
                sb.insert(0,0);
            }
            bits[i] = sb.toString();
        }
        return bits;
    }


    /** Method selecting unique symbols in text file and make byte array of them
     * @param input name of inputted text file
     * @return byte array of unique symbols
     */
    private static Character[] getUniqueChars(String input) {
        Character[] result;
        Set<Character> chars = new HashSet<>();
        try {
            FileInputStream fis = new FileInputStream(input);
            int current;
            while ((current = fis.read()) != -1) {
                chars.add((char)current);
            }
            fis.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        result = new Character [chars.size()];
        int i = 0;
        for(Character c : chars){
            result[i] = c;
            i++;
        }
        return  result;
    }

}
