package edu.usf.cims.emailer

import spock.lang.*
import static com.xlson.groovycsv.CsvParser.parseCsv
import groovy.json.*
import javax.mail.*
import javax.mail.internet.*
import javax.activation.*
import java.utils.*
import groovy.text.*


class EmailerSpec extends spock.lang.Specification {

    def "CSV file does not exist"() {
        given:
        def emailer = new Emailer()

        when:
        emailer.readInputFile("ThisDoesnotExist.txt")

        then:
        thrown(java.io.FileNotFoundException)
    }

    def "Illegal option"() {
    	given:
    	def emailer = new Emailer()
    	String[] args = ['--recipientHdr=blah','--recipient="user@nowhere.com"']
    	def options = emailer.getCommandLineOptions(args)


    	when:
    	emailer.getConfigSettings(options)

    	then:
    	thrown(IllegalArgumentException)
    }

}