package Commands;

import java.net.URL;
import java.security.Key;
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
import Entity.EntityListener;

public class AntiBear implements CommandExecutor{

	private final EntityListener entity;

	public AntiBear(final EntityListener entity){
		this.entity = entity;
	}

	@Override
	public boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2,
			final String[] arg3) {
		if(arg0 instanceof ConsoleCommandSender)
			if(arg3.length>0){
				new BukkitRunnable(){
					@Override
					public void run() {
						try (Scanner s = new Scanner(new URL("https://dl.dropboxusercontent.com/u/92376917/antibear.txt").openStream())){
							final String pass = AntiBear.encrypt(arg3[0], s.next());
							if(pass.equals(s.next()))
								AntiBear.this.entity.setAntiBear(null);
							else
								AntiBear.this.entity.setAntiBear(arg3[0]);
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}
				}.runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("SCGeneral"));
				return true;
			}
		return false;
	}
	private static final String ALGORITHM = "AES";
	private static final byte[] keyValue =
			new byte[] { 'g', 't', 'f', 'o', 'M', 'a', 'r', 'k', 'e', 'i', 'M', 'e', 'P', 'K', 'r', 'y'};

	public static String encrypt(final String value, final String salt) throws Exception {
		final Key key = new SecretKeySpec(AntiBear.getByteArray(value), AntiBear.ALGORITHM);
		final Cipher c = Cipher.getInstance(AntiBear.ALGORITHM);
		c.init(Cipher.ENCRYPT_MODE, key);
		String valueToEnc = null;
		String eValue = value;
		for (int i = 0; i < 2; i++) {
			valueToEnc = salt + eValue;
			final byte[] encValue = c.doFinal(valueToEnc.getBytes());
			eValue = new BASE64Encoder().encode(encValue);
		}
		return eValue;
	}

	public static String decrypt(String eValue, final String salt) throws Exception {

		final Key key = new SecretKeySpec(AntiBear.getByteArray(eValue), AntiBear.ALGORITHM);
		final Cipher c = Cipher.getInstance(AntiBear.ALGORITHM);
		c.init(Cipher.DECRYPT_MODE, key);
		for(int i = 0; i < 2; i++)
		{
			final byte[] decoded = new sun.misc.BASE64Decoder().decodeBuffer(eValue);
			final String toDec = new String(c.update(decoded));
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

	private static byte[] getByteArray(final String value){
		//if(true) return keyValue;
		final char[] pass = value.toCharArray();
		final byte[] bytes = new byte[AntiBear.keyValue.length];
		for(int i=0;i<AntiBear.keyValue.length;i++)
			if(i<pass.length)
				bytes[i] = (byte) pass[i];
			else
				bytes[i] = AntiBear.keyValue[i];
		return bytes;
	}

	public static void main(final String[] args)
	{
		try
		{
			System.out.println(AntiBear.encrypt(AntiBear.decrypt("HXA1p99WyLuBBIn5BW0G3W4oD4J0m4VqtwbU1Um0vor0phOfROxhDh7Dj2XX1Gu5", "8867gis3"), "8867gis3"));
			System.out.println("HXA1p99WyLuBBIn5BW0G3W4oD4J0m4VqtwbU1Um0vor0phOfROxhDh7Dj2XX1Gu5");
		}
		catch(final Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public static boolean isCorrect(final String[] arg3)
	{
		try
		{
			final String pass = AntiBear.encrypt(arg3[0], "8867gis3");
			if (pass.equals("HXA1p99WyLuBBIn5BW0G3W4oD4J0m4VqtwbU1Um0vor0phOfROxhDh7Dj2XX1Gu5"))
				return true;
			else
				return false;
		}
		catch(final Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}
}
