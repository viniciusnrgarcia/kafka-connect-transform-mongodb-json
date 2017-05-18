#!/usr/bin/env groovy

@Field String PROJECT = "kafka-connect-transform-mongodb-json"
@Field String GIT_ORGANIZATION = "ERP"
@Field String MAVEN_PATH = "/bin/mvn"
@Field String NS_GITHUB_OAUTH_TOKEN = "1944698ae35f8b199555527e3dd158f3fecb586b"

def clearWorkspace() {
    stage("Cleaning Workspace for ${PROJECT}") {
        sh 'pwd'
        deleteDir()
    }
}

def gitClone() {
    stage("Git Clone ${PROJECT}") {
        git credentialsId: '35256404-e1a7-4b81-bc38-9eb67d20a7a8', url: "https://github.ns2online.com.br/${GIT_ORGANIZATION}/${PROJECT}"
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
     tagvalue = CUSTOM_TAG
     def job = build job: 'freedom-middleware-app_run-docker-HMG', parameters: [[$class: 'StringParameterValue', name: 'FBMTAG', value: tagvalue]]
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
