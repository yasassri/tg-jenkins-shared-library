import org.wso2.tg.jenkins.util.common

// First we need to validate all the properties, variables for not null or empty in //vars

// The pipeline should resite in a call block
def call() {

    pipeline {
        agent any
        stages {
            stage('Testing') {
                steps {
                    script {
                        echo "This is a test"
                        def a = new common()
                        a.echoFunc()
                    }
                }
            }
        }
    }
}
