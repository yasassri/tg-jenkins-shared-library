package org.wso2.tg.jenkins.pipeline

// First we need to validate all the properties, variables for not null or empty in //vars

// The pipeline should resite in a call block
def call() {
    pipeline {
        agent {
            node {
                label ""
                customWorkspace '/home/ubuntu/tmp'
            }
        }
        stages {
            stage('Testing') {
                steps {
                    echo "This is a test"
                }
            }
        }
    }
}
