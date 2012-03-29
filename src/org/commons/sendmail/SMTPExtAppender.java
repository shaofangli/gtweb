package org.commons.sendmail;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.CyclicBuffer;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;

/** */
/**
 * SMTP Appender扩展，增加对邮件认证的支持
 * 
 * 
 */
public class SMTPExtAppender extends AppenderSkeleton {

	private String sendName;

	private String to;

	private String from;

	private String subject;

	private String smtpHost;

	// define auth info
	private String smtpUsername;

	private String smtpPassword;

	private String smtpAuth;

	// --------------------------

	private int bufferSize = 512;

	private boolean locationInfo = false;

	protected CyclicBuffer cb = new CyclicBuffer(bufferSize);

	protected Message msg;

	protected TriggeringEventEvaluator evaluator;

	private static final Log log = LogFactory.getLog(SMTPExtAppender.class);

	/** */
	/**
	 * The default constructor will instantiate the appender with a
	 * {@link TriggeringEventEvaluator} that will trigger on events with level
	 * ERROR or higher.
	 */
	public SMTPExtAppender() {
		this(new DefaultEvaluator());
	}

	/** */
	/**
	 * Use <code>evaluator</code> passed as parameter as the {@link
	 * TriggeringEventEvaluator} for this SMTPAppender.
	 */
	public SMTPExtAppender(TriggeringEventEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	/** */
	/**
	 * Activate the specified options, such as the smtp host, the recipient,
	 * from, etc.
	 */
	public void activateOptions() {
		Properties props = new Properties(System.getProperties());
		Authenticator authenticator = null;

		if (smtpHost != null)
			props.put("mail.smtp.host", smtpHost);

		/**//* ADD auth code */
		if (smtpAuth != null && smtpAuth.trim().equals("true")) {
			props.put("mail.smtp.auth", "true");
			authenticator = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(smtpUsername,
							smtpPassword);
				}
			};
		}

		// Session session = Session.getInstance(props, null);
		Session session = Session.getInstance(props, authenticator);

		// session.setDebug(true);
		msg = new MimeMessage(session);

		try {
			if (from != null)
				msg.setFrom(getAddress(from, this.sendName));
			else
				msg.setFrom();

			msg.setRecipients(Message.RecipientType.TO, parseAddress(to));
			if (subject != null)
				msg.setSubject(subject);
		} catch (MessagingException e) {
			log.error("activateOptions:" + e.getMessage());
		}
	}

	/** */
	/**
	 * Perform SMTPAppender specific appending actions, mainly adding the event
	 * to a cyclic buffer and checking if the event triggers an e-mail to be
	 * sent.
	 */
	public void append(LoggingEvent event) {

		if (!checkEntryConditions()) {
			return;
		}

		event.getThreadName();
		event.getNDC();
		if (locationInfo) {
			event.getLocationInformation();
		}
		cb.add(event);
		if (evaluator.isTriggeringEvent(event)) {
			sendBuffer();
		}
	}

	/** */
	/**
	 * This method determines if there is a sense in attempting to append.
	 * 
	 * <p>
	 * It checks whether there is a set output target and also if there is a set
	 * layout. If these checks fail, then the boolean value <code>false</code>
	 * is returned.
	 */
	protected boolean checkEntryConditions() {
		if (this.msg == null) {
			errorHandler.error("Message object not configured.");
			return false;
		}

		if (this.evaluator == null) {
			errorHandler
					.error("No TriggeringEventEvaluator is set for appender ["
							+ name + "].");
			return false;
		}

		if (this.layout == null) {
			errorHandler.error("No layout set for appender named [" + name
					+ "].");
			return false;
		}
		return true;
	}

	synchronized public void close() {
		this.closed = true;
	}

	InternetAddress getAddress(String addressStr, String sendName) {
		try {
			return new InternetAddress(addressStr, sendName);
		} catch (UnsupportedEncodingException e) {
			// errorHandler.error("Could not parse address [" + addressStr
			// +"ga"+ "].", e, ErrorCode.ADDRESS_PARSE_FAILURE);
			log.error("getAddress:" + e.getMessage());
			return null;
		}
	}

	InternetAddress[] parseAddress(String addressStr) {
		try {
			return InternetAddress.parse(addressStr, true);
		} catch (AddressException e) {
			// errorHandler.error("Could not parse address [" + addressStr +
			// "].", e, ErrorCode.ADDRESS_PARSE_FAILURE);
			log.error("parseAddress:" + e.getMessage());
			return null;
		}
	}

	/** */
	/**
	 * Returns value of the <b>To</b> option.
	 */
	public String getTo() {
		return to;
	}

	/** */
	/**
	 * The <code>SMTPAppender</code> requires a {@link org.apache.log4j.Layout
	 * layout}.
	 */
	public boolean requiresLayout() {
		return true;
	}

	/** */
	/**
	 * Send the contents of the cyclic buffer as an e-mail message.
	 */
	protected void sendBuffer() {

		// Note: this code already owns the monitor for this
		// appender. This frees us from needing to synchronize on 'cb'.
		try {
			MimeBodyPart part = new MimeBodyPart();

			StringBuffer sbuf = new StringBuffer();
			String t = layout.getHeader();
			if (t != null)
				sbuf.append(t);
			int len = cb.length();
			for (int i = 0; i < len; i++) {
				// sbuf.append(MimeUtility.encodeText(layout.format(cb.get())));
				LoggingEvent event = cb.get();
				sbuf.append(layout.format(event));
				if (layout.ignoresThrowable()) {
					String[] s = event.getThrowableStrRep();
					if (s != null) {
						for (int j = 0; j < s.length; j++) {
							sbuf.append(s[j]);
						}
					}
				}
			}
			t = layout.getFooter();
			if (t != null)
				sbuf.append(t);
			part.setContent(sbuf.toString(), layout.getContentType()
					+ ";charset=UTF-8");

			Multipart mp = new MimeMultipart();
			mp.addBodyPart(part);
			msg.setContent(mp);
			msg.setSentDate(new Date());
			Transport.send(msg);
		} catch (Exception e) {
			// LogLog.error("Error occured while sending e-mail notification.",
			// e);
			log.error("sendBuffer:" + e.getMessage());
		}
	}

	// by lsf ;
	public void sendMessage(String message) throws Exception {
		try {
			MimeBodyPart mdp = new MimeBodyPart();// 新建一个存放信件内容的BodyPart对象
			mdp.setContent(message, "text/html;charset=GBK");

			Multipart mm = new MimeMultipart();// 新建一个MimeMultipart对象用来存放BodyPart对象(事实上可以存放多个)
			mm.addBodyPart(mdp);// 将BodyPart加入到MimeMultipart对象中(可以加入多个BodyPart)

			msg.setContent(mm);
			msg.setSentDate(new Date());
			Transport.send(msg);
		} catch (Exception x) {
			log.error("sendMessage:" + x.getMessage());
			throw new Exception("SMTPExtAppender sendMessage error:"
					+ x.getMessage());
		}

	}

	/** */
	/**
	 * Returns value of the <b>EvaluatorClass</b> option.
	 */
	public String getEvaluatorClass() {
		return evaluator == null ? null : evaluator.getClass().getName();
	}

	/** */
	/**
	 * Returns value of the <b>From</b> option.
	 */
	public String getFrom() {
		return from;
	}

	/** */
	/**
	 * Returns value of the <b>Subject</b> option.
	 */
	public String getSubject() {
		return subject;
	}

	/** */
	/**
	 * The <b>From</b> option takes a string value which should be a e-mail
	 * address of the sender.
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/** */
	/**
	 * The <b>Subject</b> option takes a string value which should be a the
	 * subject of the e-mail message.
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/** */
	/**
	 * The <b>BufferSize</b> option takes a positive integer representing the
	 * maximum number of logging events to collect in a cyclic buffer. When the
	 * <code>BufferSize</code> is reached, oldest events are deleted as new
	 * events are added to the buffer. By default the size of the cyclic buffer
	 * is 512 events.
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
		cb.resize(bufferSize);
	}

	/** */
	/**
	 * The <b>SMTPHost</b> option takes a string value which should be a the
	 * host name of the SMTP server that will send the e-mail message.
	 */
	public void setSMTPHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	/** */
	/**
	 * Returns value of the <b>SMTPHost</b> option.
	 */
	public String getSMTPHost() {
		return smtpHost;
	}

	/** */
	/**
	 * The <b>To</b> option takes a string value which should be a comma
	 * separated list of e-mail address of the recipients.
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/** */
	/**
	 * Returns value of the <b>BufferSize</b> option.
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/** */
	/**
	 * The <b>EvaluatorClass</b> option takes a string value representing the
	 * name of the class implementing the {@link TriggeringEventEvaluator}
	 * interface. A corresponding object will be instantiated and assigned as
	 * the triggering event evaluator for the SMTPAppender.
	 */
	public void setEvaluatorClass(String value) {
		evaluator = (TriggeringEventEvaluator) OptionConverter
				.instantiateByClassName(value, TriggeringEventEvaluator.class,
						evaluator);
	}

	/** */
	/**
	 * The <b>LocationInfo</b> option takes a boolean value. By default, it is
	 * set to false which means there will be no effort to extract the location
	 * information related to the event. As a result, the layout that formats
	 * the events as they are sent out in an e-mail is likely to place the wrong
	 * location information (if present in the format).
	 * 
	 * <p>
	 * Location information extraction is comparatively very slow and should be
	 * avoided unless performance is not a concern.
	 */
	public void setLocationInfo(boolean locationInfo) {
		this.locationInfo = locationInfo;
	}

	/** */
	/**
	 * Returns value of the <b>LocationInfo</b> option.
	 */
	public boolean getLocationInfo() {
		return locationInfo;
	}

	public String getSMTPAuth() {
		return smtpAuth;
	}

	/** */
	/**
	 * 设置是否进行SMTP认证。
	 */
	public void setSMTPAuth(String smtpAuth) {
		this.smtpAuth = smtpAuth;
	}

	/** */
	/**
	 * Returns value of the <b>SMTPPassword</b> option.
	 * 
	 * @return <b>SMTPPassword</b>
	 */
	public String getSMTPPassword() {
		return smtpPassword;
	}

	/** */
	/**
	 * 设置访问SMTP服务器的密码。
	 */
	public void setSMTPPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	/** */
	/**
	 * Returns value of the <b>SMTPUsername</b> option.
	 * 
	 * @return <b>SMTPUsername</b>
	 */
	public String getSMTPUsername() {
		return smtpUsername;
	}

	/** */
	/**
	 * 设置访问SMTP服务器的用户名。
	 */
	public void setSMTPUsername(String smtpUsername) {
		this.smtpUsername = smtpUsername;
	}

	public String getSendName() {
		return sendName;
	}

	public void setSendName(String sendName) {
		this.sendName = sendName;
	}

}

class DefaultEvaluator implements TriggeringEventEvaluator {
	/** */
	/**
	 * Is this <code>event</code> the e-mail triggering event?
	 * 
	 * <p>
	 * This method returns <code>true</code>, if the event level has ERROR
	 * level or higher. Otherwise it returns <code>false</code>.
	 */
	public boolean isTriggeringEvent(LoggingEvent event) {
		return event.getLevel().isGreaterOrEqual(Level.ERROR);
	}
}
