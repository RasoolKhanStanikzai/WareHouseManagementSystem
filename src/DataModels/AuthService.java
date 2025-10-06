/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DataModels;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author Rasookhan
 */
public class AuthService {
    private static final int ITERATIONS=65536;
    private static final int KEY_LENGTH=256;
    
    // Generating random salt
    public static String getSalt(){
        SecureRandom sr=new SecureRandom();
        byte[] salt=new byte[16];
        sr.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    // Hash password with salt Base64 in or out
    public static String hashPassword(String password,String salt)throws NoSuchAlgorithmException,InvalidKeySpecException{
        PBEKeySpec spec=new PBEKeySpec(password.toCharArray(),Base64.getDecoder().decode(salt),ITERATIONS,KEY_LENGTH);
        SecretKeyFactory skf=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash=skf.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }
    
    // Verify entered password matches storedhash using storedsalt
    public static boolean verifyPassword(String enteredPassword,String storedHash,String storedSalt) throws NoSuchAlgorithmException,InvalidKeySpecException{
        String hashedEntered=hashPassword(enteredPassword,storedSalt);
        return hashedEntered.equals(storedHash);
    }
}
