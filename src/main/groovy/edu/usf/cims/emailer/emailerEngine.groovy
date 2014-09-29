package edu.usf.cims.emailer

//for email functionality
import javax.mail.*
import javax.mail.internet.*
import javax.activation.*

import java.utils.*

//for reading csv input files specified at runtime
import static com.xlson.groovycsv.CsvParser.parseCsv



class EmailerEngine {
	
	static def version = "20140402"
	
	def runTemplate(template,templateData) {
		// is the templat an empty string
		if (template == "") {
			throw  new EmailerEngineEmptyTemplateException()
		}

		// is the templateData an empty string
		if (templateData == "") {
			throw  new EmailerEngineNoTemplateDataException()
		}

		//construct the template
		def engine = new groovy.text.GStringTemplateEngine()
		def text =  engine.createTemplate(template).make(templateData)

		//return stringified template
		template = template.toString()
	}

	def parseCSVContents(csvdata) {
		if (csvdata == "") {
			throw new EmailerEngineCSVEmptyStringException()
		}
		def data = parseCsv(csvdata)
		
		//parse data one line per element into result
		def result = []
		for(line in data){
			result+=([line])
		}
		result
	}

	def sendEmail(config,templateText) {
		def props = config.toProperties()

		def session = Session.getDefaultInstance(props, null)

		// Construct the message
		def msg = new MimeMessage(session)
		def toAddr = new InternetAddress(props.recipient)
		//TODO: process where there are multiple recipients
		//TODO: add options for bcc/cc
		if (props.ccfield) {
			msg.addRecipient(Message.RecipientType.CC, new InternetAddress(props.ccfield))
		}
		if (config.ccfields) {
			config.ccfields.each() { msg.addRecipient(Message.RecipientType.CC, new InternetAddress(it))}
		}
		if (props.bccfield) {
			msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(props.bccfield))
		}
		if (config.bccfields) {
			config.bccfields.each() { msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(it))}
		}

		msg.from = new InternetAddress(props.fromAddr)
		msg.sentDate = new Date()
		msg.subject = props.subject
		msg.setRecipient(Message.RecipientType.TO, toAddr)
		//TODO: create Header iterator
		msg.setHeader('Organization', 'USF-IT')
		msg.setContent(templateText, "text/html")
		// Send the message
		Transport.send(msg)
	}

}

class EmailerEngineCSVEmptyStringException extends RuntimeException{}
class EmailerEngineNoTemplateDataException extends RuntimeException{}
class EmailerEngineEmptyTemplateException extends RuntimeException{}
