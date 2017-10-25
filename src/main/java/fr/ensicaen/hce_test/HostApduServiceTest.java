package fr.ensicaen.hce_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import com.example.utils.StringUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by Joan on 02/12/2015.
 */
public class HostApduServiceTest extends HostApduService {
    public static final String TAG = "HceTest";

    public final static String COUNTER_PREF = "counter";
    public final static String AMOUNT = "amount";
    //protected final static byte[] HCE = StringUtils.convertASCIIStringToByteArray("HCE");
    protected final static byte[] HCE = { 0x48, 0x43, 0x45 };
    protected final static byte[] SW_OK = { (byte)0x90, 0x00 };
    protected final static byte[] SW_INS_NOT_SUPPORTED = { 0x6D, 0x00 };
    protected final static byte[] SW_WRONG_LE_FIELD = { 0x6C, 0x04 };
    protected final static byte[] SW_UNKNOWN = { 0x6F, 0x00 };

    private short counter;
    private SharedPreferences prefs;

    public HostApduServiceTest() {
        super();
        // Can't access SharedPreferences here because there is no context at this time
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // we can access the context after super.onCreate so we can access SharedPreferences
        prefs = getSharedPreferences("hce_test", 0);
        counter = (short)prefs.getInt(COUNTER_PREF, 0);
    }

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        byte [] ret = null;
        Log.i(TAG, "Received APDU: " + StringUtils.convertByteArrayToHexString(commandApdu));
        switch(commandApdu[1]) {
            case (byte)0xA4:
                Log.d(TAG, "This is a select apdu");
                ret = ConcatArrays(HCE, SW_OK);
                break;
            case (byte)0x10:
                Log.d(TAG, "This is a get counter");
                byte [] count_resp = convertShortToByteArray(incrementCounter());
                ret =  ConcatArrays(count_resp, SW_OK);
                break;
            case (byte)0x20:
                Log.d(TAG, "This is a transaction");
                if (commandApdu[4] != 4) {
                    return SW_WRONG_LE_FIELD;
                }
                int amount = convertByteArrayToInt(commandApdu, (short)5);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(AMOUNT, amount);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                ret = SW_OK;
                break;
            default:
                ret = SW_INS_NOT_SUPPORTED;
                break;
        }
        Log.i(TAG, "Response = "+StringUtils.convertByteArrayToHexString(ret));
        return ret;
    }

    @Override
    public void onDeactivated(int reason) {

    }

    /**
     * Increment the counter and make it persistent
     * @return the new counter value
     */
    private short incrementCounter() {
        counter++;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(COUNTER_PREF, counter);
        editor.commit();
        return counter;
    }

    /**
     * Utility method to concatenate two byte arrays.
     * @param first  First array
     * @param rest  Any remaining arrays
     * @return Concatenated copy of input arrays
     */
    public static byte[] ConcatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    /**
     * Utility method to create a byte array from a short.
     * @param s the short in question
     * @return a newly allocated byte array
     */
    public static byte[] convertShortToByteArray(short s) {
        byte [] ret = new byte[2];
        ret[0] = (byte)(s >> 8);
//        ret[1] = (byte)(s & (short)0x00FF);
        ret[1] = (byte)s;
        return ret;
    }

    /**
     * Utility method to convert 4 bytes from an array to an integer.
     * The 4 bytes' order must be big-endian.
     * @param array source byte array
     * @param offset beginning offset to the 4 bytes
     * @return the converted integer
     */
    public static int convertByteArrayToInt(byte[] array, short offset) {
        return ByteBuffer.wrap(array, offset, 4).getInt();
//        int i = array[offset++];
//        i = i*10 + array[offset++];
//        i = i*10 + array[offset++];
//        i = i*10 + array[offset++];
//        return i;
    }

    /**
     * Utility method to create a byte array from an integer.
     * The 4 bytes' order is big-endian.
     * @param i integer to convert
     * @return a newly allocated byte array
     */
    public static byte[] convertIntToByteArray(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
//        return new byte[] {
//                (byte)(i >> 24),
//                (byte)(i >> 16),
//                (byte)(i >> 8),
//                (byte)i };
    }
}
