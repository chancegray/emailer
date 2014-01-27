package edu.usf.cims.emailer

import spock.lang.*
import groovy.json.*

class EmailerSpec extends spock.lang.Specification {

    def "Bad Template"() {
        given:
        def emailer = new Emailer()

        when:
        emailer.runTemplate()

        then:
        def e = thrown(java.io.FileNotFoundException)
    }
}