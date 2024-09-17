package activation;

import database.Database;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.net.http.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.logging.*;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

/**
 *
 * @author Mustafa Mohamed.
 */
public class Activate {

    private static final Logger LOGGER = Logger.getLogger(Activate.class.getName());

    private static final char[] ENCRYPTION_PASSWORD = {'H', 'S', 'w', 'M', 'o', 'n', 'r', '8', 'z', 'U'};
    private static final char[] ENCRYPTION_SALT = {'c', 'n', 'B', 'Q', 'K', '1', 'Q', 'l', 'h', '0'};

    private static final String ACTIVATION_FILE_PATH
            = Database.APP_DATA_FOLDER + File.separator + Database.APP_NAME + File.separator + "KeenClinic";

    private static boolean checkedActivation = false;
    private static boolean activated = false;

    public static boolean isActivated() {
        // checking for activation each time is expensive, check once only
        if (checkedActivation) {
            return activated;
        } else {
            File file = new File(ACTIVATION_FILE_PATH);
            if (file.exists()) {
                try (Scanner scanner = new Scanner(file)) {
                    String content = "";
                    while (scanner.hasNext()) {
                        content += scanner.next();
                    }
                    content = decrypt(content);
                    String lines[] = content.split("\n");
                    if (lines.length < 2) {
                        return false;
                    }
                    checkedActivation = true;
                    String identifer = lines[0];
                    activated = identifer.equals(getSerialNumber());
                    return activated;
                } catch (Exception ex) {
                    Logger.getLogger(Activate.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    private static String getSerialNumber() {
        var hal = new SystemInfo().getHardware();
        var serialNumber = hal.getComputerSystem().getSerialNumber();
        return serialNumber;
    }

    private static LocalDateTime getInternetTime() throws UnknownHostException, IOException {
        NTPUDPClient timeClient = new NTPUDPClient();
        TimeInfo time = timeClient.getTime(InetAddress.getByName("time-a.nist.gov"));
        time.computeDetails();
        long currentTime = System.currentTimeMillis();
        long time1 = TimeStamp.getNtpTime(currentTime + time.getOffset()).getTime();
        LocalDateTime internetTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time1),
                TimeZone.getDefault().toZoneId());
        return internetTime;
    }

    private static void saveActivationContent(String serialNumber, LocalDate activationDate, LocalDate expiryDate) 
            throws FileNotFoundException, InvalidKeySpecException, UnsupportedEncodingException, 
            UnsupportedEncodingException, UnsupportedEncodingException, UnsupportedEncodingException, 
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException, 
            InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {
        String content = serialNumber + "\n" + activationDate.format(DateTimeFormatter.ISO_DATE) + "\n";
        if (expiryDate != null) {
            content += expiryDate.format(DateTimeFormatter.ISO_DATE) + "\n";
        }
        content = encrypt(content);
        try (PrintWriter writer = new PrintWriter(new File(ACTIVATION_FILE_PATH))) {
            writer.write(content);

        }
    }

    public static boolean checkActivation(String filePath) throws UnknownHostException, IOException {
        File file = new File(filePath);
        if (file.exists()) {
            String content = "";
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNext()) {
                    content += scanner.next();
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Activate.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            try {
                String decrypted = decrypt(content);
                String lines[] = decrypted.split("\n");
                String codeSerialNumber = lines[0];
                LocalDate activationDate = LocalDate.parse(lines[1], DateTimeFormatter.ISO_DATE);
                LocalDateTime internetTime = getInternetTime();
                if (activationDate.isEqual(internetTime.toLocalDate())) {
                    if (codeSerialNumber.equalsIgnoreCase(getSerialNumber())) {
                        boolean forever = Boolean.parseBoolean(lines[2]);
                        LocalDate expiryDate = null;
                        if (!forever) {
                            expiryDate = LocalDate.parse(lines[3], DateTimeFormatter.ISO_DATE);
                        }
                        saveActivationContent(codeSerialNumber, activationDate, expiryDate);
                        checkedActivation = true;
                        activated = true;
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    LOGGER.log(Level.INFO, "Activation date and current date not the same.");
                    return false;
                }

            } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | UnsupportedEncodingException ex) {
                Logger.getLogger(Activate.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } else {
            return false;
        }
    }

    private static String getUniqueIdentifier() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        var serialNumber = hal.getComputerSystem().getSerialNumber();
        var computerSystem = hal.getComputerSystem();
        var firmwareName = computerSystem.getFirmware().getName();
        var model = computerSystem.getModel();
        var hardwareUUID = computerSystem.getHardwareUUID();
        var baseBoardSerialNumber = computerSystem.getBaseboard().getSerialNumber();
        String uniqueInfo = String.format("%s,%s,%s,%s,%s", serialNumber,
                model, firmwareName, hardwareUUID, baseBoardSerialNumber);
        return uniqueInfo;
    }

    public static void setActivated(boolean activated) throws IOException,
            UnsupportedEncodingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {

        String content = "";
        content += getUniqueIdentifier() + "\n";
        content += String.valueOf(activated) + "\n";
        content += LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + "\n";

        var encrypted = encrypt(content);

        // overwrite existing file
        File file = new File(ACTIVATION_FILE_PATH);
        file.createNewFile();
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(encrypted);
        }

        Activate.activated = activated;

    }

    //https://howtodoinjava.com/java/java-security/aes-256-encryption-decryption/
    private static final int KEY_LENGTH = 256;
    private static final int ITERATION_COUNT = 65536;

    private static String decrypt(String encryptedText)
            throws NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
        String secretKey = String.valueOf(ENCRYPTION_PASSWORD);
        String salt = String.valueOf(ENCRYPTION_SALT);

        byte[] encryptedData = Base64.getDecoder().decode(encryptedText);
        byte[] iv = new byte[16];
        System.arraycopy(encryptedData, 0, iv, 0, iv.length);
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);

        byte[] cipherText = new byte[encryptedData.length - 16];
        System.arraycopy(encryptedData, 16, cipherText, 0, cipherText.length);

        byte[] decryptedText = cipher.doFinal(cipherText);
        return new String(decryptedText, "UTF-8");

    }

    private static String encrypt(String plainText)
            throws UnsupportedEncodingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {

        String secretKey = String.valueOf(ENCRYPTION_PASSWORD);
        String salt = String.valueOf(ENCRYPTION_SALT);

        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);

        byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF-8"));
        byte[] encryptedData = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, encryptedData, 0, iv.length);
        System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(encryptedData);
        //return encryptedData;

    }
}
