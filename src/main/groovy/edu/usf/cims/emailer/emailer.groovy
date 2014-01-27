package edu.usf.cims.emailer

import javax.mail.*
import javax.mail.internet.*
import groovy.util.CliBuilder
import org.apache.commons.cli.Option
import javax.activation.*
import groovy.sql.Sql
import java.utils.*

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

		def myGroups = getVIPGroups(props)
		def myexpiredVIPs = getExpiredVIPs(props)
println 'got data'
		def myTemplate = runTemplate(conf,props)

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

	private static runTemplate (config,props) {
println 'running template'
		// def props = config.toProperties()
		def text = new File(config.templatePath).getText()
println 'got text'
		def expiredVIPs = getExpiredVIPs(props)
println 'got ExpiredVIPs'
		def groups = [ groups : expiredVIPs.groupBy {"${it.gid}-${it.created_vipid}"}.values() ]
println 'defined groups'
		def engine = new groovy.text.GStringTemplateEngine()
println 'created template engine'
		def template =  engine.createTemplate(text).make(groups)
println 'defining template'
		println template.toString()
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

	private static exitOnError(errorString){
		println("\nEmailer ERROR: ${errorString}\n")
		System.exit(1)
	}

}