package edu.usf.cims.emailer

//for email functionality
import javax.mail.*
import javax.mail.internet.*
import javax.activation.*

//for cli parsing and config munging
import groovy.util.CliBuilder
import org.apache.commons.cli.Option
import java.utils.*

//for reading csv input files specified at runtime
import static com.xlson.groovycsv.CsvParser.parseCsv


class Emailer {

	def myTemplate
	static def version = "20140220-Alpha"
	def conf = new ConfigObject()

	public static void main(String[] args) {

	try {			
		def opt = getCommandLineOptions(args)
		def conf = getConfigSettings(opt)

		//get data
		def CSVContents = retrieveCSVContents(conf)
		
		//create template
		def templateData = [ templateData : CSVContents ]

		//process the template
		def myTemplate = runTemplate(conf,templateData)

		// final action
		if (opt.debug) {

			println myTemplate
			println ""
			println conf
			println ""
		
			} else {
				sendEmail(conf, myTemplate)
			}

		}catch(Exception e) {
			exitOnError e.message
		}

	}

	private static getCommandLineOptions(String[] args){
		//Parse command-line options
		def cli = new CliBuilder(
						usage:"emailer [options]",
						header:"\nAvailable options (use -h for help):\n",
						width:100)

		
		cli.h longOpt:'help', 'usage information', required: false
		cli.v longOpt:'version', 'version information', required: false 
		cli.d longOpt:'debug', 'Turn on debugging', required: false 
		cli.i longOpt:'inputFile', args:1, argName:'inputFile', 'csv file with email address and template values'
		cli.f longOpt:'fromAddr', args:1, argName:'fromAddr', 'address of the sender'
		cli.t longOpt:'template', args:1, argName:'template', 'template of the message'
		cli.r longOpt:'recipient', args:1, argName:'recipient', 'single recipient'
		//TODO: add the possibility of a per message custom subject heading?
		cli.s longOpt:'subject', args:1, argName:'subject', 'subject heading of the message'
		cli.e longOpt:'recipientHdr', args:1, argName:'recipientHdr', 'Header in csv file to identify column with recipient email addresses'
		cli._ longOpt:'defaults', args:1, argName:'configFileName', 'groovy config file', required: false
		

		def options = cli.parse(args)

		//Display version info
		if(options.version){
			println "\nVersion: ${version}\n"
			System.exit(0)
		}

		//Display usage if --help is given 
		if( (options.help) ) {
			cli.usage() 
			System.exit(0)
		}

		
		return options
	}

	private static getConfigSettings(options){
		def config = new ConfigObject()

		/** Defaut configuration values can be set in $HOME/emailer/emailer-conf.groovy **/                   
		def defaultConfigFile = new  File(System.getProperty("user.home")+'/emailer/emailer-conf.groovy')

		//The default file is not required, so if it doesn't exist don't throw an exception
		if (defaultConfigFile.exists() && defaultConfigFile.canRead()) {
			config = new ConfigSlurper().parse(defaultConfigFile.toURL())
		} else {
			System.exit(1)
		}

		//Merge the defaults file that was passed on the commandline
		if(options.defaults){
			def newConfigFile = new File(options.defaults)
			config = config.merge(new ConfigSlurper().parse(newConfigFile.toURL()))
		}

		if( options?.fromAddr ) {
			config.put('fromAddr', options.fromAddr)
		}

		if( options?.template ) {
			config.put('template', options.template)
		}

		if( options?.recipient ) {
			config.put('recipient', options.recipient)
		}

		if( options?.recipientHdr ) {
			config.put('recipientHdr', options.recipientHdr)
		}

		if( options?.subject ) {
			config.put('subject', options.subject)
		}

		if( options?.sender ) {
			config.put('sender', options.sender)
		}

		if( options?.inputFile ) {
			config.put('inputFile', options.inputFile)
		}

		config
	}

	private static runTemplate (config,templateData) {
		//read the template
		def text = new File(config.template).getText()

		//construct the template
		def engine = new groovy.text.GStringTemplateEngine()
		def template =  engine.createTemplate(text).make(templateData)

		//return stringified template
		template = template.toString()
	}

	private static retrieveCSVContents(config) {
		//read our data file
   		def fstring = new File(config.inputFile).getText()
		def data = parseCsv(fstring)
		
		//parse data one line per element into result
		def result = []
		for(line in data){
			result+=([line])
		}
		result

	}

	private static exitOnError(errorString){
		println("\nEmailer ERROR: ${errorString}\n")
		System.exit(1)
	}

	private static sendEmail(config,templateText) {
		def props = config.toProperties()

		def session = Session.getDefaultInstance(props, null)

		// Construct the message
		def msg = new MimeMessage(session)
		def devteam = new InternetAddress(props.recipient)
		msg.from = new InternetAddress(props.sender)
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