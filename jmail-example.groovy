import javax.mail.*
import javax.mail.internet.*

port = 25
// fixture = new EmailFixture(port)

props = new Properties()
props.put('mail.smtp.host', 'aspmx.l.google.com')
props.put('mail.smtp.port', port.toString())
session = Session.getDefaultInstance(props, null)

// Construct the message
msg = new MimeMessage(session)
devteam = new InternetAddress('chance@mail.usf.edu')
//partners = new InternetAddress('partners@mycompany.org')
msg.from = new InternetAddress('chance@larn.it.usf.edu')
msg.sentDate = new Date()
msg.subject = 'Successful build'
msg.setRecipient(Message.RecipientType.TO, devteam)
//msg.setRecipient(Message.RecipientType.CC, partners)
msg.setHeader('Organization', 'mycompany.org')
msg.setContent('Successful build for ' + new Date(),
               'text/plain')

// Send the message
Transport.send(msg)

/*
fixture.assertEmailArrived(from:'cruise@mycompany.org',
                           subject:'Successful build')
*/