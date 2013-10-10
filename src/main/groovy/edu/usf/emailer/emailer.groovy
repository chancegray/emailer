import javax.mail.*
import javax.mail.internet.*
import javax.activation.*;


config = new ConfigSlurper().parse(new File(System.getProperty("user.home")+'/emailer/emailer.properties').toURL())

props = new Properties()
props.put('mail.smtp.host', config.smtpHost)
props.put('mail.smtp.port', '25')
session = Session.getDefaultInstance(props, null)

// Construct the message
msg = new MimeMessage(session)
devteam = new InternetAddress('chance@mail.usf.edu')
msg.from = new InternetAddress('chance@usf.edu','Chance Gray')
msg.sentDate = new Date()
msg.subject = 'VIP Expiration Warning'
msg.setRecipient(Message.RecipientType.TO, devteam)
msg.setHeader('Organization', 'IT')
msg.setContent(config.content,config.contentType)

// Send the message
Transport.send(msg)