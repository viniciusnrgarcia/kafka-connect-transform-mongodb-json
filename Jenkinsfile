#!/usr/bin/env groovy

import groovy.transform.Field
import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import java.util.concurrent.TimeUnit

@Field String PROJECT = "kafka-connect-transform-mongodb-json"
@Field String GIT_ORGANIZATION = "netshoes"
@Field String MAVEN_PATH = "/bin/mvn"
@Field String NS_GITHUB_OAUTH_TOKEN = "c262b64c81d50187893a5e691500fc0d6923727a"

def clearWorkspace() {
    stage("Cleaning Workspace for ${PROJECT}") {
        sh 'pwd'
        deleteDir()
    }
}

def gitClone() {
    stage("Git Clone ${PROJECT}") {
        git credentialsId: '56cfb7ed-219d-4132-8975-68fee35035d5', url: "https://github.com/${GIT_ORGANIZATION}/${PROJECT}"
    }
}

def buildJava() {
    stage("Build Java application") {
        withMaven(maven: 'maven-3.3.9') {
            sh "mvn clean package -U"
        }
    }
}

def deployHMG(){
  stage ("Deploy on HMG"){
  node {
      //deploy em hmg on /usr/share/java/kafka-connect-transform-mongodb-json
      //freedom-bi-connectors (hmg) https://www.confluent.io/
      //hmg-free-bi-connector-01.netshoes.local 172.18.56.234
      //hmg-free-bi-connector-02.netshoes.local 172.18.56.235
      //hmg-free-bi-connector-03.netshoes.local 172.18.56.236
     }
  }
}

node("erp-jenkins-slave0") {
    try {
        clearWorkspace()
        gitClone()
        buildJava()

    } catch (Exception err) {
        println(err)
        currentBuild.result = "FAILURE"
        //deleteLocalDockerImages()
    }
    finally {
        //notifyBuild()
        println ("Pipeline finished")
        }
}
