import org.wso2.tg.jenkins.Log

// First we need to validate all the properties, variables for not null or empty in //vars

// The pipeline should resite in a call block
def call() {
    def name = "a"
    if (name == "a") {
        pipeline {
            agent any
            stages {
                stage('Testing') {
                    steps {
                        script {
                            echo "This is a test"
                            def a = new Log()
                            a.info("This is a info")
                        }
                    }
                }
            }
        }
    } else {



    }
}
