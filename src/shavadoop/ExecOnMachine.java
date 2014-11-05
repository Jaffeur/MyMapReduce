package shavadoop;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class ExecOnMachine extends Thread{  //extend thread
	private String ip;
	private String command;
	
	public ExecOnMachine(String ip, String command){ //constructeur pour passer les parametres
		
		this.command = command;
		this.ip = ip;
	}
	/**
	 * method to execute command on a given ip adress
	 * **/
	public void run(){ //méthode du thread, doit toujour s'appeller run!!
		
		String private_key_repository = "/cal/homes/adupont/.ssh/id_dsa";  //dossier de clée pour l'authentification
		String user = "adupont"; //user name
		//String host = "c130-25"; //IP  or machine name
		
		try{
			
			//create session
			JSch jsch = new JSch();  //create new configuration for session
			jsch.addIdentity(private_key_repository, "passphrase"); // Associer clé à la session jsch
			Session session = jsch.getSession(user, this.ip, 22); //Créer nouvelle session, port 22 = ssh
			
			//To avoid Pop UPs
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			
			//param session
			UserInfo ui = new MyUserInfo();
			session.setUserInfo(ui); //link session to a user
			session.connect(); //connection!
			
			//execut command
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(this.command);	
			
			//get result
			channel.setInputStream(null);
			((ChannelExec)channel).setErrStream(System.err);
			InputStream in=channel.getInputStream();
			channel.connect();  //connexion
			
			//Show result on terminal
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String inputLine;
			while ((inputLine = br.readLine()) != null)
			    System.out.println("@" + this.ip + "#\t" + inputLine);
			in.close();
			
			
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	/**
	 * class with user info (nothing to understand there)
	 * **/
	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive{public String getPassword(){ return null; }public boolean promptYesNo(String str){  Object[] options={ "yes", "no" };int foo=JOptionPane.showOptionDialog(null,str,"Warning",JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,null, options, options[0]);
	return foo==0; } String passphrase; JTextField passphraseField=(JTextField)new JPasswordField(20); public String getPassphrase(){ return passphrase; }public boolean promptPassphrase(String message){Object[] ob={passphraseField}; int result=JOptionPane.showConfirmDialog(null, ob, message,
	JOptionPane.OK_CANCEL_OPTION);if(result==JOptionPane.OK_OPTION){passphrase=passphraseField.getText(); return true;}else{ return false; } }public boolean promptPassword(String message){ return true; }public void showMessage(String message){JOptionPane.showMessageDialog(null, message);}
	final GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0);private Container panel;public String[] promptKeyboardInteractive(String destination, String name,String instruction,String[] prompt,boolean[] echo){panel = new JPanel();
	panel.setLayout(new GridBagLayout());gbc.weightx = 1.0;gbc.gridwidth = GridBagConstraints.REMAINDER;gbc.gridx = 0;panel.add(new JLabel(instruction), gbc);gbc.gridy++;gbc.gridwidth = GridBagConstraints.RELATIVE;JTextField[] texts=new JTextField[prompt.length];
	for(int i=0; i<prompt.length; i++){gbc.fill = GridBagConstraints.NONE;gbc.gridx = 0;gbc.weightx = 1; panel.add(new JLabel(prompt[i]),gbc);gbc.gridx = 1;gbc.fill = GridBagConstraints.HORIZONTAL;gbc.weighty = 1;if(echo[i]){texts[i]=new JTextField(20);}else{texts[i]=new JPasswordField(20);}panel.add(texts[i], gbc);gbc.gridy++;}
	if(JOptionPane.showConfirmDialog(null, panel,destination+": "+name,JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.OK_OPTION){String[] response=new String[prompt.length];for(int i=0; i<prompt.length; i++){response[i]=texts[i].getText();}return response; }else{return null;} }}
}
