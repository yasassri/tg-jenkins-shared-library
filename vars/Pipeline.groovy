import org.wso2.tg.jenkins.*

// First we need to validate all the properties, variables for not null or empty in //vars

// The pipeline should resite in a call block
def call() {
    echoFunc()
    pipeline {
        agent any
        stages {
            stage('Testing') {
                steps {
                    echo "This is a test"
                }
            }
        }
    }
}
