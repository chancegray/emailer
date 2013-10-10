package edu.usf.cims.emailer

import javax.mail.*
import javax.mail.internet.*
import javax.activation.*
import groovy.sql.Sql


class emailer {

	public static void main(String[] args) {

		def config = new ConfigSlurper().parse(new File(System.getProperty("user.home")+'/emailer/emailer.properties').toURL())

		def props = config.toProperties()

		//props = new Properties()
		/*
		props.put('mail.smtp.host', config.smtpHost)
		props.put('mail.smtp.port', '25')
		session = Session.getDefaultInstance(props, null)
		*/

		def text = File(config.template).getText()

		def sql = Sql.newInstance("jdbc:mysql://dev.it.usf.edu:3306/nams",props)

		def expiredVIPs = getExpiredVIPs(sql)

		def engine = new groovy.text.GStringTemplateEngine()

		def template =  engine.createTemplate(text).make(expiredVIPs)
		println template.toString()
	}
}