package edu.usf.cims.emailer

//for email functionality
import javax.mail.*
import javax.mail.internet.*
import javax.activation.*

//for cli parsing and config munging
//import groovy.util.CliBuilder
//import org.apache.commons.cli.Option
import java.utils.*

//for reading csv input files specified at runtime
import static com.xlson.groovycsv.CsvParser.parseCsv



class EmailerEngine {
	
	static def version = "20140402"

	def conf = new ConfigObject()
	def myTemplate

	
	def runTemplate (text,templateData) {
		//construct the template
		def engine = new groovy.text.GStringTemplateEngine()
		def template =  engine.createTemplate(text).make(templateData)

		//return stringified template
		template = template.toString()
	}

	def parseCSVContents(fstring) {
		def data = parseCsv(fstring)
		
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
		def devteam = new InternetAddress(props.recipient)
		//TODO: process where there are multiple recipients
		//TODO: add options for bcc/cc
		if (props.ccfield) {
			msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(props.ccfield))
		}

		msg.from = new InternetAddress(props.fromAddr)
		msg.sentDate = new Date()
		msg.subject = props.subject
		msg.setRecipient(Message.RecipientType.TO, devteam)
		//TODO: create Header iterator
		msg.setHeader('Organization', 'USF-IT')
		msg.setContent(templateText, "text/html")
		// Send the message
		Transport.send(msg)


	}

}