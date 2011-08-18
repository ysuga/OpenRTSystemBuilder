
import java.io.File;

import net.ysuga.rtsbuilder.RTSystemBuilder;
import net.ysuga.rtsystem.profile.RTSystemProfile;


public class RTSProfileTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
		
			RTSystemProfile profile = new RTSystemProfile(new File("FungiEaterSystem.xml"));

			
			RTSystemBuilder.buildRTSystem(profile);
			RTSystemBuilder.deactivateRTCs(profile);

			RTSystemBuilder.resetRTCs(profile);
			
			RTSystemBuilder.activateRTCs(profile);
			
			Thread.sleep(10000);
			
			RTSystemBuilder.resetRTCs(profile);
			RTSystemBuilder.deactivateRTCs(profile);
			RTSystemBuilder.destroyRTSystem(profile);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.exit(0);
	}

}
