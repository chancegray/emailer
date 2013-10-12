package edu.usf.cims.emailer

import javax.mail.*
import javax.mail.internet.*
import javax.activation.*
import groovy.sql.Sql
import java.utils.*

class emailer {

	def sql
	def myTemplate

	public static void main(String[] args) {

	def config = new ConfigSlurper().parse(new File(System.getProperty("user.home")+'/emailer/emailer.properties').toURL())

	def myGroups = getVIPGroups()

	def myexpiredVIPs = getExpiredVIPs()


	def myTemplate = runTemplate()

	// println myTemplate.toString()

	}

	private static runTemplate () {

				def config = new ConfigSlurper().parse(new File(System.getProperty("user.home")+'/emailer/emailer.properties').toURL())

		def props = config.toProperties()
		def text = new File(config.templatePath).getText()

		def expiredVIPs = getExpiredVIPs()
		def groups = [ groups : expiredVIPs.groupBy {"${it.gid}-${it.created_vipid}"}.values() ]

		def engine = new groovy.text.GStringTemplateEngine()

		def template =  engine.createTemplate(text).make(groups)

		println template.toString()
		template
	}

	private static getVIPGroups() {
				def config = new ConfigSlurper().parse(new File(System.getProperty("user.home")+'/emailer/emailer.properties').toURL())

		def props = config.toProperties()
		def groups = [:]
		def sql = Sql.newInstance("jdbc:mysql://dev.it.usf.edu:3306/nams",props)
		sql.eachRow('select gid, label from vip_group') {
			groups.put(it.gid, it.label)
		}
		groups
	}

	private static getExpiredVIPs() {
				def config = new ConfigSlurper().parse(new File(System.getProperty("user.home")+'/emailer/emailer.properties').toURL())

		def props = config.toProperties()
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
}