import javax.mail.*
import javax.mail.internet.*
import javax.activation.*;


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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8"><title>VIP Expiration Warning.pdf</title><style type="text/css" media="print">.hide{display:none}</style></head><body style="margin:0;padding:0"><div class="hide"><div style="background:#ffffcc;padding:4 8;border-bottom:thin solid #eeeeee;font-family:Arial,sans-serif"><b>If there are images in this attachment, they will not be displayed.</b>&nbsp;&nbsp;<a href="/attachment/u/0/?view=att&amp;th=13f77dcd9df6b215&amp;attid=0.1&amp;disp=attd&amp;zw">Download the original attachment</a></div></div><div style="margin:1ex">





<div bgcolor="#ffffff" vlink="blue" link="blue">
<table border="0" width="100%"><tbody><tr><td bgcolor="eeeeee" align="right"><font face="arial,sans-serif"><a name="0.1_1"><b>Page 1</b></a></font></td></tr></tbody></table><font size="3" face="Times"><span style="font-size:19px;font-family:Times">
<div style="position:absolute;top:287;left:108">Good morning, (Sponsor):</div>
<div style="position:absolute;top:339;left:108">Thank you for being a Sponsor in our VIP System. As agreed, we will contact you </div>
<div style="position:absolute;top:364;left:108">to confirm the individuals you sponsored are still affiliated with USF and they still </div>
<div style="position:absolute;top:390;left:108">require the access to services that was originally requested by you on their behalf.</div>
<div style="position:absolute;top:441;left:108">The following individuals have accounts that are set to expire, resulting in loss of</div>
<div style="position:absolute;top:467;left:108">services, in the next _____ days if no further action is taken by you as their </div>
<div style="position:absolute;top:492;left:108">Sponsor:</div>
<div style="position:absolute;top:544;left:108">Last Name, First Name</div>
<div style="position:absolute;top:569;left:108">Last Name, First Name</div>
<div style="position:absolute;top:595;left:108">Last Name, First Name</div>
<div style="position:absolute;top:654;left:108">Please provide a status for each person listed, including their current relationship </div>
<div style="position:absolute;top:683;left:108">with USF, so we can take the appropriate action on their account.</div>
<div style="position:absolute;top:742;left:108">We appreciate your time and assistance in ensuring only authorized persons have </div>
<div style="position:absolute;top:772;left:108">access to IT services and resources, thus protecting the information contained</div>
<div style="position:absolute;top:801;left:108">therein.</div>
<div style="position:absolute;top:860;left:108">If you have any questions, please contact <font color="#0000ff" face="Times"><a href="mailto:IT-Security@usf.edu" target="_blank">IT-Security@usf.edu </a></font>for more </div>
<div style="position:absolute;top:890;left:108">information.</div>
<div style="position:absolute;top:949;left:108">Thank you.</div>
<div style="position:absolute;top:1008;left:108">Kay Svendgard</div>
<div style="position:absolute;top:1037;left:108">VIP System Owner</div>
<div style="position:absolute;top:1067;left:108">USF-IT- Office of Information Security</div>
</span></font>
</div>

</div></body></html>
 	""", "text/html")

// Send the message
Transport.send(msg)

/*
fixture.assertEmailArrived(from:'cruise@mycompany.org',
                           subject:'Successful build')
*/