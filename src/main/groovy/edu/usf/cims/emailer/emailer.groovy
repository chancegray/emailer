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
	static def version = "0.0.1"
	def conf = new ConfigObject()

	public static void main(String[] args) {

	try {			
		def opt = getCommandLineOptions(args)
		def conf = getConfigSettings(opt)
		def props = conf.toProperties()
		def myTemplate = runTemplate(conf,props,opt)

		sendEmail(props,myTemplate)

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

		cli.with {
			h longOpt:'help', 'usage information', required: false
			v longOpt:'version', 'version information', required: false  
			i longOpt:'inputFile', args:1, argName:'inputFile', 'csv file with email address and template values'
			f longOpt:'fromAddr', args:1, argName:'fromAddr', 'address of the sender'
			t longOpt:'template', args:1, argName:'template', 'template of the message'
			e longOpt:'emailHdr', args:1, argName:'emailHdr', 'Header in csv file to identify column with recipient email addresses'
		
			_ longOpt:'defaults', args:1, argName:'configFileName', 'groovy config file', required: false
		}

		def options = cli.parse(args)

		//Display version info
		if(options.version){
			println "\nVersion: ${version}\n"
			System.exit(0)
		}

		//Display usage if --help is given 
		if( (options.help) ){
			cli.usage() 
			System.exit(0)
		}

		if( (options.fromAddr) ) {
			println "\n${options.fromAddr}\n"
			System.exit(0)
		}

		if( (options.template) ) {
			println "\n${options.template}\n"
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
			return config
		} else {
			System.exit(1)
		}

		//Merge the defaults file that was passed on the commandline
		if(options.defaults){
			def newConfigFile = new File(options.defaults)
			config = config.merge(new ConfigSlurper().parse(newConfigFile.toURL()))
		}

		return config
	}

	private static runTemplate (config,props,options) {
		def text = new File(config.templatePath).getText()
		def expiredVIPs = retrieveCSVContents(options)
		def groups = [ groups : expiredVIPs.groupBy {"${it.gid}-${it.created_vipid}"}.values() ]
		def engine = new groovy.text.GStringTemplateEngine()
		def template =  engine.createTemplate(text).make(groups)
		template = template.toString()
		template
	}

private static retrieveCSVContents(options) {
   		def fstring = new File(options.inputFile).getText()
		def data = parseCsv(fstring)
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
	private static sendEmail(props, templateText) {

		def session = Session.getDefaultInstance(props, null)

		// Construct the message
		def msg = new MimeMessage(session)
		def devteam = new InternetAddress(props.recipient)
		msg.from = new InternetAddress(props.sender)
		msg.sentDate = new Date()
		msg.subject = props.subject
		msg.setRecipient(Message.RecipientType.TO, devteam)
		msg.setHeader('Organization', 'USF-IT')
		msg.setContent(templateText, "text/html")
		// Send the message
		Transport.send(msg)


	}

}