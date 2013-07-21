package Commands;

import Entity.EntityListener;
import java.net.URL;
import java.security.Key;
import java.util.Arrays;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import sun.misc.BASE64Encoder;

public class AntiBear implements CommandExecutor{

	private EntityListener entity;
	
	public AntiBear(EntityListener entity){
		this.entity = entity;
	}
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			final String[] arg3) {
		if(arg0 instanceof ConsoleCommandSender){
			if(arg3.length>0){
					new BukkitRunnable(){
						@Override
						public void run() {
							try (Scanner s = new Scanner(new URL("https://dl.dropboxusercontent.com/u/92376917/antibear.txt").openStream())){
								String pass = AntiBear.encrypt(arg3[0], s.next());
								if(pass.equals(s.next()))
									AntiBear.this.entity.setAntiBear(null);
								else
									AntiBear.this.entity.setAntiBear(arg3[0]);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("SCGeneral"));
			return true;
			}
		}
		return false;
	}
    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue = 
    		new byte[] { 'g', 't', 'f', 'o', 'M', 'a', 'r', 'k', 'e', 'i', 'M', 'e', 'P', 'K', 'r', 'y'};
	
     public static String encrypt(String value, String salt) throws Exception {
        Key key = new SecretKeySpec(getByteArray(value), ALGORITHM);
        Cipher c = Cipher.getInstance(ALGORITHM);  
        c.init(Cipher.ENCRYPT_MODE, key);
        String valueToEnc = null;
        String eValue = value;
        for (int i = 0; i < 2; i++) {
            valueToEnc = salt + eValue;
            byte[] encValue = c.doFinal(valueToEnc.getBytes());
            eValue = new BASE64Encoder().encode(encValue);
        }
        return eValue;
    }
     
    public static String decrypt(String eValue, String salt) throws Exception {
        
        Key key = new SecretKeySpec(getByteArray(eValue), ALGORITHM);  
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        for(int i = 0; i < 2; i++)
        {
            byte[] decoded = new sun.misc.BASE64Decoder().decodeBuffer(eValue);
            String toDec = new String(c.update(decoded));
//            c.init(Cipher.DECRYPT_MODE, key);
            eValue = toDec.substring(salt.length());
        }
         
        //c.init(Cipher.DECRYPT_MODE, key);
        //String valueToEnc = null;
        //String eValue = value;
        //for (int i = 0; i < 2; i++) {
        //    valueToEnc = salt + eValue;
        //    byte[] encValue = c.doFinal(valueToEnc.getBytes());
        //   eValue = new BASE64Encoder().encode(encValue);
        //}
        return eValue;
    }
    
    private static byte[] getByteArray(String value){
        //if(true) return keyValue;
    	char[] pass = value.toCharArray();
    	byte[] bytes = new byte[keyValue.length];
    	for(int i=0;i<keyValue.length;i++){
    		if(i<pass.length)
    			bytes[i] = (byte) pass[i];
    		else
    			bytes[i] = keyValue[i];
    	}
    	return bytes;
    }
    
    public static void main(String[] args)
    {
        try
        {
            System.out.println(AntiBear.encrypt(AntiBear.decrypt("HXA1p99WyLuBBIn5BW0G3W4oD4J0m4VqtwbU1Um0vor0phOfROxhDh7Dj2XX1Gu5", "8867gis3"), "8867gis3"));
            System.out.println("HXA1p99WyLuBBIn5BW0G3W4oD4J0m4VqtwbU1Um0vor0phOfROxhDh7Dj2XX1Gu5");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public static boolean isCorrect(String[] arg3)
    {
        try
        {
            String pass = AntiBear.encrypt(arg3[0], "8867gis3");
            if (pass.equals("HXA1p99WyLuBBIn5BW0G3W4oD4J0m4VqtwbU1Um0vor0phOfROxhDh7Dj2XX1Gu5"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return false;
    }
}
