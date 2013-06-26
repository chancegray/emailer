import javax.mail.*
import javax.mail.internet.*
import javax.activation.*;


port = 25
// fixture = new EmailFixture(port)

props = new Properties()
// props.put('mail.smtp.host', 'aspmx.l.google.com')
props.put('mail.smtp.host', 'bumblebee.forest.usf.edu')

props.put('mail.smtp.port', port.toString())
session = Session.getDefaultInstance(props, null)

// Construct the message
msg = new MimeMessage(session)
devteam = new InternetAddress('chance@mail.usf.edu')
//partners = new InternetAddress('partners@mycompany.org')
msg.from = new InternetAddress('chance@usf.edu','Chance Gray')
msg.sentDate = new Date()
msg.subject = 'VIP Expiration Warning'
msg.setRecipient(Message.RecipientType.TO, devteam)
//msg.setRecipient(Message.RecipientType.CC, partners)
msg.setHeader('Organization', 'USF-IT')
//commented for attachment msg.setContent('Successful build for ' + new Date(),
//commented for attachment             'text/plain')

/*
messageBodyPart = new MimeBodyPart()
messageBodyPart.setText("""
	Good morning, (Sponsor):
Thank you for being a Sponsor in our VIP System. As agreed, we will contact you to confirm the individuals you sponsored are still affiliated with USF and they still require the access to services that was originally requested by you on their behalf.
The following individuals have accounts that are set to expire, resulting in loss of services, in the next _____ days if no further action is taken by you as their Sponsor:
Last Name, First Name Last Name, First Name Last Name, First Name
Please provide a status for each person listed, including their current relationship with USF, so we can take the appropriate action on their account.
We appreciate your time and assistance in ensuring only authorized persons have access to IT services and resources, thus protecting the information contained therein.
If you have any questions, please contact IT-Security@usf.edu for more information.
Thank you.
Kay Svendgard
VIP System Owner
USF-IT- Office of Information Security
	""")
multipart = new MimeMultipart()
multipart.addBodyPart(messageBodyPart)

//add the attachment
		messageBodyPart = new MimeBodyPart();
         String filename = "file.txt";
         DataSource source = new FileDataSource(filename);
         messageBodyPart.setDataHandler(new DataHandler(source));
         messageBodyPart.setFileName(filename);
         multipart.addBodyPart(messageBodyPart);
 msg.setContent(multipart )
 */
 msg.setContent("""
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>VIP Expiration Warning</title>
</head>

<body style="margin:0;padding:0">
&nbsp;&nbsp;

<table align="left">

Good morning, Sponsor:
<br><br>

Thank you for being a Sponsor in our VIP System. As agreed, we will contact you 
to confirm the individuals you sponsored are still affiliated with USF and they still 
require the access to services that was originally requested by you on their behalf.
<br><br>

The following individuals have accounts that are set to expire, resulting in loss of
services, in the next _____ days if no further action is taken by you as their 
Sponsor:
<br><br>

Last Name, First Name
<br>
Last Name, First Name
<br>
Last Name, First Name
<br>
<br><br>

Please provide a status for each person listed, including their current relationship 
with USF, so we can take the appropriate action on their account.
<br><br>

We appreciate your time and assistance in ensuring only authorized persons have 
access to IT services and resources, thus protecting the information contained
therein.
<br><br>

If you have any questions, please contact <font color="#0000ff" face="Times">
<a href="mailto:IT-Security@usf.edu" target="_blank">IT-Security@usf.edu </a></font>for more </div>
information.
<br><br>

Thank you.
<br><br>

Kay Svendgard
<br>
VIP System Owner
<br>
USF-IT- Office of Information Security
<br>
</body>
</html>
 	""", "text/html")

// Send the message
Transport.send(msg)

/*
fixture.assertEmailArrived(from:'cruise@mycompany.org',
                           subject:'Successful build')
*/