package org.commons.sendmail;

import java.util.Properties;

import org.apache.velocity.texen.util.PropertiesUtil;
import org.utils.MyUtil;

public class Mail {
	private static final Properties p;

	private SMTPExtAppender smtpmail = null;
	// 显示发送人昵称
	private String sendName = null;

	private String from = null;
	// 标题
	private String subject = null;

	private String smtpHost = null;

	private String smtpUsername = null;

	private String smtpPassword = null;

	private String smtpAuth = "true";
	// 内容
	private String msg;

	static {
		p = new PropertiesUtil().load("log4j.properties");
	}

	public static void main(String[] args) {
		try {
			new Mail("群发测试!", "温家宝给港通公司各位发来亲切慰问!", "136776662@qq.com")
					.sendMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param bt
	 *            发送标题
	 * @param msg
	 *            发送内容
	 * @param to
	 *            发送给
	 * @param subject
	 *            发送标题
	 */
	public Mail(String bt, String msg, String... to) {

		this.sendName = p.getProperty("sendName").trim();
		this.from = p.getProperty("emailAddres").trim();
		this.smtpHost = p.getProperty("stmpHost").trim();
		this.smtpUsername = p.getProperty("userName").trim();
		this.smtpPassword = p.getProperty("userPassword").trim();
		this.subject = bt;

		smtpmail = new SMTPExtAppender();
		smtpmail.setSendName(this.sendName);
		smtpmail.setFrom(this.from);
		smtpmail.setTo(MyUtil.joinStr(",", to));
		smtpmail.setSubject(this.subject);
		smtpmail.setSMTPHost(this.smtpHost);
		smtpmail.setSMTPUsername(this.smtpUsername);
		smtpmail.setSMTPPassword(this.smtpPassword);
		smtpmail.setSMTPAuth(this.smtpAuth);
		this.msg = msg;
	}

	public void sendMessage() throws Exception {
		smtpmail.activateOptions();
		smtpmail.sendMessage(this.msg);
	}
}
