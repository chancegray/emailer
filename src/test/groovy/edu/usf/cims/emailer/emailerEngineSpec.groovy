package edu.usf.cims.emailer

import spock.lang.*
//import static com.xlson.groovycsv.CsvParser.parseCsv
//import groovy.json.*
//import javax.mail.*
//import javax.mail.internet.*
//import javax.activation.*
//import java.utils.*
import groovy.text.*


class EmailerEngineSpec extends spock.lang.Specification {

    def "Empty template"() {
    	given:
    	def emEng = new EmailerEngine()
    	def templateData = [ templateData : 'fname: blah,lname: bleh', recipientAddr : 'winner@sweepstakes.com', recipientHdr : 'email' ]

    	when:
    	def blah=emEng.runTemplate("",templateData)

    	then:
    	thrown(EmailerEngineEmptyTemplateException)
    }

    def "Bad template"() {
        given:
        def emEng = new EmailerEngine()
        def templateData = [ templateData : 'fname: blah,lname: bleh', recipientAddr : 'winner@sweepstakes.com', recipientHdr : 'email' ]

        when:
        def blah=emEng.runTemplate('<% for (line in templates) { print "${line.(blah)}" } this might be a bad template ${blahdiblah}',templateData)

        then:
        thrown(groovy.lang.GroovyRuntimeException)
    }

    def "No Template Data"() {
    	given:
    	def emEng = new EmailerEngine()

    	when:
    	def blah=emEng.runTemplate("There is actually a template here","")

    	then:
    	thrown(EmailerEngineNoTemplateDataException)
    }

    def "CSV is Empty String"() {
        given:
        def emEng = new EmailerEngine()

        when:
        def blah=emEng.parseCSVContents("")

        then:
        thrown(EmailerEngineCSVEmptyStringException)
    }
}