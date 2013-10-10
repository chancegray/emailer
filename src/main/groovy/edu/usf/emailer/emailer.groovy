package edu.usf.cims.emailer

import javax.mail.*
import javax.mail.internet.*
import javax.activation.*
import groovy.sql.Sql



config = new ConfigSlurper().parse(new File(System.getProperty("user.home")+'/emailer/emailer.properties').toURL())

props = config.toProperties()

//props = new Properties()
props.put('mail.smtp.host', config.smtpHost)
props.put('mail.smtp.port', '25')
session = Session.getDefaultInstance(props, null)

text = File(config.template)

def sql = Sql.newInstance("jdbc:mysql://dev.it.usf.edu:3306/nams",props)

def expiredVIPs = getExpiredVIPs(sql)

def template = engine.createTemplate(text).make(expiredVIPs)
println template.toString()