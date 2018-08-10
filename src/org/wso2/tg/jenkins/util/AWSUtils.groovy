package org.wso2.tg.jenkins

def getS3BucketName() {
    script {
        loadProperties()
    }
    def bucket = properties['AWS_S3_BUCKET_NAME']
    if ("${bucket}" == "null") {
        bucket = "unknown"
    }
    return bucket
}

def uploadToS3() {
    def s3BucketName = getS3BucketName()
    sh """
      aws s3 sync ${TESTGRID_HOME}/jobs/${PRODUCT}/builds/ s3://${s3BucketName}/artifacts/jobs/${PRODUCT}/builds --include "*"
      """
}

def loadProperties() {
    node {
        properties = readProperties file: "${TESTGRID_HOME}/config.properties"
    }
}