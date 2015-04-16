package fr.epsi.kimsavinfo.qrcheese.Lib_Binary;

/**
 * Created by kimsavinfo on 16/04/15.
 */
public class BinaryManager
{
    public static String binaryToASCII(String _binaryText)
    {
        String asciiText = "";
        char nextChar;

        // [0, 7], [9, 16] ...
        for(int iBinary = 0; iBinary <= _binaryText.length()-8; iBinary += 8)
        {
            nextChar = (char)Integer.parseInt(_binaryText.substring(iBinary, iBinary+8), 2);
            asciiText += nextChar;
        }

        return asciiText;
    }
}
