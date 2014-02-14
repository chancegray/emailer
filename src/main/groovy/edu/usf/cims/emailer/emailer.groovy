package edu.usf.cims.emailer

import javax.mail.*
import javax.mail.internet.*
import groovy.util.CliBuilder
import org.apache.commons.cli.Option
import javax.activation.*
import groovy.sql.Sql
import java.utils.*

import static com.xlson.groovycsv.CsvParser.parseCsv


class Emailer {

	def sql
	def myTemplate
	static def version = "0.0.1"
	def conf = new ConfigObject()

	public static void main(String[] args) {

	try {			
		def opt = getCommandLineOptions(args)
		def conf = getConfigSettings(opt)
		def props = conf.toProperties()

		def myvips = getExpiredVIPsFromCSV(opt)
		//def myGroups = getVIPGroups(props)
		//def myexpiredVIPs = getExpiredVIPs(props)
println 'got data'
		//def myTemplate = runTemplate(conf,props)
		def myTemplate = runTemplate(conf,props,opt)
/*
for(it in myvips){println "$it.fname $it.lname $it.expiration_dt"}
*/
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
			e longOpt:'emailHdr', args:1, argName:'emailHdr', 'Header in csv file to identify column with email addresses'
		
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
println 'running template'
		// def props = config.toProperties()
		def text = new File(config.templatePath).getText()
println 'got text'
		//def expiredVIPs = getExpiredVIPs(props)
		def expiredVIPs = getExpiredVIPsFromCSV(options)
println 'got ExpiredVIPs'
		def groups = [ groups : expiredVIPs.groupBy {"${it.gid}-${it.created_vipid}"}.values() ]
		//def groups = [ groups : expiredVIPs.values() ]

println 'defined groups'
		def engine = new groovy.text.GStringTemplateEngine()
println 'created template engine'
		def template =  engine.createTemplate(text).make(groups)
println 'defining template'
		template = template.toString()
		template
	}

	private static getVIPGroups(props) {
		def groups = [:]
		def sql = Sql.newInstance("jdbc:mysql://dev.it.usf.edu:3306/nams",props)
		sql.eachRow('select gid, label from vip_group') {
			groups.put(it.gid, it.label)
		}
		groups
	}

	private static getExpiredVIPs(props) {

		def sql = Sql.newInstance("jdbc:mysql://dev.it.usf.edu:3306/nams",props)

		def vips = sql.rows("""select
		vg.gid as gid,
		v.fname as fname,
		v.lname as lname,
		vgm.created_dt as created_dt,
		vgm.created_vipid as created_vipid,
		vgm.expiration_dt as expiration_dt
		from vip_group_member vgm 
		LEFT JOIN vip_group vg ON vgm.gid=vg.gid 
		LEFT JOIN vip v on v.vipid=vgm.vipid
		where 
		expiration_dt between '2013-01-03 00:00:00' and '2013-01-20 00:00:00' 
		and vgm.function='M'
		""")
	}

private static getExpiredVIPsFromCSV(options) {
   		def fstring = new File(options.inputFile).getText()
		def data = parseCsv(fstring)
		def result = []
		for(line in data){
			result+=[gid: "$line.gid" ,
					fname: "$line.fname",
					lname: "$line.lname",
					created_dt: "$line.created_dt",
					created_vipid: "$line.created_vipid",
					expiration_dt: "$line.expiration_dt", ]

		}
		result

	}

	private static exitOnError(errorString){
		println("\nEmailer ERROR: ${errorString}\n")
		System.exit(1)
	}
	private static sendEmail(props, templateText) {
		//props = new Properties()
props.put('mail.smtp.host', 'aspmx.l.google.com')
//props.put('mail.smtp.host', 'bumblebee.forest.usf.edu')

//props.put('mail.smtp.port', port.toString())
def session = Session.getDefaultInstance(props, null)

// Construct the message
def msg = new MimeMessage(session)
//def devteam = new InternetAddress('dwest@mail.usf.edu')
def devteam = new InternetAddress(props.recipient)
//partners = new InternetAddress('partners@mycompany.org')
msg.from = new InternetAddress('chance@usf.edu','Chance Gray')
msg.sentDate = new Date()
msg.subject = 'VIP Expiration Warning'
msg.setRecipient(Message.RecipientType.TO, devteam)
//msg.setRecipient(Message.RecipientType.CC, partners)
msg.setHeader('Organization', 'USF-IT')
msg.setContent(templateText, "text/html")
// Send the message
Transport.send(msg)


	}

}